package com.warehouse.config;

import com.warehouse.interceptor.ShoppingManngeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
 * Configuration class to register custom interceptors for the application.
 * Specifically, this class configures interceptors related to shopping management
 * to ensure that only authorized users can access or modify shopping-related data.
 *
 * This class implements {@link WebMvcConfigurer}, which allows it to customize
 * the way Spring handles web requests.
 * @author Lu Cheng
*/

@Configuration
public class ShoppingManageConfig implements WebMvcConfigurer {

    @Autowired
    private ShoppingManngeInterceptor sMI;

/*
     * Adds the UserPermissionInterceptor to the interceptor registry.
     *
     * @param registry The interceptor registry.
*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sMI).addPathPatterns("/shopping/**").excludePathPatterns("/login", "/verifycode");
    }
}
