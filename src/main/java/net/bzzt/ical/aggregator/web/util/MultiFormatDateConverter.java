package net.bzzt.ical.aggregator.web.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converters.DateConverter;
import org.apache.wicket.util.string.Strings;

/** voor de YUI datepicker uit wicket is het nodig dat dit een DateConverter instance is */
public class MultiFormatDateConverter extends DateConverter implements IConverter
{
	/** */
	private static final long serialVersionUID = 1L;

	/** */
	private final SimpleDateFormat defaultFormat;

	/** */
	private final SimpleDateFormat[] allowedFormats;

	/**
	 * @param defaultFormat
	 * @param allowedFormats
	 */
	public MultiFormatDateConverter(String defaultFormat, String... allowedFormats)
	{
		this.defaultFormat = new SimpleDateFormat(defaultFormat);
		this.allowedFormats = new SimpleDateFormat[allowedFormats.length];
		for (int i = 0; i < allowedFormats.length; i++)
		{
			this.allowedFormats[i] = new SimpleDateFormat(allowedFormats[i]);
			this.allowedFormats[i].setLenient(false);
		}
	}

	/**
	 * @param defaultFormat
	 * @param allowedFormats
	 */
	public MultiFormatDateConverter(SimpleDateFormat defaultFormat, SimpleDateFormat[] allowedFormats)
	{
		super();
		this.allowedFormats = allowedFormats;
		this.defaultFormat = defaultFormat;
	}

	/**
	 * @see org.apache.wicket.util.convert.converters.DateConverter#convertToObject(java.lang.String, java.util.Locale)
	 */
	@Override
	@SuppressWarnings("PMD")
	public Date convertToObject(String value, Locale locale)
	{
		if (value == null || Strings.isEmpty(value))
		{
			return null;
		}
		else
		{
			if (this.allowedFormats.length == 0)
			{
				throw new IllegalStateException("No DateFormat's configured");
			}
			else
			{
				for (SimpleDateFormat format : this.allowedFormats)
				{
					try
					{
						return (Date) parse(format, value, locale);
					}
					catch (ConversionException e)
					{
						// do nothing
					}
				}
				throw newConversionException("Cannot parse '" + value + "' using format ", value, locale);
			}
		}
	}

	/**
	 * @see org.apache.wicket.util.convert.converters.DateConverter#convertToString(java.lang.Object, java.util.Locale)
	 */
	@Override
	public String convertToString(Object value, Locale locale)
	{
		return this.defaultFormat.format(value);
	}

	/**
	 * @return the defaultFormat
	 */
	public SimpleDateFormat getDefaultFormat()
	{
		return this.defaultFormat;
	}

}
