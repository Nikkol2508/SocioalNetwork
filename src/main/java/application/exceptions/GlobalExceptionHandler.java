package application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PasswordsNotEqualsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePasswordsNotEqual(PasswordsNotEqualsException exception) {
        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildError(Exception exception, HttpStatus httpStatus) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(PersonNotFindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePersonNotFindException(PersonNotFindException exception) {
        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

}
