package application.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class LikeResponseDto {

    private String likes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> users;
}
