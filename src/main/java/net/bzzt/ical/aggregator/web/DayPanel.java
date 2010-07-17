/**
 * 
 */
package net.bzzt.ical.aggregator.web;

import java.util.Calendar;
import java.util.Date;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;

public class DayPanel extends Panel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean(name="feedService")
	private FeedService feedService;
	
	public DayPanel(String id, Date date, final WeekView parent)
	{
		super(id);
		
		WebMarkupContainer head = new WebMarkupContainer("head");
		if (isToday(date))
		{
			head.add(new AttributeModifier("class", true, new Model<String>("selected")));
		}
		
		Link<Date> link = new Link<Date>("link", new Model<Date>(date))
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(new DayView(getModelObject()));
			}
			
		};
		Label label = new Label("date", new Model<Date>(date))
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.Component#getConverter(java.lang.Class)
			 */
			@Override
			public IConverter getConverter(Class<?> type)
			{
				return new LongDateFormatter();
			}
			
		};
		link.add(label);
		head.add(link);
		add(head);
		
		add(new ListView<Event>("events", feedService.getEventsForDay(AggregatorSession.get().getSelectedFeeds(), date)){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Event> item)
			{
				item.add(new EventPanel("event", item.getModel())
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected void refresh(AjaxRequestTarget target)
					{
						parent.refresh(target);
					}
					
				});
			}});
	}

	private boolean isToday(Date date)
	{
		Calendar today = Calendar.getInstance();
		Calendar given = Calendar.getInstance();
		given.setTime(date);
		
		for (int field : new Integer[] { Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR })
		{
			if (given.get(field) != today.get(field))
			{
				return false;
			}
		}
		return true;
	}

}