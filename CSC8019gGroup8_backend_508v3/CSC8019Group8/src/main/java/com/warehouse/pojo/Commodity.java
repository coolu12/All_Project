package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * Represents a commodity or product in the warehouse inventory.
 * This POJO (Plain Old Java Object) class encapsulates the attributes of a commodity, including its unique identifier,
 * product ID, name, type, original price, and discount rate.
 * It provides methods to calculate the discounted price of the commodity based on its original price and discount rate.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */

@Data // Lombok annotation to generate getters, setters, equals, hashcode, and toString methods automatically.
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor.
@AllArgsConstructor // Lombok annotation to generate an all-argument constructor.
public class Commodity {
    /**
     * Unique identifier for the commodity, automatically incremented in the database.
     */
    private Integer commodityID;

    /**
     * External or internal product identifier.
     */
    private String productID;

    /**
     * The name of the product.
     */
    private String productName;

    /**
     * The type or category of the product.
     */
    private String productType;

    /**
     * The original price of the product before any discounts.
     */
    private BigDecimal originalPrice;

    /**
     * The discount rate applied to the product. Represented as a decimal (e.g., 0.20 for 20% discount).
     */
    private Double commodityDiscount;

    /**
     * Constructor with parameters.
     */

    /**
     * Calculates the discounted price of the product.
     * @return The price after applying the discount.
     */
    public BigDecimal getPrice() {
        BigDecimal discountMultiplier = BigDecimal.valueOf(commodityDiscount);
        return originalPrice.multiply(discountMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
