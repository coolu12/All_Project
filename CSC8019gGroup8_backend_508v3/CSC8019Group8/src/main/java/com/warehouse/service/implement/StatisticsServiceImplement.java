package com.warehouse.service.implement;

import com.warehouse.dao.StatisticsDao;
import com.warehouse.pojo.ChartData;
import com.warehouse.pojo.PieChartData;
import com.warehouse.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link StatisticsService} interface, providing concrete methods for
 * retrieving statistical data related to warehouse operations.
 * This class facilitates the retrieval of chart data and pie chart data for visualization purposes,
 * as well as retrieving postcode data for displaying places in the map.
 *
 * The class is annotated with Spring's {@link Service} annotation, designating it as a component
 * suitable for handling business logic. It utilizes {@link Autowired} annotation for dependency
 * injection of {@link StatisticsDao}, which is responsible for data access operations.
 *
 * This class is part of the service layer in the application architecture, serving as an intermediary
 * between the controller layer and the data access layer. It encapsulates the business logic associated
 * with statistical operations, ensuring separation of concerns and maintainability of the codebase.
 *
 * @author Jianan Zhao
 */
@Slf4j
@Service
public class StatisticsServiceImplement implements StatisticsService {
    @Autowired
    private StatisticsDao statisticsDao;

    /**
     * Retrieves line and column charts data for visualization based on the specified year.
     *
     * @param year The year for which chart data is to be retrieved.
     * @return A list of {@link ChartData} objects containing the chart data.
     */
    @Override
    public List<ChartData> getChartData(Integer year) {
        List<ChartData> returnChartData = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            returnChartData.add(new ChartData(i,0,0,0,0));
        }



        List<ChartData> chartDataResult = statisticsDao.getChartData(year);
        for (ChartData chartDatum : chartDataResult) {
            for (ChartData returnChartDatum : returnChartData) {
                if(returnChartDatum.getMonth().equals(chartDatum.getMonth())){
                    returnChartData.set(returnChartData.indexOf(returnChartDatum), chartDatum);
                    break;
                }
            }
        }
        return returnChartData;
    }

    /**
     * Retrieves pie chart data for visualization.
     *
     * @return A list of {@link PieChartData} objects containing the pie chart data.
     */
    @Override
    public List<PieChartData> getPieChartData() {
        return statisticsDao.getPieChartData();
    }

    /**
     * Retrieves postcode data for displaying places in the map based on the specified year.
     *
     * @param year The year for which postcode data is to be retrieved.
     * @return A list of strings representing postcode data.
     */
    @Override
    public List<String> getPostcode(Integer year) {
        return statisticsDao.getPostcode(year);
    }


}
