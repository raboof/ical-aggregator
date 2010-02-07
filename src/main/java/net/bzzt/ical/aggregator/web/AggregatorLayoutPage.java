package net.bzzt.ical.aggregator.web;

import net.bzzt.ical.aggregator.web.admin.EventDetailPage;
import net.bzzt.ical.aggregator.web.admin.ManageFeeds;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public abstract class AggregatorLayoutPage extends WebPage implements IHeaderContributor {

	public AggregatorLayoutPage()
	{
        add(new BookmarkablePageLink<Object>("home", HomePage.class));
        
        add(new BookmarkablePageLink<Object>("manageFeeds", ManageFeeds.class));

        add(new BookmarkablePageLink<Object>("addEvent", EventDetailPage.class));

	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		response.renderCSSReference(new ResourceReference(AggregatorLayoutPage.class, "style.css"));
	}

	public AggregatorSession getSession()
	{
		return (AggregatorSession) Session.get();
	}
}
