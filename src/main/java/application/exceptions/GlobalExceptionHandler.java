package application.exceptions;

import com.github.dockerjava.api.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.ParseException;
import java.util.Optional;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PasswordsNotEqualsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePasswordsNotEqualsException(
            PasswordsNotEqualsException exception, HttpServletRequest request) {
        log.error("PasswordsNotEqualsException stackTrace = {}", exception.getStackTrace());
        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException exception, HttpServletRequest request) {
        log.error("EmailAlreadyExistsException stackTrace = {}", exception.getStackTrace());

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException exception, HttpServletRequest request) {
        log.error("UsernameNotFoundException stackTrace = {}", exception.getStackTrace());

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException exception, HttpServletRequest request) {
        log.error("EntityNotFoundException stackTrace = {}", exception.getStackTrace());

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException exception, HttpServletRequest request) {
        log.error("BadCredentialsException stackTrace = {}", exception.getStackTrace());

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException exception, HttpServletRequest request) {
        log.error("UnauthorizedException stackTrace = {}", exception.getStackTrace());

        return buildError(exception, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleParseException(
            ParseException exception, HttpServletRequest request) {

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UserIsBlockedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUserIsBlockedException(
            UserIsBlockedException exception, HttpServletRequest request) {

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception, HttpServletRequest request) {

        return buildError(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception, HttpServletRequest request) {

        Optional<ConstraintViolation<?>> optionalConstraintViolation = exception.getConstraintViolations()
                .stream().findAny();
        if (optionalConstraintViolation.isPresent()) {
            String validationError = optionalConstraintViolation.get().getMessage();
            ErrorResponse errorResponse = new ErrorResponse(
                    Error.INVALID_REQUEST.getErrorName(), validationError, request.getPathInfo());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {

        FieldError fieldError = exception.getBindingResult().getFieldError();
        String validationError = fieldError != null ? fieldError.getDefaultMessage() : null;
        ErrorResponse errorResponse = new ErrorResponse(Error.INVALID_REQUEST.getErrorName(), validationError,
                ((ServletWebRequest)request).getRequest().getRequestURI());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildError(Exception exception,
                                                     HttpStatus httpStatus,
                                                     HttpServletRequest request) {

        String error = httpStatus.equals(HttpStatus.BAD_REQUEST)
                ? Error.INVALID_REQUEST.getErrorName()
                : Error.UNAUTHORIZED.getErrorName();
        ErrorResponse errorResponse = new ErrorResponse(error, exception.getMessage(), request.getPathInfo());
        log.debug("buildError(): errorResponse = {}", errorResponse);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
