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
public class Event implements Serializable, Identifiable<Long>, Cloneable {
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
	
	private Boolean manual = false;

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
	
	public Event clone() {
		if (duplicate_of != null)
		{
			throw new IllegalStateException();
		}
		if (manual != null && manual)
		{
			throw new IllegalStateException();
		}
		Event result;
		try {
			result = (Event) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
		result.id = null;
		result.uid = null;
		result.manual = true;
		return result;
	}

	/**
	 * @return the manual
	 */
	public boolean getManual() {
		return manual != null && manual;
	}

	/**
	 * @param manual the manual to set
	 */
	public void setManual(Boolean manual) {
		this.manual = manual;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return summary;
	}
	
	
}
