package pl.bsf.lukasz.koc.currencyaccounts.util;

import java.math.BigDecimal;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeRequestDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.WithdrawRequestDTO;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

public class ExchangeRatesFactory {

	public static WithdrawRequestDTO getExchangeRequestDTO(BigDecimal amount, Currency orderedCurrency) {
		return WithdrawRequestDTO.builder()
				.amount(amount)
				.targetCurrency(orderedCurrency)
				.build();
	}

	public static ExchangeRequestDTO getExchangeRequestDTO(BigDecimal amount, Currency sourceCurrency, Currency orderedCurrency) {
		return ExchangeRequestDTO.builder()
				.amount(amount)
				.sourceCurrency(sourceCurrency)
				.targetCurrency(orderedCurrency)
				.build();
	}
}
