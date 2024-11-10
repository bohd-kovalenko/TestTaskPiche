package ex.piche.testtaskpiche.service;

import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.model.enums.TransactionType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

  Long createNewAccount(@Nullable BigDecimal initialBalance);

  Account findAccountById(@Nonnull Long id);

  List<Account> findAccountPaginated(@Nonnull Integer pageId, @Nonnull Integer pageSize);

  List<Account> findAllById(@Nonnull List<Long> ids);

  BigDecimal changeAccountBalance(@Nonnull Account account,
                                  @Nonnull BigDecimal amount,
                                  @Nonnull TransactionType changeType);
}
