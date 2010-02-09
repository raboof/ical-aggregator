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

    private Feed feed;
    
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
    	feed = getFeed("/exampleCalendar.ics");

    	super.setUp();
	}

	private Feed getFeed(String string) {
		URL feedUrl = FeedTest.class.getResource(string);
    	assertNotNull(feedUrl);
    	
		feed = new Feed(string, string);
		feed.url = feedUrl;
    	feedService.saveOrUpdate(feed);
    	return feed;
    }

	public void testFeed() throws IOException, ParserException
    {
    	assertNotNull(feedService);
    	
    	// Initial import: example calendar has 40 events
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false).size());
		assertEquals(40, feedService.getEvents(feed, false, false).size());
    }
	
	public void testReload() throws IOException, ParserException
	{
    	// Initial import: example calendar has 40 events
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false).size());
		assertEquals(40, feedService.getEvents(feed, false, false).size());

		// Loading it again should not result in duplicates
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false).size());
		assertEquals(40, feedService.getEvents(feed, false, false).size());
	}
	
	public void testReloadNoUid() throws IOException, ParserException
	{
		Feed feed = getFeed("/tooUnique.ics");
		feedService.reloadFeed(feed);
		assertEquals(20, feedService.getEvents(feed, true, false).size());
		assertEquals(20, feedService.getEvents(feed, false, false).size());

		feed.url = FeedTest.class.getResource("/tooUnique2.ics");
		feedService.reloadFeed(feed);
		assertEquals(20, feedService.getEvents(feed, true, false).size());
		assertEquals(20, feedService.getEvents(feed, false, false).size());
		
		feedService.delete(feed);
	}
	
	public void testDuplicate () throws IOException, ParserException
	{
    	// Initial import: example calendar has 40 events
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false).size());
		assertEquals(40, feedService.getEvents(feed, false, false).size());

		// Mark one event as duplicate
		List<Event> events = feedService.getEvents(feed, false, false);
		feedService.markDuplicate(events.get(0), events.get(1));
		
		assertEquals(40, feedService.getEvents(feed, false, false).size());
		assertEquals(39, feedService.getEvents(feed, true, false).size());
    }
	
	public void testDuplicateAcrossFeeds() throws IOException, ParserException
	{
		// If an event is a duplicate of another event, and we find all 
		// events for a given feed, we want to see the duplicates too
		feedService.reloadFeed(feed);
		assertEquals(40, feedService.getEvents(feed, true, false).size());
		List<Event> events = feedService.getEvents(feed, false, false);
		assertEquals(40, events.size());

		Event newEvent = new Event();
		feedService.saveOrUpdateEvent(newEvent);
		feedService.markDuplicate(newEvent, events.get(0));
		
		assertEquals(40, feedService.getEvents(feed, true, false).size());
		assertEquals(40, feedService.getEvents(feed, false, false).size());
	}
	
	public void testRefreshFeeds()
	{
		feedService.reloadFeeds();
	}
	
	@Override
	protected void tearDown() throws Exception {
		feed = null;
		
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