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
import org.springframework.test.web.servlet.MockMvc;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.service.CurrencyAccountService;
import pl.bsf.lukasz.koc.currencyaccounts.util.CurrencyAccountTestFactory;

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
				.andExpect(jsonPath("$.balance").value(CurrencyAccountTestFactory.BALANCE))
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.CURRENCY.toString()));
	}

	@Test
	public void createAccount_ShouldReturnCreated_WhenValidRequest_zeroBalance() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO createCurrencyAccountDTO = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				BigDecimal.ZERO,
				CurrencyAccountTestFactory.CURRENCY
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
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.CURRENCY.toString()));
	}

	@Test
	public void createAccount_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
		// Arrange
		CreateCurrencyAccountDTO invalidRequest = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				BigDecimal.valueOf(-1.00), // Invalid balance
				CurrencyAccountTestFactory.CURRENCY
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
				CurrencyAccountTestFactory.CURRENCY
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
				CurrencyAccountTestFactory.CURRENCY
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
				.andExpect(jsonPath("$.balance").value(CurrencyAccountTestFactory.BALANCE))
				.andExpect(jsonPath("$.currency").value(CurrencyAccountTestFactory.CURRENCY.toString()));
	}

	@Test
	public void getAccountById_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
		// Arrange Act Assert
		mockMvc.perform(get(API_URL + "/9999"))
				// Assert
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorMessage").value("Account not found with id: 9999"));
	}
}
