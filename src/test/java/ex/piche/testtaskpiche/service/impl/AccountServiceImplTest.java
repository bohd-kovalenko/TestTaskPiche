package ex.piche.testtaskpiche.service.impl;

import ex.piche.testtaskpiche.exception.EntityNotFoundException;
import ex.piche.testtaskpiche.model.Account;
import ex.piche.testtaskpiche.model.enums.TransactionType;
import ex.piche.testtaskpiche.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @Mock
  private AccountRepository repository;

  @InjectMocks
  private AccountServiceImpl accountService;

  @Test
  void testCreateNewAccountPositive() {
    BigDecimal testInitialValue = new BigDecimal(10000);
    Account testAccount = new Account(testInitialValue);
    Account savedAccount = new Account(testInitialValue);
    Long expectedId = 1L;
    savedAccount.setId(expectedId);
    when(repository.save(testAccount)).thenReturn(savedAccount);

    Long actualId = accountService.createNewAccount(testInitialValue);

    assertEquals(expectedId, actualId);
  }

  @Test
  void testCreateNewAccountNullInitialValue() {
    BigDecimal testInitialValue = null;
    Account testAccount = new Account(testInitialValue);
    Long expectedId = 1L;
    Account savedAccount = new Account(new BigDecimal(0));
    savedAccount.setId(expectedId);
    when(repository.save(testAccount)).thenReturn(savedAccount);

    Long actualId = accountService.createNewAccount(testInitialValue);

    assertEquals(expectedId, actualId);
  }

  @Test
  void testFindAccountByIdPositive() {
    Long testId = 1L;
    BigDecimal testBalance = new BigDecimal(2000);
    Account expectedAccount = new Account(testBalance);
    expectedAccount.setId(testId);
    when(repository.findById(testId)).thenReturn(Optional.of(expectedAccount));

    Account actualAccount = accountService.findAccountById(testId);

    assertAll(
        () -> assertEquals(expectedAccount.getId(), actualAccount.getId()),
        () -> assertEquals(expectedAccount.getBalance(), actualAccount.getBalance())
    );
    verify(repository).findById(testId);
  }

  @Test
  void testFindAccountByIdWithNonExistentId() {
    Long testId = 1L;
    when(repository.findById(testId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> accountService.findAccountById(testId));
  }

  @Test
  void testFindAccountPaginatedPositive() {
    int testPageNumber = 1, testPageSize = 20;
    Pageable testPageable = PageRequest.of(testPageNumber, testPageSize);
    Account firstTestAccount = new Account(BigDecimal.ONE);
    Account secondTestAccount = new Account(BigDecimal.TEN);
    firstTestAccount.setId(1L);
    secondTestAccount.setId(2L);
    List<Account> expectedAccounts = List.of(firstTestAccount, secondTestAccount);
    Page<Account> mockPage = new PageImpl<>(expectedAccounts);

    when(repository.findAllPaginated(testPageable)).thenReturn(mockPage);

    List<Account> actualAccounts = accountService.findAccountPaginated(testPageNumber, testPageSize);

    assertEquals(expectedAccounts, actualAccounts);
  }

  @Test
  void testFindAllByIdPositive() {
    List<Long> testIds = List.of(1L, 2L, 3L);
    List<Account> testAccounts = testIds.stream().map(id -> {
      Account testAccount = new Account(BigDecimal.ZERO);
      testAccount.setId(id);
      return testAccount;
    }).toList();

    when(repository.findAllById(testIds)).thenReturn(testAccounts);

    List<Account> result = accountService.findAllById(testIds);

    assertEquals(3, result.size());
    verify(repository, times(1)).findAllById(testIds);
  }

  @Test
  void testFindAllByIdSomeIdsAbsent() {
    List<Long> testIds = List.of(1L, 2L, 3L);
    Account firstTestAccount = new Account(BigDecimal.ZERO);
    Account secondTestAccount = new Account(BigDecimal.ZERO);
    firstTestAccount.setId(testIds.get(0));
    secondTestAccount.setId(testIds.get(1));
    List<Account> testAccounts = List.of(firstTestAccount, secondTestAccount);

    when(repository.findAllById(testIds)).thenReturn(testAccounts);

    assertThrows(EntityNotFoundException.class, () -> accountService.findAllById(testIds));

    verify(repository, times(1)).findAllById(testIds);
  }

  @Test
  void testChangeAccountBalanceDebitPositive() {
    Account testAccount = new Account(BigDecimal.valueOf(50));
    BigDecimal testAmount = new BigDecimal(25);

    when(repository.save(testAccount)).thenReturn(testAccount);

    BigDecimal updatedBalance = accountService.changeAccountBalance(testAccount, testAmount, TransactionType.DEBIT);

    assertEquals(BigDecimal.valueOf(25), updatedBalance);
    assertEquals(BigDecimal.valueOf(25), testAccount.getBalance());
    verify(repository, times(1)).save(testAccount);
  }

  @Test
  void testChangeAccountBalanceCreditPositive() {
    Account testAccount = new Account(BigDecimal.valueOf(100));
    BigDecimal testAmount = new BigDecimal(25);

    when(repository.save(testAccount)).thenReturn(testAccount);

    BigDecimal updatedBalance = accountService.changeAccountBalance(testAccount, testAmount, TransactionType.CREDIT);

    assertEquals(BigDecimal.valueOf(125), updatedBalance);
    assertEquals(BigDecimal.valueOf(125), testAccount.getBalance());
    verify(repository, times(1)).save(testAccount);
  }

}
