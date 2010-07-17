package net.bzzt.ical.aggregator.web;

import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MarkDuplicatesPage extends AggregatorLayoutPage
{
	@SpringBean
	public FeedService feedService;
	
	private Event parent;
	
	public MarkDuplicatesPage(final IModel<Event> model, final Class<? extends Page> previousView)
	{
		add(new Label("summary", model.getObject().summary));
		
		Form<Void> form = new Form<Void>("form")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
			 */
			@Override
			protected void onSubmit()
			{
				if (parent != null)
				{
					Event event = model.getObject();
					event.duplicate_of = parent;
					feedService.saveOrUpdateEvent(event);
					setResponsePage(previousView);
				}
				else
				{
					error("Geen parent geselecteerd");
				}
			}
			
		};

		List<Event> events = feedService.getDuplicateCandidates(model.getObject());
		if (!events.isEmpty())
		{
			parent = events.get(0);
		}
		
		form.add(new RadioChoice<Event>("parent", new PropertyModel<Event>(this, "parent"), events));
		
		add(form);
	}

}
