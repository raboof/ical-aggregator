package net.bzzt.ical.aggregator.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.impl.SessionImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

@Transactional(propagation=Propagation.REQUIRED)
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
		@SuppressWarnings("unchecked")
		List<Feed> resultList = em.createQuery("select f from Feed f")
				.getResultList();
		return resultList;
	}
	
	@Override
	public List<Feed> getDefaultFeeds() {
		@SuppressWarnings("unchecked")
		List<Feed> resultList = em.createQuery("select f from Feed f where showByDefault = true")
				.getResultList();
		return resultList;
	}

	public void reloadFeed(@Nonnull Feed feed) throws IOException, ParserException {
		Calendar calendar = getCalendar(feed);

		@SuppressWarnings("unchecked")
		Collection<VEvent> components = (Collection<VEvent>) calendar
				.getComponents(VEvent.VEVENT);
		LOG.info(components.size() + " events found");
		for (VEvent event : components) {
			Event previousVersion = findPreviousVersion(feed, event);
			if (previousVersion == null) {
				addEvent(feed, event, false);
			} else {
				updateEvent(previousVersion, event);
			}
		}
		LOG.info("Done reloading " + feed.url);
		
	}

	private void updateEvent(Event previousVersion, VEvent event) {
		updateFields(previousVersion, event);
	}

	private void addEvent(Feed feed, VEvent vevent, Boolean hidden) {
		Event event = new Event(feed, hidden);

		updateFields(event, vevent);
	}

	@Override
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
		event.setHidden(false);
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
						+ ", '&', 'and')) and day(start) = day(:start) and month(start) = month(:start) and year(start) = year(:start)");

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
	public List<Event> getEvents(Feed feed, boolean resolveDuplicates,
			boolean onlyUpcoming) {
		return getEvents(feed, resolveDuplicates, onlyUpcoming, true);
	}
	@Override
	public List<Event> getEvents(Feed feed, boolean resolveDuplicates,
			boolean onlyUpcoming, boolean noHidden) {
		String queryString = "select e from Event e where feed = :feed ";
		if (noHidden)
		{
			queryString += " and (hidden is null or hidden = false) ";
		}
		if (onlyUpcoming) {
			queryString += " and (year(start) > year(now) or (year(start) = year(now) and (month(start) > month(now) or " +
					" (month(start) = month(now) and day(start) >= day(now)))))";
		}
		Query query = em.createQuery(queryString);
		query.setParameter("feed", feed);

		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();

		if (resolveDuplicates) {
			for (Event event : new ArrayList<Event>(results)) {
				if (event.duplicate_of != null) {
					Event master = getMaster(event, noHidden, new HashSet<Long>());
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
	public void markDuplicate(Event master, Event duplicate) {
		duplicate.duplicate_of = master;

		duplicate = em.merge(duplicate);
		em.persist(duplicate);
	}

	@Override
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
	public void delete(Feed feed) {
		clear(feed);
		feed = em.merge(feed);
		em.remove(feed);
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
		Query query = em
		.createQuery("select e from Event e where duplicate_of = :event");
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
	public List<Event> getEvents(List<Feed> selectedFeeds)
	{
		Set<Event> events = new HashSet<Event>();
		for (Feed feed : selectedFeeds)
		{
			events.addAll(getEvents(feed, true, true, true));
		}
		List<Event> result = new ArrayList<Event>(events);
		Collections.sort(result, eventComparator);
		
		return result;
	}

	@Override
	public void delete(Event event)
	{
		event = em.merge(event);
		em.remove(event);
	}

	@Override
	public List<Event> getEventsToVerify()
	{
		return em.createQuery("select e from Event e where hidden = true").getResultList();
	}
}
