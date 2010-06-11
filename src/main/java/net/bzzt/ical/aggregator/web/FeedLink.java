package net.bzzt.ical.aggregator.web;

import java.util.ArrayList;
import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;


public class FeedLink extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FeedLink(String id, Feed feed) {
		super(id);
	
		Link<Feed> link = new Link<Feed>("feed", new JpaEntityModel<Feed>(feed))
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				List<Feed> feedList = new ArrayList<Feed>();
				feedList.add(getModelObject());
				((AggregatorSession) Session.get()).setSelectedFeeds(feedList);
				setResponsePage(EventListPage.class);
			}
			
		};
		link.add(new Label("shortName", feed.shortName));
		add(link);
	}

}
