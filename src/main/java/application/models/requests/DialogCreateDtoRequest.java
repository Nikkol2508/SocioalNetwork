package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class DialogCreateDtoRequest {

    @JsonProperty("users_ids")
    @Size(min = 1, message = "Dialog can be created with only 1 user")
    private List<Integer> usersIds;
}
