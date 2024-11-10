package ex.piche.testtaskpiche;

import ex.piche.testtaskpiche.dto.AccountCreationRequest;
import ex.piche.testtaskpiche.dto.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AccountControllerIT extends BaseIT {

  private String baseUrl;

  @BeforeEach
  public void setUp() {
    baseUrl = "http://localhost:" + port + "/api/accounts";
  }

  @Test
  void createNewAccountShouldReturnCreatedStatusAndAccountId() {
    AccountCreationRequest request = new AccountCreationRequest(BigDecimal.valueOf(100.00));
    ResponseEntity<Long> response = restTemplate.postForEntity(baseUrl, request, Long.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void findAccountByIdShouldReturnAccountDetails() {
    AccountCreationRequest request = new AccountCreationRequest(BigDecimal.valueOf(50.00));
    Long accountId = restTemplate.postForEntity(baseUrl, request, Long.class).getBody();

    ResponseEntity<AccountResponse> response = restTemplate.getForEntity(
        baseUrl + "/" + accountId,
        AccountResponse.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(accountId);
    assertThat(response.getBody().balance()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
  }

  @Test
  void findAllAccountsWithValidPaginationShouldReturnPaginatedAccounts() {
    restTemplate.postForEntity(baseUrl, new AccountCreationRequest(BigDecimal.valueOf(10.00)), Long.class);
    restTemplate.postForEntity(baseUrl, new AccountCreationRequest(BigDecimal.valueOf(20.00)), Long.class);

    String paginatedUrl = baseUrl + "?page=0&size=2";
    ResponseEntity<AccountResponse[]> response = restTemplate.getForEntity(paginatedUrl, AccountResponse[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(Objects.requireNonNull(response.getBody())).hasSize(2);
  }

  @Test
  void findAllAccountsWithSizeGreaterThan50ShouldReturnBadRequest() {
    String invalidPaginatedUrl = baseUrl + "?page=0&size=51";
    ResponseEntity<List<AccountResponse>> response = restTemplate.exchange(
        invalidPaginatedUrl,
        HttpMethod.GET,
        HttpEntity.EMPTY,
        new ParameterizedTypeReference<>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
