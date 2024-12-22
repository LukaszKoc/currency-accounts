package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import jakarta.validation.constraints.NotNull;
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
public class ExchangeRequestDTO extends WithdrawRequestDTO {

	@NotNull(message = "Currency must not be null")
	@ValidCurrency
	protected Currency targetCurrency;

	@NotNull(message = "Currency must not be null")
	@ValidCurrency
	private Currency sourceCurrency;
}
