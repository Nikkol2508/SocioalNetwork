package application.exceptions;

public class SetPasswordException extends Exception {

  @Override
  public String getMessage() {
    return "Email is not unique";
  }
}