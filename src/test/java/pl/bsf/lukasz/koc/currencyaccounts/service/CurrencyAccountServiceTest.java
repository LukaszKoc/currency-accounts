package pl.bsf.lukasz.koc.currencyaccounts.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
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
}
