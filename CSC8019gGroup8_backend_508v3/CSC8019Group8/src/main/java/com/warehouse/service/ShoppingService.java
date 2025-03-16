package com.warehouse.service;

import com.warehouse.pojo.Commodity;
import com.warehouse.pojo.PageBean;

import java.math.BigDecimal;
import java.util.List;
/**
 * Service interface for managing commodities in the shopping module.
 * This interface defines methods for retrieving, adding, updating, and deleting commodities,
 * as well as fetching commodities within a specified discounted price range.
 *
 * Methods:
 * - listCommodities: Retrieves a paginated list of commodities based on specified criteria.
 * - delete: Deletes a commodity by its ID.
 * - insert: Inserts a new commodity.
 * - update: Updates an existing commodity.
 * - getCommodityByID: Retrieves a commodity by its ID.
 * - getCommoditiesByDiscountedPriceRange: Retrieves a list of commodities within a specified discounted price range.
 *
 * This interface serves as a contract for implementing classes to provide commodity management functionality.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */

public interface ShoppingService {

    /**
     * Retrieves a paginated list of commodities based on specified criteria.
     * @param sortBy The field to sort by.
     * @param order The sorting order (ascending or descending).
     * @param productName The name of the product.
     * @param productType The type of the product.
     * @param minPrice The minimum price of the product.
     * @param maxPrice The maximum price of the product.
     * @param page The page number.
     * @param size The number of items per page.
     * @return A PageBean object containing the paginated list of {@link Commodity}.
     */
    PageBean<Commodity> listCommodities(String sortBy, String order, String productName, String productType, BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

    /**
     * Deletes a commodity by its ID.
     * @param num The ID of the commodity to delete.
     */
    void delete(Integer num);

    /**
     * Inserts a new commodity.
     * @param commodity The commodity object to insert.
     */
    void insert(Commodity commodity);

    /**
     * Updates an existing commodity.
     * @param commodity The commodity object with updated information.
     */
    void update(Commodity commodity);

    /**
     * Retrieves a commodity by its ID.
     * @param id The ID of the commodity to retrieve.
     * @return The {@link Commodity} object.
     */
    Commodity getCommodityByID(Integer id);

    /**
     * Retrieves a list of commodities within a specified discounted price range.
     * @param minPrice The minimum discounted price.
     * @param maxPrice The maximum discounted price.
     * @return List of {@link Commodity} within the specified price range.
     */
    List<Commodity> getCommoditiesByDiscountedPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

}
