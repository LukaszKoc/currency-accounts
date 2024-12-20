package pl.bsf.lukasz.koc.currencyaccounts.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;
import pl.bsf.lukasz.koc.currencyaccounts.util.CurrencyAccountTestFactory;

public class CurrencyAccountMapperTest {

	private final CurrencyAccountMapper mapper = Mappers.getMapper(CurrencyAccountMapper.class);

	@Test
	void shouldMapCreateCurrencyAccountDTOToCurrencyAccount() {
		// Arrange
		CreateCurrencyAccountDTO createCurrencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CREATE_CURRENCY_ACCOUNT_DTO;

		// Act
		CurrencyAccount currencyAccount = mapper.toEntity(createCurrencyAccountDTO);

		// Assert
		assertThat(currencyAccount).isNotNull();
		assertThat(currencyAccount.getFirstName()).isEqualTo(createCurrencyAccountDTO.getFirstName());
		assertThat(currencyAccount.getLastName()).isEqualTo(createCurrencyAccountDTO.getLastName());
		assertThat(currencyAccount.getBalance()).isEqualTo(createCurrencyAccountDTO.getBalance());
		assertThat(currencyAccount.getCurrency()).isEqualTo(createCurrencyAccountDTO.getCurrency());
	}

	@Test
	void shouldMapCurrencyAccountToCurrencyAccountDTO() {
		// Arrange
		CurrencyAccount currencyAccount = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT;

		// Act
		CurrencyAccountDTO currencyAccountDTO = mapper.toDTO(currencyAccount);

		// Assert
		assertThat(currencyAccountDTO).isNotNull();
		assertThat(currencyAccountDTO.getId()).isEqualTo(currencyAccount.getId());
		assertThat(currencyAccountDTO.getFirstName()).isEqualTo(currencyAccount.getFirstName());
		assertThat(currencyAccountDTO.getLastName()).isEqualTo(currencyAccount.getLastName());
		assertThat(currencyAccountDTO.getBalance()).isEqualTo(currencyAccount.getBalance());
		assertThat(currencyAccountDTO.getCurrency()).isEqualTo(currencyAccount.getCurrency());
	}

	@Test
	void shouldMapCurrencyAccountDTOToCurrencyAccount() {
		// Arrange
		CurrencyAccountDTO currencyAccountDTO = CurrencyAccountTestFactory.SAMPLE_CURRENCY_ACCOUNT_DTO;

		// Act
		CurrencyAccount currencyAccount = mapper.toEntity(currencyAccountDTO);

		// Assert
		assertThat(currencyAccount).isNotNull();
		assertThat(currencyAccountDTO.getId()).isEqualTo(currencyAccount.getId());
		assertThat(currencyAccount.getFirstName()).isEqualTo(currencyAccountDTO.getFirstName());
		assertThat(currencyAccount.getLastName()).isEqualTo(currencyAccountDTO.getLastName());
		assertThat(currencyAccount.getBalance()).isEqualTo(currencyAccountDTO.getBalance());
		assertThat(currencyAccount.getCurrency()).isEqualTo(currencyAccountDTO.getCurrency());
	}
}
