package net.bzzt.ical.aggregator.web;

import java.util.Locale;

import net.bzzt.ical.aggregator.model.Feed;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

public abstract class AggregatorLayoutPage extends WebPage implements IHeaderContributor
{

	public class LocaleLink extends Link<Locale>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LocaleLink(String id, Locale locale)
		{
			super(id, new Model<Locale>(locale));
		}

		@Override
		public void onClick()
		{
			Session.get().setLocale(getModelObject());
		}

	}

	public AggregatorLayoutPage()
	{
		String title = WicketApplication.getTitle();
		add(new Label("title", title));
		add(new Label("header", title));

		
		add(new BookmarkablePageLink<Void>("home", HomePage.class).setEnabled(!(this instanceof HomePage)));
		add(new BookmarkablePageLink<Void>("list", EventListPage.class).setEnabled(!(this instanceof EventListPage)));

		add(new BookmarkablePageLink<Void>("day", DayView.class).setEnabled(!(this instanceof DayView)));
		add(new BookmarkablePageLink<Void>("weekView", WeekView.class).setEnabled(!(this instanceof WeekView)));

		add(new LocaleLink("toDutch", new Locale("nl", "NL")));

		add(new LocaleLink("toEnglish", Locale.ENGLISH));

		add(new FeedbackPanel("feedback"));
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		response.renderCSSReference(new ResourceReference(AggregatorLayoutPage.class, "style.css"));
		response.renderJavascriptReference(new ResourceReference(AggregatorLayoutPage.class, "jquery-1.4.2.min.js"),
			"JQUERY");
		response.renderJavascriptReference(new ResourceReference(AggregatorLayoutPage.class,
			"jquery.layout.min-1.2.0.js"), "JQUERY_LAYOUT");
		// response.renderOnDomReadyJavascript("$('body').layout({ applyDefaultStyles: true });");
		response.renderJavascriptReference(new ResourceReference(AggregatorLayoutPage.class,
			"ajax/uniqueurls/wicket.ajax.uniqueurls.js"), "UNIQUEURLS");
		response.renderOnLoadJavascript("initialiseStateFromURL()");
		
		addAlternateLink(response, WicketApplication.getTitle() + " Upcoming Events Feed", "");
		for (Feed feed : AggregatorSession.get().getSelectedFeeds())
		{
			addAlternateLink(response, feed.name + " at " + WicketApplication.getTitle() + " Upcoming Events Feed", "?sn=" + feed.shortName);
		}
		response.renderString("<link rel=\"outline\" type=\"text/x-opml\" title=\""
			+ WicketApplication.getTitle() + " Source Feeds\" href=\"" + WicketApplication.getLink()
			+ "/feeds/opml\"/>");
		
	}
	
	private void addAlternateLink(IHeaderResponse response, String title, String postfix)
	{
		CharSequence fmtTitle = Strings.escapeMarkup(title);
		
		response.renderString("<link rel=\"alternate\" type=\"application/rss+xml\" title=\""
			+ fmtTitle + "\" href=\"" + WicketApplication.getLink()
			+ "/feeds/upcoming/rss" + postfix + "\"/>");
		response.renderString("<link rel=\"alternate\" type=\"text/calendar\" title=\""
			+ fmtTitle + "\" href=\"" + WicketApplication.getLink()
			+ "/feeds/upcoming/ical" + postfix + "\"/>");
	}


	public AggregatorSession getSession()
	{
		return (AggregatorSession) Session.get();
	}

	public void refresh(AjaxRequestTarget target)
	{
	}

}
