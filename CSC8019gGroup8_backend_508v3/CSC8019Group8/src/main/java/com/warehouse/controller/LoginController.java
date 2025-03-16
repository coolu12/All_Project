package com.warehouse.controller;

import com.warehouse.utils.Result;
import com.warehouse.pojo.User;
import com.warehouse.service.UserService;
import com.warehouse.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages user authentication processes including login, password reset, and forgotten password recovery.
 * This controller handles sensitive user information and therefore, should ensure all data handling conforms
 * to the organization's security policies.
 *
 * The controller employs JWTs (JSON Web Tokens) to authenticate and authorize users after successful login attempts,
 * providing a mechanism for secure, efficient stateless authentication. It also utilizes session management
 * for additional security checks during operations like password reset and verification processes.
 *
 * Key endpoints include:
 * - POST /login: Authenticates user credentials and generates a JWT if successful.
 * - POST /resetPassword: POST /resetPassword: Allows privileged users (system administrators) to securely reset passwords without having to go through the password questions and answers.
 * - POST /forgetPassword: Assists users in recovering their passwords using security questions.
 *
 * Each endpoint is designed to handle specific user authentication and security tasks, ensuring compliance with
 * best security practices such as HTTPS usage, input validation, and error handling.
 *
 * Usage of this controller assumes that it runs within a secure server environment with HTTPS enabled
 * to protect data in transit and that all incoming data is validated against SQL injection and XSS attacks.
 *
 * a type of Result message may be thrown if unexpected conditions or security violations occur.
 *
 * @author Lu Cheng
 */
@Slf4j
@RestController
@RequestMapping("/login")
@Tag(name = "Login Management", description = "Handles user login and password operations.")
@CrossOrigin
public class LoginController {
    private static final long Expiration_Second = 30;//verify code has 30 seconds available time
    @Autowired
    private UserService userService;
    @Autowired
    HttpServletRequest request;

    /**
     * Authenticate the user based on the credentials and verification code provided in the body of the request.
     * Check. It ensures that the login attempt complies with security measures by validating the temporary CAPTCHA and checking the credentials against stored data.
     * Authentication code and checking credentials against stored data.
     *
     * The method first verifies that the session's stored captcha matches and is valid. If the authentication code is within the validity period and is valid
     * then a JWT (JSON Web Token) is generated for the user to facilitate secure session management.
     * Session management.
     *
     * A {@link User} object containing login credentials (such as a username and password) and a user-entered CAPTCHA.
     * The user-entered verification code. This object is reversed from the JSON body of the request.
     * The current HTTP session associated with the client. Used to store data about the authenticated user.
     * If the login is successful.
     * Returns a {@code Result} object representing the result of the login attempt. It returns a JWT
     * If the authentication was successful, it returns a JWT; if the login failed due to incorrect credentials, it returns an error message,
     * expired or the CAPTCHA is incorrect.
     * @apiNote This method is sensitive to CAPTCHA expiration and correctness.
     * CAPTCHA is intended to prevent automated attacks and must be regenerated periodically or after a failed login attempt.
     */
    @PostMapping
    public Result login(@RequestBody User user, HttpSession session) {
        String verifyCode = VerifyController.backendCode;
        try {
            if (verifyCode == null) {
                return Result.error("Login failed, please refresh verify code and try again.");
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(VerifyController.geneTime, endTime);
            long seconds = duration.getSeconds();
            //check if verify code is expired
            if(seconds>Expiration_Second){
                verifyCode=null;
                return Result.error("Login failed, verification code is expired.");
            }
            //check if verifycode is right
            if(!verifyCode.equals(user.getVerifyInput())){
                return Result.error("verifycode is incorrect.");
            }

            User authenticatedUser = userService.login(user); // try to log in
            if (authenticatedUser != null) {
                // generate JWT when log in successful
                session = request.getSession(true);
                session.setAttribute("user", authenticatedUser);
                Map<String, Object> claims = new HashMap<>();
                claims.put("userID", authenticatedUser.getUserID());
                claims.put("userName", authenticatedUser.getUserName());
                claims.put("userPermission", authenticatedUser.getUserPermission());
                String jwt = JwtUtils.generateJwt(claims);
                // generate JwT
                return Result.success(jwt);
            } else {
                verifyCode=null;
                return Result.error("Login failed, user ID or password is incorrect.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            VerifyController.backendCode = null;
        }
    }

    /**
     * Reset User password(Only for system manager)
     * Allows a user to reset their password without security question and answer.
     * This endpoint should be secure and usually only available to privileged users who have verified their identity in the real world.
     *
     * @param user The user object containing the updated password.
     *             Must be authenticated or have a valid session.
     * @return A {@code Result} object representing the outcome of the operation, success or failure.
     */
    @Operation(summary = "Reset password",
            description = "Resets the user's password and returns a success message.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content(schema = @Schema(implementation = Result.class)))
            })
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody User user) {
        userService.resetPassword(user);
        return Result.success("Password reset successfully");
    }

    /**
     * Processes password recovery requests from users who have forgotten their passwords.
     * It will validate the user's security question and answer. If successful, the password will be reset.
     *
     * A user object containing the security question and answer.
     * @param user The user object containing the updated password.
     * @return A {@code Result} object representing the result of the operation, either success or a detailed error message.
     */
    @PostMapping("/forgetPassword")
    public Result forgetPassword(@RequestBody User user) {
        if (userService.checkSecurity(user)) {
            userService.resetPassword(user);
            return Result.success("Password reset successfully");
        }
        return Result.error("something went wrong");
    }
}
