package net.bzzt.ical.aggregator.web;

import java.util.Locale;

import net.bzzt.ical.aggregator.web.admin.EventDetailPage;
import net.bzzt.ical.aggregator.web.admin.ManageFeeds;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

public abstract class AggregatorLayoutPage extends WebPage implements IHeaderContributor {

	public class LocaleLink extends Link<Locale> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LocaleLink(String id, Locale locale) {
			super(id, new Model<Locale>(locale));
		}

		@Override
		public void onClick() {
			Session.get().setLocale(getModelObject());
		}

	}

	public AggregatorLayoutPage()
	{
        add(new BookmarkablePageLink<Object>("home", HomePage.class));
        
        add(new BookmarkablePageLink<Object>("manageFeeds", ManageFeeds.class));

        add(new BookmarkablePageLink<Object>("addEvent", EventDetailPage.class));

        add(new LocaleLink("toDutch", new Locale("nl", "NL")));
        
        add(new LocaleLink("toEnglish", Locale.ENGLISH));
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
