package com.warehouse.controller;

import com.warehouse.pojo.Commodity;
import com.warehouse.pojo.PageBean;
import com.warehouse.service.ShoppingService;
import com.warehouse.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
/**
 * Manages operations related to commodity management in the shopping module, including listing, adding, updating,
 * and deleting commodities.
 * This controller class provides endpoints for managing commodities, allowing users to retrieve a paginated list
 * of commodities with optional sorting and filtering, add new commodities, update existing commodities, delete
 * commodities by ID, and fetch commodities within a specified price range.
 *
 * Endpoints provided by this controller facilitate interactions with commodities in the shopping module,
 * enabling CRUD (Create, Read, Update, Delete) operations.
 *
 * Key Endpoints:
 * - GET /shopping/list: Retrieves a paginated list of commodities with optional sorting and filtering.
 * - POST /shopping/add: Adds a new commodity to the database.
 * - GET /shopping/discountedPriceRange: Retrieves commodities within a specified price range.
 * - PUT /shopping/update: Updates an existing commodity in the database.
 * - DELETE /shopping/{commodityId}: Deletes a commodity based on its ID.
 *
 * Error Handling:
 * - The controller handles exceptions and error responses gracefully during commodity management operations.
 * - Internal server errors and failures in listing, adding, updating, or deleting commodities are appropriately handled
 *   and logged for troubleshooting purposes.
 *
 * This controller assumes operation in a secure environment with proper security configurations in place,
 * including HTTPS for secure communication and access control measures to restrict unauthorized access.
 *
 * Usage of this controller should align with security best practices and data privacy regulations to ensure
 * secure and reliable management of commodities in the shopping module.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@RestController
@CrossOrigin
@RequestMapping("/shopping")
@Tag(name = "Shopping Management", description = "Handles operations related to the commodity management in the shopping module.")
public class ShoppingController {

    @Autowired
    private ShoppingService shoppingService;

    /**
     * Retrieves a paginated list of commodities with optional sorting and filtering.
     * Allows filtering by product name, type, and price range. Sorting can be performed on any column.
     *
     * @param productName Optional filter by product name using partial match.
     * @param productType Optional filter by product type.
     * @param minPrice Optional minimum price filter.
     * @param maxPrice Optional maximum price filter.
     * @param sortBy Column to sort the results by.
     * @param order Sort direction ('asc' or 'desc').
     * @param page Page number for pagination.
     * @param size Number of records per page.
     * @return Paginated list of {@link Commodity}.
     */
    @GetMapping("/list")
    public Result getSortedCommodities(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "commodityID") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageBean<Commodity> pageBean = shoppingService.listCommodities(sortBy, order, productName, productType, minPrice, maxPrice, page, size);
            return new Result(1, "Successfully retrieved sorted and paginated commodities", pageBean);
        } catch (Exception e) {
            return new Result(0, "Failed to fetch commodities", null);
        }
    }

    /**
     * Adds a new commodity to the database.
     *
     * @param commodity Commodity object to be inserted.
     * @return Result indicating success or failure.
     */
    @PostMapping("/add")
    @ResponseBody
    public Result insertCommodity(@RequestBody Commodity commodity) {
        try {
            shoppingService.insert(commodity);
            return new Result(1, "Commodity inserted successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to insert commodity", null);
        }
    }

    /**
     * Retrieves commodities within a specified price range.
     *
     * @param minPrice Minimum price of the commodities to fetch.
     * @param maxPrice Maximum price of the commodities to fetch.
     * @return Result containing the list of {@link Commodity}.
     */
    @GetMapping("/discountedPriceRange")
    public Result getCommoditiesByDiscountedPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        try {
            List<Commodity> commodities = shoppingService.getCommoditiesByDiscountedPriceRange(minPrice, maxPrice);
            return new Result(1, "Commodities fetched successfully", commodities);
        } catch (Exception e) {
            return new Result(0, "Failed to fetch commodity", null);
        }
    }

    /**
     * Updates an existing commodity in the database.
     *
     * @param commodity Updated commodity object.
     * @return Result indicating success or failure of the update.
     */
    @PutMapping("/update")
    @ResponseBody
    public Result updateCommodity(@RequestBody Commodity commodity) {
        try {
            shoppingService.update(commodity);
            return new Result(1, "Commodity updated successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to update commodity", null);
        }
    }

    /**
     * Deletes a commodity based on its ID.
     *
     * @param commodityId ID of the commodity to delete.
     * @return Result indicating success or failure of the deletion.
     */
    @DeleteMapping("/{commodityId}")
    @ResponseBody
    public Result deleteCommodity(@PathVariable Integer commodityId) {
        try {
            shoppingService.delete(commodityId);
            return new Result(1, "Commodity deleted successfully", null);
        } catch (Exception e) {
            return new Result(0, "Failed to delete commodity", null);
        }
    }
}
