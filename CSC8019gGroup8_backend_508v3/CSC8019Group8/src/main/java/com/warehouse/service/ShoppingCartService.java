package com.warehouse.service;

import com.warehouse.pojo.Commodity;
import com.warehouse.pojo.ShoppingCart;

import java.util.List;

/**
 * Service interface for managing shopping cart operations.
 * This interface defines methods for adding, retrieving, and deleting items in the shopping cart.
 *
 * Methods:
 * - addShoppingCart: Adds a commodity to the shopping cart.
 * - shoppingCartsList: Retrieves a list of items in the shopping cart.
 * - getShoppingCartDetails: Retrieves the details of items in the shopping cart.
 * - delete: Deletes a commodity from the shopping cart.
 * - clean: Clears the shopping cart.
 * - deleteBatch: Deletes a batch of commodities from the shopping cart.
 *
 * This interface serves as a contract for implementing classes to provide shopping cart management functionality.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
public interface ShoppingCartService {

    /**
     * Adds a commodity to the shopping cart.
     * @param commodityId ID of the commodity to add.
     */
    void addShoppingCart(Integer commodityId);

    /**
     * Retrieves a list of items in the shopping cart.
     * @return List of {@link ShoppingCart} items.
     */
    List<ShoppingCart> shoppingCartsList();

    /**
     * Retrieves the details of items in the shopping cart.
     * @return List of {@link Commodity} in the shopping cart.
     */
    List<Commodity> getShoppingCartDetails();

    /**
     * Deletes a commodity from the shopping cart.
     * @param commodityId ID of the commodity to delete.
     */
    void delete(Integer commodityId);

    /**
     * Clears the shopping cart.
     */
    void clean();

    /**
     * Deletes a batch of commodities from the shopping cart.
     * @param commodityIds List of commodity IDs to delete.
     */
    void deleteBatch(List<Integer> commodityIds);
}
