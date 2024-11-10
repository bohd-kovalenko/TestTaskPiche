package ex.piche.testtaskpiche.service.impl;

import ex.piche.testtaskpiche.exception.InsufficientBalanceException;
import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.model.AccountTransaction;
import ex.piche.testtaskpiche.model.enums.TransactionType;
import ex.piche.testtaskpiche.repository.AccountTransactionRepository;
import ex.piche.testtaskpiche.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountTransactionServiceImplTest {

  @Mock
  private AccountTransactionRepository repository;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountTransactionServiceImpl service;

  private Account testAccount;

  @BeforeEach
  void setUp() {
    Long testAccountId = 1L;
    testAccount = new Account(BigDecimal.valueOf(200));
    testAccount.setId(testAccountId);
  }

  @Test
  void testDepositToAccountPositive() {
    BigDecimal testDepositAmount = BigDecimal.valueOf(100);
    AccountTransaction testTransaction = AccountTransaction.credit(testDepositAmount, testAccount);

    when(accountService.findAccountById(testAccount.getId())).thenReturn(testAccount);
    when(repository.save(any(AccountTransaction.class))).thenReturn(testTransaction);
    when(accountService.changeAccountBalance(testAccount, testDepositAmount, TransactionType.CREDIT)).thenReturn(testDepositAmount);

    service.depositToAccount(testAccount.getId(), testDepositAmount);

    verify(repository).save(any(AccountTransaction.class));
    verify(accountService).changeAccountBalance(testAccount, testDepositAmount, TransactionType.CREDIT);
  }

  @Test
  void testWithdrawFromAccountPositive() {
    BigDecimal testAmount = BigDecimal.valueOf(100);
    AccountTransaction transaction = AccountTransaction.debit(testAmount, testAccount);

    when(accountService.findAccountById(testAccount.getId())).thenReturn(testAccount);
    when(repository.save(any(AccountTransaction.class))).thenReturn(transaction);
    when(accountService.changeAccountBalance(testAccount, testAmount, TransactionType.DEBIT))
        .thenReturn(BigDecimal.valueOf(100));

    BigDecimal remainingBalance = service.withdrawFromAccount(testAccount.getId(), testAmount);

    assertEquals(BigDecimal.valueOf(100), remainingBalance);
    verify(repository).save(any(AccountTransaction.class));
    verify(accountService).changeAccountBalance(testAccount, testAmount, TransactionType.DEBIT);
  }

  @Test
  void testWithdrawFromAccountWithInsufficientBalance() {
    BigDecimal testAmount = BigDecimal.valueOf(300);
    Long testAccountId = testAccount.getId();

    when(accountService.findAccountById(testAccount.getId())).thenReturn(testAccount);

    assertThrows(InsufficientBalanceException.class, () -> service.withdrawFromAccount(testAccountId, testAmount));
    verify(repository, never()).save(any(AccountTransaction.class));
  }

  @Test
  void testInternalTransferPositive() {
    Account testInitiator = testAccount;
    Account testReceiver = new Account(BigDecimal.ZERO);
    Long testReceiverId = 2L;
    BigDecimal testAmount = BigDecimal.valueOf(100);
    testReceiver.setId(testReceiverId);
    AccountTransaction debitTransaction = AccountTransaction.debit(testAmount, testInitiator);
    AccountTransaction creditTransaction = AccountTransaction.credit(testAmount, testReceiver);

    when(accountService.findAllById(List.of(testInitiator.getId(), testReceiver.getId())))
        .thenReturn(List.of(testInitiator, testReceiver));
    when(repository.saveAll(anyList())).thenReturn(List.of(debitTransaction, creditTransaction));
    when(accountService.changeAccountBalance(testInitiator, testAmount, TransactionType.DEBIT))
        .thenReturn(BigDecimal.valueOf(100));
    when(accountService.changeAccountBalance(testReceiver, testAmount, TransactionType.CREDIT))
        .thenReturn(BigDecimal.valueOf(150));

    BigDecimal balanceAfterTransaction = service.internalTransfer(testInitiator.getId(), testReceiverId, testAmount);

    assertEquals(BigDecimal.valueOf(100), balanceAfterTransaction);
    verify(repository).saveAll(anyList());
    verify(accountService).changeAccountBalance(testInitiator, testAmount, TransactionType.DEBIT);
    verify(accountService).changeAccountBalance(testReceiver, testAmount, TransactionType.CREDIT);
  }

  @Test
  void testInternalTransferWithInsufficientBalance() {
    BigDecimal testAmount = BigDecimal.valueOf(300);
    Account testInitiator = testAccount;
    Account testReceiver = new Account(BigDecimal.ZERO);
    Long testReceiverId = 2L;
    Long testInitiatorId = testAccount.getId();
    testReceiver.setId(testReceiverId);

    when(accountService.findAllById(List.of(testInitiator.getId(), testReceiver.getId())))
        .thenReturn(List.of(testInitiator, testReceiver));

    assertThrows(InsufficientBalanceException.class, () -> service.internalTransfer(testInitiatorId, testReceiverId, testAmount));
    verify(repository, never()).saveAll(anyList());
  }

}
