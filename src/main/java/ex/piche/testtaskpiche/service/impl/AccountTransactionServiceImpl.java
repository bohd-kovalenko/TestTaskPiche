package ex.piche.testtaskpiche.service.impl;

import ex.piche.testtaskpiche.exception.InsufficientBalanceException;
import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.model.AccountTransaction;
import ex.piche.testtaskpiche.repository.AccountTransactionRepository;
import ex.piche.testtaskpiche.service.AccountService;
import ex.piche.testtaskpiche.service.AccountTransactionService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Handles transactions related to accounts, including deposits, withdrawals, and internal transfers.
 * Methods that decrease an account's balance return the account's updated balance, while deposit methods
 * return nothing, as they are designed for external calls, reducing the risk of balance information leakage.
 */
@Service
@RequiredArgsConstructor
public class AccountTransactionServiceImpl implements AccountTransactionService {

  public static final Logger log = LoggerFactory.getLogger(AccountTransactionServiceImpl.class);

  private final AccountTransactionRepository repository;
  private final AccountService accountService;

  @Override
  @Transactional
  public void depositToAccount(@Nonnull Long accountId, @Nonnull BigDecimal amount) {
    Account targetAccount = accountService.findAccountById(accountId);
    AccountTransaction transaction = AccountTransaction.credit(amount, targetAccount);
    transaction = repository.save(transaction);
    processTransaction(transaction);
    log.info("Successfully handled deposit (credit) transaction {} for amount: {}", transaction.getUniqueIdentifier(), amount);
  }

  /**
   * @return Balance of account from with the withdrawal
   */
  @Override
  @Transactional
  public BigDecimal withdrawFromAccount(@Nonnull Long accountId, @Nonnull BigDecimal amount) {
    Account targetAccount = accountService.findAccountById(accountId);
    if (targetAccount.getBalance().compareTo(amount) < 0) {
      throw new InsufficientBalanceException("Not enough funds to do a withdrawal");
    }
    AccountTransaction transaction = AccountTransaction.debit(amount, targetAccount);
    transaction = repository.save(transaction);
    BigDecimal balanceAfterTransaction = processTransaction(transaction);
    log.info("Successfully handled withdrawal (debit) transaction {} for amount: {}", transaction.getUniqueIdentifier(), amount);
    return balanceAfterTransaction;
  }


  /**
   * @return Balance of the initiator after the transaction (account whose balance was decreased)
   */
  @Override
  @Transactional
  public BigDecimal internalTransfer(@Nonnull Long fromAccountId,
                                     @Nonnull Long toAccountId,
                                     @Nonnull BigDecimal amount) {
    Map<Long, Account> accountMap = retrieveAndValidateAccounts(fromAccountId, toAccountId, amount);

    Account initiator = accountMap.get(fromAccountId);
    Account receiver = accountMap.get(toAccountId);

    List<AccountTransaction> transactions = createInternalTransactions(amount, initiator, receiver);
    BigDecimal balanceAfterTransaction = processInternalTransactions(transactions);
    logInternalTransfer(transactions, amount);
    return balanceAfterTransaction;
  }

  private Map<Long, Account> retrieveAndValidateAccounts(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    Map<Long, Account> accountMap = accountService.findAllById(List.of(fromAccountId, toAccountId)).stream()
        .collect(Collectors.toMap(Account::getId, Function.identity()));
    Account initiator = accountMap.get(fromAccountId);
    if (initiator.getBalance().compareTo(amount) < 0) {
      throw new InsufficientBalanceException("Not enough funds to do a withdrawal");
    }
    return accountMap;
  }

  private List<AccountTransaction> createInternalTransactions(BigDecimal amount, Account initiator, Account receiver) {
    AccountTransaction debitTransaction = AccountTransaction.debit(amount, initiator);
    AccountTransaction creditTransaction = AccountTransaction.credit(amount, receiver);
    debitTransaction.setRelatedAccountTransaction(creditTransaction);
    creditTransaction.setRelatedAccountTransaction(debitTransaction);
    return repository.saveAll(List.of(debitTransaction, creditTransaction));
  }

  private BigDecimal processInternalTransactions(List<AccountTransaction> transactions) {
    BigDecimal balanceAfterDebit = processTransaction(transactions.get(0));
    processTransaction(transactions.get(1));
    return balanceAfterDebit;
  }

  private void logInternalTransfer(List<AccountTransaction> transactions, BigDecimal amount) {
    log.info(
        "Successfully handled internal transaction, consisted of debit {} and credit {} for amount: {}",
        transactions.get(0).getUniqueIdentifier(),
        transactions.get(1).getUniqueIdentifier(),
        amount
    );
  }

  private BigDecimal processTransaction(AccountTransaction transaction) {
    return accountService.changeAccountBalance(
        transaction.getTargetAccount(),
        transaction.getAmount(),
        transaction.getTransactionType()
    );
  }
}
