package com.warehouse.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.warehouse.context.BaseContext;
import com.warehouse.utils.Result;
import com.warehouse.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/*
 * Interceptor for checking login status.
 *
 * This interceptor is responsible for checking the login status of incoming requests.
 * It intercepts requests and performs the following actions:
 * 1. Checks if the request URL contains "login". If it does, allows the request to proceed.
 * 2. Retrieves the JWT token from the request header.
 * 3. If no token is found, returns an error response indicating the user is not logged in.
 * 4. Parses the JWT token. If an error occurs during parsing, returns an error response indicating the user is not logged in.
 * 5. If the JWT token is successfully parsed, allows the request to proceed.
 *
 * @author Lu Cheng
 */

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
/*
     * Intercepts the incoming request before it is handled by the controller.
     *
     * @param req     The HTTP request object.
     * @param resp    The HTTP response object.
     * @param handler The handler for the request.
     * @return True if the request should proceed, false otherwise.
     * @throws Exception If an error occurs during processing.
     */

    @Override//TURE--DO,FALSE--UNDO
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        //check login information
        //1.get request url。
        String url = req.getRequestURL().toString();
        log.info("request url: {}", url);
        //2.if contain login,next step
        if (url.contains("login")) {
            log.info("do log-in ");
            return true;
        }

        //3.get token from request head
        String jwt = req.getHeader("token");

        //4. do not have jwt,return false
        if (!StringUtils.hasLength(jwt)) {
            log.info("empty token, return log-in");
            Result error = Result.error("NOT_LOGIN");
            //transfer to fastjson
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        //5.analyze JWT,if error,return to log-in。
        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {//jwt error,throw exception
            e.printStackTrace();
            log.info("error, can not find log-in information");
            Result error = Result.error("NOT_LOGIN");
            //transfer type
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        //6.next-step
        log.info("true jwt, next step");

        // add userid to ThreadLocal
        Claims claims = JwtUtils.parseJWT(jwt);
        String userID = claims.get("userID", String.class);
        BaseContext.setCurrentId(userID);
        return true;
    }

}
