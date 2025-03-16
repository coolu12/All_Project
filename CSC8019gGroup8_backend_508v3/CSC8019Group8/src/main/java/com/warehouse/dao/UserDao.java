package com.warehouse.dao;
import com.warehouse.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
/**
 * Interface for performing CRUD operations on user data.
 *
 * This interface defines methods for performing CRUD (Create, Read, Update, Delete) operations on user data.
 * It includes methods for retrieving users, deleting users, adding users, updating user information,
 * and handling user authentication and password-related operations.
 *
 * @author Lu Cheng
 */
@Mapper
public interface UserDao {
    //find user
    List<User> list(String userID, String userName, Integer userPermission);
    //delete user
    void deleteUser(List<String> ids);
    //add user
    @Insert("INSERT INTO user (user_id, user_name, password, user_permission, security_question, security_answer, email)" +
            "VALUES (#{userID}, #{userName}, #{password}, #{userPermission}, #{securityQuestion}, #{securityAnswer}, #{email})")
    void addUser(User user);
    //find user by id
    @Select("select * from user where user_id=#{userID}")
    User getUserByID(String userID);
    //change permission
    void updateUserPermission(User user);
    //find user by userid and user password
    @Select("select * from user where user_id=#{userID} AND password=#{password}")
    User getUserByIDAndPassword(User user);
    //change password
    void updatePassword(User user);
    //find user by id question and answer
    User getUserByIDAndQueAndAns(User user);

}