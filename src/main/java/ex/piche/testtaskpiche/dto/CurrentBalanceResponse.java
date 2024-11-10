package ex.piche.testtaskpiche.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CurrentBalanceResponse(@JsonProperty("accountId") Long accountId,
                                     @JsonProperty("currentBalance") BigDecimal currentBalance) {
}
