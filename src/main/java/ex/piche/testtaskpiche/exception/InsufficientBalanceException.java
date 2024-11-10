package ex.piche.testtaskpiche.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InsufficientBalanceException extends ErrorResponseException {

  private static final HttpStatusCode RESPONSE_STATUS_CODE = HttpStatus.BAD_REQUEST;

  public InsufficientBalanceException(String message) {
    super(RESPONSE_STATUS_CODE, ProblemDetail.forStatusAndDetail(RESPONSE_STATUS_CODE, message), null);
  }
}
