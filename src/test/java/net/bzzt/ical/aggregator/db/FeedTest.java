package net.bzzt.ical.aggregator.db;import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.fortuna.ical4j.data.ParserException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
 
@ContextConfiguration(locations={"/applicationContext.xml"})
public class FeedTest extends AbstractJUnit38SpringContextTests {
    
	@Autowired
	private FeedService feedService;

	private Integer maxRecurrence = null;
	
	@Override
	protected void setUp() throws Exception {
	}
	
	private Feed getFeed()
	{
		Feed feed = getFeed("/exampleCalendar.ics");
		
		return feed;
	}

	private Feed getFeed(String string) {
		URL feedUrl = FeedTest.class.getResource(string);
    	assertNotNull(feedUrl);
    	
		Feed feed = new Feed(string, string);
		feed.url = feedUrl;
		assertNull(feed.getId());
    	feedService.saveOrUpdate(feed);
    	assertNotNull(feed.getId());
    	return feed;
    }

	public void testFeed() throws IOException, ParserException
    {
    	assertNotNull(feedService);
    	
    	// Initial import: example calendar has 40 events
		Feed feed = getFeed();
		assertNotNull(feed.getId());
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(40, feedService.getEvents(feed, false, false, maxRecurrence).size());
    }
	
	public void testReload() throws IOException, ParserException
	{
    	// Initial import: example calendar has 40 events
		Feed feed = getFeed();
		
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(40, feedService.getEvents(feed, false, false, maxRecurrence).size());

		// Loading it again should not result in duplicates
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(40, feedService.getEvents(feed, false, false, maxRecurrence).size());
	}
	
	public void testReloadNoUid() throws IOException, ParserException
	{
		Feed feed = getFeed("/tooUnique.ics");
		feedService.reloadFeed(feed);
		assertEquals(20, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(20, feedService.getEvents(feed, false, false, maxRecurrence).size());

		feed.url = FeedTest.class.getResource("/tooUnique2.ics");
		feedService.reloadFeed(feed);
		assertEquals(20, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(20, feedService.getEvents(feed, false, false, maxRecurrence).size());
		
		feedService.delete(feed);
	}
	
	public void testDuplicate () throws IOException, ParserException
	{
		Feed feed = getFeed();
		
    	// Initial import: example calendar has 40 events
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(40, feedService.getEvents(feed, false, false, maxRecurrence).size());

		// Mark one event as duplicate
		List<Event> events = feedService.getEvents(feed, false, false, maxRecurrence);
		feedService.markDuplicate(events.get(0), events.get(1));
		
		assertEquals(40, feedService.getEvents(feed, false, false, maxRecurrence).size());
		assertEquals(39, feedService.getEvents(feed, true, false, maxRecurrence).size());
    }
	
	public void testDuplicateAcrossFeeds() throws IOException, ParserException
	{
		Feed feed = getFeed();
		
		// If an event is a duplicate of another event, and we find all 
		// events for a given feed, we want to see the duplicates too
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false, maxRecurrence).size());
		List<Event> events = feedService.getEvents(feed, false, false, maxRecurrence);
		assertEquals(40, events.size());

		Event newEvent = new Event();
		feedService.saveOrUpdateEvent(newEvent);
		feedService.markDuplicate(newEvent, events.get(0));
		
		assertEquals(40, feedService.getEvents(feed, true, false, maxRecurrence).size());
		assertEquals(40, feedService.getEvents(feed, false, false, maxRecurrence).size());
	}
	
	public void testRefreshFeeds()
	{
		feedService.reloadFeeds();
	}
	
	@Override
	protected void tearDown() throws Exception {
		for (Feed feed : feedService.find("/%"))
		{
			feedService.delete(feed);
		}
		for (Feed feed : feedService.find("fdsa"))
		{
			feedService.delete(feed);
		}
		for (Feed feed : feedService.find("asdf"))
		{
			feedService.delete(feed);
		}
	}
}