package com.warehouse.service.implement;

import com.warehouse.context.BaseContext;
import com.warehouse.dao.ShoppingCartDao;
import com.warehouse.dao.ShoppingDao;
import com.warehouse.pojo.Commodity;
import com.warehouse.pojo.ShoppingCart;
import com.warehouse.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing shopping cart operations.
 * This class provides methods to add, retrieve, and delete items in the shopping cart.
 *
 * Methods:
 * - addShoppingCart: Adds a commodity to the shopping cart for the current user.
 * - shoppingCartsList: Retrieves a list of all shopping cart items for the current user.
 * - getShoppingCartDetails: Retrieves details of all commodities in the shopping cart of the current user.
 * - delete: Deletes a specific commodity from the shopping cart.
 * - deleteBatch: Deletes multiple commodities from the shopping cart.
 * - clean: Clears all items from the shopping cart of the current user.
 *
 * This class interacts with ShoppingCartDao and ShoppingDao to perform database operations.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@Service
public class ShoppingCartServiceImplement implements ShoppingCartService {

    @Autowired
    private ShoppingCartDao shoppingCartDao; // DAO to access shopping cart data.
    @Autowired
    private ShoppingDao shoppingDao; // DAO to access commodity data.

    /**
     * Adds a commodity to the shopping cart for the current user.
     * @param commodityID The ID of the commodity to add.
     */
    @Override
    public void addShoppingCart(Integer commodityID) {
        String userId = BaseContext.getCurrentId(); // Retrieves the current user's ID.
        ShoppingCart existingCartItem = new ShoppingCart();
        existingCartItem.setUserID(userId);
        existingCartItem.setCommodityID(commodityID);
        shoppingCartDao.insertShoppingCart(existingCartItem); // Inserts the new shopping cart item.
    }

    /**
     * Retrieves a list of all shopping cart items for the current user.
     * @return List of {@link ShoppingCart} items.
     */
    @Override
    public List<ShoppingCart> shoppingCartsList() {
        String userId = BaseContext.getCurrentId(); // Retrieves the current user's ID.
//        return shoppingCartDao.getShoppingCartByUserId(userId);
        return shoppingCartDao.getShoppingCartByUserId("j01206093");
    }

    /**
     * Retrieves details of all commodities in the shopping cart of the current user.
     * @return List of {@link Commodity} in the shopping cart.
     */
    @Override
    public List<Commodity> getShoppingCartDetails() {
        String userId = BaseContext.getCurrentId();
//        List<ShoppingCart> cartItems = shoppingCartDao.getShoppingCartByUserId(userId);
        List<ShoppingCart> cartItems = shoppingCartDao.getShoppingCartByUserId("j01206093");

        // Converts shopping cart items into commodities by fetching commodity details.
        return cartItems.stream()
                .map(item -> shoppingDao.selectCommodityById(item.getCommodityID()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a specific commodity from the shopping cart.
     * @param commodityId The ID of the commodity to delete.
     */
    @Override
    public void delete(Integer commodityId) {
        shoppingCartDao.deleteCommodity(commodityId);
    }

    /**
     * Deletes multiple commodities from the shopping cart.
     * @param commodityIds A list of commodity IDs to delete.
     */
    @Override
    public void deleteBatch(List<Integer> commodityIds) {
        shoppingCartDao.deleteBatch(commodityIds);
    }

    /**
     * Clears all items from the shopping cart of the current user.
     */
    @Override
    public void clean() {
        String userId = BaseContext.getCurrentId();
        shoppingCartDao.clean(userId);
    }
}
