package ex.piche.testtaskpiche.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EntityNotFoundException extends ErrorResponseException {
  private static final HttpStatusCode RESPONSE_STATUS_CODE = HttpStatus.NOT_FOUND;

  public EntityNotFoundException(String message) {
    super(RESPONSE_STATUS_CODE, ProblemDetail.forStatusAndDetail(RESPONSE_STATUS_CODE, message), null);
  }
}
