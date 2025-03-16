package com.warehouse.config;

import com.warehouse.interceptor.UserPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
 * Configuration class for registering the UserPermissionInterceptor.
 *
 * This configuration class is responsible for registering the UserPermissionInterceptor to intercept requests related to user management.
 * It adds the interceptor to the interceptor registry and specifies the URL patterns to apply the interceptor to.
 *
 * @author Lu Cheng
 */

@Configuration
public class UserPermissionInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private UserPermissionInterceptor uPI;
    /*
     * Adds the UserPermissionInterceptor to the interceptor registry.
     *
     * @param registry The interceptor registry.
     */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(uPI).addPathPatterns("/User/**").excludePathPatterns("/login");
    }

}
