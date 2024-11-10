package ex.piche.testtaskpiche.service;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;

public interface AccountTransactionService {
  void depositToAccount(@Nonnull Long accountId, @Nonnull BigDecimal amount);

  BigDecimal withdrawFromAccount(@Nonnull Long account, @Nonnull BigDecimal amount);

  BigDecimal internalTransfer(@Nonnull Long fromAccountId, @Nonnull Long toAccountId, @Nonnull BigDecimal amount);
}
