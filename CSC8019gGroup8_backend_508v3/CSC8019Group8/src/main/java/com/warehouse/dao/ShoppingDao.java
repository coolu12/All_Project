package com.warehouse.dao;

import com.warehouse.pojo.Commodity;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
/**
 * Handles database operations related to commodities, including retrieval, insertion, updating, and deletion.
 * This DAO interface provides methods for interacting with the commodity table in the database,
 * allowing retrieval of commodities based on various criteria, insertion of new commodities,
 * updating existing commodities, deletion of commodities by ID, and counting the total number of commodities
 * and filtered commodities.
 *
 * Methods provided by this interface facilitate CRUD (Create, Read, Update, Delete) operations on commodities.
 *
 * Error Handling:
 * - The DAO methods handle exceptions and database errors gracefully during commodity operations.
 * - Database errors and failures in retrieval, insertion, updating, or deletion of commodities are appropriately handled
 *   and logged for troubleshooting purposes.
 *
 * Usage of this DAO should align with database design and transaction management best practices to ensure
 * reliable and efficient management of commodities.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@Mapper
public interface ShoppingDao {

    /**
     * Retrieves a filtered and sorted list of commodities based on various criteria.
     *
     * @param productName The name of the product to filter by (partial or full).
     * @param productType The type of the product to filter by.
     * @param minPrice The minimum price of the product.
     * @param maxPrice The maximum price of the product.
     * @param sortBy The attribute by which the results should be sorted.
     * @param order The sorting order (asc or desc).
     * @param limit The maximum number of results to return.
     * @param offset The offset of the first result to return (for pagination).
     * @return A list of commodities that match the specified criteria.
     */
    List<Commodity> selectCommodities(@Param("productName") String productName,
                                      @Param("productType") String productType,
                                      @Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice,
                                      @Param("sortBy") String sortBy,
                                      @Param("order") String order,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);

    /**
     * Retrieves a single commodity by its ID.
     *
     * @param commodityId The ID of the commodity.
     * @return The Commodity object, or null if not found.
     */
    @Select("SELECT * FROM commodity WHERE commodity_id = #{commodityId}")
    Commodity selectCommodityById(@Param("commodityId") Integer commodityId);


    /**
     * Deletes a commodity by its ID.
     *
     * @param commodityId The ID of the commodity to delete.
     */
    @Delete("DELETE FROM commodity WHERE commodity_id = #{commodityId}")
    void deleteCommodity(@Param("commodityId") Integer commodityId);

    /**
     * Inserts a new commodity into the database.
     *
     * @param commodity The commodity object to insert.
     */
    @Insert("INSERT INTO commodity (product_id, product_name, product_type, original_price, commodity_discount) " +
            "VALUES (#{productID}, #{productName}, #{productType}, #{originalPrice}, #{commodityDiscount})")
    void insertCommodity(Commodity commodity);

    /**
     * Updates an existing commodity in the database.
     *
     * @param commodity The commodity object with updated fields.
     */
    @Update("UPDATE commodity SET product_id = #{productID}, product_name = #{productName}, product_type = #{productType}, " +
            "original_price = #{originalPrice}, commodity_discount = #{commodityDiscount} " +
            "WHERE commodity_id = #{commodityID}")
    void updateCommodity(Commodity commodity);


    /**
     * Retrieves commodities within a specified price range after discount.
     *
     * @param minPrice The minimum discounted price.
     * @param maxPrice The maximum discounted price.
     * @return A list of commodities within the specified discounted price range.
     */
    @Select("SELECT * FROM commodity WHERE original_price * commodity_discount BETWEEN #{minPrice} AND #{maxPrice}")
    List<Commodity> selectByDiscountedPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Counts the total number of commodities in the database.
     *
     * @return The total count of commodities.
     */
    @Select("SELECT COUNT(*) FROM commodity")
    Long count();


    /**
     * Counts commodities that fit specified filter criteria.
     *
     * @param productName The product name to filter by.
     * @param productType The product type to filter by.
     * @param minPrice The minimum price to filter by.
     * @param maxPrice The maximum price to filter by.
     * @return The count of commodities that meet the filter criteria.
     */
    long countFilteredCommodities(@Param("productName") String productName,
                                  @Param("productType") String productType,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice);
}

