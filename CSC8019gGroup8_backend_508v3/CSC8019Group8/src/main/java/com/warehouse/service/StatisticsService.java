package com.warehouse.service;

import com.warehouse.pojo.ChartData;
import com.warehouse.pojo.PieChartData;

import java.util.List;

/**
 * Service interface for retrieving statistical data related to warehouse operations.
 * This service provides methods for retrieving chart data, pie chart data, and postcode data
 * for analytical and visualization purposes.
 * Implementations of this interface handle the retrieval of statistical data from the underlying
 * data layer and provide it to the calling components.
 *
 * The methods defined in this interface allow retrieval of chart data for a specific year,
 * pie chart data for overall visualization, and postcode data for displaying places in the map.
 *
 * @author Jianan Zhao
 */
public interface StatisticsService {
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
