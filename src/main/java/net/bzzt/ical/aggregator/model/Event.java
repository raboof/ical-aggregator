package net.bzzt.ical.aggregator.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.wicket.util.time.Time;

import net.bzzt.ical.aggregator.web.model.Identifiable;
import net.fortuna.ical4j.model.Iso8601;

@Entity
public class Event implements Serializable, Identifiable<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	public Long id;
	
	@ManyToOne
	public Feed feed;
	
	@ManyToOne
	public Event duplicate_of;
	
	public String uid;
	
	public String summary;
	
	private Date start;
	
	private Date ending;
	
	public String rawEvent;

	public URL url;

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		if (start instanceof Iso8601)
		{
			this.start = new Date(start.getTime());
		}
		else
		{
			this.start = start;
		}
	}

	public Date getEnding() {
		return ending;
	}

	public void setEnding(Date ending) {
		if (ending instanceof Iso8601)
		{
			this.ending = new Date(ending.getTime());
		}
		else
		{
			this.ending = ending;
		}
	}
	
	public Time getStartTime()
	{
		return Time.valueOf(start);
	}

	@Override
	public Long getId() {
		return id;
	}
}
