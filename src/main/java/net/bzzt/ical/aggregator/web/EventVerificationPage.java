package net.bzzt.ical.aggregator.web;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class EventVerificationPage extends AggregatorLayoutPage
{
	@SpringBean
	private FeedService feedService;
	
	public EventVerificationPage()
	{
		add(new PropertyListView<Event>("event", feedService.getEventsToVerify())
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final ListItem<Event> item)
				{
					item.add(new Label("summary"));
					item.add(new Link<Void>("deny")
						{

							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public void onClick()
							{
								feedService.delete(item.getModelObject());
								setResponsePage(EventVerificationPage.class);
							}
							
						});
					item.add(new Link<Void>("confirm")
						{

							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public void onClick()
							{
								Event event = item.getModelObject();
								event.setHidden(false);
								feedService.saveOrUpdateEvent(event);
								setResponsePage(EventVerificationPage.class);
							}
							
						});
				}
			
			});
	}
}
