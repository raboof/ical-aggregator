package net.bzzt.ical.aggregator.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class DayView extends AggregatorLayoutPage
{
	@SpringBean
	private FeedService feedService;
	
	private WebMarkupContainer dayContainer;
	
	private Date date;
	
	public DayView()
	{
		this(new Date());
	}
	
	public DayView(Date dateToShow)
	{
		this.date = dateToShow;

		dayContainer = getDayContainer("dayContainer");
		add(dayContainer);
	}
	
	private WebMarkupContainer getDayContainer(String id)
	{
		WebMarkupContainer dayContainer = new WebMarkupContainer(id);
		dayContainer.setOutputMarkupId(true);

		dayContainer.add(new AjaxLink<Void>("previous")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					date = DateUtils.addDays(date, -1);
					refresh(target);
				}
				
			});
		dayContainer.add(new Label("date", new Model<Date>(date)));
		
		DateConverter converter = new StyleDateConverter(false);
		DateTextField dateTextField = new DateTextField("dateField", new PropertyModel<Date>(DayView.this, "date"), converter);
		dateTextField.add(new DatePicker());
		dayContainer.add(dateTextField);
		dateTextField.add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				refresh(target);
			}
		});
		
		dayContainer.add(new AjaxLink<Void>("next")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					date = DateUtils.addDays(date, 1);
					refresh(target);
				}
				
			});
		
		List<Event> eventsForDay = feedService.getEventsForDay(AggregatorSession.get().getSelectedFeeds(), date);
		Collections.sort(eventsForDay);
		
		List<List<Event>> columns = splitEvents(eventsForDay, 3);
		
		dayContainer.add(new ListView<List<Event>>("column", columns)
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
								item.add(new MoreInfoPanel("infoPanel", item.getModel(), true, true)
								{

									/**
									 * 
									 */
									private static final long serialVersionUID = 1L;

									@Override
									protected void refresh(AjaxRequestTarget target)
									{
										DayView.this.refresh(target);
									}
									
								});
							}
						});
				}
			
			}
			);
		return dayContainer;
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

	/* (non-Javadoc)
	 * @see net.bzzt.ical.aggregator.web.AggregatorLayoutPage#refresh(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	public void refresh(AjaxRequestTarget target)
	{
		WebMarkupContainer newDayContainer = getDayContainer("dayContainer");
		dayContainer.replaceWith(newDayContainer);
		target.addComponent(newDayContainer);
		dayContainer = newDayContainer;
	}
	
	
}
