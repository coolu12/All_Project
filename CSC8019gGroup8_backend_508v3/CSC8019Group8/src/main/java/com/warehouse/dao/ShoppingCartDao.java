package com.warehouse.dao;

import com.warehouse.pojo.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;
/**
 * Handles database operations related to the shopping cart, including retrieving, adding, updating, and deleting
 * shopping cart items.
 * This DAO interface provides methods for interacting with the shopping cart table in the database,
 * allowing retrieval of shopping cart items by user ID, insertion of new items into the shopping cart,
 * deletion of specific commodities from the shopping cart, cleaning all items from a user's shopping cart,
 * and deleting a batch of commodities from the shopping cart by their IDs.
 *
 * Methods provided by this interface facilitate CRUD (Create, Read, Update, Delete) operations on shopping cart items.
 *
 * Error Handling:
 * - The DAO methods handle exceptions and database errors gracefully during shopping cart operations.
 * - Database errors and failures in retrieving, adding, updating, or deleting shopping cart items are appropriately handled
 *   and logged for troubleshooting purposes.
 *
 * Usage of this DAO should align with database design and transaction management best practices to ensure
 * reliable and efficient management of shopping cart items.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@Mapper
public interface ShoppingCartDao {

    /**
     * Retrieves all shopping cart items for a specific user.
     *
     * @param userID The user ID associated with the shopping cart items.
     * @return A list of ShoppingCart objects belonging to the specified user.
     */
    @Select("SELECT * FROM shoppingcart WHERE user_id = #{userID}")
    List<ShoppingCart> getShoppingCartByUserId(@Param("userID") String userID);

    /**
     * Deletes a specific commodity from all users' shopping carts.
     *
     * @param commodityID The ID of the commodity to be deleted.
     */
    @Delete("DELETE FROM shoppingcart WHERE commodity_id = #{commodityID}")
    void deleteCommodity(@Param("commodityID") Integer commodityID);

    /**
     * Inserts a new item into the shopping cart.
     *
     * @param shoppingCart The ShoppingCart object containing the commodity ID, product name, and user ID.
     */
    @Insert("INSERT INTO shoppingcart (commodity_id, product_name, user_id) VALUES (#{cart.commodityID},#{cart.productName}, #{cart.userID})")
    void insertShoppingCart(@Param("cart") ShoppingCart shoppingCart);

    /**
     * Clears all items from a user's shopping cart.
     *
     * @param userID The user ID whose shopping cart will be cleared.
     */
    @Delete("DELETE FROM shoppingcart WHERE user_id = #{userID}")
    void clean(@Param("userID") String userID);

    /**
     * Deletes a batch of commodities from the shopping cart by their IDs.
     *
     * @param commodityIds A list of commodity IDs to be deleted from the shopping cart.
     */
    @Delete("<script>"
            + "DELETE FROM shoppingcart WHERE commodity_id IN "
            + "<foreach item='id' collection='list' open='(' separator=',' close=')'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    void deleteBatch(@Param("list") List<Integer> commodityIds);
}
