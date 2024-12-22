package pl.bsf.lukasz.koc.currencyaccounts.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeResultDTO;
import pl.bsf.lukasz.koc.currencyaccounts.exception.AccountNotFoundException;
import pl.bsf.lukasz.koc.currencyaccounts.exception.InsufficientFundsException;
import pl.bsf.lukasz.koc.currencyaccounts.mapper.CurrencyAccountMapper;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;
import pl.bsf.lukasz.koc.currencyaccounts.repository.CurrencyAccountRepository;
import pl.bsf.lukasz.koc.currencyaccounts.util.CurrencyAccountTestFactory;

@SpringBootTest
class CurrencyAccountServiceTest {

	@Mock
	private CurrencyAccountRepository repository;

	@Mock
	private CurrencyAccountMapper mapper;

	@Mock
	private ExchangeService exchangeService;

	@InjectMocks
	private CurrencyAccountService service;

	@Test
	void testCreateAccount() {
		// Arrange
		CreateCurrencyAccountDTO createCurrencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CREATE_CURRENCY_ACCOUNT_DTO;
		CurrencyAccount currencyAccount = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		CurrencyAccountDTO currencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT_DTO;

		when(mapper.toEntity(createCurrencyAccountDTO)).thenReturn(currencyAccount);
		when(repository.save(currencyAccount)).thenReturn(currencyAccount);
		when(mapper.toDTO(currencyAccount)).thenReturn(currencyAccountDTO);

		// Act
		CurrencyAccountDTO result = service.createAccount(createCurrencyAccountDTO);

		// Assert
		assertNotNull(result);
		assertEquals(currencyAccountDTO.getFirstName(), result.getFirstName());
		assertEquals(currencyAccountDTO.getLastName(), result.getLastName());
		assertEquals(currencyAccountDTO.getBalance(), result.getBalance());
		assertEquals(currencyAccountDTO.getCurrency(), result.getCurrency());
	}

	@Test
	void testGetAccountDetails_AccountFound() {
		// Arrange
		Long accountId = CurrencyAccountTestFactory.ACCOUNT_ID;
		CurrencyAccount currencyAccount = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		CurrencyAccountDTO currencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT_DTO;

		when(repository.findById(accountId)).thenReturn(Optional.of(currencyAccount));
		when(mapper.toDTO(currencyAccount)).thenReturn(currencyAccountDTO);
		when(exchangeService.exchangeCurrency(any(), any(), any()))
				.thenReturn(ExchangeResultDTO.builder().amount(new BigDecimal("1.00")).build());

		// Act
		CurrencyAccountDTO result = service.getAccountDetails(accountId);

		// Assert
		assertNotNull(result);
		assertEquals(currencyAccountDTO.getFirstName(), result.getFirstName());
		assertEquals(currencyAccountDTO.getLastName(), result.getLastName());
		assertEquals(currencyAccountDTO.getBalance(), result.getBalance());
		assertEquals(currencyAccountDTO.getCurrency(), result.getCurrency());
		assertEquals(new BigDecimal("1.00"), result.getBalanceExchanged());
		verify(exchangeService, times(1))
				.exchangeCurrency(eq(currencyAccount.getBalance()), eq(Currency.PLN), eq(Currency.USD));
	}

	@Test
	public void testGetAccountByIdNotFound() {
		// Arrange
		when(repository.findById(CurrencyAccountTestFactory.ACCOUNT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> service.getAccountDetails(CurrencyAccountTestFactory.ACCOUNT_ID));
		assertEquals("Account not found with ID: " + CurrencyAccountTestFactory.ACCOUNT_ID, exception.getMessage());
		verify(repository, times(1)).findById(CurrencyAccountTestFactory.ACCOUNT_ID);
	}

	@Test
	void testWithdrawInCurrency_Success() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToWithdraw = new BigDecimal("100.00");
		Currency orderedCurrency = Currency.USD;
		CurrencyAccount account = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		BigDecimal resultAmount = new BigDecimal("400.00");
		account.setBalance(resultAmount);

		// Mocks
		when(repository.findById(accountId)).thenReturn(Optional.of(account));
		when(exchangeService.exchangeCurrency(any(), any(), any()))
				.thenReturn(ExchangeResultDTO.builder()
						.amount(resultAmount)
						.currency(Currency.PLN)
						.build());

		// Act
		ExchangeResultDTO result = service.withdrawInCurrency(accountId, amountToWithdraw, orderedCurrency);

		// Assert
		assertNotNull(result);
		assertEquals(Currency.PLN, result.getCurrency());
		assertEquals(resultAmount, result.getAmount());
		assertEquals(new BigDecimal("0.00"), account.getBalance());
		verify(repository, times(1)).findById(accountId);
		verify(exchangeService, times(1))
				.exchangeCurrency(eq(amountToWithdraw), eq(orderedCurrency), eq(account.getCurrency()));
	}

	@Test
	void testWithdrawInCurrency_InsufficientFunds() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToExchange = new BigDecimal("1000.00");
		Currency orderedCurrency = Currency.USD;
		CurrencyAccount account = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		account.setBalance(new BigDecimal("500.00"));

		// Mocks
		when(repository.findById(accountId)).thenReturn(Optional.of(account));
		when(exchangeService.exchangeCurrency(any(), any(), any()))
				.thenReturn(ExchangeResultDTO.builder().amount(new BigDecimal("4000.00")).build());

		// Act & Assert
		InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
				() -> service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency));
		assertEquals("Insufficient funds to perform the operation", exception.getMessage());
		verify(repository, times(1)).findById(accountId);

		verify(exchangeService, times(1))
				.exchangeCurrency(eq(amountToExchange), eq(orderedCurrency), eq(account.getCurrency()));
	}

	@Test
	void testWithdrawInCurrency_AccountNotFound() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToExchange = new BigDecimal("100.00");
		Currency orderedCurrency = Currency.USD;

		// Mocks
		when(repository.findById(accountId)).thenReturn(Optional.empty());

		// Act & Assert
		AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
				() -> service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency));
		assertEquals("Account not found with ID: " + accountId, exception.getMessage());
		verify(repository, times(1)).findById(accountId);
	}

}
