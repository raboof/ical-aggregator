package net.bzzt.ical.aggregator.web.admin;

import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ManageFeeds extends AggregatorLayoutPage {
	@SpringBean
	private FeedService feedService;
	
	public ManageFeeds()
	{
		add(new ListView<Feed>("feeds", new PropertyModel<List<Feed>>(this, "feeds"))
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Feed> item) {
						IModel<Feed> model = new JpaEntityModel<Feed>(item.getModelObject());
						item.add(new FeedPanel("feed", model));
					}
			
				});
		
		add(new FeedPanel("addFeed"));
		add(new FeedbackPanel("feedback"));
	}
	
	public List<Feed> getFeeds()
	{
		return feedService.getFeeds();
	}
}
