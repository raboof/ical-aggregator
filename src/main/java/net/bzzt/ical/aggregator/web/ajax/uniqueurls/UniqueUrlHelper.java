package net.bzzt.ical.aggregator.web.ajax.uniqueurls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.string.JavascriptUtils;
import org.apache.wicket.util.time.Time;

public class UniqueUrlHelper
{

	public static void resetHash(AjaxRequestTarget target, String name, Date date)
	{
		PageParameters parameters = new PageParameters();
		
		parameters.put(name, Application.get().getConverterLocator().getConverter(date.getClass()).convertToString(date, null));
		setHash(target, parameters);
	}

	private static void setHash(AjaxRequestTarget target, PageParameters parameters)
	{
		List<String> pairs = new ArrayList<String>();
		for (Entry<String, String[]> entry : parameters.toRequestParameters().entrySet())
		{
			for (String value : entry.getValue())
			{
				// TODO escape &, = and perhaps other characters
				pairs.add(entry.getKey() + "=" + value);
			}
		}
		
		String hash = "#" + StringUtils.join(pairs, '&');
		target.appendJavascript("window.location.hash = '" + JavascriptUtils.escapeQuotes(hash) + "';");
	}

}
