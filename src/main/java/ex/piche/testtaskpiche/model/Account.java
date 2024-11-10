package ex.piche.testtaskpiche.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;
  @Column(name = "balance", precision = 15, scale = 2)
  private BigDecimal balance;
  @Column(name = "creation_date", nullable = false)
  @CreationTimestamp
  private LocalDateTime creationDate;

  public Account(BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Account account)) return false;

    return Objects.equals(id, account.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
