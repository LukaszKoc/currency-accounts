package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.validator.ValidCurrency;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDTO {

	@NotNull(message = "Amount must not be null")
	@DecimalMin(value = "0", message = "Amount must be positive number")
	@Digits(integer = 999, fraction = 2, message = "Amount must have up to two decimal places")
	private BigDecimal amount;

	@NotNull(message = "Currency must not be null")
	@ValidCurrency
	private Currency currency;
}
