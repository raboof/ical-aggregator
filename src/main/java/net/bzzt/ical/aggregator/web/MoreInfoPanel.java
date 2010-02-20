package net.bzzt.ical.aggregator.web;

import java.net.URL;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MoreInfoPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private FeedService feedService;
	
	public MoreInfoPanel(String id, final IModel<Event> model) {
		super(id, new CompoundPropertyModel<Event>(model));
		
		add(new Label("description"));
		add(new Label("id").setVisible(Application.get().getConfigurationType().equals(Application.DEVELOPMENT)));
		
		WebMarkupContainer link = new WebMarkupContainer("link");
		
		URL url = model.getObject().url;
		if (url != null)
		{
			link.add(new AttributeModifier("href", true, new Model<URL>(url)));
		}
		else
		{
			link.setVisible(false);
		}
		add(link);
		
		List<Event> alternatives = feedService.getAlternatives(model.getObject());
		ListView<Event> listView = new ListView<Event>("alternatives", alternatives)
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Event> item) {
						item.add(new FeedLink("feedLink", item.getModelObject().feed));

						WebMarkupContainer link = new WebMarkupContainer("link");
						
						URL url = item.getModelObject().url;
						if (url != null)
						{
							link.add(new AttributeModifier("href", true, new Model<URL>(url)));
						}
						else
						{
							link.setVisible(false);
						}
						item.add(link);
						
						Label summary = new Label("summary", item.getModelObject().summary);
						link.add(summary);
					}
			
				};
		listView.setVisible(!alternatives.isEmpty());
		add(listView);
		
		add(new AjaxLink("duplicate")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				setResponsePage(new MarkDuplicatesPage(model));
			}
			
		});
	}

}
