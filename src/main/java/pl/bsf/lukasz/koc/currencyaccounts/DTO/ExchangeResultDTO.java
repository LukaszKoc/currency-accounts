package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.validator.ValidCurrency;
import pl.bsf.lukasz.koc.currencyaccounts.config.BigDecimalScaleSerializer;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResultDTO {

	@JsonSerialize(using = BigDecimalScaleSerializer.class)
	private BigDecimal amount;

	@ValidCurrency
	private Currency currency;
}
