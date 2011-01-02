package net.bzzt.ical.aggregator.web.ical;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.WicketApplication;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Time;


public class UpcomingEventsIcalPage extends IcalPage
{
	private static final Log LOG = LogFactory.getLog(UpcomingEventsIcalPage.class);
	
	private static final Transformer eventToVeventTransformer = new EventToVEventTransformer();

	@SpringBean
	private FeedService feedService;

	@Nonnull
	private final List<String> shortNames;

	private Integer maxRecurrence;
	
	private Date date;
	
	public UpcomingEventsIcalPage(PageParameters parameters)
	{
		String[] sn = parameters.getStringArray("sn");
		if (sn == null)
		{
			shortNames = Collections.emptyList();
		}
		else
		{
			shortNames = Arrays.asList(sn);
		}
		maxRecurrence = parameters.getAsInteger("maxRecurrence");
		String date = parameters.getString("date");
		if (StringUtils.isNotBlank(date))
		{
			this.date = (Date) WicketApplication.get().getConverterLocator().getConverter(Date.class).convertToObject(date, null);
		}
	}
	
	@Override
	protected Calendar getCalendar()
	{
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("aggregator"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(new XProperty("X-WR-CALNAME", WicketApplication.getTitle()));
		calendar.getProperties().add(new XProperty("X-URL", WicketApplication.getLink()));
		
		List<Feed> selectedFeeds = feedService.getSelectedFeeds(shortNames);

		List<Event> events;
		if (date == null)
		{
			events = feedService.getEvents(selectedFeeds, maxRecurrence);
		}
		else
		{
			events = feedService.getEventsForDay(selectedFeeds, date, maxRecurrence);
		}
		
		for (Event event : events)
		{
			calendar.getComponents().add(eventToVeventTransformer.transform(event));
		}
		
		return calendar;

	}

}
