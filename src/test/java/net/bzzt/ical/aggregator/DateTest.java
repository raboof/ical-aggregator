package net.bzzt.ical.aggregator;

import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.util.convert.IConverter;

import junit.framework.TestCase;
import net.bzzt.ical.aggregator.web.WicketApplication;

public class DateTest extends TestCase
{
	public void testDateConverter()
	{
		WicketApplication wicketApplication = new WicketApplication();
		IConverter converter = wicketApplication.newConverterLocator().getConverter(Date.class);

		{
			Date date = (Date) converter.convertToObject("2010/08/01", null);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(8, calendar.get(Calendar.MONTH) + 1);
			assertEquals(2010, calendar.get(Calendar.YEAR));
		}

		converter.convertToObject("2011/1/2", null);
		converter.convertToObject("2011/01/2", null);
		converter.convertToObject("2011/1/02", null);
		converter.convertToObject("2011/01/02", null);
	}
}
