package com.warehouse.service;

import com.warehouse.pojo.PageBean;
import com.warehouse.pojo.User;

import java.util.List;
/**
 * Interface for user services that defines operations for managing user data.
 * This service handles CRUD operations and authentication processes for users within the system.
 * @author Lu Cheng
 *
 */
public interface UserService {


    PageBean getUserList(Integer page, Integer pageSize, String userID, String userName, Integer userPermission);

    void deleteUser(List<String> ids);

    void addUser(User user);

    User getUserByID(String userID);

    void updateUserPermission(User user);

    User login(User user);


    void resetPassword(User user);

    boolean checkSecurity(User user);
}
