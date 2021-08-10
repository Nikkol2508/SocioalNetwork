package application.exceptions;

public class PersonNotFindException extends Exception{

    @Override
    public String getMessage() {
        return "Person not find";
    }
}
