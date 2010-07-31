package net.bzzt.ical.aggregator.web;

import java.util.Collection;
import java.util.List;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class FeedSelection extends Panel
{

	private final AggregatorLayoutPage parent;

	public class FeedRenderer implements IChoiceRenderer<Feed>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object getDisplayValue(Feed object)
		{
			return object.name;
		}

		@Override
		public String getIdValue(Feed object, int index)
		{
			return String.valueOf(index);
		}

	}

	public class FeedSelectionForm extends Form<List<Feed>>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SpringBean(name = "feedService")
		private FeedService feedService;

		@SuppressWarnings("unchecked")
		public FeedSelectionForm(String id, IModel<List<Feed>> model)
		{
			super(id, model);

			IModel<Collection<Feed>> collectionModel = (IModel) model;

			final CheckGroup feeds = new CheckGroup<Feed>("feed", collectionModel);
			add(feeds);

			feeds.add(new PropertyListView<Feed>("feeds", feedService.getFeeds())
			{

				/**
					 * 
					 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Feed> item)
				{
					Feed modelObject = item.getModelObject();
					Check check = new Check("check", new Model<Feed>(modelObject));
					check.setOutputMarkupId(true);
					item.add(check);
					
					Label name = new Label("name");
					name.add(new AttributeModifier("for", true, new Model<String>(feeds.getMarkupId() + "-" + check.getMarkupId())));
					item.add(name);
					
					WebMarkupContainer description = new WebMarkupContainer("descriptionContainer");
					description.setOutputMarkupId(true);
					item.add(description);
					description.add(new Label("description"));
					WebMarkupContainer link = new WebMarkupContainer("link");
					if (modelObject.link == null)
					{
						link.setVisible(false);
					}
					else
					{
						link.add(new AttributeModifier("href", true, new Model<String>(modelObject.link.toString())));
					}
					description.add(link);
					
					{
						WebMarkupContainer toggle = new WebMarkupContainer("toggle");
						item.add(toggle);
						
						if (StringUtils.isBlank(item.getModelObject().description))
						{
							toggle.setVisible(false);
						}
						else
						{
							Model<String> toggleDescription = new Model<String>("if (document.getElementById('"
								+ description.getMarkupId() + "').style.display == 'block') { document.getElementById('"
								+ description.getMarkupId() + "').style.display = 'none'; } else { document.getElementById('"
								+ description.getMarkupId() + "').style.display = 'block'; }");
							toggle.add(new AttributeModifier("onclick", true, toggleDescription));
						}
					}
				}
			});
			feeds.add(new AjaxFormChoiceComponentUpdatingBehavior()
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					((AggregatorSession) getSession()).setSelectedFeeds(getModelObject());
					parent.refresh(target);
				}
			});

			
			final CheckBox showRecurring = new CheckBox("showRecurring", new Model<Boolean>(((AggregatorSession) getSession()).getMaxRecurrence() == null));
			showRecurring.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					if (showRecurring.getModelObject())
					{
						((AggregatorSession) getSession()).setMaxRecurrence(null);
					}
					else
					{
						((AggregatorSession) getSession()).setMaxRecurrence(5);
					}
					parent.refresh(target);
				}
			});
			add(showRecurring);
		}

		@Override
		protected void onSubmit()
		{
			((AggregatorSession) getSession()).setSelectedFeeds(getModelObject());
			setResponsePage(WicketApplication.get().getHomePage());
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FeedSelection(String id, AggregatorLayoutPage aggregatorLayoutPage)
	{
		super(id);
		this.parent = aggregatorLayoutPage;
		add(new FeedSelectionForm("form", new PropertyModel<List<Feed>>(this, "selectedFeeds")));
	}

	public List<Feed> getSelectedFeeds()
	{
		return ((AggregatorSession) Session.get()).getSelectedFeeds();
	}
}
