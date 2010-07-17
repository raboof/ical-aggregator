package net.bzzt.ical.aggregator.web.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.model.Right;
import net.bzzt.ical.aggregator.model.User;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.service.UserService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.AggregatorSession;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;
import net.bzzt.ical.aggregator.web.opml.OpmlPage;
import net.bzzt.ical.aggregator.web.opml.OpmlUploadPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ManageFeeds extends AggregatorLayoutPage {
	private static final Log LOG = LogFactory.getLog(ManageFeeds.class);
	
	@SpringBean
	private FeedService feedService;
	
	@SpringBean
	private UserService userService;
	
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
		
		User loggedInUser = AggregatorSession.get().getLoggedInUser();
		add(new FeedPanel("addFeed").setVisible(userService.hasRight(loggedInUser, Right.ADD_FEED)));
		add(new BookmarkablePageLink<OpmlPage>("opml", OpmlPage.class));
		
		Form<Void> opmlUploadForm = new Form<Void>("opmlUploadForm");
		final Model<FileUpload> fileUploadModel = new Model<FileUpload>();
		opmlUploadForm.add(new FileUploadField("opmlFile", fileUploadModel));
		opmlUploadForm.add(new SubmitLink("submit")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.form.SubmitLink#onSubmit()
			 */
			@Override
			public void onSubmit()
			{
				try
				{
					setResponsePage(new OpmlUploadPage(fileUploadModel.getObject().getInputStream()));
				}
				catch (IOException e)
				{
					error("Error opening OPML file: " + e.getMessage());
					LOG.error(e.getMessage(), e);
				}
			}
			
			
		});
		opmlUploadForm.setVisible(userService.hasRight(loggedInUser, Right.ADD_FEED));
		add(opmlUploadForm);
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
