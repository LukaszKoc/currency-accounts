package pl.bsf.lukasz.koc.currencyaccounts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CreateCurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.CurrencyAccountDTO;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;

@Mapper(componentModel = "spring")
public interface CurrencyAccountMapper {

	@Mapping(source = "balance", target = "balance")
	CurrencyAccount toEntity(CreateCurrencyAccountDTO source);

	@Mapping(source = "balance", target = "balance")
	CurrencyAccount toEntity(CurrencyAccountDTO source);

	@Mapping(source = "balance", target = "balance")
	CurrencyAccountDTO toDTO(CurrencyAccount source);
}
