package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents an item in the shopping cart, linking a commodity to a specific user.
 * This POJO (Plain Old Java Object) class encapsulates the attributes of an item in the shopping cart,
 * including the unique identifier of the commodity, the name of the commodity, and the user ID of the owner.
 *
 * @author Zilong Li
 * @since 2024-05-08
 */
@Data // Lombok annotation to generate getters, setters, equals, hashcode, and toString methods automatically.
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor.
@AllArgsConstructor // Lombok annotation to generate an all-argument constructor.
public class ShoppingCart {

    /**
     * Unique identifier for the commodity, refers to the specific commodity in the cart.
     */
    private Integer commodityID;

    /**
     * Name of the commodity added to the shopping cart.
     */
    private String productName;

    /**
     * Identifier for the user who owns the shopping cart.
     */
    private String userID;
}
