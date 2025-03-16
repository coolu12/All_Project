package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A POJO (Plain Old Java Object) class representing a return order.
 * It includes information of the return order number, reason for return, return request status, and postcode.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrder {
    private String returnOrderNum;
    private String reasonOfReturn;
    private Integer returnRequest; // 0 for refund, 1 for repair, 2 for recycle
    private String postcode;
}
