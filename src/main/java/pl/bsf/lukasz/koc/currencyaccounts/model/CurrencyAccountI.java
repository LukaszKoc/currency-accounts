package pl.bsf.lukasz.koc.currencyaccounts.model;

import java.math.BigDecimal;

public interface CurrencyAccountI {

	Long getId();

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	BigDecimal getBalance();

	void setBalance(BigDecimal balance);

	Currency getCurrency();

	void setCurrency(Currency currency);
}
