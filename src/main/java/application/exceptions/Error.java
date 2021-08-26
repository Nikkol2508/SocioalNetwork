package application.exceptions;

public enum Error {
    INVALID_REQUEST("invalid_request"),
    UNAUTHORIZED("unauthorized");

    private final String error;

    Error(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
