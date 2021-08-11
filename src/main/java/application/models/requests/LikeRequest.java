package application.models.requests;

import lombok.Data;

@Data
public class LikeRequest {

    private Integer item_id;
    private String type;
}
