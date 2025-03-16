package com.warehouse.service;

import com.warehouse.pojo.Operate;
import com.warehouse.pojo.PageBean;
import com.warehouse.pojo.Storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing storage-related operations.
 * This service provides methods for CRUD operations on storage records and retrieval of storage data.
 * Implementations of this interface handle the business logic for storage management.
 * It interacts with the underlying data layer to perform database operations related to storage records.
 *
 * @author Jianan Zhao
 */
public interface StorageService {
    /**
     * Retrieves a paginated list of storage entries based on specified criteria.
     *
     * @param storageID     The ID of the storage entry.
     * @param productName   The name of the product associated with the storage entry.
     * @param productType   The type of product associated with the storage entry.
     * @param returnRequest The return order request of the storage entry,
     *                      0 for refund, 1 for repair, 2 for recycle.
     * @param currentStatus The current status of the storage entry,
     *                      0 for recorded, 1 for checked, 2 for refunded, 3 for repaired, 4 for recycled.
     * @param operateTime   The time of operation for the storage entry.
     * @param operatorName  The name of the operator associated with the storage entry.
     * @param begin         The start date for filtering storage entries.
     * @param end           The end date for filtering storage entries.
     * @param page          The page number for pagination.
     * @param pageSize      The size of each page for pagination.
     * @return A {@link PageBean} object containing the paginated list of storage entries.
     */
    PageBean getStorageList(Integer storageID, String productName, String productType, String returnRequest,
                            Integer currentStatus, LocalDateTime operateTime, String operatorName,
                            LocalDate begin, LocalDate end, Integer page, Integer pageSize);

    /**
     * Deletes the specified storage entries based on their IDs.
     *
     * @param storageIDList A list of storage entry IDs to be deleted.
     */
    void deleteStorage(List<String> storageIDList);

    /**
     * Inserts new storage entries into the system.
     *
     * @param storageList A list of {@link Storage} objects representing the storage entries to be inserted.
     */
    void insertStorage(List<Storage> storageList);

    /**
     * Updates existing storage entries.
     *
     * @param operateList A list of {@link Operate} objects containing the updates to be applied to storage entries.
     */
    void updateStorage(List<Operate> operateList);

    /**
     * Retrieves storage data for a specific ID.
     *
     * @param storageID The ID of the storage entry to retrieve.
     * @return A {@link Storage} object containing the storage data for the specified ID.
     */
    Storage getStorageDataByID(Integer storageID);
}
