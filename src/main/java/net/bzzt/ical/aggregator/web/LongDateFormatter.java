/**
 * 
 */
package net.bzzt.ical.aggregator.web;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

final class LongDateFormatter implements IConverter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String convertToString(Object value, Locale locale) {
		SimpleDateFormat dateConverter = new SimpleDateFormat("EEE dd-MM-yyyy", locale);
		return dateConverter.format(value);
	}

	@Override
	public Object convertToObject(String value, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
}