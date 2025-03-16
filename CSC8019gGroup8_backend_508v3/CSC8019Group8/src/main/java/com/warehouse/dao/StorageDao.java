package com.warehouse.dao;

import com.warehouse.pojo.Operate;
import com.warehouse.pojo.Storage;
import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access interface for managing storage-related operations.
 * This interface defines methods for retrieving, inserting, updating, and deleting storage records
 * from the data source.
 *
 * The methods in this interface enable fetching storage records based on various criteria,
 * inserting new storage records, updating existing records, and deleting records and associated data.
 *
 * This interface is typically implemented by classes that interact with the database or other
 * data sources to perform CRUD operations on storage records.
 *
 * This interface is annotated with {@link Mapper}, indicating it as a MyBatis mapper interface.
 * MyBatis will generate the implementation for this interface at runtime.
 *
 * @implNote This interface is implemented by a MyBatis mapper XML file located at
 * resources/com/warehouse/dao/StorageDao.xml. The SQL queries for the methods defined in
 * this interface are specified in the XML file.
 * @author Jianan Zhao
 */
@Mapper
public interface StorageDao {
    /**
     * Retrieves a list of storage records based on the specified criteria.
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
     * @param begin         The start date for filtering storage records.
     * @param end           The end date for filtering storage records.
     * @return A list of {@link Storage} objects matching the specified criteria.
     */
    List<Storage> getStorageList(Integer storageID, String productName, String productType,
                                 String returnRequest, Integer currentStatus,
                                 LocalDateTime operateTime, String operatorName,
                                 LocalDate startDate, LocalDate endDate);

    /**
     * Inserts a new storage record into the database.
     *
     * @param storage The storage record to be inserted.
     */
    void insertStorageData(Storage storage);

    /**
     * Retrieves the ID of the last inserted storage record.
     *
     * @return The ID of the last inserted storage record.
     */
    Integer getLastInsertedStorageID();

    /**
     * Inserts product data associated with the storage record into the database.
     *
     * @param storage The storage record containing product data to be inserted.
     */
    void insertProductData(Storage storage);

    /**
     * Inserts return order data associated with the storage record into the database.
     *
     * @param storage The storage record containing return order data to be inserted.
     */
    void insertReturnOrderData(Storage storage);

    /**
     * Inserts operation data associated with the storage record into the database.
     *
     * @param storage The storage record containing operation data to be inserted.
     */
    void insertOperateData(Storage storage);

    /**
     * Retrieves a storage record based on the specified storage ID.
     *
     * @param storageID The ID of the storage record to retrieve.
     * @return The {@link Storage} object representing the storage record.
     */
    Storage getStorageDataByID(Integer storageID);

    /**
     * Updates a storage record based on the provided operation details.
     *
     * @param operate The operation details for updating the storage record.
     */
    void updateStorage(Operate operate);

    /**
     * Deletes storage records based on the provided list of storage IDs.
     *
     * @param storageIDList The list of storage IDs to be deleted.
     */
    void deleteStorageData(List<Integer> storageIDList);

    /**
     * Deletes product data associated with the provided list of product IDs.
     *
     * @param productIDList The list of product IDs whose data is to be deleted.
     */
    void deleteProductData(List<String> productIDList);

    /**
     * Deletes return order data associated with the provided list of return order numbers.
     *
     * @param returnOrderNumList The list of return order numbers whose data is to be deleted.
     */
    void deleteReturnOrderData(List<String> returnOrderNumList);

    /**
     * Deletes operation data associated with the provided list of storage IDs.
     *
     * @param storageIDList The list of storage IDs whose operation data is to be deleted.
     */
    void deleteOperateData(List<Integer> storageIDList);

}
