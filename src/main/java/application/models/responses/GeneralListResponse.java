package application.models.responses;

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

    public GeneralListResponse(List<T> data, int offset, int perPage) {
        error = "Error";
        timestamp = System.currentTimeMillis();
        this.data = data;
        total = data.size();
        this.perPage = perPage;
        this.offset = offset;
    }
}

