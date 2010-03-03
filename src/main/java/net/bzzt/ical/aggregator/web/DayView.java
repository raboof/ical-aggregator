package net.bzzt.ical.aggregator.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class DayView extends AggregatorLayoutPage
{
	@SpringBean
	private FeedService feedService;
	
	public DayView()
	{
		this(new Date());
	}
	
	public DayView(final Date date)
	{
		add(new Link<Void>("previous")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					setResponsePage(new DayView(DateUtils.addDays(date, -1)));
				}
				
			});
		add(new Label("date", new Model<Date>(date)));
		add(new Link<Void>("next")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					setResponsePage(new DayView(DateUtils.addDays(date, 1)));
				}
				
			});
		
		List<Event> eventsForDay = feedService.getEventsForDay(AggregatorSession.get().getSelectedFeeds(), date);
		Collections.sort(eventsForDay);
		
		List<List<Event>> columns = splitEvents(eventsForDay, 3);
		
		add(new ListView<List<Event>>("column", columns)
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<List<Event>> item)
				{
					item.add(new ListView<Event>("event", item.getModel())
						{
							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							protected void populateItem(ListItem<Event> item)
							{
								item.add(new MoreInfoPanel("infoPanel", item.getModel(), true));
							}
						});
				}
			
			}
			);
		
	}

	private List<List<Event>> splitEvents(List<Event> eventsForDay, int sublists)
	{
		List<List<Event>> result = new ArrayList<List<Event>>();
		for (int i = 0; i < sublists; i++)
		{
			result.add(new ArrayList<Event>());
		}
		
		int i = 0;
		for (Event event : eventsForDay)
		{
			result.get(i%sublists).add(event);
			i++;
		}
		return result;
	}
}
