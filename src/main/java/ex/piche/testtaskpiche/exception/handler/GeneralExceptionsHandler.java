package ex.piche.testtaskpiche.exception.handler;

import ex.piche.testtaskpiche.exception.EntityNotFoundException;
import ex.piche.testtaskpiche.exception.InsufficientBalanceException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionsHandler {
  public static final Logger log = LoggerFactory.getLogger(GeneralExceptionsHandler.class);

  @ExceptionHandler(value = EntityNotFoundException.class)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
    log.warn("Entity with requested id was not found", exception);
    return exception;
  }

  @ExceptionHandler(value = InsufficientBalanceException.class)
  public ErrorResponse handleInsufficientBalanceException(InsufficientBalanceException exception) {
    log.warn("Not enough funds for requested action", exception);
    return exception;
  }

  @ExceptionHandler(value = ValidationException.class)
  public ErrorResponse handleValidationException(ValidationException exception) {
    HttpStatusCode responseStatusCode = HttpStatus.BAD_REQUEST;
    return new ErrorResponseException(
        responseStatusCode,
        ProblemDetail.forStatusAndDetail(responseStatusCode, exception.getMessage()),
        null
    );
  }
}
