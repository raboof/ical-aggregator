package net.bzzt.ical.aggregator.web.rss;

import java.util.ArrayList;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorSession;
import net.bzzt.ical.aggregator.web.WicketApplication;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.rome.web.FeedPage;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

public class UpcomingEventsFeedPage extends FeedPage
{
	private static final Transformer eventToSyndEntryTransformer = new Transformer()
	{

		@Override
		public Object transform(Object rawEvent)
		{
			Event event = (Event) rawEvent;
			
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(event.summary);
			if (event.url != null)
			{
				entry.setLink(event.url.toExternalForm());
			}
			entry.setPublishedDate(event.getStart());
			
			SyndContent description = new SyndContentImpl();
			description.setType("text/plain");
			description.setValue(event.description);
			entry.setDescription(description);
			return entry;
		}

	};

	@SpringBean
	private FeedService feedService;

	@Override
	protected SyndFeed getFeed()
	{
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle(WicketApplication.getTitle() + " Feed");
		feed.setLink(WicketApplication.getLink());
		feed.setDescription(WicketApplication.getTitle());

		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		CollectionUtils.collect(feedService.getEvents(((AggregatorSession) getSession()).getSelectedFeeds()),
			eventToSyndEntryTransformer, entries);

		feed.setEntries(entries);

		return feed;

	}

}
