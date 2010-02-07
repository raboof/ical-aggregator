package net.bzzt.ical.aggregator.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;

public class FeedServiceImpl implements FeedService {
	private static final Log LOG = LogFactory.getLog(FeedServiceImpl.class);
	
	public static EntityManager em;

	private EntityManagerFactory emf;
	
	public FeedServiceImpl() {
		if (em == null) {
			 emf = Persistence
					.createEntityManagerFactory("aggregatorPersistenceUnit");
			FeedServiceImpl.em = emf.createEntityManager();
		}
	}

	public void saveOrUpdate(Feed feed) {
		em.getTransaction().begin();
		em.persist(feed);
		em.getTransaction().commit();
	}

	public List<Feed> getFeeds() {
		@SuppressWarnings("unchecked")
		List<Feed> resultList = em.createQuery("select f from Feed f").getResultList();
		return resultList;
	}

	public void reloadFeed(Feed feed) throws IOException, ParserException {
		Calendar calendar = getCalendar(feed);

		@SuppressWarnings("unchecked")
		Collection<VEvent> components = (Collection<VEvent>) calendar
				.getComponents(VEvent.VEVENT);
		for (VEvent event : components) {
			Event previousVersion = findPreviousVersion(feed, event);
			if (previousVersion == null) {
				addEvent(feed, event);
			} else {
				updateEvent(previousVersion, event);
			}
		}
	}

	private void updateEvent(Event previousVersion, VEvent event) {
		updateFields(previousVersion, event);
	}

	private void addEvent(Feed feed, VEvent vevent) {
		Event event = new Event();
		event.feed = feed;
			
		updateFields(event, vevent);
	}

	@Override
	public void saveOrUpdateEvent(Event event) {
		em.getTransaction().begin();
		em.persist(event);
		em.getTransaction().commit();
	}

	private void updateFields(Event event, VEvent vevent) {
		event.setStart(vevent.getStartDate().getDate());
		DtEnd endDate = vevent.getEndDate();
		if (endDate != null)
		{
			event.setEnding(endDate.getDate());
		}
		event.rawEvent = vevent.toString();
		event.uid = vevent.getUid().getValue();
		event.summary = vevent.getSummary().getValue();
		Url url = vevent.getUrl();
		if (url != null)
		{
			try {
				event.url = new URL(url.getValue());
			} catch (MalformedURLException e) {
				LOG.info("Invalid URL, skipping: " + url.getValue());
			}
		}
		
		// See if this event is a duplicate of an existing event
		if (event.duplicate_of == null)
		{
			event.duplicate_of = findParent(event);
		}
		
		saveOrUpdateEvent(event);
	}

	private Event findParent(Event event) {
		String summary = event.summary;
		if (summary.contains(" + more"))
		{
			summary.replace(" + more", "");
		}
		if (summary.contains(" at "))
		{
			summary = summary.substring(0, summary.indexOf(" at "));
		}
		if (summary.contains(" @ "))
		{
			summary = summary.substring(0, summary.indexOf(" @ "));
		}
		Query query = em.createQuery("select e from Event e where feed.prio > :prio and upper(summary) like upper(:summary) and day(start) = day(:start) and month(start) = month(:start) and year(start) = year(:start)");
		query.setParameter("prio", event.feed.getPrio());
		query.setParameter("summary", "%" + summary + "%");
		query.setParameter("start", event.getStart());
		List<Event> resultList = query.getResultList();
		if (!resultList.isEmpty())
		{
			return resultList.get(0);
		}
		return null;
	}

	private Event findPreviousVersion(Feed feed, VEvent event) {
		String uid = event.getUid().getValue();
		
		Event previous = findPreviousVersionByUid(feed, uid);
		if (previous != null)
		{
			return previous;
		}

		previous = findPreviousVersionByStartAndSummary(feed, event.getStartDate(), event.getSummary());
		
		return previous;
	}

