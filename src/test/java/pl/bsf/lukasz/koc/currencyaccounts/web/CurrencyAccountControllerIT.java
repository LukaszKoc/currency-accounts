package pl.bsf.lukasz.koc.currencyaccounts.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.WithdrawRequestDTO;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
import pl.bsf.lukasz.koc.currencyaccounts.service.CurrencyAccountService;
import pl.bsf.lukasz.koc.currencyaccounts.util.CurrencyAccountTestFactory;
import pl.bsf.lukasz.koc.currencyaccounts.util.ExchangeRatesFactory;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CurrencyAccountControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CurrencyAccountService service;

	private static final String API_URL = "/api/accounts";

	@Test
	public void createAccount_ShouldReturnCreated_WhenValidRequest() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO createCurrencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CREATE_CURRENCY_ACCOUNT_DTO;

		// Act
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createCurrencyAccountDTO)))
				// Assert
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value(CurrencyAccountTestFactory.FIRST_NAME))
				.andExpect(jsonPath("$.lastName").value(CurrencyAccountTestFactory.LAST_NAME))
				.andExpect(jsonPath("$.balance").value(CurrencyAccountTestFactory.THAUSAND))
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.PLN.toString()));
	}

	@Test
	public void createAccount_ShouldReturnCreated_withDefaultCurrency() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO createCurrencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CREATE_CURRENCY_ACCOUNT_DTO;
		createCurrencyAccountDTO.setCurrency(null);
		// Act
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createCurrencyAccountDTO)))
				// Assert
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value(CurrencyAccountTestFactory.FIRST_NAME))
				.andExpect(jsonPath("$.lastName").value(CurrencyAccountTestFactory.LAST_NAME))
				.andExpect(jsonPath("$.balance").value(CurrencyAccountTestFactory.THAUSAND))
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.PLN.toString()));
	}

	@Test
	public void createAccount_ShouldReturnCreated_WhenValidRequest_zeroBalance() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO createCurrencyAccountDTO = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				BigDecimal.ZERO,
				CurrencyAccountTestFactory.PLN
		);

		// Act
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createCurrencyAccountDTO)))
				// Assert
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value(CurrencyAccountTestFactory.FIRST_NAME))
				.andExpect(jsonPath("$.lastName").value(CurrencyAccountTestFactory.LAST_NAME))
				.andExpect(jsonPath("$.balance").value(new BigDecimal("0.00").toString()))
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.PLN.toString()));
	}

	@Test
	public void createAccount_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO invalidRequest = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				BigDecimal.valueOf(-1.00), // Invalid balance
				CurrencyAccountTestFactory.PLN
		);

		// Act
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				// Assert
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.balance").value("Balance must be positive number"));
	}

	@Test
	public void createAccount_ShouldReturnBadRequest_BalanceIsNull() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO invalidRequest = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				null,
				CurrencyAccountTestFactory.PLN
		);

		// Act
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				// Assert
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.balance").value("Balance must not be null"));
	}

	@Test
	public void createAccount_ShouldReturnBadRequest_NameIsNull() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO invalidRequest = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				null,
				null,
				new BigDecimal(1),
				CurrencyAccountTestFactory.PLN
		);

		// Act
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				// Assert
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.lastName").value("must not be blank"))
				.andExpect(jsonPath("$.firstName").value("must not be blank"));
	}

	@Test
	public void getAccountById_ShouldReturnAccount_WhenAccountExists() throws Exception {
		// Arrange
		CurrencyAccountDTO createdAccount = service.createAccount(CurrencyAccountTestFactory.SAMPLE_CREATE_CURRENCY_ACCOUNT_DTO);

		// Act
		mockMvc.perform(get(API_URL + "/" + createdAccount.getId()))
				// Assert
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(createdAccount.getId().intValue()))
				.andExpect(jsonPath("$.firstName").value(CurrencyAccountTestFactory.FIRST_NAME))
				.andExpect(jsonPath("$.lastName").value(CurrencyAccountTestFactory.LAST_NAME))
				.andExpect(jsonPath("$.balance").value(CurrencyAccountTestFactory.THAUSAND))
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.PLN.toString()))
				.andExpect(jsonPath("$.balanceExchanged").value(new BigDecimal("250.00")));
	}

	@Test
	public void getAccountById_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
		// Arrange Act Assert
		mockMvc.perform(get(API_URL + "/9999"))
				// Assert
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorMessage").value("Account not found with ID: 9999"));
	}

	@Test
	public void withdrawMoneyInCurrency_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
		// Arrange
		long nonExistentAccountId = 9999L;
		BigDecimal amountToWithdraw = BigDecimal.valueOf(100.00);
		Currency orderedCurrency = Currency.USD;

		WithdrawRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToWithdraw, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL + "/" + nonExistentAccountId + "/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorMessage").value("Account not found with ID: " + nonExistentAccountId));

	}

	@Test
	public void withdrawMoneyInCurrency_ShouldReturnBadRequest_WhenNegativeAmount() throws Exception {
		// Arrange
		CurrencyAccountDTO createdAccount = service.createAccount(CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				"John", "Doe", BigDecimal.valueOf(1000.00), Currency.PLN));
		BigDecimal amountToWithdraw = BigDecimal.valueOf(-100.00);
		Currency orderedCurrency = Currency.USD;

		WithdrawRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToWithdraw, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL + "/" + createdAccount.getId() + "/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.amount").value("Amount must be positive number"));

		assertBalanceAfterOperation(createdAccount, new BigDecimal("1000.00"));
	}

	@Test
	public void withdrawMoneyInCurrency_ShouldExchangePlnToUsd() throws Exception {
		// Arrange
		CurrencyAccountDTO createdAccount = service.createAccount(CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				"John", "Doe", BigDecimal.valueOf(1000.00), Currency.PLN));
		BigDecimal amountToWithdraw = BigDecimal.valueOf(100.00);
		Currency orderedCurrency = Currency.USD;

		WithdrawRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToWithdraw, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL + "/" + createdAccount.getId() + "/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currency").value(Currency.PLN.name()))
				.andExpect(jsonPath("$.amount").value(new BigDecimal("400.00")));

		assertBalanceAfterOperation(createdAccount, new BigDecimal("600.00"));
	}

	@Test
	public void withdrawMoneyInCurrency_ShouldExchangeUsdToPln() throws Exception {
		// Arrange
		CurrencyAccountDTO createdAccount = service.createAccount(CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				"John", "Doe", BigDecimal.valueOf(1000.00), Currency.USD));
		BigDecimal amountToWithdraw = BigDecimal.valueOf(100.00);
		Currency orderedCurrency = Currency.PLN;

		WithdrawRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToWithdraw, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL + "/" + createdAccount.getId() + "/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());

		assertBalanceAfterOperation(createdAccount, new BigDecimal("975.00"));
	}

	@Test
	public void withdrawMoneyInCurrency_ShouldReturnBadRequest_WhenInsufficientFunds() throws Exception {
		// Arrange
		CurrencyAccountDTO createdAccount = service.createAccount(CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				"John", "Doe", BigDecimal.valueOf(50.00), Currency.PLN));
		BigDecimal amountToWithdraw = BigDecimal.valueOf(100.00);
		Currency orderedCurrency = Currency.USD;

		WithdrawRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToWithdraw, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL + "/" + createdAccount.getId() + "/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorMessage").value("Insufficient funds to perform the operation"));

		assertBalanceAfterOperation(createdAccount, new BigDecimal("50.00"));
	}

	private void assertBalanceAfterOperation(CurrencyAccountDTO createdAccount, BigDecimal expectedValue) throws Exception {
		mockMvc.perform(get(API_URL + "/" + createdAccount.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.balance").value(expectedValue.toString()));
	}

}
