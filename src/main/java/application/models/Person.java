package application.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
@Component
@Getter
@Setter
public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private long regDate;
    private long birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;

    private HashMap city;
    private HashMap country;

    private String messages_permission;
    private long lastOnlineTime;
    private boolean isBlocked;
    private String token;
}

