package pl.bsf.lukasz.koc.currencyaccounts.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppVersionPrinter implements SmartInitializingSingleton {

	private final Environment env;

	@Override
	public void afterSingletonsInstantiated() {
		log.info("Build version: *** {} ***", env.getProperty("build.version"));
	}
}
