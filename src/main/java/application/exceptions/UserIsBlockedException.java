package application.exceptions;

public class UserIsBlockedException extends Exception {

    @Override
    public String getMessage() {
        return "You can't send message (one of the participants of the dialog is blocked)";
    }
}
