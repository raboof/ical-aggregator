package net.bzzt.ical.aggregator.web;

import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import sun.util.calendar.CalendarUtils;

public class WeekView extends AggregatorLayoutPage
{
	private Date date;
	
	public WeekView()
	{
		this(new Date());
	}

	public WeekView(Date dateToShow)
	{
		this.date = dateToShow;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		add(new Link<Void>("previous")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.WEEK_OF_YEAR, -1);
				setResponsePage(new WeekView(calendar.getTime()));
			}
			
		});
		add(new Label("week", new Model<Integer>(calendar.get(Calendar.WEEK_OF_YEAR))));
		add(new Link<Void>("next")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.WEEK_OF_YEAR, 1);
				setResponsePage(new WeekView(calendar.getTime()));
			}
			
		});
		
		// sunday=1, saturday=7
		int selected_day = calendar.get(Calendar.DAY_OF_WEEK);
		
		// after this, monday=0, sunday=6
		selected_day = (selected_day + 5) % 7;
		
		calendar.add(Calendar.DAY_OF_MONTH, -selected_day);
		add(new DayPanel("monday", calendar.getTime()));
		
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		add(new DayPanel("tuesday", calendar.getTime()));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		add(new DayPanel("wednesday", calendar.getTime()));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		add(new DayPanel("thursday", calendar.getTime()));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		add(new DayPanel("friday", calendar.getTime()));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		add(new DayPanel("saturday", calendar.getTime()));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		add(new DayPanel("sunday", calendar.getTime()));
	}

	/* (non-Javadoc)
	 * @see net.bzzt.ical.aggregator.web.AggregatorLayoutPage#refresh(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	public void refresh(AjaxRequestTarget target)
	{
		setResponsePage(new WeekView(date));
	}
	
	
}
