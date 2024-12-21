package pl.bsf.lukasz.koc.currencyaccounts.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.ExchangeResultDTO;
import pl.bsf.lukasz.koc.currencyaccounts.exception.AccountNotFoundException;
import pl.bsf.lukasz.koc.currencyaccounts.exception.InsufficientFundsException;
import pl.bsf.lukasz.koc.currencyaccounts.mapper.CurrencyAccountMapper;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;
import pl.bsf.lukasz.koc.currencyaccounts.repository.CurrencyAccountRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CurrencyAccountService {

	private final CurrencyAccountRepository repository;

	private final CurrencyService currencyService;

	private final CurrencyAccountMapper mapper;

	@Transactional
	public CurrencyAccountDTO createAccount(CreateCurrencyAccountDTO createCurrencyAccountDTO) {
		log.debug("Creating account for user: {} {}", createCurrencyAccountDTO.getFirstName(), createCurrencyAccountDTO.getLastName());

		CurrencyAccount account = mapper.toEntity(createCurrencyAccountDTO);
		CurrencyAccount savedAccount = repository.save(account);
		return mapper.toDTO(savedAccount);
	}

	@Transactional(readOnly = true)
	public CurrencyAccountDTO getAccountById(Long id) {
		log.debug("Fetching account with ID: {}", id);

		return mapper.toDTO(getAccountEntityById(id));
	}

	@Transactional
	public ExchangeResultDTO withdrawInCurrency(Long id, BigDecimal amountToExchange, Currency orderedCurrency) {
		log.debug("Exchanging {} {} from account with ID: {}", amountToExchange, orderedCurrency, id);

		CurrencyAccount account = getAccountEntityById(id);
		BigDecimal amountToWithdraw = exchangeCurrency(account, amountToExchange, orderedCurrency);
		withdraw(account, amountToWithdraw);

		return ExchangeResultDTO.builder()
				.currency(orderedCurrency)
				.amount(amountToExchange)
				.build();
	}

	private BigDecimal exchangeCurrency(CurrencyAccount account, BigDecimal amountToExchange, Currency orderedCurrency) {
		BigDecimal exchangeRate;
		if (!account.getCurrency().equals(Currency.PLN)) {
			exchangeRate = currencyService.getExchangeRate(account.getCurrency());
			exchangeRate = BigDecimal.ONE.divide(exchangeRate, 2, RoundingMode.HALF_UP);
		} else {
			exchangeRate = currencyService.getExchangeRate(orderedCurrency);
		}

		log.debug("Exchanging with rate: {}", exchangeRate);
		return amountToExchange.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
	}

	private void withdraw(CurrencyAccount account, BigDecimal amount) {
		log.debug("Withdrawing  {} {} from account with ID: {}", amount, account.getCurrency(), account.getId());

		validateWithdrawal(account, amount);
		account.setBalance(account.getBalance().subtract(amount));
	}

	private void validateWithdrawal(CurrencyAccount account, BigDecimal amount) {
		if (account.getBalance().compareTo(amount) < 0) {
			throw new InsufficientFundsException("Insufficient funds to perform the operation");
		}
		log.debug("Valid withdrawal {} {} from account with ID: {}", amount, account.getCurrency(), account.getId());
	}

	private CurrencyAccount getAccountEntityById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
	}

}
