package pl.bsf.lukasz.koc.currencyaccounts.service;

import java.math.BigDecimal;
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

	private final ExchangeService exchangeService;

	private final CurrencyAccountMapper mapper;

	@Transactional
	public CurrencyAccountDTO createAccount(CreateCurrencyAccountDTO createCurrencyAccountDTO) {
		log.debug("Creating account for user: {} {}", createCurrencyAccountDTO.getFirstName(), createCurrencyAccountDTO.getLastName());

		CurrencyAccount account = mapper.toEntity(createCurrencyAccountDTO);
		if (account.getCurrency() == null) {
			account.setCurrency(Currency.PLN);
		}
		CurrencyAccount savedAccount = repository.save(account);
		return mapper.toDTO(savedAccount);
	}

	@Transactional(readOnly = true)
	public CurrencyAccountDTO getAccountDetails(Long id) {
		log.debug("Fetching account details with ID: {}", id);

		CurrencyAccountDTO accountDTO = mapper.toDTO(getAccountEntityById(id));
		if (accountDTO.getCurrency().equals(Currency.PLN)) {
			accountDTO.setBalanceExchanged(
					exchangeService.exchangeCurrency(accountDTO.getBalance(), accountDTO.getCurrency(), Currency.USD).getAmount());
		} else {
			accountDTO.setBalanceExchanged(
					exchangeService.exchangeCurrency(accountDTO.getBalance(), accountDTO.getCurrency(), Currency.PLN).getAmount());
		}
		return accountDTO;
	}

	@Transactional
	public ExchangeResultDTO withdrawInCurrency(Long id, BigDecimal amountToWithdraw, Currency orderedCurrency) {
		log.debug("Exchanging {} {} from account with ID: {}", amountToWithdraw, orderedCurrency, id);

		CurrencyAccount account = getAccountEntityById(id);
		ExchangeResultDTO result = exchangeService.exchangeCurrency(amountToWithdraw, orderedCurrency, account.getCurrency());
		withdraw(account, result.getAmount());

		return result;
	}

	private void withdraw(CurrencyAccount account, BigDecimal amount) {
		log.debug("Withdrawing {} {} from account with ID: {}", amount, account.getCurrency(), account.getId());

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
