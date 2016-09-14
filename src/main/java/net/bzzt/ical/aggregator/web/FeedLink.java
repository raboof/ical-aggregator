package net.bzzt.ical.aggregator.web;

import java.util.ArrayList;
import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.Panel;


public class FeedLink extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FeedLink(String id, Feed feed) {
		super(id);
	
		add(new ExternalLink("feed", feed.link.toString(), feed.shortName));
	}

}
