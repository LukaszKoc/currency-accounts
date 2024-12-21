package pl.bsf.lukasz.koc.currencyaccounts.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.bsf.lukasz.koc.currencyaccounts.DTO.NbpExchangeRatesResponseDTO;

@FeignClient(name = "nbpClient", url = "${nbp.exchange.rates.url}", primary = false)
public interface NbpCurrencyExchangeFeignClient {

	@GetMapping("{currency}?format=json")
	NbpExchangeRatesResponseDTO getExchangeRate(@PathVariable("currency") String currency);

}
