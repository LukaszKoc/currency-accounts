package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.validator.ValidCurrency;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDTO {

	@NotNull(message = "Amount must not be null")
	@DecimalMin(value = "0", message = "Amount must be positive number")
	@Digits(integer = 999, fraction = 2, message = "Amount must have up to two decimal places")
	protected BigDecimal amount;

	@ValidCurrency
	protected Currency targetCurrency;
}
