package net.bzzt.ical.aggregator.web.admin;

import java.net.URL;

import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.HomePage;
import net.bzzt.ical.aggregator.web.model.Identifiable;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;

public class EventDetailPage extends AggregatorLayoutPage {
	@SpringBean
	private FeedService feedService;
	
	public class EventForm extends Form<Event> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EventForm(String id, Event event) {
			super(id, new CompoundPropertyModel<Event>(new JpaEntityModel<Identifiable<Long>>(event)));
			
			add(new TextField<String>("summary").setRequired(true));
			add(new DateField("start").setRequired(true));
			add(new DropDownChoice<Feed>("feed", feedService.getManualFeeds()).setRequired(true));
			add(new TextField<URL>("url"));
		}

		@Override
		protected void onSubmit() {
			feedService.saveOrUpdateEvent(getModelObject());
			setResponsePage(HomePage.class);
		}
	}

	public EventDetailPage()
	{
		this(new Event());
	}

	public EventDetailPage(Event event) {
		add(new EventForm("form", event));
	}
}
