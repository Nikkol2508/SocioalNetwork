package application.exceptions;

import com.github.dockerjava.api.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PasswordsNotEqualsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePasswordsNotEqualsException(PasswordsNotEqualsException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.BAD_REQUEST);
        log.error("PasswordsNotEqualsException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.BAD_REQUEST);
        log.error("EmailAlreadyExistsException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.BAD_REQUEST);
        log.error("UsernameNotFoundException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.BAD_REQUEST);
        log.error("EntityNotFoundException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.BAD_REQUEST);
        log.error("BadCredentialsException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    @ExceptionHandler(PasswordNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePasswordNotValidException(PasswordNotValidException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.BAD_REQUEST);
        log.error("PasswordNotValidException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {
        ResponseEntity<ErrorResponse> error = buildError(exception, HttpStatus.UNAUTHORIZED);
        log.error("UnauthorizedException stackTrace = {}", exception.getStackTrace());
        return error;
    }

    private ResponseEntity<ErrorResponse> buildError(Exception exception, HttpStatus httpStatus) {
        String error = httpStatus.equals(HttpStatus.BAD_REQUEST)
                ? Error.INVALID_REQUEST.getError()
                : Error.UNAUTHORIZED.getError();
        ErrorResponse errorResponse = new ErrorResponse(error, exception.getMessage());
        ResponseEntity <ErrorResponse> responseEntity = ResponseEntity.status(httpStatus).body(errorResponse);
        log.debug("buildError(): responseEntity = {}", responseEntity);
        return responseEntity;
    }
}
