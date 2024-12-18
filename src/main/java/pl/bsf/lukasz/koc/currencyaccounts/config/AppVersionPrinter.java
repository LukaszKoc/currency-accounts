package pl.bsf.lukasz.koc.currencyaccounts.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppVersionPrinter implements SmartInitializingSingleton {

	@Autowired
	private Environment env;

	@Override
	public void afterSingletonsInstantiated() {
		log.info("Build version: *** {} ***", env.getProperty("build.version"));
	}
}
