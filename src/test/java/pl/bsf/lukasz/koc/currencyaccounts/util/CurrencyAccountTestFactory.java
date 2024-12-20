package pl.bsf.lukasz.koc.currencyaccounts.util;

import java.math.BigDecimal;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;

public class CurrencyAccountTestFactory {

	public static final String FIRST_NAME = "John";

	public static final String LAST_NAME = "Doe";

	public static final BigDecimal BALANCE = new BigDecimal("1000.00");

	public static final Currency CURRENCY = Currency.PLN;

	public static final Long ACCOUNT_ID = 1L;

	public static final CreateCurrencyAccountDTO SAMPLE_CREATE_CURRENCY_ACCOUNT_DTO =
			createCreateCurrencyAccountDTO(FIRST_NAME, LAST_NAME, BALANCE, CURRENCY);

	public static final CurrencyAccount SAMPLE_CURRENCY_ACCOUNT =
			CurrencyAccount.builder()
					.id(1L)
					.firstName(FIRST_NAME)
					.lastName(LAST_NAME)
					.balance(BALANCE)
					.currency(CURRENCY)
					.build();

	public static final CurrencyAccountDTO SAMPLE_CURRENCY_ACCOUNT_DTO = CurrencyAccountDTO.builder()
			.id(1L)
			.firstName(FIRST_NAME)
			.lastName(LAST_NAME)
			.balance(BALANCE)
			.currency(CURRENCY)
			.build();

	public static CreateCurrencyAccountDTO createCreateCurrencyAccountDTO(String firstName, String lastName, BigDecimal balance,
			Currency currency) {
		CreateCurrencyAccountDTO dto = new CreateCurrencyAccountDTO();
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		dto.setBalance(balance);
		dto.setCurrency(currency);
		return dto;
	}

}
