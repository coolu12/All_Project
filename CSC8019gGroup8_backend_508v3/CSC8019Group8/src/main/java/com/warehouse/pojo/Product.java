package com.warehouse.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * A POJO (Plain Old Java Object) class representing a product.
 * It contains information of the product ID, name, type, and original price.
 *
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productID;
    private String productName;
    private String productType;
    private BigDecimal originalPrice;
}
