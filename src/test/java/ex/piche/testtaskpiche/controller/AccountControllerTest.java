package ex.piche.testtaskpiche.controller;

import ex.piche.testtaskpiche.exception.EntityNotFoundException;
import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountService accountService;

  private Account testAccount;

  @BeforeEach
  void setUp() {
    Long testAccountId = 1L;
    testAccount = new Account(BigDecimal.valueOf(200));
    testAccount.setId(testAccountId);
  }

  @Test
  void createNewAccountPositive() throws Exception {
    when(accountService.createNewAccount(any())).thenReturn(testAccount.getId());

    mockMvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"initialBalance\": 1000.0}"))
        .andExpect(status().isCreated())
        .andExpect(content().string(String.valueOf(testAccount.getId())));
  }

  @Test
  void findAccountByIdPositive() throws Exception {
    when(accountService.findAccountById(1L)).thenReturn(testAccount);

    mockMvc.perform(get("/accounts/" + testAccount.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testAccount.getId()))
        .andExpect(jsonPath("$.accountBalance").value(testAccount.getBalance()));
  }

  @Test
  void findAccountByIdShouldReturnNotFoundForNonExistentAccount() throws Exception {
    when(accountService.findAccountById(anyLong())).thenThrow(new EntityNotFoundException("Entity not found"));

    mockMvc.perform(get("/accounts/99"))
        .andExpect(status().isNotFound());
  }

  @Test
  void findAllAccountsPositive() throws Exception {
    Account secondTestAccount = new Account(BigDecimal.ZERO);
    Long secondTestAccountId = 2L;
    secondTestAccount.setId(secondTestAccountId);
    List<Account> accounts = List.of(testAccount, secondTestAccount);
    when(accountService.findAccountPaginated(0, 20)).thenReturn(accounts);

    mockMvc.perform(get("/accounts")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(testAccount.getId()))
        .andExpect(jsonPath("$[0].accountBalance").value(testAccount.getBalance()))
        .andExpect(jsonPath("$[1].id").value(secondTestAccount.getId()))
        .andExpect(jsonPath("$[1].accountBalance").value(secondTestAccount.getBalance()));
  }

  @Test
  void findAllAccountsShouldReturnBadRequestForPageSizeAboveLimit() throws Exception {
    mockMvc.perform(get("/accounts")
            .param("page", "0")
            .param("size", "51"))
        .andExpect(status().isBadRequest());
  }
}
