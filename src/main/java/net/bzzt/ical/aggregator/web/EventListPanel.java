package net.bzzt.ical.aggregator.web;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.admin.EventDetailPage;
import net.bzzt.ical.aggregator.web.model.CategorizedList;
import net.bzzt.ical.aggregator.web.model.EventsCategorizer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;

public class EventListPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@SpringBean
	private FeedService feedService;
	
	public EventListPanel(String id) {
		super(id);
		
		List<Event> eventsSorted = feedService.getEvents(((AggregatorSession)getSession()).getSelectedFeeds());
		
		add(new CategorizedList<Date, Event>("dates", eventsSorted, new EventsCategorizer()){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void populateCaption(ListItem<Date> item) {
				item.add(new Label("date", item.getModel())
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.apache.wicket.Component#getConverter(java.lang.Class)
					 */
					@Override
					public IConverter getConverter(Class<?> type) {
						return new IConverter() {
							
							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public String convertToString(Object value, Locale locale) {
								SimpleDateFormat dateConverter = new SimpleDateFormat("EEEEE dd-MM-yyyy", locale);
								return dateConverter.format(value);
							}
							
							@Override
							public Object convertToObject(String value, Locale locale) {
								// TODO Auto-generated method stub
								return null;
							}
						};
					}
					
				});
			}

			@Override
			protected void populateChild(ListItem<Event> item) {
				item.add(new EventPanel("event", item.getModel()));
			}
			
		});
		
//		add(new PropertyListView<Event>("event", eventsSorted){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void populateItem(ListItem<Event> item) {
//				item.add(new Label("start"));
//				item.add(new Label("summary"));
//			}});
	}

}
