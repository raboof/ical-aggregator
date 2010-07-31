package net.bzzt.ical.aggregator.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.model.User;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class AggregatorSession extends WebSession {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private FeedService feedService;
	
	private User loggedInUser;
	
	private List<Feed> selectedFeeds;
	
	private Integer maxRecurrence = 5;
	
	public AggregatorSession(Request request) {
		super(request);
		
		// Default locale. TODO: make this configurable.
		setLocale(new Locale("nl", "NL"));
		
		InjectorHolder.getInjector().inject(this);
		
		selectedFeeds = feedService.getDefaultFeeds();
	}

	public List<Feed> getSelectedFeeds() {
		return selectedFeeds;
	}

	public void setSelectedFeeds(List<Feed> selectedFeeds) {
		this.selectedFeeds = selectedFeeds;
	}
	
	public static AggregatorSession get()
	{
		return (AggregatorSession) Session.get();
	}

	public boolean ingelogd()
	{
		return loggedInUser != null;
	}

	public User getLoggedInUser()
	{
		return loggedInUser;
	}

	public void login(User user)
	{
		loggedInUser = user;
	}

	public void removeFromFeedSelection(Feed feed)
	{
		for (Feed selectedFeed : new ArrayList<Feed>(selectedFeeds))
		{
			if (feed.id.equals(selectedFeed.id))
			{
				selectedFeeds.remove(selectedFeed);
			}
		}
	}

	public Integer getMaxRecurrence()
	{
		return maxRecurrence;
	}

	public void setMaxRecurrence(Integer maxRecurrence)
	{
		this.maxRecurrence = maxRecurrence; 
	}
}
