package application.exceptions;

public enum Error {
    INVALID_REQUEST("invalid_request"),
    UNAUTHORIZED("unauthorized");

    private final String errorName;

    Error(String error) {
        this.errorName = error;
    }

    public String getErrorName() {
        return errorName;
    }
}
