package application.exceptions;

public class EmailAlreadyExistsException extends Exception {

    @Override
    public String getMessage() {
        return "The user with this email is already registered";
    }
}
