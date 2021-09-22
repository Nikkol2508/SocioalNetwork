package application.exceptions;

import com.github.dockerjava.api.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PasswordsNotEqualsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePasswordsNotEqualsException(PasswordsNotEqualsException exception) {

        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {

        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {

        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {

        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception) {

        return buildError(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {

        return buildError(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {

        ErrorResponse errorResponse = new ErrorResponse(Error.INVALID_REQUEST.toString(),
                "Validation error. Check 'errors' field for details");
        exception.getConstraintViolations().forEach(e ->
                errorResponse.addValidationError(e.getPropertyPath().toString(), e.getMessage()));
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(Error.INVALID_REQUEST.toString(),
                "Validation error. Check 'errors' field for details");
        exception.getBindingResult().getFieldErrors().forEach(e ->
                errorResponse.addValidationError(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildError(Exception exception, HttpStatus httpStatus) {

        String error = httpStatus.equals(HttpStatus.BAD_REQUEST)
                ? Error.INVALID_REQUEST.getErrorName()
                : Error.UNAUTHORIZED.getErrorName();
        ErrorResponse errorResponse = new ErrorResponse(error, exception.getMessage());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
