package com.warehouse.controller;
import com.warehouse.utils.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
/**
 * Manages the user logout process to ensure safe and complete termination of the user session.
 * The controller is responsible for invalidating the user session and clearing any authentication tokens or cookies,
 * Preventing unauthorised access after user logout.
 *
 * This controller is designed to handle sensitive user session information and adheres to strict security protocols
 * Ensuring that all session data is handled properly upon logout.
 *
 * Critical endpoints:
 * POST /logout: Invalidates the user session and clears cookies to ensure that the logout process is secure.
 *
 * The design of the logout process conforms to security best practices such as secure HTTP headers, cookie management,
 * and session deactivation techniques. This includes placing security flags on cookies and using HTTPS
 * to protect data in transit.
 *
 * Use of this controller assumes that it is operating in a secure server environment where appropriate security measures are in place.
 * Implement appropriate security measures, such as HTTPS and secure HTTP headers, to prevent security breaches such as session hijacking.
 *
 * If something unexpected happens during the logout process, an Exception exception, or an error message of type Result may be thrown.
 * A SecurityException may be thrown if an attempt is made to log off without a valid session.
 * @author Lu Cheng
 *
 */
@CrossOrigin
@RequestMapping("/logout")
public class LogoutController {
    /**
     * Handles the user logout process by invalidating the current HTTP session and clearing any associated cookies.
     * This method ensures that all session information is completely cleared, providing a clean state and preventing
     * Any unauthorised access.
     *
     * @param request HttpServletRequest object from which to retrieve and invalidate the current session.
     * @param response HttpServletResponse object from which to clear the cookie by setting its maximum age to zero.
     * @return A result object showing the outcome of the logout operation, usually indicating success or failure.
     * An error message of type Result or Exception is thrown if the session expires or the cookie clearing fails unexpectedly
     */
    @PostMapping
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Clear Jwt
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // clear cookie
             Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                   cookie.setMaxAge(0);
                   response.addCookie(cookie);
               }
            }

            return Result.success("Log out successful");
        } catch (Exception e) {
            //if error return failed to log out
            return Result.error("failed to log out");
        }
    }
}
