package ex.piche.testtaskpiche.repository;

import ex.piche.testtaskpiche.model.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
}
