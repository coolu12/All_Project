package com.warehouse.dao;

import com.warehouse.pojo.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Data access interface for logging operations performed within the application.
 * This interface defines a method for inserting log data into the database.
 *
 * The method in this interface allows insertion of log data including operator ID, operation time,
 * class name, method name, method parameters, return value, and execution time.
 *
 * This interface is typically implemented by classes that interact with the database or other
 * data sources to log application operations for monitoring and analysis purposes.
 *
 * This interface is annotated with {@link Mapper}, indicating it as a MyBatis mapper interface.
 * MyBatis will generate the implementation for this interface at runtime.
 *
 * @author Jianan Zhao
 */
@Mapper
public interface OperateLogDao {

    /**
     * Inserts log data into the database.
     *
     * @param log The {@link OperateLog} object containing the log data to be inserted.
     */
    @Insert("insert into operate_log (operator_id, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "values (#{operatorID}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime});")
    public void insert(OperateLog log);

}
