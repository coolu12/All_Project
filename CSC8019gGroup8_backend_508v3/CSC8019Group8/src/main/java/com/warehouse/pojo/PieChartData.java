package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A POJO (Plain Old Java Object) class representing data for the pie chart,
 * including various statistics for different product types.
 * It includes the product type, as well as the number of items refunded, repaired, and recycled.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieChartData {
    private String productType;
    private Integer refundedNum;
    private Integer repairedNum;
    private Integer recycledNum;
}
