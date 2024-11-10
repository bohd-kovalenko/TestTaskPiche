package ex.piche.testtaskpiche.controller;

import ex.piche.testtaskpiche.dto.AccountDepositRequest;
import ex.piche.testtaskpiche.dto.AccountWithdrawRequest;
import ex.piche.testtaskpiche.dto.CurrentBalanceResponse;
import ex.piche.testtaskpiche.dto.InternalTransferRequest;
import ex.piche.testtaskpiche.service.AccountTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts/transactions")
@RequiredArgsConstructor
public class AccountTransactionController {

  private final AccountTransactionService accountTransactionService;

  @PostMapping("/deposit")
  public ResponseEntity<Void> depositToAccount(@Valid @RequestBody AccountDepositRequest request) {
    accountTransactionService.depositToAccount(request.accountId(), request.amount());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/withdraw")
  public ResponseEntity<CurrentBalanceResponse> withdrawFromAccount(@Valid @RequestBody AccountWithdrawRequest request) {
    BigDecimal currentAccountBalance = accountTransactionService.withdrawFromAccount(
        request.accountId(),
        request.amount()
    );
    return ResponseEntity.ok(new CurrentBalanceResponse(request.accountId(), currentAccountBalance));
  }

  @PostMapping("/internal")
  public ResponseEntity<CurrentBalanceResponse> internalTransfer(@Valid @RequestBody InternalTransferRequest request) {
    BigDecimal currentInitiatorAccountBalance = accountTransactionService.internalTransfer(
        request.fromAccountId(),
        request.toAccountId(),
        request.amount()
    );
    return ResponseEntity.ok(new CurrentBalanceResponse(request.fromAccountId(), currentInitiatorAccountBalance));
  }
}
