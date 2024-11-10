package ex.piche.testtaskpiche.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AccountCreationRequest(@JsonProperty(value = "initialBalance") BigDecimal initialBalance) {
}
