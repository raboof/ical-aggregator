package net.bzzt.ical.aggregator.web;

import java.util.Locale;

import net.bzzt.ical.aggregator.model.Right;
import net.bzzt.ical.aggregator.service.UserService;
import net.bzzt.ical.aggregator.web.admin.EventDetailPage;
import net.bzzt.ical.aggregator.web.admin.ManageFeeds;

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
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class AggregatorLayoutPage extends WebPage implements IHeaderContributor
{
	@SpringBean
	private UserService userService;

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
		String title = System.getenv("title");
		if (title == null)
		{
			title = "ICalendar Aggregator";
		}
		add(new Label("title", title));
		add(new Label("header", title));
		
		add(new BookmarkablePageLink<Void>("home", HomePage.class).setEnabled(!(this instanceof HomePage)));

		add(new BookmarkablePageLink<Void>("manageFeeds", ManageFeeds.class));

		add(new BookmarkablePageLink<Void>("day", DayView.class).setEnabled(!(this instanceof DayView)));
		add(new BookmarkablePageLink<Void>("weekView", WeekView.class).setEnabled(!(this instanceof WeekView)));

		add(new BookmarkablePageLink<Void>("addEvent", EventDetailPage.class));

		add(new BookmarkablePageLink<Void>("verifyEvents", EventVerificationPage.class).setVisible(userService
			.hasRight(AggregatorSession.get().getLoggedInUser(), Right.VERIFY_EVENTS)));

		add(new LocaleLink("toDutch", new Locale("nl", "NL")));

		add(new LocaleLink("toEnglish", Locale.ENGLISH));

		add(new LoginPanel("login"));

		add(new FeedbackPanel("feedback"));

		add(new FeedSelection("feedSelection", this));
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		response.renderCSSReference(new ResourceReference(AggregatorLayoutPage.class, "style.css"));
		response.renderJavascriptReference(new ResourceReference(AggregatorLayoutPage.class, "jquery-1.4.2.min.js"), "JQUERY");
		response.renderJavascriptReference(new ResourceReference(AggregatorLayoutPage.class, "jquery.layout.min-1.2.0.js"), "JQUERY_LAYOUT");
		response.renderOnDomReadyJavascript("$('body').layout({ applyDefaultStyles: true });");
	}

	public AggregatorSession getSession()
	{
		return (AggregatorSession) Session.get();
	}

	public void refresh(AjaxRequestTarget target)
	{
	}
	
	
}
