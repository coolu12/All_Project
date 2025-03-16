package com.warehouse.service.implement;

import com.warehouse.annotation.Log;
import com.warehouse.dao.ShoppingDao;
import com.warehouse.pojo.Commodity;
import com.warehouse.pojo.PageBean;
import com.warehouse.service.ShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation for managing commodity operations.
 * This class provides methods for retrieving, adding, updating, and deleting commodities.
 *
 * Methods:
 * - listCommodities: Retrieves a paginated list of commodities based on various filters and sorting parameters.
 * - delete: Deletes a commodity based on its ID.
 * - insert: Inserts a new commodity into the database.
 * - update: Updates an existing commodity in the database.
 * - getCommodityByID: Retrieves a commodity by its ID.
 * - getCommoditiesByDiscountedPriceRange: Retrieves a list of commodities where the discounted price falls within the specified range.
 *
 * This class interacts with ShoppingDao to perform database operations.
 *
 * This class is annotated with @Log to enable logging of method calls.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@Service
public class ShoppingServiceImplement implements ShoppingService {

    @Autowired
    private ShoppingDao shoppingDao; // DAO to access commodity data.

    /**
     * Retrieves a paginated list of commodities based on various filters and sorting parameters.
     * @param sortBy Field to sort by (e.g., 'product_type' or 'discountedPrice').
     * @param order Sorting order ('asc' or 'desc').
     * @param productName Optional filter by product name.
     * @param productType Optional filter by product type.
     * @param minPrice Optional filter by minimum price.
     * @param maxPrice Optional filter by maximum price.
     * @param page Current page number.
     * @param size Number of records per page.
     * @return A page bean containing the list of {@link Commodity} and total number of records.
     */
    @Override
    public PageBean<Commodity> listCommodities(String sortBy, String order, String productName, String productType, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        int offset = (page - 1) * size;
        List<Commodity> commodities = shoppingDao.selectCommodities(productName, productType, minPrice, maxPrice, sortBy, order, size, offset);
        long total = shoppingDao.countFilteredCommodities(productName, productType, minPrice, maxPrice);
        return new PageBean<>(total, commodities);
    }

    /**
     * Deletes a commodity based on its ID.
     * @param num ID of the commodity to delete.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer num) {
        shoppingDao.deleteCommodity(num);
    }

    /**
     * Inserts a new commodity into the database.
     * @param commodity Commodity to insert.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void insert(Commodity commodity) {
        shoppingDao.insertCommodity(commodity);
    }

    /**
     * Updates an existing commodity in the database.
     * @param commodity Commodity to update.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void update(Commodity commodity) {
        shoppingDao.updateCommodity(commodity);
    }

    /**
     * Retrieves a commodity by its ID.
     * @param commodityId ID of the commodity.
     * @return The {@link Commodity} if found.
     */
    @Override
    public Commodity getCommodityByID(Integer commodityId) {
        return shoppingDao.selectCommodityById(commodityId);
    }

    /**
     * Retrieves a list of commodities where the discounted price falls within the specified range.
     * @param minPrice Minimum price of the range.
     * @param maxPrice Maximum price of the range.
     * @return List of {@link Commodity} within the specified price range.
     */
    @Override
    public List<Commodity> getCommoditiesByDiscountedPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return shoppingDao.selectByDiscountedPriceRange(minPrice, maxPrice);
    }
}
