package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A POJO (Plain Old Java Object) class represents data for line and column charts,
 * including various statistics for a specific month.
 * It includes the total number of items, the number of items refunded, repaired, and recycled.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartData {
    private Integer month;
    private Integer totalNum;
    private Integer refundedNum;
    private Integer repairedNum;
    private Integer recycledNum;
}
