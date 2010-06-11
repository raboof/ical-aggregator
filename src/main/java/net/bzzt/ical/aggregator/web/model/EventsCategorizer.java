package net.bzzt.ical.aggregator.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.bzzt.ical.aggregator.model.Event;
import net.bzzt.ical.aggregator.util.Categorizer;

public class EventsCategorizer implements Categorizer<Event, Date>
{
	@Override
	public Date getCategory(Event object) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		try {
			return format.parse(format.format(object.getStart()));
		} catch (ParseException e) {
			return null;
		}
	}

}
