package pl.bsf.lukasz.koc.currencyaccounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.exception.AccountNotFoundException;
import pl.bsf.lukasz.koc.currencyaccounts.mapper.CurrencyAccountMapper;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;
import pl.bsf.lukasz.koc.currencyaccounts.repository.CurrencyAccountRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CurrencyAccountService {

	private final CurrencyAccountRepository repository;

	private final CurrencyAccountMapper mapper;

	public CurrencyAccountDTO createAccount(CreateCurrencyAccountDTO createCurrencyAccountDTO) {
		log.debug("Creating account for user: {} {}", createCurrencyAccountDTO.getFirstName(), createCurrencyAccountDTO.getLastName());

		CurrencyAccount account = mapper.toEntity(createCurrencyAccountDTO);
		CurrencyAccount savedAccount = repository.save(account);

		log.debug("Account created with ID: {}", savedAccount.getId());

		return mapper.toDTO(savedAccount);
	}

	public CurrencyAccountDTO getAccountById(Long id) {
		log.debug("Fetching account with ID: {}", id);

		CurrencyAccount account = getAccountEntityById(id);

		log.debug("Fetched account with ID: {} and balance: {}", account.getId(), account.getBalance());

		return mapper.toDTO(account);
	}

	private CurrencyAccount getAccountEntityById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
	}
}
