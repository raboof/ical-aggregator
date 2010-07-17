package net.bzzt.ical.aggregator.web.admin;

import java.net.URL;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.model.Right;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.service.UserService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.AggregatorSession;
import net.bzzt.ical.aggregator.web.EventListPage;
import net.bzzt.ical.aggregator.web.WicketApplication;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;

import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class EventDetailPage extends AggregatorLayoutPage {
	@SpringBean
	private FeedService feedService;
	
	@SpringBean
	private UserService userService;
	
	public class EventForm extends Form<Event> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private JpaEntityModel<Event> originalModel;
		
		public EventForm(String id, Event event, Event original) {
			super(id, new CompoundPropertyModel<Event>(new JpaEntityModel<Event>(event)));
			
			if (!event.getManual())
			{
				throw new IllegalArgumentException();
			}
			
			this.originalModel = new JpaEntityModel<Event>(original);
			
			add(new TextField<String>("summary").setRequired(true));
			add(new DateField("start").setRequired(true));
			add(new DropDownChoice<Feed>("feed", feedService.getManualFeeds()).setRequired(true).setVisible(event.feed == null || event.feed.url == null));
			add(new TextField<URL>("url"));
			
			add(new FeedbackPanel("feedback"));
		}

		@Override
		protected void onSubmit() {
			Event event = getModelObject();
			event.setHidden(!userService.hasRight(AggregatorSession.get().getLoggedInUser(), Right.ADD_EVENT_DIRECT));
			feedService.saveOrUpdateEvent(event);
			Event original = originalModel.getObject();
			if (original != event)
			{
				original.duplicate_of = event;
				feedService.saveOrUpdateEvent(original);
			}
			setResponsePage(WicketApplication.get().getHomePage());
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onDetach()
		 */
		@Override
		protected void onDetach() {
			super.onDetach();
			originalModel.detach();
		}
		
		
	}

	public EventDetailPage()
	{
		this(new Event(true));
	}

	public EventDetailPage(Event original) {
		
		if (original.duplicate_of != null)
		{
			throw new IllegalArgumentException();
		}
		
		Event event;
		if (original.getManual() == true)
		{
			event = original;
		}
		else
		{
			event = (Event) original.clone();
		}
		add(new EventForm("form", event, original));
	}
}
