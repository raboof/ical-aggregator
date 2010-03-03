package net.bzzt.ical.aggregator.web;

import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

public class WeekView extends AggregatorLayoutPage
{
	public WeekView()
	{
		this(new Date());
	}

	public WeekView(final Date date)
	{
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
		
		calendar.add(Calendar.DAY_OF_MONTH, 2-calendar.get(Calendar.DAY_OF_WEEK));
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
}
