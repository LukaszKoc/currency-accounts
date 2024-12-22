package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.validator.ValidCurrency;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class CreateCurrencyAccountDTO {

	@NotBlank
	@Size(max = 50, message = "First name must be at most 50 characters")
	private String firstName;

	@NotBlank
	@Size(max = 100, message = "Last name must be at most 100 characters")
	private String lastName;

	@NotNull(message = "Balance must not be null")
	@DecimalMin(value = "0", message = "Balance must be positive number")
	@Digits(integer = 999, fraction = 2, message = "Balance must have up to two decimal places")
	private BigDecimal balance;

	public void setBalance(BigDecimal insertedBalance) {
		if (insertedBalance != null && insertedBalance.scale() < 2) {
			log.debug("Setting balance scale to exactly two decimal places for {}", insertedBalance);
			insertedBalance = insertedBalance.setScale(2, RoundingMode.DOWN);
		}
		this.balance = insertedBalance;
	}

	@ValidCurrency
	private Currency currency;
}
