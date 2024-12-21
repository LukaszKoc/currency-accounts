package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateDTO {

	private String no;

	private String effectiveDate;

	private double mid;

}
