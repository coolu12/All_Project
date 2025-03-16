package com.warehouse.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.warehouse.pojo.ShoppingCart;
import com.warehouse.service.ShoppingCartService;
import com.warehouse.service.ShoppingService;
import com.warehouse.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Manages payment processing for purchases in the application.
 * This controller class orchestrates the creation of checkout sessions for Stripe payment processing,
 * handles successful payment completion, and manages payment cancellation scenarios.
 *
 * Endpoints provided by this controller facilitate the creation of checkout sessions,
 * handling successful payments, and managing payment cancellations.
 *
 * Key Endpoints:
 * - POST /payment/create-checkout-session: Creates a checkout session for Stripe payment processing.
 * - GET /payment/success: Handles successful payment processing and cleans up the shopping cart.
 * - GET /payment/cancel: Handles payment cancellation scenarios.
 *
 * Error Handling:
 * - The controller handles exceptions and error responses gracefully during payment processing.
 * - Internal server errors and payment verification failures are appropriately handled and logged for troubleshooting purposes.
 *
 * This controller assumes operation in a secure environment with proper security configurations in place,
 * including HTTPS for secure communication and access control measures to restrict unauthorized access.
 *
 * Usage of this controller should align with security best practices and payment processing regulations
 * to ensure secure and reliable payment processing for purchases.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@CrossOrigin
@Controller
public class PaymentController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingService shoppingService;

    @Value("${stripe.api.key}")
    private String apiKey;

    /**
     * Initializes the Stripe API with the provided API key.
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    /**
     * Creates a checkout session for Stripe payment processing.
     * It calculates the total price from the shopping cart, creates a session, and redirects to the Stripe checkout page.
     *
     * @param response the HttpServletResponse object for redirecting the user to the payment page
     * @return a Result object that either contains a URL to the payment session or an error message
     */
    @PostMapping("/payment/create-checkout-session")
    @ResponseBody
    public Result createCheckoutSession(HttpServletResponse response) {
        try {
            List<ShoppingCart> cartItems = shoppingCartService.shoppingCartsList();
            BigDecimal totalPrice = cartItems.stream()
                    .map(item -> shoppingService.getCommodityByID(item.getCommodityID()).getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long priceInCents = totalPrice.multiply(new BigDecimal(100)).longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:8080/payment/cancel")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(priceInCents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Total Cart Value")
                                            .build())
                                    .build())
                            .build())
                    .build();

            Session session = Session.create(params);
            response.sendRedirect(session.getUrl());
            return null;
        } catch (StripeException | IOException e) {
            e.printStackTrace();
            return new Result(0, "Failed to create payment session", null);
        }
    }

    /**
     * Handles the successful payment process. It cleans up the shopping cart and updates the system's state.
     *
     * @param sessionId the session ID from Stripe to verify the payment status
     * @return a Result object indicating success or failure
     */
    @GetMapping("/payment/success")
    public Result handlePaymentSuccess(@RequestParam("session_id") String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("paid".equals(session.getPaymentStatus())) {
                List<ShoppingCart> cartItems = shoppingCartService.shoppingCartsList();
                cartItems.forEach(item -> shoppingService.delete(item.getCommodityID()));
                shoppingCartService.clean();
                return new Result(1, "Payment successful and cart cleaned", null);
            }
        } catch (StripeException e) {
            e.printStackTrace();
            return new Result(0, "Payment verification failed", null);
        }
        return new Result(0, "Payment verification failed", null);
    }

    /**
     * Handles payment cancellation and informs the user that the payment was not completed.
     *
     * @return a Result object indicating that the payment was cancelled
     */
    @GetMapping("/payment/cancel")
    public Result handlePaymentCancellation() {
        return new Result(0, "Payment cancelled", null);
    }
}
