package pl.bsf.lukasz.koc.currencyaccounts.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.NbpExchangeRatesResponseDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.RateDTO;
import pl.bsf.lukasz.koc.currencyaccounts.client.NbpCurrencyExchangeFeignClient;

@Component
@Primary
public class NbpCurrencyExchangeFeignClientMock implements NbpCurrencyExchangeFeignClient {

	@Override
	public NbpExchangeRatesResponseDTO getExchangeRate(String currency) {
		return switch (currency) {
			case "USD" -> getNbpExchangeRatesResponseDTO("USD", "4.00");
			case "PLN" -> getNbpExchangeRatesResponseDTO("PLN", "1.00");
			default -> null;
		};
	}

	private static NbpExchangeRatesResponseDTO getNbpExchangeRatesResponseDTO(String currency, String rate) {
		return new NbpExchangeRatesResponseDTO(
				"test", currency, currency,
				Collections.singletonList(new RateDTO("no", LocalDate.now().minusDays(1), new BigDecimal(rate))));
	}
}
