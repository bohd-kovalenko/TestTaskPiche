package ex.piche.testtaskpiche.repository;

import ex.piche.testtaskpiche.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

  @Query("SELECT acc FROM Account acc")
  Page<Account> findAllPaginated(Pageable pageable);
}
