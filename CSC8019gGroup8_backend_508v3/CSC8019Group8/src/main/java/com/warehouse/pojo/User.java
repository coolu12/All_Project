package com.warehouse.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * A POJO (Plain Old Java Object) representing a user in the system.
 *
 * This class encapsulates the properties of a user, including user ID, user name , password, user permission level,
 * security question, security answer, verification code, and email address.
 * It is used for storing and manipulating user data within the system.
 *
 * @author Lu Cheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userID;
    private String userName;
    private String password;
    private Integer userPermission;
    private String securityQuestion;
    private String securityAnswer;
    private String verifyInput;
    private String vCode;
    private String email;
}
