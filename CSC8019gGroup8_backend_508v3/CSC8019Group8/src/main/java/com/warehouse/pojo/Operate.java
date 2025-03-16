package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A POJO (Plain Old Java Object) class representing an operation for a special storage unit.
 * It contains information of the operation ID, status, time, storage ID,
 * and the user who performed the operation.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operate {
    private Integer operateID;
    private Integer operateStatus; // 0 for recorded, 1 for checked, 2 for refunded, 3 for repaired, 4 for recycled
    private LocalDateTime operateTime;
    private Integer storageID;
    private User operator;
}
