package ex.piche.testtaskpiche.controller;

import ex.piche.testtaskpiche.dto.AccountCreationRequest;
import ex.piche.testtaskpiche.dto.AccountResponse;
import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public ResponseEntity<Long> createNewAccount(@RequestBody AccountCreationRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(accountService.createNewAccount(request.initialBalance()));
  }

  @GetMapping("/{accountId}")
  public ResponseEntity<AccountResponse> findAccountById(@PathVariable("accountId") Long accountId) {
    Account foundAccount = accountService.findAccountById(accountId);
    return ResponseEntity.ok(new AccountResponse(foundAccount.getId(), foundAccount.getBalance()));
  }

  @GetMapping
  public ResponseEntity<List<AccountResponse>> findAllAccounts(@RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
                                                               @RequestParam(required = false, defaultValue = "20", name = "size") Integer size) {
    ResponseEntity<List<AccountResponse>> response;
    if (size > 50) {
      response = ResponseEntity.badRequest().build();
    } else {
      response = ResponseEntity.ok(accountService
          .findAccountPaginated(page, size)
          .stream()
          .map(account -> new AccountResponse(account.getId(), account.getBalance()))
          .toList());
    }
    return response;
  }
}
