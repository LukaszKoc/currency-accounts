package pl.bsf.lukasz.koc.currencyaccounts.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalScaleSerializer extends JsonSerializer<BigDecimal> {

	@Override
	public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value != null) {
			gen.writeString(value.setScale(2, RoundingMode.DOWN).toString());
		} else {
			gen.writeNull();
		}
	}
}
