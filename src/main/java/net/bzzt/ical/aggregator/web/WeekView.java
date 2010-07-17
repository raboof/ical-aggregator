package net.bzzt.ical.aggregator.web;

import java.util.Calendar;
import java.util.Date;

import net.bzzt.ical.aggregator.web.ajax.uniqueurls.UniqueUrlHelper;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public class WeekView extends AggregatorLayoutPage
{
	private Date date;
	
	private WebMarkupContainer weekViewContainer;
	
	public WeekView()
	{
		this(new Date());
	}
	
	public WeekView(PageParameters parameters)
	{
		this(getDate(parameters));
	}
	
	private static Date getDate(PageParameters parameters)
	{
		String dateAsString = parameters.getString("date");

		Date date = (Date) Application.get().getConverterLocator().getConverter(Date.class).convertToObject(dateAsString, null);
		
		return date;
	}

	public WeekView(Date dateToShow)
	{
		this.date = dateToShow;
		
		weekViewContainer = getWeekContainer("weekContainer");
		add(weekViewContainer);
	}
	
	private WebMarkupContainer getWeekContainer(String id)
	{
		WebMarkupContainer result = new WebMarkupContainer(id);
		result.setOutputMarkupId(true);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		result.add(new AjaxLink<Void>("previous")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				date = DateUtils.addWeeks(date, -1);
				refresh(target);
			}
			
		});
		result.add(new Label("week", new Model<Integer>(calendar.get(Calendar.WEEK_OF_YEAR))));
		result.add(new AjaxLink<Void>("next")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				date = DateUtils.addWeeks(date, 1);
				refresh(target);
			}
			
		});
		
		// sunday=1, saturday=7
		int selected_day = calendar.get(Calendar.DAY_OF_WEEK);
		
		// after this, monday=0, sunday=6
		selected_day = (selected_day + 5) % 7;
		
		calendar.add(Calendar.DAY_OF_MONTH, -selected_day);
		result.add(new DayPanel("monday", calendar.getTime(), this));
		
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		result.add(new DayPanel("tuesday", calendar.getTime(), this));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		result.add(new DayPanel("wednesday", calendar.getTime(), this));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		result.add(new DayPanel("thursday", calendar.getTime(), this));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		result.add(new DayPanel("friday", calendar.getTime(), this));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		result.add(new DayPanel("saturday", calendar.getTime(), this));

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		result.add(new DayPanel("sunday", calendar.getTime(), this));
		
		return result;
	}

	/* (non-Javadoc)
	 * @see net.bzzt.ical.aggregator.web.AggregatorLayoutPage#refresh(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	public void refresh(AjaxRequestTarget target)
	{
		WebMarkupContainer newWeekContainer = getWeekContainer("weekContainer");
		weekViewContainer.replaceWith(newWeekContainer);
		target.addComponent(newWeekContainer);
		UniqueUrlHelper.resetHash(target, "date", date);
		weekViewContainer = newWeekContainer;
	}	
}
