package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A POJO (Plain Old Java Object) class representing an operation log.
 * It contains information such as the log ID, operator ID, operation time, class name, method name,
 * method parameters, return value, and the time taken for the operation.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperateLog {
    private Integer logID;
    private String operatorID;
    private LocalDateTime operateTime;
    private String className; // operate class name
    private String methodName; // operate method Name
    private String methodParams; // operate method parameters
    private String returnValue; // operate method return value
    private Long costTime; //operate costs time
}
