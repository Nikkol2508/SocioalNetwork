package application.models.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TagRequest {

    @NotBlank (message = "{Tag can't be blank}")
    private String tag;
}
