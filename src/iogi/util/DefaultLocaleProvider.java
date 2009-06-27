package iogi.util;

import java.util.Locale;

import iogi.spi.LocaleProvider;

public class DefaultLocaleProvider implements LocaleProvider {

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
}
