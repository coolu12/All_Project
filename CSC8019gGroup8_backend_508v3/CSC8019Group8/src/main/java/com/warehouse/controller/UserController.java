package com.warehouse.controller;
import com.warehouse.utils.Result;
import com.warehouse.pojo.PageBean;
import com.warehouse.pojo.User;
import com.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Manages user-related operations in the application to ensure effective and secure user management.
 * The controller is responsible for handling CRUD operations on user data, working in conjunction with {@link UserService}.
 * This ensures that sensitive user information is handled correctly and maintains the integrity and security of the overall user management task.
 *
 * The controller is designed to handle requests related to user management and adhere to strict security protocols,
 * * Ensure that all user data is handled properly when creating, updating, and deleting users.
 *
 * Key endpoints:
 * GET /User: retrieves a paginated list of users based on search criteria.
 * POST /User: adds a new user to the system.
 * PUT /User: update user information.
 * - DELETE /User/{ids}: deletes the specified user based on ID.
 * - GET /User/{userID}: Retrieve the specified user based on ID.
 * - GET /User/{userID}
 The user management process is designed to comply with security best practices such as secure data handling, authentication * and error management,
 * and error management. This includes using HTTPS to protect data in transit and implementing strong authorisation
 * checks to prevent unauthorised access.
 * All of these methods in the class are only prepared for system manager.
 *
 * Use of this controller assumes that it operates in a secure server environment with appropriate security measures,
 * such as HTTPS and secure HTTP headers. This is to prevent security breaches such as data leakage or unauthorised access.
 *
 * If something unexpected happens during user administration, an exception or result type error message may be thrown.
 * If an unauthorised attempt is made to access or manipulate user data, an Exception exception or Result type error message may be thrown.
 * @author Lu Cheng
 * @see UserService
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/User")
@Tag(name = "User Management", description = "Operations related to user management in the system.")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * Retrieves a paged list of users based on specified search criteria. This method reduces server resource load and improves response time by returning a subset of users in a paged format.
     * This method reduces server resource load and improves response time by returning a subset of users in a paged format.
     *
     * This method performs dynamic filtering based on the supplied user ID, name, and permission level, thereby enabling flexible data queries.
     * It queries the database using the {@link UserService#getUserList} method and encapsulates the results in a {@link PageBean} object.
     *
     * @param page The page number to retrieve. Used for paging, which helps to get a specific subset of data.
     * If not specified, the default value is 1 for the first page.
     * param pageSize The number of records per page. This parameter determines the maximum number of users displayed on a single page.
     * The default value is 5, which means that each page contains up to 5 users (if any).
     * @param userID Optional filter parameter to retrieve users by specific ID. If empty, users will not be filtered by ID.
     * @param userName Optional filter parameter to retrieve users by user name. If null, users are not filtered by name.
     * @param userPermission Optional filter parameter to retrieve users by permission level. If empty, users are not filtered by permission level.
     * Returns a {@code Result} object containing a list of paged users. The result includes the data encapsulated in the {@link PageBean} object,
     * The object provides additional details such as the total number of pages and the total number of records.
     * If an unexpected error occurs during the execution of the query or processing of the data, an error message of type Result or an Exception exception is thrown.
     */
    @GetMapping
    @Operation(summary = "Get user list",
            description = "Retrieves a paginated list of users based on search criteria.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users", content = @Content(schema = @Schema(implementation = PageBean.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result getUserList(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "8") Integer pageSize,
                              String userID,
                              String userName,
                              Integer userPermission) {//System Manager can find user by ID,Name,Permission
        PageBean pageBean = userService.getUserList(page, pageSize, userID, userName, userPermission);
        return Result.success(pageBean);
    }
    /**
     * Removes a list of users identified by a unique ID from the system. This method supports batch operations,
     * Allows multiple users to be deleted in a single request, thus increasing the efficiency of user management tasks.
     /* * The deletion process checks the list of users against non-deletable IDs.
     The deletion process checks each user ID against a list of undeletable IDs to ensure that certain critical users * are retained, such as super administrator users, which can be used to remove users from the system.
     * retained, such as super administrator users, to maintain system integrity and security.
     * * @param ids
     * @param ids List of user IDs to delete. These IDs are resolved from the URL path.
     * Users corresponding to these IDs will be deleted unless they are protected.
     * @return A {@code Result} object representing the result of the operation. If all users were successfully deleted,
     * then a success message is returned. If any users could not be deleted because they were protected, an error message is returned.
     * If the provided ID is not in the correct format or is null, an error of type Result will be thrown.
     * @throws RuntimeException If an unexpected error occurs during a database operation, such as a connection failure or a
     * @apiNote This method will not delete users with ID "A00000001" as these users are considered critical to the operation of the system.
     * Attempts to delete such users will result in an error response.
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "Delete users",
            description = "Deletes a list of users by their IDs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result deleteUser(@PathVariable List<String> ids) {
       for (String id : ids) {
           if(id=="A00000001"){
               return Result.error("can not delete user");
           }
       }
        userService.deleteUser(ids);
        return Result.success("Users deleted successfully");
    }
    /**
     * :: Adds a new user to the system based on the specified details provided in the body of the request. This method checks before adding a user
     * Checks the validity of a user's privilege level before adding a user to ensure that users with certain restricted privilege levels are not inadvertently added, thus maintaining the integrity and security of the system.
     * Not inadvertently added, thus maintaining the integrity and security of the system.
     *
     * This method processes the user information provided in JSON format, converts it to a {@link User} object, and then attempts to add the user to the system.
     * * The method then attempts to add the user to the system database via the {@link UserService}.
     *
     * A user object containing all the necessary information about the user to be added. This object is controlled by the
     * The user object. It should contain all the necessary user attributes such as name, email, * permission level,
     * and permission level.
     * @return A {@code Result} object encapsulating the result of the operation. If the addition was successful
     * then a success message is returned. If the user could not be added due to an invalid permission level or other validation error, an error message is returned.
     * Then an error message is returned.
     * This exception is thrown and handled internally if the user details provided are incomplete or do not pass validation checks.
     * Will be thrown and handled internally, resulting in a 400 response code.

     * This method checks the user privilege level; a user with a privilege level of "0" is considered a restricted user and cannot be added.
     * Users with permission level "0" are considered restricted and cannot be added through this endpoint. Attempting to add such a user
     * will result in an error.
     */
    @PostMapping
    @Operation(summary = "Add a new user",
            description = "Adds a new user to the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid user details provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result addUser(@RequestBody User user) {
        /*User Permission:
        * 0---System Manager: Default ID：a00000001
        * 1---Warehouse Manager
        * 2---Senior Staff
        * 3---Junior Staff
        * Each user will have default password:123456,which function was set in database;
        * */

        if(user.getUserPermission()==0){
            return Result.error("can not add user to system");
        }else {
            userService.addUser(user);
            return Result.success("User added successfully");
        }
    }
    /**
     * Retrieves the user from the system based on the unique user ID provided. This method is essential for accessing
     * This method is essential for accessing the details of a specific user and can be used to view user profiles or perform other operations that require user authentication.
     * User ID is required.
     *
     * This method queries the database via the {@link UserService#getUserByID} method in an attempt to find a user matching the given user ID.
     * * The user matching the given user ID. If found, the user details are returned; if not, the corresponding error message is returned.
     * Returns the corresponding error message.
     *
     *param userID The unique identifier of the user to retrieve. This should be a valid ID that corresponds to the user * in the database.
     * The user in the database. id is extracted directly from the URL path.
     * If the user is found, a {@code Result} object encapsulating the user's data is returned. The result object will contain
     * The user information will be included in the data field in case of success, or an error message in case the user is not found or an error occurs.
     * Any other issues not specifically handled by the user check.
     * This method relies on userID being a unique identifier for the user on the system. A non-existent or
     * incorrectly formatted userID will result in a "User not found" message.
     */
    @GetMapping("/{userID}")
    @Operation(summary = "Find user by ID",
            description = "Retrieves a user by their user ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result getUserByID(@PathVariable String userID) {
        User user = userService.getUserByID(userID);
        return Result.success(user);
    }
    /**
     * :: Updates the permission levels of existing users in the system. This method checks that the new permission level before applying the update
     * is valid and different from the prohibited permission level (e.g., level 0, which may be restricted) before applying the update.
     *
     * This operation modifies user data based on the input provided in the request body. If a user with the specified
     * details exist and the permission level is valid, the user information in the database is updated.
     * *
     * Parameters user {@link User} An object containing updated information about the user, including the new permission level.
     * The object is deserialised from the requested JSON body and must contain a valid user ID and a * user name.
     * other information needed to find and update the user in the database.
     * @return A {@code Result} object encapsulating the result of the operation. If
     * A success message is returned if the update was successful; an error message is returned if the update could not be performed because the input was invalid or the user does not exist in the database.

     * Users with a privilege level of "0" are considered restricted users and cannot update permissions through this endpoint.
     * Attempting to update such a user will result in an error.
     */
    @PutMapping
    @Operation(summary = "Update user",
            description = "Updates a user's information in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid user details provided"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public Result updateUserPermission(@RequestBody User user) {//only can change permission
        if(user.getUserPermission()!=0){
            userService.updateUserPermission(user);
            return Result.success("User updated successfully");
        }
        return Result.error("can not update user");
    }
}