	private Event findPreviousVersionByStartAndSummary(Feed feed,
			DtStart startDate, Summary summary) {
		if (startDate == null || summary == null)
		{
			return null;
		}
		
		Query query = em.createQuery("select e from Event e where feed = :feed and summary = :summary and start = :start");
		query.setParameter("feed", feed);
		query.setParameter("summary", summary.getValue());
		query.setParameter("start", startDate.getDate());
		
		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();
		if (results.isEmpty() || results.size() > 1)
		{
			return null;
		}
		else
		{
			return results.get(0);
		}
	}

	private Event findPreviousVersionByUid(Feed feed, String uid) {
		if (StringUtils.isBlank(uid))
		{
			return null;
		}
		Query query = em.createQuery("select e from Event e where feed = :feed and uid = :uid");
		query.setParameter("feed", feed);
		query.setParameter("uid", uid);
		
		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();
		if (results.isEmpty())
		{
			return null;
		}
		else
		{
			if (results.size() > 1)
			{
				LOG.error("Multiple events with uid " + uid);
			}
			return results.get(0);
		}
	}

	private Calendar getCalendar(Feed feed) throws IOException, ParserException {
		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, true);

		CalendarBuilder builder = new CalendarBuilder();

		// TODO FIXME we should look at the header to find out the charset...
		return builder.build(feed.getUrl().openStream());
	}

	@Override
	public List<Event> getEvents(Feed feed, boolean resolveDuplicates, boolean alleenUpcoming) {
		String queryString = "select e from Event e where feed = :feed";
		if (alleenUpcoming)
		{
			queryString += " and start >= now";
		}
		Query query = em.createQuery(queryString);
		query.setParameter("feed", feed);
		
		@SuppressWarnings("unchecked")
		List<Event> results = query.getResultList();
		
		if (resolveDuplicates)
		{
			for(Event event : new ArrayList<Event>(results))
			{
				if (event.duplicate_of != null)
				{
					results.remove(event);
					Event master = getMaster(event);
					if (!results.contains(master))
					{
						results.add(master);
					}
				}
			}
		}
		
		return results;

	}

	private Event getMaster(@Nonnull Event event) {
		if (event.duplicate_of != null)
		{
			return getMaster(event.duplicate_of);
		}
		else
		{
			return event;
		}
	}

	@Override
	public void markDuplicate(Event master, Event duplicate) {
		duplicate.duplicate_of = master;
		
		em.getTransaction().begin();
		em.persist(duplicate);
		em.getTransaction().commit();
	}

	@Override
	public void clear(@Nonnull Feed feed) {
		em.getTransaction().begin();
		unlinkEvents(feed);
		
		Query query = em.createQuery("delete from Event where feed = :feed");
		query.setParameter("feed", feed);
		query.executeUpdate();
		em.getTransaction().commit();
	}

	/**
	 * Unlink events from events in the given feed, so that the given feed can be cleared.
	 * 
	 * @param feed
	 */
	private void unlinkEvents(Feed feed) {
		Query query = em.createQuery("select e from Event e where duplicate_of.feed = :feed");
		query.setParameter("feed", feed);
	
		@SuppressWarnings("unchecked")
		List<Event> events = query.getResultList();
		for (Event event : events)
		{
			if (event.duplicate_of.duplicate_of != null && event.duplicate_of.duplicate_of.feed != feed)
			{
				event.duplicate_of = event.duplicate_of.duplicate_of;
			}
			else
			{
				event.duplicate_of = null;
			}
			em.persist(event);
		}
	}

	@Override
	public List<Feed> getManualFeeds() {
		@SuppressWarnings("unchecked")
		List<Feed> resultList = em.createQuery("select f from Feed f where url is null").getResultList();
		return resultList;
	}

	@Override
	public void delete(Feed feed) {
		clear(feed);
		em.getTransaction().begin();
		em.remove(feed);
		em.getTransaction().commit();
	}

	@Override
	public List<Feed> find(String name) {
		Query query = em.createQuery("select f from Feed f where name like :name");
		query.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<Feed> resultList = query.getResultList();
		return resultList;
	}
}
