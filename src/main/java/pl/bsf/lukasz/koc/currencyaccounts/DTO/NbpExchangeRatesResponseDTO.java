package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NbpExchangeRatesResponseDTO {

	private String table;

	private String currency;

	private String code;

	private List<RateDTO> rates;
}
