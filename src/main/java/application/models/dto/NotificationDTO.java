package application.models.dto;

import lombok.Data;

@Data
public class NotificationDTO {

    private int id;
    private int type_id;

    private String notification_type;
  //  private int

    private long sent_time;
    private int entity_id;
    private String info;

}
