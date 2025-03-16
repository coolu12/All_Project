

package com.warehouse.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.warehouse.pojo.User;
import com.warehouse.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for checking user permissions.
 *
 * This interceptor is responsible for checking the permissions of logged-in users.
 * It intercepts requests and performs the following actions:
 * 1. Retrieves the logged-in user information from the session.
 * 2. Checks if the user has the required permission to access certain URLs.
 * 3. If the user does not have the required permission, returns an error response indicating permission denied.
 * 4. If the user permission is null or the user is not logged in, returns an error response indicating not logged in.
 *
 * @author Lu Cheng
 */

@Component
@Slf4j
public class UserPermissionInterceptor implements HandlerInterceptor {
/**
     * Intercepts the incoming request before it is handled by the controller.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object.
     * @param handler  The handler for the request.
     * @return True if the request should proceed, false otherwise.
     * @throws Exception If an error occurs during processing.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // get log-in user information
        HttpSession session = request.getSession(false);
        if (session != null) {
            User u = (User) session.getAttribute("user");
            if (u != null && u.getUserPermission() != null) {
                String url = request.getRequestURL().toString();

                if (url.contains("User")) {
                    if (u.getUserPermission() != 0) {
                        Result error = Result.error("PERMISSION_DENIED");
                        response.getWriter().write(JSONObject.toJSONString(error));
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    }
                }
                return true;
            }
        }

        log.info("Permission is null,or log out");
        Result error = Result.error("NOT_LOGIN");
        String notLogin = JSONObject.toJSONString(error);
        response.getWriter().write(notLogin);
        return false;
    }

}

