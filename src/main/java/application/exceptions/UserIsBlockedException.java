package application.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserIsBlockedException extends Exception {

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
