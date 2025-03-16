package com.warehouse.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.warehouse.pojo.User;
import com.warehouse.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/*
 * Interceptor to enforce permission checks on shopping management operations.
 * This interceptor ensures that only users with the appropriate permission level
 * can access specific endpoints related to shopping management, such as deleting items
 * from the shopping page.
 *
 * This class is configured to intercept requests and perform security checks before
 * the request reaches the controller handling the shopping actions.
 * @author Lu Cheng
 */

@Component
public class ShoppingManngeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI().toString();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User u = (User) session.getAttribute("user");
            if (u != null && u.getUserPermission() != null) {
                if (url.contains("shopping/delete")) {
                    if (u.getUserPermission() != 0) {
                        Result error = Result.error("PERMISSION_DENIED");
                        response.getWriter().write(JSONObject.toJSONString(error));
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
