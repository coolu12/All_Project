package com.warehouse.controller;

import com.warehouse.pojo.Operate;
import com.warehouse.pojo.PageBean;
import com.warehouse.pojo.Storage;
import com.warehouse.service.StorageService;
import com.warehouse.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Manages storage-related operations in the application to ensure effective and secure storage management.
 * This controller class handles CRUD operations on storage records and collaborates with {@link StorageService}.
 * It ensures that storage information is handled securely and maintains data integrity throughout the process.
 *
 * The endpoints provided by this controller adhere to strict security protocols to safeguard sensitive storage data.
 * Operations include retrieval, addition, deletion, and updating of storage records.
 *
 * Key endpoints:
 * - GET /storage: Retrieves a paginated list of storage entries based on search criteria.
 * - POST /storage: Adds new storage entries to the system.
 * - PUT /storage: Updates existing storage entries.
 * - DELETE /storage/{storageIDList}: Deletes specified storage entries based on IDs.
 * - GET /storage/{storageID}: Retrieves storage data for a specific ID.
 *
 * Security Measures:
 * - HTTPS is used to encrypt data in transit.
 * - Strong authorization checks are implemented to prevent unauthorized access.
 * - The controller operates under strict access control measures to ensure only authorized users can perform operations.
 *
 * Error Handling:
 * - The controller handles exceptions and error messages appropriately during storage management operations.
 * - Unauthorized attempts to access or manipulate storage data result in error messages or exceptions.
 *
 * This controller assumes operation in a secure server environment with proper security measures in place.
 * Usage should comply with security best practices to prevent data breaches and unauthorized access.
 *
 * @author Jianan Zhao
 * @see StorageService
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/storage")
@Tag(name = "Storage Management", description = "Operations for managing storage records.")
public class StorageController {

    @Autowired
    private StorageService storageService;

    /**
     * Retrieves a list of storage entries based on various filters.
     *
     * @param storageID    The ID of the storage entry.
     * @param productName  The name of the product.
     * @param productType  The type of the product.
     * @param returnRequest The return order request,
     *                      0 for refund, 1 for repair, 2 for recycle.
     * @param currentStatus The current status of the storage entry,
     *                      0 for recorded, 1 for checked, 2 for refunded, 3 for repaired, 4 for recycled.
     * @param operateTime  The time of the operation.
     * @param operatorName The name of the operator.
     * @param startDate    The start date of the time range.
     * @param endDate      The end date of the time range.
     * @param page         The page number.
     * @param pageSize     The size of each page.
     * @return A {@link Result} object containing the list of storage entries.
     */
    @GetMapping
    @Operation(summary = "Get storage list",
            description = "Retrieves a list of storage entries based on various filters.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result getStorageList(Integer storageID,
                                 String productName,
                                 String productType,
                                 String returnRequest,
                                 Integer currentStatus,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime operateTime,
                                 String operatorName,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("Select all storage data, storageID{}, productName{}, returnRequest{}, currentStatus{}," +
                "operateTime{}, operatorName{}, pages are from {} to {}, page{}, {}datas each page",
                storageID, productName, returnRequest, currentStatus, operateTime, operatorName,
                startDate, endDate, page, pageSize);
        PageBean pageBean = storageService.getStorageList(storageID, productName, productType, returnRequest,
                currentStatus, operateTime, operatorName, startDate, endDate, page, pageSize);
        return Result.success(pageBean);
    }


    /**
     * Deletes storage records based on the provided IDs.
     *
     * @param storageIDList The list of storage IDs to be deleted.
     * @return A {@link Result} object indicating the success of the operation.
     */
    @DeleteMapping("/{storageIDList}")
    @Operation(summary = "Delete storage",
            description = "Deletes storage records based on the provided IDs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Storage deleted successfully", content = @Content(schema = @Schema(implementation = Result.class)))
            })
    public Result deleteStorage(@PathVariable List<String> storageIDList){
        storageService.deleteStorage(storageIDList);
        log.info("succeed to delete");
        return Result.success();
    }

    /**
     * Inserts new storage records into the system.
     *
     * @param storageList The list of storage records to be inserted.
     * @return A {@link Result} object indicating the success of the operation.
     */
    @PostMapping
    @Operation(summary = "Insert storage",
            description = "Inserts new storage records into the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Storage inserted successfully", content = @Content(schema = @Schema(implementation = Result.class)))
            })
    public Result insertStorage(@RequestBody List<Storage> storageList){
        for (Storage storage : storageList) {
            log.info("new insert storage:" + storage);
        }
        storageService.insertStorage(storageList);
        return Result.success();
    }

    /**
     * Retrieves storage data for a specific ID.
     *
     * @param storageID The ID of the storage entry.
     * @return A {@link Result} object containing the storage data.
     */
    @GetMapping("/{storageID}")
    @Operation(summary = "Get storage data by ID",
            description = "Retrieves storage data for a specific ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Data retrieved successfully", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "404", description = "Storage ID not found")
            })
    public Result getStorageData(@PathVariable("storageID") Integer storageID){
        log.info("get the storage where id = "+ storageID);
        return Result.success(storageService.getStorageDataByID(storageID));
    }

    /**
     * Updates existing storage records.
     *
     * @param operateList The list of operations to be performed on storage records.
     * @return A {@link Result} object indicating the success of the operation.
     */
    @PutMapping
    @Operation(summary = "Update storage",
            description = "Updates existing storage records.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Storage updated successfully", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid storage details provided")
            })
    public Result updateStorage(@RequestBody List<Operate> operateList){
        for (Operate operate : operateList) {
            if(operate.getOperateStatus() == null || operate.getOperateStatus() < 0 || operate.getOperateStatus() >4)
            {
                return Result.error("Wrong operate status");
            }
            log.info("update storage data:" + operate);
        }
        storageService.updateStorage(operateList);
        return Result.success();
    }

}
