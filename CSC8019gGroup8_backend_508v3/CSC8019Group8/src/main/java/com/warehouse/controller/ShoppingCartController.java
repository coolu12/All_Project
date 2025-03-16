package com.warehouse.controller;

import com.warehouse.pojo.Commodity;
import com.warehouse.service.ShoppingCartService;
import com.warehouse.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Manages operations related to the shopping cart, including retrieving, adding, and deleting commodities.
 * This controller class provides endpoints for managing the user's shopping cart, allowing users to view,
 * add, delete single or multiple commodities, and clean the entire shopping cart.
 *
 * Endpoints provided by this controller facilitate interactions with the shopping cart, such as adding,
 * removing, and cleaning commodities.
 *
 * Key Endpoints:
 * - GET /shoppingCart/list: Retrieves a list of all commodities in the user's shopping cart.
 * - GET /shoppingCart/{commodityId}: Adds a commodity to the shopping cart based on its ID.
 * - DELETE /shoppingCart/{commodityId}: Deletes a single commodity from the shopping cart based on its ID.
 * - DELETE /shoppingCart/batch: Deletes multiple commodities from the shopping cart based on their IDs.
 * - DELETE /shoppingCart/clean: Cleans all commodities from the shopping cart.
 *
 * Error Handling:
 * - The controller handles exceptions and error responses gracefully during shopping cart operations.
 * - Internal server errors and failures in adding, deleting, or cleaning commodities are appropriately handled
 *   and logged for troubleshooting purposes.
 *
 * This controller assumes operation in a secure environment with proper security configurations in place,
 * including HTTPS for secure communication and access control measures to restrict unauthorized access.
 *
 * Usage of this controller should align with security best practices and data privacy regulations to ensure
 * secure and reliable management of the shopping cart.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@RestController
@CrossOrigin
@RequestMapping("/shoppingCart")
@Tag(name = "Shopping Cart Management", description = "Handles operations related to the shopping cart.")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * Retrieves a list of all commodities in the user's shopping cart.
     * @return Result containing the list of {@link Commodity} or an error message.
     */
    @GetMapping("/list")
    @ResponseBody
    public Result getList() {
        try {
            List<Commodity> commodities = shoppingCartService.getShoppingCartDetails();
            return new Result(1, "Success", commodities);
        } catch (Exception e) {
            return new Result(0, "Failed to fetch commodities", null);
        }
    }

    /**
     * Adds a commodity to the shopping cart based on a commodity ID.
     * @param commodityId The ID of the commodity to add.
     * @return Result indicating success or failure of the addition.
     */
    @GetMapping("/{commodityId}")
    @ResponseBody
    public Result insertCommodity(@PathVariable Integer commodityId) {
        try {
            shoppingCartService.addShoppingCart(commodityId);
            return new Result(1, "Commodity added successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to add commodity", null);
        }
    }

    /**
     * Deletes a single commodity from the shopping cart based on its ID.
     * @param commodityId The ID of the commodity to delete.
     * @return Result indicating the success or failure of the deletion.
     */
    @DeleteMapping("/{commodityId}")
    @ResponseBody
    public Result deleteCommodity(@PathVariable Integer commodityId) {
        try {
            shoppingCartService.delete(commodityId);
            return new Result(1, "Commodity deleted successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to delete commodity", null);
        }
    }

    /**
     * Deletes multiple commodities from the shopping cart based on their IDs.
     * @param commodityIds List of IDs for the commodities to be deleted.
     * @return Result indicating the success or failure of the batch deletion.
     */
    @DeleteMapping("/batch")
    @ResponseBody
    public Result deleteCommoditiesByBatch(@RequestBody List<Integer> commodityIds) {
        try {
            shoppingCartService.deleteBatch(commodityIds);
            return new Result(1, "Commodities deleted successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to delete commodities", null);
        }
    }

    /**
     * Cleans all commodities from the shopping cart.
     * @return Result indicating the success or failure of cleaning the shopping cart.
     */
    @DeleteMapping("/clean")
    @ResponseBody
    public Result CleanCart() {
        try {
            shoppingCartService.clean();
            return new Result(1, "Shopping cart cleaned successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to clean the shopping cart", null);
        }
    }
}
