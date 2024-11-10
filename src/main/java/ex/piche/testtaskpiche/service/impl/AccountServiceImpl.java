package ex.piche.testtaskpiche.service.impl;

import ex.piche.testtaskpiche.exception.EntityNotFoundException;
import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.model.enums.TransactionType;
import ex.piche.testtaskpiche.repository.AccountRepository;
import ex.piche.testtaskpiche.service.AccountService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  public static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

  private final AccountRepository repository;

  @Override
  public Long createNewAccount(@Nullable BigDecimal initialBalance) {
    Account createdAccount = new Account(initialBalance);
    createdAccount = repository.save(createdAccount);
    log.info("Created new account with initial balance: {}", initialBalance);
    return createdAccount.getId();
  }

  @Override
  public Account findAccountById(@Nonnull Long id) {
    Account account = repository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(String.format("No entity with id %d was found", id)));
    log.info("Found an account under id: {}", id);
    return account;
  }

  @Override
  public List<Account> findAccountPaginated(@Nonnull Integer pageId, @Nonnull Integer pageSize) {
    Page<Account> requestedPage = repository.findAllPaginated(PageRequest.of(pageId, pageSize));
    log.info("Found {} entries in paginated request", requestedPage.getTotalElements());
    return requestedPage.getContent();
  }

  @Override
  public List<Account> findAllById(@Nonnull List<Long> ids) {
    List<Account> accounts = repository.findAllById(ids);
    if (accounts.size() != ids.size()) {
      throw new EntityNotFoundException("One or more of transaction entities hasn't been found");
    }
    log.info("Found {} accounts by their id's", accounts.size());
    return accounts;
  }

  @Override
  public BigDecimal changeAccountBalance(@Nonnull Account account,
                                         @Nonnull BigDecimal amount,
                                         @Nonnull TransactionType changeType) {
    BigDecimal currentAccountBalance = account.getBalance();
    if (changeType == TransactionType.DEBIT) {
      currentAccountBalance = currentAccountBalance.subtract(amount);
    } else if (changeType == TransactionType.CREDIT) {
      currentAccountBalance = currentAccountBalance.add(amount);
    }
    account.setBalance(currentAccountBalance);
    repository.save(account);
    log.info("Successfully changed account balance");
    return currentAccountBalance;
  }

}
