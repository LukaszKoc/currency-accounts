package pl.bsf.lukasz.koc.currencyaccounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurrencyAccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyAccountsApplication.class, args);
	}

}
