package net.bzzt.ical.aggregator.web;

import java.net.URL;
import java.util.List;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.model.Right;
import net.bzzt.ical.aggregator.model.User;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.service.UserService;
import net.bzzt.ical.aggregator.web.addthis.AddThisPanel;
import net.bzzt.ical.aggregator.web.admin.EventDetailPage;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
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

	@SpringBean(name="feedService")
	private FeedService feedService;
	
	@SpringBean(name="userService")
	private UserService userService;
	
	public MoreInfoPanel(String id, IModel<Event> model, boolean showTitle, boolean truncateLongTexts) {
		super(id, new CompoundPropertyModel<Event>(model));
		
		add(new Label("summary").setVisible(showTitle));
		add(new Label("feed.name").setVisible(showTitle));
		
		if (truncateLongTexts)
		{
			add(new AutoEllipsedText("description"));
		}
		else
		{
			add(new Label("description"));
		}
		
		add(new Label("startTime").setVisible(model.getObject().getStartTime() != null));
		add(new Label("endTime").setVisible(model.getObject().getEndTime() != null));
		
		Label idLabel = new Label("id");
		if (!Application.get().getConfigurationType().equals(Application.DEVELOPMENT))
		{
			idLabel.add(new AttributeModifier("style", true, new Model<String>("display: none")));
		}
		add(idLabel);
		
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
		
		User loggedInUser = AggregatorSession.get().getLoggedInUser();
		add(new AjaxLink<Event>("duplicate", model)
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					setResponsePage(new MarkDuplicatesPage(getModel()));
				}
				
			}.setVisible(userService.hasRight(loggedInUser, Right.MARK_DUPLICATE)));
		add(new AjaxLink<Event>("hide", model)
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					Event event = getModelObject();
					event.setHidden(true);
					feedService.saveOrUpdateEvent(event);
					setResponsePage(EventListPage.class);
				}
				
			}.setVisible(userService.hasRight(loggedInUser, Right.HIDE_EVENT)));
		add(new Link<Event>("detailLink", model){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new EventDetailPage(getModelObject()));
			}
			
		}.setVisible(userService.hasRight(loggedInUser, Right.EDIT_EVENT)));
		if (url == null)
		{
			add(new WebMarkupContainer("addThis").setVisible(false));
		}
		else
		{
			String postfix = " (via " + WicketApplication.getLink() + ")";
			add(new AddThisPanel("addThis", model.getObject().summary + postfix, model.getObject().description + postfix, url.toExternalForm(), "raboof"));
		}
	}

}
