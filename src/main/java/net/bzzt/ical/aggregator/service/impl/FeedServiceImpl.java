package net.bzzt.ical.aggregator.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.impl.SessionImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.SUPPORTS)
public class FeedServiceImpl implements FeedService {
	private static final Log LOG = LogFactory.getLog(FeedServiceImpl.class);

	private static final Comparator<Event> eventComparator = new Comparator<Event>(){

		@Override
		public int compare(Event o1, Event o2) {
			return o1.getStart().compareTo(o2.getStart());
		}};

	
	@PersistenceContext(unitName="aggregatorPersistenceUnit")
	private EntityManager em;

	@Transactional(propagation=Propagation.REQUIRED)
	public void saveOrUpdate(Feed feed) {
		if (feed.getId() != null)
		{
			feed = em.merge(feed);
		}
		em.persist(feed);
	}

	public List<Feed> getFeeds() {
		Query query = em.createQuery("select f from Feed f");
		query.setHint("org.hibernate.cacheable", true);
		@SuppressWarnings("unchecked")
		List<Feed> resultList = query.getResultList();
		return resultList;
	}
	
	@Override
	public List<Feed> getDefaultFeeds() {
		@SuppressWarnings("unchecked")
		List<Feed> resultList = em.createQuery("select f from Feed f where showByDefault = true")
				.getResultList();
		return resultList;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void reloadFeed(@Nonnull Feed feed) throws IOException, ParserException {
		int added = 0;
		int updated = 0;
		try
		{
			Calendar calendar = getCalendar(feed);
	
			@SuppressWarnings("unchecked")
			Collection<VEvent> components = (Collection<VEvent>) calendar
					.getComponents(VEvent.VEVENT);
			LOG.info(components.size() + " events found");
			
			
			for (VEvent event : components) {
				Event previousVersion = findPreviousVersion(feed, event);
				if (previousVersion == null) {
					addEvent(feed, event, false);
					added++;
				} else {
					updateEvent(previousVersion, event);
					updated++;
				}
			}
			feed.lastUpdateError = "";
		}
		catch (Exception e)
		{
			feed.lastUpdateError = e.getMessage();
		}
		feed.lastUpdate = new Date();
		feed.lastUpdateAdded = added;
		feed.lastUpdateEvents = updated + added;
		saveOrUpdate(feed);
		
		calculateRecurrences(feed);
		
		LOG.info("Done reloading " + feed.url);
		
	}

	private void calculateRecurrences(Feed feed)
	{
		Query query = em.createQuery("update Event e set aantalHerhalingen = (select count(id) from Event e2 where e.feed = e2.feed and e.summary = e2.summary and e.description = e2.description) where feed = :feed");
		query.setParameter("feed", feed);
		query.executeUpdate();
	}

	private void updateEvent(Event previousVersion, VEvent event) {
		updateFields(previousVersion, event);
	}

	private void addEvent(Feed feed, VEvent vevent, Boolean hidden) {
		Event event = new Event(feed, hidden);

		updateFields(event, vevent);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveOrUpdateEvent(Event event) {
		if (event.getId() != null)
		{
			event = em.merge(event);
		}
		em.persist(event);
	}

	private void updateFields(Event event, VEvent vevent) {
		event.setStart(vevent.getStartDate().getDate());
		// it's a timestamp by default
			event.setStartHasTime(vevent.getStartDate().getParameter(Parameter.VALUE) == null || !vevent.getStartDate().getParameter(Parameter.VALUE).getValue().equals("DATE"));
		DtEnd endDate = vevent.getEndDate();
		if (endDate != null) {
			event.setEnding(endDate.getDate());
			event.setEndHasTime(vevent.getEndDate().getParameter(Parameter.VALUE) == null || !vevent.getEndDate().getParameter(Parameter.VALUE).getValue().equals("DATE"));
		}
		event.rawEvent = vevent.toString();
		event.uid = vevent.getUid().getValue();
		event.summary = vevent.getSummary().getValue();
		event.setHidden(event.getHidden());
		event.setManual(event.getManual());
		Description description = vevent.getDescription();
		if (description != null && StringUtils.isNotBlank(description.getValue()))
		{
			event.description = description.getValue();
		}
		Url url = vevent.getUrl();
		if (url != null) {
			try {
				event.url = new URL(url.getValue());
			} catch (MalformedURLException e) {
				LOG.info("Invalid URL, skipping: " + url.getValue());
			}
		}

		// See if this event is a duplicate of an existing event
		if (event.duplicate_of == null) {
			event.duplicate_of = findParent(event);
		}

		saveOrUpdateEvent(event);
	}

	private Event findParent(Event event) {
		String summary = event.summary;
		if (summary.contains(" + more")) {
			summary.replace(" + more", "");
		}
		if (summary.contains(" at ")) {
			summary = summary.substring(0, summary.indexOf(" at "));
		}
		if (summary.contains(" @ ")) {
			summary = summary.substring(0, summary.indexOf(" @ "));
		}

		// we want to do some smarter query'ing here.... :/
		SessionImpl impl = (SessionImpl) em.getDelegate();

		String charsToIgnore = ",- \t\n/";
		org.hibernate.Query query = impl
				.createQuery("from Event where feed.prio > :prio and upper(replace("
						+ ignoreMultiple("summary", charsToIgnore)
						+ ", '&', 'and')) like upper(replace("
						+ ignoreMultiple(":summary", charsToIgnore)
						+ ", '&', 'and')) and day(cast(start as date)) = day(cast(:start as date)) and month(cast(start as date)) = month(cast(:start as date)) and year(cast(start as date)) = year(cast(:start as date))");

		query.setParameter("prio", event.feed.getPrio());
		query.setParameter("summary", "%" + summary + "%");
		query.setParameter("start", event.getStart());

		@SuppressWarnings("unchecked")
		List<Event> resultList = query.list();// getResults(query);
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

	private String ignoreMultiple(String original, String charsToIgnore) {
		String result = original;
		for (int i = 0; i < charsToIgnore.length(); i++) {
			result = "replace(" + result + ", '" + charsToIgnore.charAt(i)
					+ "', '')";
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getResults(Query query) {
		return query.getResultList();
	}

	private Event findPreviousVersion(Feed feed, VEvent event) {
		String uid = event.getUid().getValue();

		Event previous = findPreviousVersionByUid(feed, uid);
		if (previous != null) {
			return previous;
		}

		previous = findPreviousVersionByStartAndSummary(feed, event
				.getStartDate(), event.getSummary());

		return previous;
	}

	private Event findPreviousVersionByStartAndSummary(Feed feed,
			DtStart startDate, Summary summary) {
		if (startDate == null || summary == null) {
			return null;
		}

		Query query = em
				.createQuery("select e from Event e where feed = :feed and summary = :summary and start = :start");
		query.setParameter("feed", feed);
		query.setParameter("summary", summary.getValue());
		query.setParameter("start", startDate.getDate());

		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();
		if (results.isEmpty() || results.size() > 1) {
			return null;
		} else {
			return results.get(0);
		}
	}

	private Event findPreviousVersionByUid(Feed feed, String uid) {
		if (StringUtils.isBlank(uid)) {
			return null;
		}
		Query query = em
				.createQuery("select e from Event e where feed = :feed and uid = :uid");
		query.setParameter("feed", feed);
		query.setParameter("uid", uid);

		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();
		if (results.isEmpty()) {
			return null;
		} else {
			if (results.size() > 1) {
				LOG.error("Multiple events with uid " + uid);
			}
			return results.get(0);
		}
	}

	private Calendar getCalendar(@Nonnull Feed feed) throws IOException, ParserException {
		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, true);
		if (feed.getUrl() == null)
		{
			throw new IllegalStateException("Cannot get calendar for manual feed");
		}
		
		CalendarBuilder builder = new CalendarBuilder();

		LOG.info("Fetching " + feed.getUrl() + " and building calendar");

		// TODO FIXME we should look at the header to find out the charset...
		
		Calendar calendar = builder.build(feed.getUrl().openStream());
		LOG.info("Finished building calendar for " + feed.getUrl());
		return calendar;
	}

	@Override
	public List<Event> getEventsForDay(List<Feed> selectedFeeds, Date date, int maxRecurrence)
	{
		return getEvents(selectedFeeds, new EventFilter(date, maxRecurrence));
	}
	
	@Override
	public List<Event> getEvents(Feed feed, boolean resolveDuplicates,
			boolean onlyUpcoming, Integer maxRecurrence) {
		EventFilter filter = new EventFilter(maxRecurrence);
		filter.resolveDuplicates = resolveDuplicates;
		if (onlyUpcoming)
		{
			filter.fromDate = new Date();
		}
		else
		{
			filter.fromDate = null;
		}
		return getEvents(feed, filter);
	}

	public List<Event> getEvents(Feed feed, EventFilter filter) {
		String queryString = "select e from Event e";
		queryString += " where feed = :feed ";
		if (filter.noHidden)
		{
			queryString += " and (hidden is null or hidden = false) ";
		}
		if (filter.fromDate != null) {
			queryString += " and (year(start) > year(cast(:fromDate as date)) or (year(start) = year(cast(:fromDate as date)) and (month(start) > month(cast(:fromDate as date)) or " +
					" (month(start) = month(cast(:fromDate as date)) and day(start) >= day(cast(:fromDate as date))))))";
		}
		if (filter.toDate != null) {
			queryString += " and (year(start) < year(cast(:toDate as date)) or   (year(start) = year(cast(:toDate as date))   and (month(start) < month(cast(:toDate as date)) or " +
					" (month(start) = month(cast(:toDate as date)) and day(start) <= day(cast(:toDate as date))))))";
		}
		if (filter.maxRecurrence != null)
		{
			queryString += " and (aantalHerhalingen is null or aantalHerhalingen < :maxRecurrence) ";
		}

		Query query = em.createQuery(queryString);
		query.setHint("org.hibernate.cacheable", true);
		query.setParameter("feed", feed);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		try
		{
			if (filter.fromDate != null)
			{
				query.setParameter("fromDate", format.parse(format.format(filter.fromDate)), TemporalType.DATE);
			}
			if (filter.toDate != null)
			{
				query.setParameter("toDate", format.parse(format.format(filter.toDate)), TemporalType.DATE);
			}
			if (filter.maxRecurrence != null)
			{
				query.setParameter("maxRecurrence", filter.maxRecurrence);
			}
		}
		catch (ParseException e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
		
		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();

		if (filter.resolveDuplicates) {
			for (Event event : new ArrayList<Event>(results)) {
				if (event.duplicate_of != null) {
					Event master = getMaster(event, filter.noHidden, new HashSet<Long>());
					if (master != null && master != event)
					{
						results.remove(event);
						if (!results.contains(master)) {
							results.add(master);
						}
					}
				}
			}
		}

		return results;

	}

	/**
	 * @param event the child event
	 * @param noHidden skip non-verified parents
	 * @param seen 
	 * @return the event itself or its top master. null if onlyVerified is true, the event is not verified and 
	 * does not have any verified parents.
	 */
	private Event getMaster(@Nonnull Event event, boolean noHidden, HashSet<Long> seen) {
		if (seen.contains(event.getId()))
		{
			LOG.error("Event " + event.getId() + " is part of a loop");
			return event;
		}
		if (event.duplicate_of != null) {
			seen.add(event.getId());
			Event masterCandidate = getMaster(event.duplicate_of, noHidden, seen);
			if (masterCandidate != null)
			{
				return masterCandidate;
			}
		}
		if (!noHidden || !event.getHidden())
		{
			return event;
		}
		else
		{
			return null;
		}		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void markDuplicate(Event master, Event duplicate) {
		duplicate.duplicate_of = master;

		duplicate = em.merge(duplicate);
		em.persist(duplicate);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void clear(@Nonnull Feed feed) {
		unlinkEvents(feed);

		Query query = em.createQuery("delete from Event where feed = :feed");
		query.setParameter("feed", feed);
		query.executeUpdate();
	}

	/**
	 * Unlink events from events in the given feed, so that the given feed can
	 * be cleared.
	 * 
	 * @param feed
	 */
	private void unlinkEvents(Feed feed) {
		Query query = em
				.createQuery("select e from Event e where duplicate_of.feed = :feed");
		query.setParameter("feed", feed);

		@SuppressWarnings("unchecked")
		List<Event> events = query.getResultList();
		for (Event event : events) {
			if (event.duplicate_of.duplicate_of != null
					&& event.duplicate_of.duplicate_of.feed != feed) {
				event.duplicate_of = event.duplicate_of.duplicate_of;
			} else {
				event.duplicate_of = null;
			}
			em.persist(event);
		}
	}

	@Override
	public List<Feed> getManualFeeds() {
		@SuppressWarnings("unchecked")
		List<Feed> resultList = em.createQuery(
				"select f from Feed f where url is null").getResultList();
		return resultList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void delete(Feed feed) {
		clear(feed);
		feed = em.merge(feed);
		em.remove(feed);
		em.flush();
	}

	@Override
	public List<Feed> find(String name) {
		Query query = em
				.createQuery("select f from Feed f where name like :name");
		query.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<Feed> resultList = query.getResultList();
		return resultList;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reloadFeeds() {
		Query query = em
				.createQuery("select f from Feed f where url is not null order by prio desc");

		List<Feed> resultList = getResults(query);
		for (Feed feed : resultList) {
			try {
				reloadFeed(feed);
			} catch (Exception e) {
				LOG.error("Error reloading feed: " + e.getMessage(), e);
				// ... and continue to next feed.
			}
		}
		LOG.info("Done reloading all feeds");
	}

	@Override
	public List<Event> getAlternatives(Event event) {
		List<Event> result = new ArrayList<Event>();
		
		getAlternativesRecursive(event, result);
		
		return result;
	}

	private void getAlternativesRecursive(Event parent,
			List<Event> results) {
		Query query = em.createQuery("select e from Event e where duplicate_of = :event");
		query.setHint("org.hibernate.cacheable", true);
		
		query.setParameter("event", parent);
		List<Event> resultList = getResults(query);

		for (Event event : resultList)
		{
			if (!results.contains(event))
			{
				results.add(event);
				getAlternativesRecursive(event, results);
			}
		}
	}

	@Override
	public <T> T load(Class<T> clazz, Serializable id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getDuplicateCandidates(Event event)
	{
		Query query = em.createQuery("select e from Event e where year(start) = year(:start) and month(start) = month(:start) and day(start) = day(:start) and e != :e");
		query.setParameter("start", event.getStart());
		query.setParameter("e", event);
		return query.getResultList();
	}

	@Override
	public List<Event> getEvents(List<Feed> selectedFeeds, Integer maxRecurrence)
	{
		return getEvents(selectedFeeds, new EventFilter(maxRecurrence));
	}
	
	private List<Event> getEvents(List<Feed> selectedFeeds, EventFilter filter)
	{
		Set<Event> events = new HashSet<Event>();
		for (Feed feed : selectedFeeds)
		{
			events.addAll(getEvents(feed, filter));
		}
		List<Event> result = new ArrayList<Event>(events);
		Collections.sort(result, eventComparator);
		
		return result;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void delete(Event event)
	{
		event = em.merge(event);
		em.remove(event);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getEventsToVerify()
	{
		return em.createQuery("select e from Event e where hidden = true").getResultList();
	}

	@Override
	public Feed getFeedByShortName(String shortName)
	{
		Query query = em.createQuery("select f from Feed f where upper(shortName) = :shortName");
		query.setParameter("shortName", shortName.toUpperCase());
		
		@SuppressWarnings("unchecked")
		List<Feed> results = query.getResultList();
		
		if (results.isEmpty())
		{
			return null;
		}
		else
		{
			return results.get(0);
		}
	}

	@Override
	public List<Feed> getSelectedFeeds(List<String> shortNames)
	{
		List<Feed> selectedFeeds = new ArrayList<Feed>();
		if (shortNames.isEmpty())
		{
			selectedFeeds = getDefaultFeeds();
		}
		else
		{
			selectedFeeds = new ArrayList<Feed>();
			for (String shortName : shortNames)
			{
				selectedFeeds.add(getFeedByShortName(shortName));
			}
		}	
		return selectedFeeds;
	}

}
