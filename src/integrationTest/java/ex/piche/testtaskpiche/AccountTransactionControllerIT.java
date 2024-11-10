package ex.piche.testtaskpiche;

import ex.piche.testtaskpiche.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AccountTransactionControllerIT extends BaseIT {

  private String accountsUrl;
  private String transactionsUrl;

  @BeforeEach
  public void setUp() {
    accountsUrl = "http://localhost:" + port + "/api/accounts";
    transactionsUrl = "http://localhost:" + port + "/api/accounts/transactions";
  }

  @Test
  void depositToAccountShouldReturnOkStatus() {
    Long accountId = createAccount(BigDecimal.valueOf(100.00));

    AccountDepositRequest depositRequest = new AccountDepositRequest(accountId, BigDecimal.valueOf(50.00));
    ResponseEntity<Void> response = restTemplate.postForEntity(
        transactionsUrl + "/deposit",
        depositRequest,
        Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void withdrawFromAccountShouldReturnCurrentBalance() {
    Long accountId = createAccount(BigDecimal.valueOf(100.00));

    AccountWithdrawRequest withdrawRequest = new AccountWithdrawRequest(accountId, BigDecimal.valueOf(50.00));
    ResponseEntity<CurrentBalanceResponse> response = restTemplate.postForEntity(
        transactionsUrl + "/withdraw", withdrawRequest, CurrentBalanceResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().currentBalance()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
  }

  @Test
  void internalTransferShouldReturnUpdatedBalance() {
    Long fromAccountId = createAccount(BigDecimal.valueOf(200.00));
    Long toAccountId = createAccount(BigDecimal.valueOf(100.00));

    InternalTransferRequest transferRequest = new InternalTransferRequest(
        fromAccountId,
        toAccountId,
        BigDecimal.valueOf(50.00)
    );
    ResponseEntity<CurrentBalanceResponse> response = restTemplate.postForEntity(
        transactionsUrl + "/internal", transferRequest, CurrentBalanceResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().currentBalance()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
  }

  private Long createAccount(BigDecimal initialBalance) {
    AccountCreationRequest request = new AccountCreationRequest(initialBalance);
    ResponseEntity<Long> response = restTemplate.postForEntity(accountsUrl, request, Long.class);
    return response.getBody();
  }
}
