package com.warehouse.dao;

import com.warehouse.pojo.ChartData;
import com.warehouse.pojo.PieChartData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Data access interface for retrieving statistical data related to warehouse operations.
 * This interface defines methods for retrieving chart data, pie chart data, and postcode data
 * from the data source.
 *
 * The methods defined in this interface allow retrieval of chart data for a specific year,
 * pie chart data for overall visualization, and postcode data for displaying places in the map.
 *
 * This interface is implemented by xml files that interact with the database
 * data sources to fetch statistical data required by the application.
 *
 * This interface is annotated with {@link Mapper}, indicating it as a MyBatis mapper interface.
 * MyBatis will generate the implementation for this interface at runtime.
 *
 * @implNote This interface is implemented by a MyBatis mapper XML file located at
 * resources/com/warehouse/dao/StatisticsDao.xml. The SQL queries for the methods defined in
 * this interface are specified in the XML file.
 *
 * @author Jianan Zhao
 */
@Mapper
public interface StatisticsDao {
    /**
     * Retrieves line and column charts data for visualization based on the specified year.
     *
     * @param year The year for which chart data is to be retrieved.
     * @return A list of {@link ChartData} objects containing the chart data.
     */
    List<ChartData> getChartData(Integer year);

    /**
     * Retrieves pie chart data for visualization.
     *
     * @return A list of {@link PieChartData} objects containing the pie chart data.
     */
    List<PieChartData> getPieChartData();

    /**
     * Retrieves postcode data for displaying places in the map based on the specified year.
     *
     * @param year The year for which postcode data is to be retrieved.
     * @return A list of strings representing postcode data.
     */
    List<String> getPostcode(Integer year);
}
