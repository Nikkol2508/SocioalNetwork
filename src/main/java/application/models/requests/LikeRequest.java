package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeRequest {

    @JsonProperty("item_id")
    private Integer itemId;
    private String type;
}
