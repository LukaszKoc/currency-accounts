package pl.bsf.lukasz.koc.currencyaccounts.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
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
	private CurrencyService currencyService;

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
	void testGetAccountById_AccountFound() {
		// Arrange
		Long accountId = CurrencyAccountTestFactory.ACCOUNT_ID;
		CurrencyAccount currencyAccount = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		CurrencyAccountDTO currencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT_DTO;

		when(repository.findById(accountId)).thenReturn(Optional.of(currencyAccount));
		when(mapper.toDTO(currencyAccount)).thenReturn(currencyAccountDTO);

		// Act
		CurrencyAccountDTO result = service.getAccountById(accountId);

		// Assert
		assertNotNull(result);
		assertEquals(currencyAccountDTO.getFirstName(), result.getFirstName());
		assertEquals(currencyAccountDTO.getLastName(), result.getLastName());
		assertEquals(currencyAccountDTO.getBalance(), result.getBalance());
		assertEquals(currencyAccountDTO.getCurrency(), result.getCurrency());
	}

	@Test
	public void testGetAccountByIdNotFound() {
		// Arrange
		when(repository.findById(CurrencyAccountTestFactory.ACCOUNT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> service.getAccountById(CurrencyAccountTestFactory.ACCOUNT_ID));
		assertEquals("Account not found with ID: " + CurrencyAccountTestFactory.ACCOUNT_ID, exception.getMessage());
		verify(repository, times(1)).findById(CurrencyAccountTestFactory.ACCOUNT_ID);
	}

	@Test
	void testWithdrawInCurrency_Success() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToExchange = new BigDecimal("100.00");
		Currency orderedCurrency = Currency.USD;
		CurrencyAccount account = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		account.setBalance(new BigDecimal("500.00"));
		BigDecimal exchangeRate = new BigDecimal("1.2");
		BigDecimal expectedAmountToWithdraw = amountToExchange.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

		// Mocks
		when(repository.findById(accountId)).thenReturn(Optional.of(account));
		when(currencyService.getExchangeRate(orderedCurrency)).thenReturn(exchangeRate);

		// Act
		ExchangeResultDTO result = service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency);

		// Assert
		assertNotNull(result);
		assertEquals(orderedCurrency, result.getCurrency());
		assertEquals(amountToExchange, result.getAmount());
		verify(repository, times(1)).findById(accountId);
		verify(currencyService, times(1)).getExchangeRate(orderedCurrency);
		assertEquals(account.getBalance(), new BigDecimal("500.00").subtract(expectedAmountToWithdraw));
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
		when(currencyService.getExchangeRate(orderedCurrency)).thenReturn(new BigDecimal("1.2"));
		when(currencyService.getExchangeRate(account.getCurrency())).thenReturn(new BigDecimal("1.0"));

		// Act & Assert
		InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
				() -> service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency));
		assertEquals("Insufficient funds to perform the operation", exception.getMessage());
		verify(repository, times(1)).findById(accountId);
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

	@Test
	void testWithdrawInCurrency_ExchangeRateApplied() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToExchange = new BigDecimal("100.00");
		Currency orderedCurrency = Currency.USD;
		CurrencyAccount account = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;
		account.setBalance(new BigDecimal("500.00"));
		BigDecimal exchangeRate = new BigDecimal("1.2");

		// Mocks
		when(repository.findById(accountId)).thenReturn(Optional.of(account));
		when(currencyService.getExchangeRate(orderedCurrency)).thenReturn(exchangeRate);
		when(currencyService.getExchangeRate(account.getCurrency())).thenReturn(new BigDecimal("1.0"));

		// Act
		ExchangeResultDTO result = service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency);

		// Assert
		assertNotNull(result);
		assertEquals(orderedCurrency, result.getCurrency());
		assertEquals(amountToExchange, result.getAmount());
		BigDecimal expectedAmountToWithdraw = amountToExchange.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
		assertEquals(account.getBalance(), new BigDecimal("500.00").subtract(expectedAmountToWithdraw));
		verify(currencyService, times(1)).getExchangeRate(orderedCurrency);
	}
	@Test
	void testWithdrawInCurrency_ExchangePlnToUsd() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToExchange = new BigDecimal("100.00");
		Currency orderedCurrency = Currency.USD;
		CurrencyAccount account = mock(CurrencyAccount.class);  // Mocking the CurrencyAccount
		BigDecimal initialBalance = new BigDecimal("500.00");

		// Setting up mock behavior
		when(account.getBalance()).thenReturn(initialBalance);
		when(account.getCurrency()).thenReturn(Currency.PLN);
		when(repository.findById(accountId)).thenReturn(Optional.of(account));
		when(currencyService.getExchangeRate(orderedCurrency)).thenReturn(new BigDecimal("1.2"));

		// Act
		service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency);

		// Assert
		verify(repository, times(1)).findById(accountId);
		verify(currencyService, times(1)).getExchangeRate(orderedCurrency);
		verify(account, times(1)).setBalance(eq(new BigDecimal("380.00")));  // Verifying the setBalance method
		verify(account, times(2)).getBalance();  // Verifying getBalance method is called
	}

	@Test
	void testWithdrawInCurrency_ExchangeUsdToPln() {
		// Arrange
		Long accountId = 1L;
		BigDecimal amountToExchange = new BigDecimal("100.00");
		Currency orderedCurrency = Currency.PLN;
		CurrencyAccount account = mock(CurrencyAccount.class);  // Mocking the CurrencyAccount
		BigDecimal initialBalance = new BigDecimal("500.00");

		// Setting up mock behavior
		when(account.getBalance()).thenReturn(initialBalance);
		when(account.getCurrency()).thenReturn(Currency.USD);
		when(repository.findById(accountId)).thenReturn(Optional.of(account));
		when(currencyService.getExchangeRate(Currency.USD)).thenReturn(new BigDecimal("1.2"));

		// Act
		service.withdrawInCurrency(accountId, amountToExchange, orderedCurrency);

		// Assert
		verify(repository, times(1)).findById(accountId);
		verify(currencyService, times(1)).getExchangeRate(Currency.USD);
		verify(account, times(1)).setBalance(eq(new BigDecimal("417.00")));  // Verifying the setBalance method
		verify(account, times(2)).getBalance();  // Verifying getBalance method is called
	}

}
