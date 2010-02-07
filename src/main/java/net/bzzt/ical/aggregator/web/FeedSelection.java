package net.bzzt.ical.aggregator.web;

import java.util.Collection;
import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class FeedSelection extends Panel {

	public class FeedRenderer implements IChoiceRenderer<Feed> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object getDisplayValue(Feed object) {
			return object.name;
		}

		@Override
		public String getIdValue(Feed object, int index) {
			return String.valueOf(index);
		}

	}

	public class FeedSelectionForm extends Form<List<Feed>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SpringBean
		private FeedService feedService;
		
		public FeedSelectionForm(String id, IModel<List<Feed>> model) {
			super(id, model);
			
			IModel<Collection<Feed>> collectionModel = (IModel)model; 
			
			IChoiceRenderer<? super Feed> renderer = new FeedRenderer();
			CheckBoxMultipleChoice<Feed> checkBoxMultipleChoice = new CheckBoxMultipleChoice<Feed>("feed", collectionModel, feedService.getFeeds(), renderer);
			
//			checkBoxMultipleChoice.add(new AjaxFormComponentUpdatingBehavior() {
//
//				@Override
//				protected void onUpdate(AjaxRequestTarget target) {
//					// TODO Auto-generated method stub
//					
//				}});
			
			add(checkBoxMultipleChoice);
			
			
//			add(new PropertyListView<Feed>("feed", model) {
//
//				/**
//				 * 
//				 */
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				protected void populateItem(ListItem<Feed> item) {
//					item.add(new Label("name"));
//					item.add(new Label("shortName"));
//				}
//			});
		}

		@Override
		protected void onSubmit() {
			((AggregatorSession) getSession()).setSelectedFeeds(getModelObject());
			setResponsePage(HomePage.class);
		}
		
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FeedSelection(String id) {
		super(id);
		add(new FeedSelectionForm("form", new PropertyModel<List<Feed>>(this, "selectedFeeds")));
	}

	public List<Feed> getSelectedFeeds()
	{
		return ((AggregatorSession) Session.get()).getSelectedFeeds();
	}
}
