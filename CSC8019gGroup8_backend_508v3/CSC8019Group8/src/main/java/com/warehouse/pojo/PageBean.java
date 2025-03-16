package com.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A generic POJO (Plain Old Java Object) class representing a page of data.
 * It contains information such as the total number of records and the data for the current page.
 *
 * @param <T> the type of objects contained in the page
 * @author Jianan Zhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class  PageBean<T> {
    private Long total;   // Total records
    private List<T> rows;  // Data for the current page
}
