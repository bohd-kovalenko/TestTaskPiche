package ex.piche.testtaskpiche.model;

import ex.piche.testtaskpiche.model.enums.TransactionType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Immutable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal amount;

  @Column(name = "unique_identifier", nullable = false, unique = true)
  @UuidGenerator
  private UUID uniqueIdentifier;

  @Column(name = "transaction_date", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime transactionDate;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "transaction_type", nullable = false)
  private TransactionType transactionType;

  @Column(name = "internal", nullable = false)
  private Boolean internal;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "related_transaction_id")
  @Fetch(FetchMode.JOIN)
  @Setter
  private AccountTransaction relatedAccountTransaction;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_account_id", nullable = false)
  @Fetch(FetchMode.SELECT)
  private Account targetAccount;

  public static AccountTransaction debit(@Nonnull BigDecimal amount, @Nonnull Account from) {
    return AccountTransaction.builder()
        .amount(amount)
        .targetAccount(from)
        .internal(false)
        .transactionType(TransactionType.DEBIT)
        .build();
  }

  public static AccountTransaction credit(@Nonnull BigDecimal amount, @Nonnull Account to) {
    return AccountTransaction.builder()
        .amount(amount)
        .targetAccount(to)
        .internal(false)
        .transactionType(TransactionType.CREDIT)
        .build();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AccountTransaction that)) return false;

    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
