package com.warehouse.controller;

import com.warehouse.pojo.ChartData;
import com.warehouse.pojo.PieChartData;
import com.warehouse.service.StatisticsService;
import com.warehouse.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Manages statistical operations related to storage records in the application.
 * This controller class orchestrates the retrieval of statistical data for storage records,
 * including line and column charts, pie charts, and map data based on postcode.
 * It works in conjunction with the {@link StatisticsService} to provide statistical insights
 * into storage data for analysis and decision-making purposes.
 *
 * Endpoints provided by this controller allow clients to fetch statistical data
 * for visualization and analysis purposes.
 *
 * Key Endpoints:
 * - GET /statistics: Retrieves line and column chart data for the specified year.
 * - POST /statistics: Retrieves pie chart data.
 * - POST /statistics/map: Retrieves map data based on postcode for the specified year.
 *
 * Error Handling:
 * - The controller handles exceptions and error responses gracefully during data retrieval operations.
 * - Internal server errors are appropriately handled and logged for troubleshooting purposes.
 *
 * This controller assumes operation in a secure environment with proper security configurations in place,
 * including HTTPS for secure communication and access control measures to restrict unauthorized access.
 *
 * Usage of this controller should align with security best practices and data privacy regulations to ensure
 * the confidentiality and integrity of storage statistical data.
 *
 * @author Jianan Zhao
 * @see StatisticsService
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/statistics")
@Tag(name = "storage statistics", description = "Statistics of storage records.")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    /**
     * Retrieves a list of line and bar charts data based on the specified year.
     * Line chart shows the total number of returned products for each month in the selected year.
     * Bar chart shows the refund, repair and recycle number of returned products for each month in the selected year.
     *
     * @param year The year for which chart data is requested.
     * @return A {@link Result} object containing the chart data.
     */
    @GetMapping
    @Operation(summary = "Get line and column charts data",
            description = "Retrieves a list of chart data based on year.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result getChartData(@RequestParam(defaultValue = "2024") Integer year){
        log.info("Query statistics data,  for year {}", year);
        List<ChartData> chartDataList = statisticsService.getChartData(year);
        return Result.success(chartDataList);
    }

    /**
     * Retrieves a list of pie chart data to display percentage of each product type.
     *
     * @return A {@link Result} object containing the pie chart data.
     */
    @PostMapping
    @Operation(summary = "Get pie chart data",
            description = "Retrieves a list of pie chart data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "display successfully", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result getPieChartData(){
        log.info("Query PieChartData");
        List<PieChartData> pieChartData = statisticsService.getPieChartData();
        return Result.success(pieChartData);
    }


    /**
     * Displays places in the map by postcode based on the specified year.
     *
     * @param year The year for which postcode data is requested.
     * @return A {@link Result} object containing the list of postcodes.
     */
    @PostMapping("/map")
    @Operation(summary = "Get postcode",
            description = "display places in map by postcode based on year.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "display successfully", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result getPostcode(@RequestParam(defaultValue = "2024") Integer year){
        log.info("Query postcode,  for year {}", year);
        List<String> postcodeList = statisticsService.getPostcode(year);
        return Result.success(postcodeList);
    }
}