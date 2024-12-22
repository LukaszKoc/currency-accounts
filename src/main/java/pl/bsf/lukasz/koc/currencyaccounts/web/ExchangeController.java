package pl.bsf.lukasz.koc.currencyaccounts.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeRequestDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeResultDTO;
import pl.bsf.lukasz.koc.currencyaccounts.service.ExchangeService;

@Tag(name = "Currency Account", description = "Currency Account operations")
@RestController
@RequestMapping("/api/currency/exchange")
@RequiredArgsConstructor
@Slf4j
public class ExchangeController {

	private final ExchangeService service;

	@Operation(summary = "Exchange currency",
			description = "Exchanges amount from currency to ordered currency, by NBP exchange rate")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	public ExchangeResultDTO exchange(@Valid @RequestBody ExchangeRequestDTO request) {
		log.info("Received request to exchange {} {} to {}",
				request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency());

		ExchangeResultDTO result = service.exchangeCurrency(
				request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency());

		log.debug("Successfully exchanged {} {} to {} {}",
				request.getAmount(), request.getSourceCurrency(),
				result.getAmount(), result.getCurrency());
		return result;
	}

}
