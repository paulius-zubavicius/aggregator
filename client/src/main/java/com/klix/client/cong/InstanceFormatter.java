package com.klix.client.cong;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.springframework.format.Formatter;

public class InstanceFormatter implements Formatter<Instant> {

	@Override
	public String print(Instant t, Locale locale) {
		if (t == null) {
			return null;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale)
				.withZone(ZoneId.systemDefault());

		return formatter.format(t);
	}

	@Override
	public Instant parse(String text, Locale locale) throws ParseException {
		throw new RuntimeException("Not convertable from string");
	}

}
