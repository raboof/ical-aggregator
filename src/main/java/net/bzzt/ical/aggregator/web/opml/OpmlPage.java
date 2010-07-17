package net.bzzt.ical.aggregator.web.opml;

import java.io.OutputStreamWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.web.WicketApplication;
import net.bzzt.ical.aggregator.web.opml.jaxb.Opml;
import net.bzzt.ical.aggregator.web.opml.jaxb.Outline;

import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class OpmlPage extends Page
{
	@SpringBean
	private FeedService feedService;
	
	@Override
	public String getMarkupType()
	{
		return "xml";
	}

	@Override
	protected final void onRender(MarkupStream markupStream)
	{
		Response response = getResponse();
		
		response.setContentType("text/x-opml");
		
		try
		{
			JAXBContext jc = JAXBContext.newInstance(Opml.class);
			Marshaller m = jc.createMarshaller();
			m.marshal(getOpml(), new OutputStreamWriter(response.getOutputStream()));
		}
		catch (JAXBException e)
		{
			throw new RuntimeException("Error creating XML");
		}
	}

	protected Opml getOpml()
	{
		Opml result = new Opml();
		
		result.head.title = WicketApplication.getTitle();
		
		for (Feed feed : feedService.getFeeds())
		{
			if (feed.getUrl() != null)
			{
				result.body.outline.add(new Outline(feed.name, 
					"text/calendar", feed.getUrl(), 
					feed.description,
					feed.link, feed.shortName, feed.getPrio(), feed.getShowByDefault()));
			}
		}
		
		return result;
	}
}
