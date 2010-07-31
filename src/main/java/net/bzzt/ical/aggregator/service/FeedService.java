package net.bzzt.ical.aggregator.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.fortuna.ical4j.data.ParserException;

public interface FeedService {
	
	<T> T load(Class<T> clazz, Serializable id);
	
	void saveOrUpdate(Feed feed);

	void saveOrUpdateEvent(Event event);
	
	List<Feed> getFeeds();

	void reloadFeed(Feed feed) throws IOException, ParserException;

	/** reload all non-manual feeds - called periodically by quartz, see applicationContext */
	void reloadFeeds();

	/**
	 * only verified, resolve duplicates.
	 * 
	 * @param selectedFeeds
	 * @param date
	 * @param maxRecurrence TODO
	 * @return
	 */
	List<Event> getEventsForDay(List<Feed> selectedFeeds, Date date, int maxRecurrence);

	/**
	 * only 'verified' events.
	 * 
	 * @param feed
	 * @param resolveDuplicates
	 * @param onlyUpcoming
	 * @param maxRecurrence TODO
	 * @return
	 */
	List<Event> getEvents(Feed feed, boolean resolveDuplicates, boolean onlyUpcoming, Integer maxRecurrence);

	void markDuplicate(Event master, Event duplicate);

	void clear(Feed feed);

	List<Feed> getManualFeeds();

	void delete(Feed feed);
	
	void delete(Event event);

	List<Feed> find(String name);

	List<Event> getAlternatives(Event event);

	/**
	 * Kijk welke events in aanmerking komen als parent van dit event
	 * 
	 * @param object
	 * @return
	 */
	List<Event> getDuplicateCandidates(Event object);

	List<Event> getEvents(List<Feed> selectedFeeds, Integer maxRecurrence);

	/** list of feeds that is shown by default */
	List<Feed> getDefaultFeeds();

	List<Event> getEventsToVerify();

	Feed getFeedByShortName(String input);

	List<Feed> getSelectedFeeds(List<String> shortNames);
}
