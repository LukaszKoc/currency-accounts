package pl.bsf.lukasz.koc.currencyaccounts.DTO.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, Currency> {

	@Override
	public void initialize(ValidCurrency constraintAnnotation) {
	}

	@Override
	public boolean isValid(Currency value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		try {
			Currency.valueOf(value.name());
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}
}
