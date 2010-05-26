/**
 * 
 */
package net.bzzt.ical.aggregator.web.rss;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.web.WicketApplication;

import org.apache.commons.collections.Transformer;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;

public final class EventToSyndEntryTransformer implements Transformer
{
	@Override
	public Object transform(Object rawEvent)
	{
		Event event = (Event) rawEvent;
		
		SyndEntryImpl entry = new SyndEntryImpl();
		entry.setTitle(event.summary);
		if (event.url != null)
		{
			entry.setLink(event.url.toExternalForm());
		}
		entry.setPublishedDate(event.getStart());

		// This will be the guid: http://wiki.java.net/bin/view/Javawsxml/Rome05URIMapping
		entry.setUri(WicketApplication.getLink() + "/event/" + event.id);
		
		SyndContent description = new SyndContentImpl();
		description.setType("text/plain");
		description.setValue(event.description);
		entry.setDescription(description);
		return entry;
	}
}