package pl.bsf.lukasz.koc.currencyaccounts.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeResultDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.WithdrawRequestDTO;
import pl.bsf.lukasz.koc.currencyaccounts.service.CurrencyAccountService;

@Tag(name = "Currency Account", description = "Currency Account operations")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class CurrencyAccountController {

	private final CurrencyAccountService service;

	@Operation(summary = "Create a new currency account", description = "Creates a new currency account with the provided details.")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public CurrencyAccountDTO createAccount(@Valid @RequestBody CreateCurrencyAccountDTO createCurrencyAccountDTO) {
		log.info("Received request to create account with id: {} {}", createCurrencyAccountDTO.getFirstName(),
				createCurrencyAccountDTO.getLastName());

		CurrencyAccountDTO accountDTO = service.createAccount(createCurrencyAccountDTO);
		log.debug("Successfully created account with ID: {}", accountDTO.getId());
		return accountDTO;
	}

	@Operation(summary = "Get a currency account details by ID", description = "Fetches the details of a currency account by its ID.")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{id}")
	public CurrencyAccountDTO getAccountDetails(@PathVariable Long id) {
		log.info("Received request to fetch account with ID: {}", id);

		CurrencyAccountDTO accountDTO = service.getAccountDetails(id);

		log.debug("Successfully fetched account with ID: {}", accountDTO.getId());
		return accountDTO;
	}

	@Operation(summary = "Withdraw amount in given currency",
			description = "Withdraws ordered amount in ordered currency, by NBP exchange rate")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/{id}/withdraw")
	public ExchangeResultDTO withdrawInCurrency(@PathVariable Long id, @Valid @RequestBody WithdrawRequestDTO request) {
		log.info("Received request to withdraw  {} {} for account Id {}",
				request.getAmount(), request.getTargetCurrency(), id);

		ExchangeResultDTO result = service.withdrawInCurrency(id, request.getAmount(), request.getTargetCurrency());
		log.debug("Successfully withdrew {} {} for id: {}",
				request.getAmount(), request.getTargetCurrency(), id);
		return result;
	}

}
