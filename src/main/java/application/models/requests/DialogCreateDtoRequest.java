package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogCreateDtoRequest {

    @JsonProperty("users_ids")
    private List<Integer> usersIds;
}
