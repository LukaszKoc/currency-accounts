package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateDTO {

	private String no;

	private LocalDate effectiveDate;

	private BigDecimal mid;

}
