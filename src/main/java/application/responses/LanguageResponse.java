package application.responses;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

public class LanguageResponse {

    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private ArrayList<HashMap> data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public ArrayList<HashMap> getData() {
        return data;
    }

    public void setData(ArrayList<HashMap> data) {
        this.data = data;
    }
}

