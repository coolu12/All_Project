package com.warehouse.config;
import com.warehouse.interceptor.LoginCheckInterceptor;
import com.warehouse.interceptor.UserPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Configuration class for registering interceptors.
 *
 * This configuration class is responsible for registering interceptors to intercept incoming requests.
 * It adds the LoginCheckInterceptor to the interceptor registry and specifies the URL patterns to apply the interceptor to.
 *
 * @author Lu Cheng
 *//*


*/
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private LoginCheckInterceptor lCI;
    /*
*/
/**
     * Adds interceptors to the interceptor registry.
     *
     * @param registry The interceptor registry.
     *//*
*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(lCI).addPathPatterns(
                "/**").excludePathPatterns("/login","/login/forgetPassword","/verifycode");
    }
}

