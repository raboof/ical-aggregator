package net.bzzt.ical.aggregator.web.ical;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.collections.Transformer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class UpcomingEventsIcalPage extends IcalPage
{
	private static final Transformer eventToVeventTransformer = new EventToVEventTransformer();

	@SpringBean
	private FeedService feedService;

	@Nonnull
	private final List<String> shortNames;

	private Integer maxRecurrence;
	
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
	}
	
	@Override
	protected Calendar getCalendar()
	{
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("aggregator"));
		calendar.getProperties().add(Version.VERSION_2_0);
		
		List<Feed> selectedFeeds = feedService.getSelectedFeeds(shortNames);

		for (Event event : feedService.getEvents(selectedFeeds, maxRecurrence))
		{
			calendar.getComponents().add(eventToVeventTransformer.transform(event));
		}
		
		return calendar;

	}

}
