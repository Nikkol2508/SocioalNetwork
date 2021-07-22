package application.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    private int id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("reg_date")
    private long regDate;

    @JsonProperty("birth_date")
    private long birthDate;

    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;

    @JsonProperty("messages_permission")
    private String messagesPermission;

    @JsonProperty("last_online_time")
    private long lastOnlineTime;

    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private String token;
}

