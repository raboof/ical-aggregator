package net.bzzt.ical.aggregator.web.opml;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.admin.ManageFeeds;
import net.bzzt.ical.aggregator.web.opml.jaxb.Opml;
import net.bzzt.ical.aggregator.web.opml.jaxb.Outline;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class OpmlUploadPage extends AggregatorLayoutPage
{

	public OpmlUploadPage(InputStream inputStream)
	{
		JAXBContext jc;
		
		Opml opml;
		
		try
		{
			jc = JAXBContext.newInstance(Opml.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			opml = (Opml) unmarshaller.unmarshal(inputStream);
		}
		catch (JAXBException e)
		{
			throw new IllegalStateException(e);
		}
		
		Form<Void> form = new Form<Void>("form");
		
		final CheckGroup<Outline> outlineSelection = new CheckGroup<Outline>("outlines", new ArrayList<Outline>());
		
		outlineSelection.add(new PropertyListView<Outline>("outline", opml.body.outline)
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Outline> item)
				{
					item.add(new Check<Outline>("check", item.getModel()));
					item.add(new Label("text"));
				}
			
			});
		form.add(outlineSelection);
		form.add(new SubmitLink("submit")
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SpringBean
			private FeedService feedService;
			
			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.form.SubmitLink#onSubmit()
			 */
			@Override
			public void onSubmit()
			{
				for (Outline outline : outlineSelection.getModelObject())
				{
					Feed feed = new Feed(outline.text, outline.shortName, outline.xmlUrl, outline.htmlUrl, outline.description,
						outline.priority, outline.showByDefault);
					feedService.saveOrUpdate(feed);
					try
					{
						feedService.reloadFeed(feed);
					}
					catch (Exception e)
					{
						error("Error refreshing '" + feed.name + "': " + e.getMessage());
					}
				}
				setResponsePage(ManageFeeds.class);
			}
			
			
		});
		
		add(form);
	}

}
