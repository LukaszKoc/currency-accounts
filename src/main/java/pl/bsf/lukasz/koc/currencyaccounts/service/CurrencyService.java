package pl.bsf.lukasz.koc.currencyaccounts.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bsf.lukasz.koc.currencyaccounts.client.NbpCurrencyExchangeFeignClient;
import pl.bsf.lukasz.koc.currencyaccounts.exception.FailedToFetchNbpDataException;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@Service
@RequiredArgsConstructor
public class CurrencyService {

	private final NbpCurrencyExchangeFeignClient nbpCurrencyExchangeFeignClient;

	public BigDecimal getExchangeRate(Currency currency) {
		return BigDecimal.valueOf(
				nbpCurrencyExchangeFeignClient.getExchangeRate(currency.name())
						.getRates().stream()
						.findFirst()
						.orElseThrow(() -> new FailedToFetchNbpDataException("NBP response does not contain any exchangeRates data."))
						.getMid());
	}
}
