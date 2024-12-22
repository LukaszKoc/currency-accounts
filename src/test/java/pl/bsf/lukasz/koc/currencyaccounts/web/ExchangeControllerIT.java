package pl.bsf.lukasz.koc.currencyaccounts.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeRequestDTO;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
import pl.bsf.lukasz.koc.currencyaccounts.util.ExchangeRatesFactory;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ExchangeControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String API_URL = "/api/currency/exchange";

	@Test
	public void exchangeTOCurrency_ShouldReturnUsdResult() throws Exception {
		// Arrange
		BigDecimal amountToExchange = BigDecimal.valueOf(100.00);
		Currency sourceCurrency = Currency.PLN;
		Currency orderedCurrency = Currency.USD;

		ExchangeRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToExchange, sourceCurrency, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currency").value(Currency.USD.name()))
				.andExpect(jsonPath("$.amount").value(new BigDecimal("25.00")));
	}

	@Test
	public void exchangeTOCurrency_ShouldReturnPlnResult() throws Exception {
		// Arrange
		BigDecimal amountToExchange = BigDecimal.valueOf(100.00);
		Currency sourceCurrency = Currency.USD;
		Currency orderedCurrency = Currency.PLN;

		ExchangeRequestDTO request = ExchangeRatesFactory.getExchangeRequestDTO(amountToExchange, sourceCurrency, orderedCurrency);

		// Act & Assert
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currency").value(Currency.PLN.name()))
				.andExpect(jsonPath("$.amount").value(new BigDecimal("400.00")));
	}

}
