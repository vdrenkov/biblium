package dev.vdrenkov.biblium.handler;

import dev.vdrenkov.biblium.exception.AuthorNotFoundException;
import dev.vdrenkov.biblium.exception.BookNotFoundException;
import dev.vdrenkov.biblium.exception.ClientNotFoundException;
import dev.vdrenkov.biblium.exception.OrderNotFoundException;
import dev.vdrenkov.biblium.exception.UserNotFoundException;
import dev.vdrenkov.biblium.exception.UserRoleNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String CAUGHT_EXCEPTION = "Caught exception: ";
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final StringBuilder errors = new StringBuilder();
        final BindingResult bindingResult = exception.getBindingResult();

        for (final FieldError error : bindingResult.getFieldErrors()) {
            errors.append(error.getDefaultMessage());
            errors.append("\n");
        }

        return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<String> handleUnexpectedTypeException(final UnexpectedTypeException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "Wrong data entered";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(
        final HttpMessageNotReadableException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "Incorrect data entered";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(
        final HttpRequestMethodNotSupportedException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "HTTP method not supported";
        return new ResponseEntity<>(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = exception.getMessage() != null ? exception.getMessage() : "Illegal argument provided";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "Invalid parameter provided";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(final DateTimeParseException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "Invalid date provided";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "Invalid URL provided";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(final ConstraintViolationException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>("Request validation failed", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<String> handleHandlerMethodValidationException(
        final HandlerMethodValidationException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>("Request validation failed", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<String> handleInternalAuthenticationServiceException(
        final InternalAuthenticationServiceException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(final BadCredentialsException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(
        final DataIntegrityViolationException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        final String message = "Violation of database rules!";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<String> handleAuthorNotFoundException(final AuthorNotFoundException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(final BookNotFoundException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFoundException(final ClientNotFoundException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFoundException(final OrderNotFoundException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(final UserNotFoundException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public ResponseEntity<String> handleUserRoleNotFoundException(final UserRoleNotFoundException exception) {
        log.warn(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(final NoResourceFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(final Exception exception) {
        log.error(CAUGHT_EXCEPTION, exception);
        return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
