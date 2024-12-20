package pl.bsf.lukasz.koc.currencyaccounts.DTO;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bsf.lukasz.koc.currencyaccounts.model.Currency;
import pl.bsf.lukasz.koc.currencyaccounts.util.CurrencyAccountTestFactory;

class CreateCurrencyAccountDTOValidationsTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	void testValidDTO() {
		// Arrange
		CreateCurrencyAccountDTO dto = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				new BigDecimal("100.00"),
				Currency.USD
		);

		// Act
		Set<ConstraintViolation<CreateCurrencyAccountDTO>> violations = validator.validate(dto);

		// Assert
		assertTrue(violations.isEmpty());
	}

	@Test
	void testBalanceLessThanZero() {
		// Arrange
		CreateCurrencyAccountDTO dto = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				new BigDecimal("-1.00"),
				Currency.USD
		);

		// Act
		Set<ConstraintViolation<CreateCurrencyAccountDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertEquals(1, violations.size());
		assertEquals("Balance must be positive number", violations.iterator().next().getMessage());
	}

	@Test
	void testBalanceWithMoreThanTwoDecimalPlaces() {
		// Arrange
		CreateCurrencyAccountDTO dto = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				new BigDecimal("100.123"),
				Currency.USD
		);

		// Act
		Set<ConstraintViolation<CreateCurrencyAccountDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertEquals(1, violations.size());
		assertEquals("Balance must have up to two decimal places", violations.iterator().next().getMessage());
	}

	@Test
	void testBalanceWithLessThanTwoDecimalPlaces() {
		// Arrange
		CreateCurrencyAccountDTO dto = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				new BigDecimal("100.1"),
				Currency.USD
		);

		// Act
		Set<ConstraintViolation<CreateCurrencyAccountDTO>> violations = validator.validate(dto);

		// Assert
		assertTrue(violations.isEmpty());
	}

	@Test
	void testNullBalance() {
		// Arrange
		CreateCurrencyAccountDTO dto = CurrencyAccountTestFactory.createCreateCurrencyAccountDTO(
				CurrencyAccountTestFactory.FIRST_NAME,
				CurrencyAccountTestFactory.LAST_NAME,
				null,
				Currency.USD
		);

		// Act
		Set<ConstraintViolation<CreateCurrencyAccountDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertEquals(1, violations.size());
		assertEquals("Balance must not be null", violations.iterator().next().getMessage());
	}
}
