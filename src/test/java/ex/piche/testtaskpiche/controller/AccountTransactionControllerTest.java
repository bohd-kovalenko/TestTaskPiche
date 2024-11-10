package ex.piche.testtaskpiche.controller;

import ex.piche.testtaskpiche.exception.EntityNotFoundException;
import ex.piche.testtaskpiche.exception.InsufficientBalanceException;
import ex.piche.testtaskpiche.service.AccountTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountTransactionController.class)
class AccountTransactionControllerTest {

  private final Long testAccountId = 1L;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountTransactionService accountTransactionService;

  @BeforeEach
  void setUp() {
    doNothing().when(accountTransactionService).depositToAccount(anyLong(), any(BigDecimal.class));
    when(accountTransactionService.withdrawFromAccount(anyLong(), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(800));
    when(accountTransactionService.internalTransfer(anyLong(), anyLong(), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(800));
  }

  @Test
  void depositToAccountPositive() throws Exception {
    mockMvc.perform(post("/accounts/transactions/deposit")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\": 1, \"amount\": 500.0}"))
        .andExpect(status().isOk());
  }

  @Test
  void withdrawFromAccountPositive() throws Exception {
    mockMvc.perform(post("/accounts/transactions/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\": 1, \"amount\": 200.0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(testAccountId))
        .andExpect(jsonPath("$.currentBalance").value(800));
  }

  @Test
  void withdrawFromAccountShouldReturnBadRequestForNegativeAmount() throws Exception {
    mockMvc.perform(post("/accounts/transactions/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\": 1, \"amount\": -100.0}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void withdrawFromAccountShouldReturnBadRequestWithInsufficientBalance() throws Exception {
    BigDecimal testAmount = new BigDecimal(100);
    when(accountTransactionService.withdrawFromAccount(testAccountId, testAmount)).thenThrow(new InsufficientBalanceException("Not enough balance for debit operation"));


    mockMvc.perform(post("/accounts/transactions/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\": 1, \"amount\": 100}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void internalTransferPositive() throws Exception {
    mockMvc.perform(post("/accounts/transactions/internal")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"fromAccountId\": 1, \"toAccountId\": 2, \"amount\": 300.0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(testAccountId))
        .andExpect(jsonPath("$.currentBalance").value(800));
  }

  @Test
  void withdrawFromAccountShouldReturnNotFoundForNonExistentAccount() throws Exception {
    when(accountTransactionService.withdrawFromAccount(anyLong(), any(BigDecimal.class)))
        .thenThrow(new EntityNotFoundException("Entity not found"));

    mockMvc.perform(post("/accounts/transactions/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\": 99, \"amount\": 200.0}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void internalTransferShouldReturnBadRequestForNegativeAmount() throws Exception {
    mockMvc.perform(post("/accounts/transactions/internal")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"fromAccountId\": 1, \"toAccountId\": 2, \"amount\": -100.0}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void internalTransferShouldReturnBadRequestWithInsufficientBalance() throws Exception {
    Long testReceiverAccountId = 2L;
    BigDecimal testAmount = new BigDecimal(100);
    when(accountTransactionService.internalTransfer(testAccountId, testReceiverAccountId, testAmount)).thenThrow(new InsufficientBalanceException("Not enough balance for debit operation"));

    mockMvc.perform(post("/accounts/transactions/internal")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"fromAccountId\": 1, \"toAccountId\": 2, \"amount\": 100}"))
        .andExpect(status().isBadRequest());
  }
}
