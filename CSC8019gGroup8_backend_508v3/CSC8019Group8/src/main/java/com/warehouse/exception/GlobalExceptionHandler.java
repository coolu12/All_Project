package com.warehouse.exception;

import com.warehouse.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling exceptions occurring within the application.
 * This class provides a centralized mechanism to handle exceptions thrown during request processing.
 *
 * The class is annotated with {@link RestControllerAdvice}, indicating that it advises all
 * {@code @RestController} classes in the application and intercepts exceptions thrown by controller
 * methods annotated with {@code @RequestMapping} or {@code @ExceptionHandler}.
 *
 * The {@code @ExceptionHandler} annotation is used to specify the types of exceptions that this handler
 * can handle. When an exception of the specified type occurs, the corresponding handler method is invoked
 * to handle the exception.
 *
 * This handler method typically prints the stack trace of the exception and returns a standardized
 * {@link Result} object with an error message to the client.
 *
 * This class is intended to provide a global exception handling mechanism for the application,
 * ensuring consistent error responses to clients.
 *
 * @author Jianan Zhao
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles exceptions occurring within the application.
     *
     * @param exception The exception to be handled.
     * @return A {@link Result} object containing an error message indicating the failure.
     */
    @ExceptionHandler(Exception.class) //指定能够处理的异常类型
    public Result ex(Exception exception){
        exception.printStackTrace(); //打印堆栈中的异常信息
        //捕获到异常之后，响应一个标准的Result
        return Result.error("对不起，操作失败，请联系管理员");
    }
}
