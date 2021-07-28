package application.responses;

import lombok.Data;

import java.util.List;

@Data
public class GeneralListResponse<T> {

    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private List<T> data;

    public GeneralListResponse(List<T> data) {
        error = "";
        timestamp = System.currentTimeMillis();
        this.data = data;
    }
}

