package com.warehouse.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for logging.
 * Methods annotated with {@code @Log} will be intercepted by the logging aspect.
 *
 * This annotation is applied to methods that require logging of their execution.
 * When used, the logging aspect intercepts the annotated methods and records their execution details.
 *
 * @author Jianan Zhao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
}
