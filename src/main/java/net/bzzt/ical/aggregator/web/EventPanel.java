package net.bzzt.ical.aggregator.web;

import net.bzzt.ical.aggregator.model.Event;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public abstract class EventPanel extends Panel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventPanel(String id, IModel<Event> model)
	{
		this(id, model, false);
	}
	
	public EventPanel(String id, IModel<Event> model, final boolean truncateLongTexts)
	{
		super(id, new CompoundPropertyModel<Event>(model));
		
		add(new FeedLink("feedLink", model.getObject().feed));

		WebMarkupContainer more = new WebMarkupContainer("more"); 		
		more.setOutputMarkupPlaceholderTag(true);
		more.setVisible(false);
		add(more);
		
		WebMarkupContainer link = new AjaxLink<Event>("link", model)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				Component more = EventPanel.this.get("more");
				if (more instanceof MoreInfoPanel)
				{
					more.setVisible(!more.isVisible());
				}
				else
				{
					MoreInfoPanel replacement = new MoreInfoPanel("more", getModel(), false, truncateLongTexts)
					{

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						protected void refresh(AjaxRequestTarget target)
						{
							EventPanel.this.refresh(target);
						}
						
					};
					replacement.setOutputMarkupPlaceholderTag(true);
					more.replaceWith(replacement);
					more = replacement;
				}
				target.addComponent(more);
			}
			
		};
		
		link.add(new Label("summary"));
		add(link);
	}

	protected abstract void refresh(AjaxRequestTarget target);

}
