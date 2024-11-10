package ex.piche.testtaskpiche.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ex.piche.testtaskpiche.exception.ValidationMessageConstants;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record AccountDepositRequest(@JsonProperty(value = "accountId", required = true) Long accountId,
                                    @JsonProperty(value = "amount", required = true)
                                    @DecimalMin(value = "0", message = ValidationMessageConstants.MIN_DECIMAL_MESSAGE) BigDecimal amount) {
}
