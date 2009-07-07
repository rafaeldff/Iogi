package br.com.caelum.iogi.util;

import java.util.Locale;

import br.com.caelum.iogi.spi.LocaleProvider;

public class DefaultLocaleProvider implements LocaleProvider {

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
}
