package net.bzzt.ical.aggregator.web.admin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorSession;
import net.bzzt.ical.aggregator.web.EventListPage;
import net.bzzt.ical.aggregator.web.model.JpaEntityModel;
import net.fortuna.ical4j.data.ParserException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class FeedPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private FeedService feedService;
	
	public class FeedForm extends Form<Feed> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FeedForm(String id, IModel<Feed> feed) {
			super(id, new CompoundPropertyModel<Feed>(feed));
			
			setEnabled(AggregatorSession.get().ingelogd());
			
			add(new TextField<String>("name"));
			add(new TextField<String>("shortName"));
			add(new TextField<String>("description"));
			add(new TextField<Integer>("prio"));
			add(new TextField<URI>("url"));
			add(new TextField<URI>("link"));
			add(new CheckBox("showByDefault"));
			add(new Label("lastUpdate"));
			
			boolean existingIcsFeed = feed.getObject().id != null && feed.getObject().getUrl() != null;
			add(new SubmitLink("reload")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					Feed feed = FeedForm.this.getModelObject();
					try {
						feedService.reloadFeed(feed);
						//setResponsePage(new FeedDetails(feed));
						setResponsePage(EventListPage.class);
					} catch (IOException e) {
						error("Error loading feed: " + e.getMessage());
					} catch (ParserException e) {
						error("Error parsing feed: " + e.getMessage());
					}
					
				}
				
			}.setVisible(existingIcsFeed));
			
			WebMarkupContainer validateLink = new WebMarkupContainer("validate");
			validateLink.setVisible(false);
			try {
				if (feed.getObject().getUrl() != null)
				{
					add(validateLink.add(new AttributeModifier("href", true, new Model<String>("http://arnout.engelen.eu/icalendar-validator/urlvalidate?url=" + 
							URLEncoder.encode(feed.getObject().getUrl().toExternalForm(), "utf-8")))));
					validateLink.setVisible(true);
				}
			} catch (UnsupportedEncodingException e1) {
			}
			validateLink.setVisible(existingIcsFeed);
			add(validateLink);
			
			add(new SubmitLink("clear")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					Feed feed = FeedForm.this.getModelObject();
					feedService.clear(feed);
					try {
						feedService.reloadFeed(feed);
						//setResponsePage(new FeedDetails(feed));
						setResponsePage(EventListPage.class);
					} catch (IOException e) {
						error("Error loading feed: " + e.getMessage());
					} catch (ParserException e) {
						error("Error parsing feed: " + e.getMessage());
					}
					
				}
				
			}.setVisible(feed.getObject().id != null));
			
			add(new SubmitLink("delete")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					Feed feed = FeedForm.this.getModelObject();
					AggregatorSession.get().removeFromFeedSelection(feed);
					feedService.delete(feed);
					setResponsePage(EventListPage.class);
					
				}
				
			}.setVisible(feed.getObject().id != null));
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			Feed modelObject = getModelObject();
			boolean isNew = modelObject.getId() == null;
			feedService.saveOrUpdate(modelObject);
			if (isNew)
			{
				setModel(new CompoundPropertyModel<Feed>(new JpaEntityModel<Feed>(new Feed())));
			}
		}

		
	}

	public FeedPanel(String id) {
		this(id, new CompoundPropertyModel<Feed>(new JpaEntityModel<Feed>(new Feed())));

	}

	public FeedPanel(String id, IModel<Feed> model) {
		super(id);
		add(new FeedForm("form", model));
	}

}
