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

	private static final Comparator<Event> eventComparator = new Comparator<Event>(){

		@Override
		public int compare(Event o1, Event o2) {
			return o1.getStart().compareTo(o2.getStart());
		}};

	@SpringBean
	private FeedService feedService;
	
	public EventListPanel(String id) {
		super(id);
		
		Set<Event> events = new HashSet<Event>();
		for (Feed feed : ((AggregatorSession)getSession()).getSelectedFeeds())
		{
			events.addAll(feedService.getEvents(feed, true, true));
		}
		
		List<Event> eventsSorted = new ArrayList<Event>(events);
		Collections.sort(eventsSorted, eventComparator);
		
		add(new CategorizedList<Date, Event>("dates", eventsSorted, new EventsCategorizer()){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateCaption(ListItem<Date> item) {
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
				item.add(new FeedLink("feedLink", item.getModelObject().feed));

				WebMarkupContainer link = new WebMarkupContainer("link");
				if (item.getModelObject().url != null)
				{
					link.add(new AttributeModifier("href", true, new Model<URL>(item.getModelObject().url)));
				}
				else
				{
					link.setRenderBodyOnly(true);
				}
				link.add(new Label("summary"));
				item.add(link);
				item.add(new Link<Event>("detailLink", item.getModel()){

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new EventDetailPage(getModelObject()));
					}
					
				});
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
