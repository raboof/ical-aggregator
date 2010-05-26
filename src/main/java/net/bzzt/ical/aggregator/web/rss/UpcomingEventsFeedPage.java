package net.bzzt.ical.aggregator.web.rss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorSession;
import net.bzzt.ical.aggregator.web.WicketApplication;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.rome.web.FeedPage;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

public class UpcomingEventsFeedPage extends FeedPage
{
	private static final Transformer eventToSyndEntryTransformer = new EventToSyndEntryTransformer();

	@SpringBean
	private FeedService feedService;

	@Nonnull
	private final List<String> shortNames;
	
	public UpcomingEventsFeedPage(PageParameters parameters)
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
	}
	
	@Override
	protected SyndFeed getFeed()
	{
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle(WicketApplication.getTitle() + " Feed");
		feed.setLink(WicketApplication.getLink());
		feed.setDescription(WicketApplication.getTitle());
		feed.setUri(WicketApplication.getLink());
		
		List<Feed> selectedFeeds = feedService.getSelectedFeeds(shortNames);
		
		
		
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		CollectionUtils.collect(feedService.getEvents(selectedFeeds),
			eventToSyndEntryTransformer, entries);

		feed.setEntries(entries);

		return feed;

	}

}
