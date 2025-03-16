package com.warehouse.aop;

import com.alibaba.fastjson.JSONObject;
import com.warehouse.dao.OperateLogDao;
import com.warehouse.pojo.OperateLog;
import com.warehouse.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Aspect for logging method execution.
 * This aspect intercepts methods annotated with {@link com.warehouse.annotation.Log}
 * and records their execution logs.
 *
 * This aspect class captures method executions annotated with {@link com.warehouse.annotation.Log},
 * retrieves necessary information such as method parameters, return values, and execution time,
 * and persists them as operation logs using {@link OperateLogDao}.
 *
 * @author Jianan Zhao
 */
@Slf4j
@Aspect
@Component   // Aspect class
public class LogAspect {
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OperateLogDao operateLogDao;

    /**
     * Records the execution log for methods annotated with {@link com.warehouse.annotation.Log}.
     *
     * @param joinPoint The join point at which the advice is being applied.
     * @return The result of the method execution.
     */
    @Around("@annotation(com.warehouse.annotation.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) {
        Object result = null;
        OperateLog operateLog = null;
        try {
            // get operatorID from jwt token
            String jwt = httpServletRequest.getHeader("token");
            Claims claims = JwtUtils.parseJWT(jwt);
            //operator ID
            String operatorID = String.valueOf(claims.get("userID"));

            // Enable for bypassing interceptors during testing
            //String operatorID = "a00000001";

            //operate time
            LocalDateTime operateTime = LocalDateTime.now();
            //operate class name
            String  className = joinPoint.getTarget().getClass().getName();
            //operate method name
            String methodName = joinPoint.getSignature().getName();
            //operate method parameters
            Object[] args = joinPoint.getArgs();
            String methodParams = Arrays.toString(args);

            long begin = System.currentTimeMillis();
            //run
            result = joinPoint.proceed();
            long end = System.currentTimeMillis();

            //operate method return value
            String returnValue = JSONObject.toJSONString(result);

            //operate costs time
            Long costTime = end - begin;

            //record operate log
            operateLog = new OperateLog(null,operatorID,operateTime,className,
                    methodName,methodParams,returnValue,costTime);
            operateLogDao.insert(operateLog);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            log.info("AOP recorded operate log: {}",operateLog);
            return result;
        }
    }
}
