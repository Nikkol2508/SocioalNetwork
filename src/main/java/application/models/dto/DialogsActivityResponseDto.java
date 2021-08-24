package application.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogsActivityResponseDto {

    private boolean online;

    @JsonProperty("last_activity")
    private long lastActivity;
}
