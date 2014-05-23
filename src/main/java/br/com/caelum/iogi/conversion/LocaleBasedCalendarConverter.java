package br.com.caelum.iogi.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.LocaleProvider;

public class LocaleBasedCalendarConverter extends TypeConverter<Calendar> {
	private final LocaleProvider localeProvider;

	public LocaleBasedCalendarConverter(final LocaleProvider localeProvider) {
		this.localeProvider = localeProvider;
	}

	public boolean isAbleToInstantiate(final Target<?> target) {
		return target.getClassType() == Calendar.class;
	}

	@Override
	protected Calendar convert(final String stringValue, final Target<?> to) throws ParseException {
		final Locale locale = providedOrDefault();
		
		final DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		
		final Date date = format.parse(stringValue);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		
		return calendar;
	}

	private Locale providedOrDefault() {
		Locale locale = localeProvider.getLocale();
		if (locale == null)
			locale = Locale.getDefault();
		return locale;
	}
}
