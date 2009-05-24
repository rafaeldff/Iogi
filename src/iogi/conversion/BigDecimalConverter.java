package iogi.conversion;

import java.math.BigDecimal;

public class BigDecimalConverter extends TypeConverter<BigDecimal> {

	@Override
	public Class<BigDecimal> targetClass() {
		return BigDecimal.class;
	}
	
	@Override
	public BigDecimal convert(String stringValue) {
		return new BigDecimal(stringValue);
	}


}
