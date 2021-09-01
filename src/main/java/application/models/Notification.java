package application.models;

import lombok.Data;

@Data
public class Notification {
    private int id;
    private int typeId;
    private long sentTime;
    private int entityId;
    private String contact;
    private String name;
}
