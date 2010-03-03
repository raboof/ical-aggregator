package net.bzzt.ical.aggregator.web;

import java.util.Calendar;
import java.util.Date;

public class WeekView extends AggregatorLayoutPage
{
	public WeekView()
	{
		this(new Date());
	}

	public WeekView(Date date)
	{
		Calendar calendar = Calendar.getInstance();
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
