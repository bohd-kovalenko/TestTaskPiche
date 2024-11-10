package ex.piche.testtaskpiche.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AccountResponse(@JsonProperty("id") Long id,
                              @JsonProperty("accountBalance") BigDecimal balance) {
}
