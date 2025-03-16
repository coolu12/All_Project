package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A POJO (Plain Old Java Object) class representing a storage unit.
 * It contains information such as the storage ID, associated product,
 * return order, list of operations, and the current operation.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
    private Integer storageID;
    private Product product;
    private ReturnOrder returnOrder;
    private List<Operate> operateList;
    private Operate currentOperate;

}
