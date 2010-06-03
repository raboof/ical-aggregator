package net.bzzt.ical.aggregator.web.ical;

import java.net.URISyntaxException;

import net.bzzt.ical.aggregator.model.Event;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;

import org.apache.commons.collections.Transformer;

public class EventToVEventTransformer implements Transformer
{

	@Override
	public Object transform(Object input)
	{
		return transform((Event) input);
	}

	public VEvent transform(Event input)
	{
		VEvent event = new VEvent(new Date(input.getStart()), new Date(input.getEnding()), input.summary);
		event.getProperties().add(new Description(input.description));
		event.getProperties().add(new Uid(input.getId().toString()));
		if (input.url != null)
		{
			try
			{
				event.getProperties().add(new Url(input.url.toURI()));
			}
			catch (URISyntaxException e)
			{
				// ook goed
			}
		}
		
		return event;
	}
}
