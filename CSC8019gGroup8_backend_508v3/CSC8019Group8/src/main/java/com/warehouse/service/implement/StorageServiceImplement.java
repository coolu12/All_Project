package com.warehouse.service.implement;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.warehouse.annotation.Log;
import com.warehouse.dao.ShoppingDao;
import com.warehouse.dao.StorageDao;
import com.warehouse.constant.ShoppingConstant;
import com.warehouse.pojo.*;
import com.warehouse.service.StorageService;
import com.warehouse.utils.JwtUtils;
import io.jsonwebtoken.Claims;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link StorageService} interface, providing concrete methods for managing
 * storage-related operations. This class handles the business logic associated with storage
 * management, including CRUD operations on storage records and retrieval of storage data.
 * <p>
 * This class is annotated with Spring's {@link Service} annotation, designating it as a component
 * suitable for handling business logic. Additionally, it utilizes {@link Transactional} annotation
 * to manage transactions, ensuring data consistency and rollback capabilities in case of failures.
 * The data access operations are performed using {@link StorageDao}, ensuring separation of concerns
 * between data access and business logic.
 * <p>
 * This class is part of the service layer in the application architecture, serving as an intermediary
 * between the controller layer and the data access layer. It encapsulates the business logic associated
 * with statistical operations, ensuring separation of concerns and maintainability of the codebase.
 *
 * @author Jianan Zhao
 */
@Slf4j
@Service
public class StorageServiceImplement implements StorageService {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private StorageDao storageDao;

    @Autowired
    private ShoppingDao shoppingDao;

    /**
     * Retrieves a paginated list of storage records based on specified criteria.
     *
     * @param storageID     The ID of the storage record.
     * @param productName   The name of the product.
     * @param productType   The type of the product.
     * @param returnRequest The return request status of the storage record,
     *                      0 for refund, 1 for repair, 2 for recycle.
     * @param currentStatus The current status of the storage record,
     *                      0 for recorded, 1 for checked, 2 for refunded, 3 for repaired, 4 for recycled.
     * @param operateTime   The time of the storage record operation.
     * @param operatorName  The name of the operator.
     * @param startDate     The start date for filtering storage records.
     * @param endDate       The end date for filtering storage records.
     * @param page          The page number for pagination.
     * @param pageSize      The number of records per page.
     * @return A paginated list of storage records.
     */
    @Override
    public PageBean getStorageList(Integer storageID, String productName, String productType,
                                   String returnRequest, Integer currentStatus,
                                   LocalDateTime operateTime, String operatorName,
                                   LocalDate startDate, LocalDate endDate, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Storage> storageList = storageDao.getStorageList(storageID, productName, productType, returnRequest,
                currentStatus, operateTime, operatorName, startDate, endDate);
        //Set currentOperate value
        for (Storage storage : storageList) {
            if (storage.getOperateList() != null) {
                Operate currentOperate = storage.getOperateList().get(0);
                for (Operate operate : storage.getOperateList()) {
                    if (operate.getOperateStatus() > currentOperate.getOperateStatus()) {
                        currentOperate = operate;
                    }
                }
                storage.setCurrentOperate(currentOperate);
            }
        }
        Page<Storage> storagePage = (Page<Storage>) storageList;
        return new PageBean(storagePage.getTotal(), storagePage.getResult());
    }

    /**
     * Deletes storage records and associated data based on the provided list of storage IDs.
     *
     * @param storageIDList The list of storage IDs to be deleted.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void deleteStorage(List<String> storageIDList) {
        try {
            List<Integer> intStorageIDList = new ArrayList<>();
            List<String> productIDList = new ArrayList<>();
            List<String> returnOrderNumList = new ArrayList<>();
            for (String strStorageID : storageIDList) {
                Integer tempStorageID = Integer.valueOf(strStorageID);
                intStorageIDList.add(tempStorageID);
                Storage tempStorage = getStorageDataByID(tempStorageID);
                productIDList.add(tempStorage.getProduct().getProductID());
                returnOrderNumList.add(tempStorage.getReturnOrder().getReturnOrderNum());
            }

            storageDao.deleteProductData(productIDList);
            storageDao.deleteReturnOrderData(returnOrderNumList);
            storageDao.deleteOperateData(intStorageIDList);
            storageDao.deleteStorageData(intStorageIDList);
        } catch (Exception e) {
            // Rollback transaction when an exception occurs
            throw new RuntimeException("Failed to delete storage data: " + e.getMessage(), e);
        }
    }

    /**
     * Inserts a list of storage records into the database.
     *
     * @param storageList The list of storage records to be inserted.
     */
    @Override
//    @Log
//    @Transactional(rollbackFor = Exception.class)
    public void insertStorage(List<Storage> storageList) {
        for (Storage storage : storageList) {
            LocalDateTime operateTime = LocalDateTime.now();
            // 成功登录后使用
            //String jwt = httpServletRequest.getHeader("token");
//            Claims claims = JwtUtils.parseJWT(jwt);
//            String operatorID = String.valueOf(claims.get("userID"));
            //operator ID
            String operatorID = "a00000001";
            User currentOperator = new User();
            currentOperator.setUserID(operatorID);
            Operate newOperate = new Operate(null, 0,
                    operateTime, 0, currentOperator);
            storage.setCurrentOperate(newOperate);
            try {
                storageDao.insertStorageData(storage);
                Integer tempStorageID = storageDao.getLastInsertedStorageID();
                storageDao.insertProductData(storage);
                storageDao.insertReturnOrderData(storage);
                storage.setStorageID(tempStorageID);
                storageDao.insertOperateData(storage);
            } catch (Exception e) {
                // Rollback transaction when an exception occurs
                throw new RuntimeException("Failed to insert storage data: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Updates storage records based on the provided list of operation details.
     *
     * @param operateList The list of operation details for updating storage records.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void updateStorage(List<Operate> operateList) {
        try {

            // get operatorID from jwt token
            String jwt = httpServletRequest.getHeader("token");
            Claims claims = JwtUtils.parseJWT(jwt);
            String operatorID = String.valueOf(claims.get("userID"));

            // Enable for bypassing interceptors during testing
            // String operatorID = "a00000001";

            for (Operate operate : operateList) {
                LocalDateTime operateTime = LocalDateTime.now();
                operate.setOperateTime(operateTime);
                // set operator_id
                User operator = new User();
                operator.setUserID(operatorID);
                operate.setOperator(operator);
                storageDao.updateStorage(operate);
                // add to shop after being refunded operateStatus= 2  --> refunded
                if (operate.getOperateStatus() == 2) {
                    Storage tempStorage = storageDao.getStorageDataByID(operate.getStorageID());
                    Commodity commodity = new Commodity(null, tempStorage.getProduct().getProductID(),
                            tempStorage.getProduct().getProductName(), tempStorage.getProduct().getProductType(),
                            tempStorage.getProduct().getOriginalPrice(), ShoppingConstant.DEFAULT_DISCOUNT);
                    shoppingDao.insertCommodity(commodity);
                }
            }
        } catch (Exception e) {
            // Rollback transaction when an exception occurs
            throw new RuntimeException("Failed to update storage data: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves storage record data based on the provided storage ID.
     *
     * @param storageID The ID of the storage record to retrieve.
     * @return The storage record data.
     */
    @Override
    public Storage getStorageDataByID(Integer storageID) {
        Storage storage = storageDao.getStorageDataByID(storageID);
        storage.setCurrentOperate(storage.getOperateList().get(storage.getOperateList().size() - 1));
        return storage;
    }
}
