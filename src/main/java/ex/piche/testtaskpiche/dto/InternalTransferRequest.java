package ex.piche.testtaskpiche.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ex.piche.testtaskpiche.exception.ValidationMessageConstants;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record InternalTransferRequest(@JsonProperty(value = "fromAccountId", required = true) Long fromAccountId,
                                      @JsonProperty(value = "toAccountId", required = true) Long toAccountId,
                                      @JsonProperty(value = "amount", required = true)
                                      @DecimalMin(value = "0", message = ValidationMessageConstants.MIN_DECIMAL_MESSAGE) BigDecimal amount) {
}
