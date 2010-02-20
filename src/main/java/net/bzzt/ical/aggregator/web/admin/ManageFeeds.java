package net.bzzt.ical.aggregator.web.admin;

import java.util.ArrayList;
import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
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
		add(new ListView<Long>("feeds", new PropertyModel<List<Long>>(this, "feeds"))
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Long> item) {
						IModel<Feed> model = new JpaEntityModel<Feed>(Feed.class, item.getModelObject());
						item.add(new FeedPanel("feed", model));
					}
			
				});
		
		add(new FeedPanel("addFeed"));
		add(new FeedbackPanel("feedback"));
	}
	
	public List<Long> getFeeds()
	{
		List<Long> result = new ArrayList<Long>();
		CollectionUtils.collect(feedService.getFeeds(), new Transformer() {

			@Override
			public Object transform(Object input)
			{
				return ((Feed)input).getId();
			}}, result);
		return result;
	}
}
