package application.responses;

import application.models.Person;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
@Component
public class PersonResponse {
    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private HashMap<String, Object> data;
    private HashMap<String, Object> country;
    private String messagesPermission;
    private Date lastOnlineTime;
    boolean isBlocked;
}
