package net.bzzt.ical.aggregator.web.ical;

import java.io.IOException;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;

/**
 * Nice simple glue page from http://jroller.com/wireframe/entry/wicket_feedpage
 */
public abstract class IcalPage extends WebPage
{
	
	
	@Override
	public String getMarkupType()
	{
		return "text";
	}

	@Override
	protected final void onRender(MarkupStream markupStream)
	{
		Response response = getResponse();
		
		response.setContentType("text/calendar; charset=UTF-8");
		
		// validating is nice, but the validator disallows calendars with 0
		// events - and what else are we going to do?
		CalendarOutputter outputter = new CalendarOutputter(false);
		
		try
		{
			outputter.output(getCalendar(), response.getOutputStream());
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error streaming feed.", e);
		}
		catch (ValidationException e)
		{
			throw new RuntimeException("Error streaming feed.", e);
		}
	}

	protected abstract Calendar getCalendar();
}
