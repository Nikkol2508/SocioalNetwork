package application.models;

import lombok.Data;

@Data
public class Person {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String confirmationCode;
    private boolean isApproved;
    private PermissionType messagesPermission;
    private long regDate;
    private long birthDate;
    private boolean isBlocked;
    private long lastOnlineTime;
}
