package application.exceptions;

public class PasswordsNotEqualsException extends Exception {

    @Override
    public String getMessage() {
        return "Passwords are not equals";
    }
}
