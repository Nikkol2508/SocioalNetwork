package application.exceptions;

public class PasswordNotValidException extends Exception {

  @Override
  public String getMessage() {
    return "Passwords are not valid.";
  }
}
