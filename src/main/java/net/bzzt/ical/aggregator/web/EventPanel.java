package net.bzzt.ical.aggregator.web;

import net.bzzt.ical.aggregator.model.Event;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EventPanel extends Panel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventPanel(String id, IModel<Event> model)
	{
		super(id, new CompoundPropertyModel<Event>(model));
		
		add(new FeedLink("feedLink", model.getObject().feed));

		final WebMarkupContainer more = new MoreInfoPanel("more", model, false);
		more.setOutputMarkupPlaceholderTag(true);
		more.setVisible(false);
		add(more);
		
		WebMarkupContainer link = new AjaxLink<Object>("link")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				more.setVisible(!more.isVisible());
				target.addComponent(more);
			}
			
		};
		
		link.add(new Label("summary"));
		add(link);
	}

}
