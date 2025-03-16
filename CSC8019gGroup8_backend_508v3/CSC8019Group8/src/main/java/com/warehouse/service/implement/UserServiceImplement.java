package com.warehouse.service.implement;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.warehouse.annotation.Log;
import com.warehouse.dao.UserDao;
import com.warehouse.pojo.PageBean;
import com.warehouse.pojo.User;
import com.warehouse.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements the UserService interface, providing a concrete class for handling business logic
 * associated with user operations. This class facilitates operations such as creating, reading,
 * updating, and deleting user information, as well as handling user login and security checks.
 *
 * The class uses Spring's {@link Service} annotation to declare it as a component suitable for handling
 * business logic and uses {@link Transactional} to manage transactions, ensuring data consistency and rollback capabilities.
 * It leverages the {@link UserDao} for data access operations, ensuring separation of concerns between data access and business logic.
 *
 * @author Lu Cheng
 */
@Slf4j
@Service
public class UserServiceImplement implements UserService {
    @Autowired
    private UserDao userDao;

    /**
     * Retrieves a paginated list of users from the database based on specified search criteria. This method is essential
     * for managing user data in applications where large sets of user data are common. It uses {@link PageHelper} to facilitate
     * pagination, enhancing performance by limiting the number of rows fetched from the database in a single query.
     *
     * @param page           The page number of the results to be displayed. This number is used by {@link PageHelper}
     *                       to calculate the starting row of the data subset to fetch from the database.
     * @param pageSize       The number of records to display per page. This determines the size of each data subset
     *                       returned in the pagination process.
     * @param userID         An optional filter by user ID to narrow down the search results. If null or empty,
     *                       no filtering by user ID is applied.
     * @param userName       An optional filter by user name to narrow down the search results. If null or empty,
     *                       no filtering by user name is applied.
     * @param userPermission An optional filter by user permission level to narrow down the search results. If null,
     *                       no filtering by user permission level is applied.
     * @return               A {@link PageBean} object containing the total number of records and the list of users
     *                       in the current page. The {@link PageBean} provides both the data and metadata about the
     *                       pagination state, such as total records and current page results.
     */
    @Override
    public PageBean getUserList(Integer page, Integer pageSize, String userID, String userName, Integer userPermission) {
        //Show information by page
        PageHelper.startPage(page, pageSize);
        //Find user
        List<User> userList = userDao.list(userID, userName, userPermission);
        Page<User> currentPage = (Page<User>) userList;
        PageBean pageBean = new PageBean(currentPage.getTotal(), currentPage.getResult());
        return pageBean;
    }

    /**
     * Removes a list of users from the database based on their unique identifiers. This method ensures that the deletion
     * The deletion process is transactional, which means that if any part of the deletion process fails (e.g., due to a database error), no changes will be committed to the database,
     * no changes are committed to the database and all partial changes are rolled back to maintain data integrity.
     *
     * This method is annotated with {@link Log} to automatically log entries, exits and exceptions for debugging and monitoring purposes.
     * * Debugging and monitoring. The {@link Transactional} annotation specifies that any exceptions will trigger a rollback.
     *
     * @param ids A list of user IDs specifying which users to delete. These IDs must be validated
     * or otherwise ensured to be correct to avoid accidental deletions.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(List<String> ids) {
        userDao.deleteUser(ids);
    }

    /**
     * Adds a new user to the system, provided that the user's permission level is not restricted.
     * This operation is transactional, meaning it will roll back if any part of the process fails.
     *
     * @param user The user object containing details of the user to be added. The user must have a
     *             permission level greater than 0 to be added to the system.
     *
     */
    //add user
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) {
        if (user.getUserPermission() != 0) {
            userDao.addUser(user);
        }
    }

    /**
     * Retrieves a user from the system based on the provided user ID. This method performs a database
     * lookup and returns the corresponding user object.
     *
     * @param userID The ID of the user to be retrieved.
     * @return The {@code User} object representing the retrieved user, or {@code null} if no user is found.
     */
    @Override
    public User getUserByID(String userID) {
        return userDao.getUserByID(userID);
    }

    /**
     * Updates a user's permission settings in the system, except for a specified protected user ID.
     * The operation is transactional and will roll back in case of any errors.
     *
     * @param user The user object containing the updated user details. Cannot update if the user ID
     *             is "A00000001" due to its protected status.
     * @throws IllegalArgumentException if the user ID is "A00000001", as this user's permissions cannot be updated.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void updateUserPermission(User user) {
        if (user.getUserID() != "A00000001") {
            userDao.updateUserPermission(user);
        }
    }

    /**
     * Authenticates a user by their login credentials. If the credentials are correct,
     * the user is logged into the system.
     *
     * @param user The user object containing login credentials.
     * @return The {@code User} object representing the logged-in user if authentication is successful,
     *         or {@code null} if login fails due to incorrect credentials.
     */
    @Override
    public User login(User user) {
        return userDao.getUserByIDAndPassword(user);
    }

    /**
     * Resets the password of a specified user, provided the user exists in the system.
     * This operation is also transactional and ensures changes are rolled back if any error occurs.
     *
     * @param user The user object containing the new password details.
     */
    @Override
    @Log
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(User user) {
        if (userDao.getUserByID(user.getUserID()) != null) {
            userDao.updatePassword(user);
        }
    }

    /**
     * Verifies a user's security question and answer. This is typically used for password recovery
     * processes to ensure that the user attempting the recovery is the legitimate account holder.
     *
     * @param user The user object containing the security question and answer.
     * @return {@code true} if the provided security question and answer match those stored in the database,
     *         otherwise {@code false}.
     */
    @Override
    public boolean checkSecurity(User user) {
        if (userDao.getUserByIDAndQueAndAns(user) != null) {
            return true;
        }
        return false;
    }
}

