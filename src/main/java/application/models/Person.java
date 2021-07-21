package application.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private Date regDate;
    private Date birthDate;
    private String email;
    private String phone;
    private String password;
    private String photo;
    private String about;
    private String town;
    private String confirmationCode;
    private boolean isApproved;
    private Date lastOnlineTime;
    private boolean isBlocked;
}

