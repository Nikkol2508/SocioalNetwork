package application.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@Component
public class LanguageResponse {

    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private ArrayList<HashMap> data;
}

