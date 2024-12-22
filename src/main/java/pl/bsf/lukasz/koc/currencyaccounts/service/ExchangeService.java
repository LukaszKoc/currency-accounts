package pl.bsf.lukasz.koc.currencyaccounts.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeResultDTO;
import pl.bsf.lukasz.koc.currencyaccounts.client.NbpCurrencyExchangeFeignClient;
import pl.bsf.lukasz.koc.currencyaccounts.exception.FailedToFetchNbpDataException;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeService {

	private final NbpCurrencyExchangeFeignClient nbpCurrencyExchangeFeignClient;

	public BigDecimal getExchangeRate(Currency currency) {
		log.info("Fetch NBP for {} exchange rates", currency);
		return nbpCurrencyExchangeFeignClient.getExchangeRate(currency.name())
				.getRates().stream()
				.filter(rate -> rate.getEffectiveDate().isBefore(LocalDate.now()))
				.findFirst()
				.orElseThrow(() -> new FailedToFetchNbpDataException("NBP response does not contain any effective exchange rates."))
				.getMid();
	}

	public ExchangeResultDTO exchangeCurrency(BigDecimal sourceAmount, Currency sourceCurrency, Currency targetCurrency) {
		log.debug("Exchange {} {} to {}", sourceAmount, sourceCurrency, targetCurrency);
		BigDecimal targetAmount;
		BigDecimal exchangeRate;

		if (sourceCurrency.equals(Currency.PLN)) {
			exchangeRate = getExchangeRate(targetCurrency);
			targetAmount = sourceAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
		} else if (targetCurrency.equals(Currency.PLN)) {
			exchangeRate = getExchangeRate(sourceCurrency);
			targetAmount = sourceAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
		} else {
			throw new NotImplementedException("Exchanges from/to other currencies than USD or PLN are not implemented.");
		}

		log.debug("Exchanged to {} {} with rate: {}", targetAmount, targetCurrency, exchangeRate);
		return getExchangeResultDTO(targetCurrency, targetAmount);
	}

	private static ExchangeResultDTO getExchangeResultDTO(Currency targetCurrency, BigDecimal targetAmount) {
		return ExchangeResultDTO.builder()
				.currency(targetCurrency)
				.amount(targetAmount)
				.build();
	}
}
