package pl.bsf.lukasz.koc.currencyaccounts.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeResultDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.NbpExchangeRatesResponseDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.RateDTO;
import pl.bsf.lukasz.koc.currencyaccounts.client.NbpCurrencyExchangeFeignClient;
import pl.bsf.lukasz.koc.currencyaccounts.exception.FailedToFetchNbpDataException;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

	@Mock
	private NbpCurrencyExchangeFeignClient nbpCurrencyExchangeFeignClient;

	@InjectMocks
	private ExchangeService exchangeService;

	@BeforeEach
	void setup() {
		when(nbpCurrencyExchangeFeignClient.getExchangeRate(Currency.USD.name())).thenReturn(getNbpExchangeRatesResponseDTO("4.00"));
	}

	@Test
	void testGetExchangeRate_Success() {
		// Act
		BigDecimal exchangeRate = exchangeService.getExchangeRate(Currency.USD);

		// Assert
		assertNotNull(exchangeRate);
		assertEquals(new BigDecimal("4.00"), exchangeRate);
		verify(nbpCurrencyExchangeFeignClient, times(1)).getExchangeRate(Currency.USD.name());
	}

	@Test
	void testGetExchangeRate_NoRates_ThrowsException() {
		// Arrange
		NbpExchangeRatesResponseDTO emptyResponseDTO = new NbpExchangeRatesResponseDTO(
				"A",
				Currency.USD.name(),
				"USD",
				List.of()
		);
		when(nbpCurrencyExchangeFeignClient.getExchangeRate(Currency.USD.name())).thenReturn(emptyResponseDTO);

		// Act & Assert
		assertThrows(FailedToFetchNbpDataException.class, () -> exchangeService.getExchangeRate(Currency.USD));
	}

	@Test
	void testExchangeCurrency_FromPLNToUSD() {
		// Arrange
		BigDecimal sourceAmount = new BigDecimal("100.00");
		BigDecimal expectedAmount = new BigDecimal("25.00"); // 100 / 4.00

		// Act
		ExchangeResultDTO result = exchangeService.exchangeCurrency(sourceAmount, Currency.PLN, Currency.USD);

		// Assert
		assertNotNull(result);
		assertEquals(Currency.USD, result.getCurrency());
		assertEquals(expectedAmount, result.getAmount());
	}

	@Test
	void testExchangeCurrency_FromUSDToPLN() {
		// Arrange
		BigDecimal sourceAmount = new BigDecimal("100.00");
		BigDecimal expectedAmount = new BigDecimal("400.00"); // 100 * 4.00

		// Act
		ExchangeResultDTO result = exchangeService.exchangeCurrency(sourceAmount, Currency.USD, Currency.PLN);

		// Assert
		assertNotNull(result);
		assertEquals(Currency.PLN, result.getCurrency());
		assertEquals(expectedAmount, result.getAmount());
	}

	private static NbpExchangeRatesResponseDTO getNbpExchangeRatesResponseDTO(String mid) {
		RateDTO rateDTO = new RateDTO("testNo", LocalDate.now().minusDays(1), new BigDecimal(mid));
		NbpExchangeRatesResponseDTO responseDTO = new NbpExchangeRatesResponseDTO(
				"A",
				Currency.USD.name(),
				"USD",
				Collections.singletonList(rateDTO)
		);
		return responseDTO;
	}
}
