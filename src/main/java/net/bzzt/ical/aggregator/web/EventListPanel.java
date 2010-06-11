package net.bzzt.ical.aggregator.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.util.Categorizer;
import net.bzzt.ical.aggregator.util.CategoryHelper;
import net.bzzt.ical.aggregator.web.model.CategorizedList;
import net.bzzt.ical.aggregator.web.model.EventsCategorizer;

import org.apache.commons.collections.MultiMap;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;

public class EventListPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class EventPerFeedCategorizer implements Categorizer<Event, Feed>
	{

		@Override
		public Feed getCategory(Event object)
		{
			return object.feed;
		}

	}
	
	@SpringBean
	private FeedService feedService;
	
	public EventListPanel(String id) {
		super(id);
		
		List<Event> eventsSorted = feedService.getEvents(((AggregatorSession)getSession()).getSelectedFeeds());
		
		final MultiMap eventsPerDate = CategoryHelper.categorize(eventsSorted, new EventsCategorizer());
		List<Date> dates = new ArrayList<Date>(eventsPerDate.keySet());
		Collections.sort(dates);
		
		add(new ListView<Date>("dates", dates)
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Date> item)
				{
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
							return new LongDateFormatter();
						}
						
					});
					item.add(new CategorizedList<Feed, Event>("feeds", (List<Event>)eventsPerDate.get(item.getModelObject()), new EventPerFeedCategorizer())
						{

							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							protected void populateCaption(ListItem<Feed> item)
							{
								item.add(new Label("feed", item.getModel()));
							}

							@Override
							protected void populateChild(ListItem<Event> item)
							{
								item.add(new EventPanel("event", item.getModel()));
							}
						
						});
				}
			
			});
		
	}

}
