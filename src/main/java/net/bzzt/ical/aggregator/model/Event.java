package net.bzzt.ical.aggregator.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.wicket.util.time.Time;

import net.bzzt.ical.aggregator.web.model.Identifiable;
import net.fortuna.ical4j.model.Iso8601;

@Entity
public class Event implements Serializable, Identifiable<Long>, Cloneable, Comparable<Event> {
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
	
	public String description;
	
	private Date start;

	/** false if 'start' is a date rather than a datetime */
	private Boolean startHasTime = false;
	
	private Date ending;
	
	private Boolean endHasTime = false;
	
	public String rawEvent;

	public URL url;
	
	/** This event was added manually */
	@Column(nullable=false)
	private Boolean manual = false;

	/** 
	 * Some manual events need to be verified before they show up.
	 */
	@Column(nullable=false)
	private Boolean hidden = true;
	
	public Event()
	{
		
	}
	
	public Event(Feed feed, Boolean hidden)
	{
		this.feed = feed;
		if (hidden == null)
		{
			this.hidden = feed.url == null;
		}
		else
		{
			this.hidden = hidden;
		}
	}
	
	public Event(boolean manual)
	{
		this.manual = manual;
	}

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
		if (startHasTime == null || startHasTime)
		{
			return Time.valueOf(start);
		}
		else
		{
			return null;
		}
	}

	public Time getEndTime()
	{
		if (ending != null && (endHasTime == null || endHasTime))
		{
			return Time.valueOf(ending);
		}
		else
		{
			return null;
		}
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
		setManual(true);
		return result;
	}

	/**
	 * @return the manual
	 */
	public Boolean getManual() {
		return manual;
	}

	/**
	 * @param manual the manual to set
	 */
	public void setManual(Boolean manual) {
		if (manual == null)
		{
			this.manual = false;
		}
		else
		{
			this.manual = manual;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + feed.shortName + "] " + summary;
	}

	/**
	 * @return the verified
	 */
	public Boolean getHidden()
	{
		return hidden != null && hidden;
	}

	/**
	 * @param hidden the verified to set
	 */
	public void setHidden(Boolean hidden)
	{
		if (hidden == null)
		{
			this.hidden = true;
		}
		else
		{
			this.hidden = hidden;
		}
	}

	/**
	 * @return the startHasTime
	 */
	public Boolean getStartHasTime()
	{
		return startHasTime;
	}

	/**
	 * @param startHasTime the startHasTime to set
	 */
	public void setStartHasTime(Boolean startHasTime)
	{
		this.startHasTime = startHasTime;
	}

	/**
	 * @return the endHasTime
	 */
	public Boolean getEndHasTime()
	{
		return endHasTime;
	}

	/**
	 * @param endHasTime the endHasTime to set
	 */
	public void setEndHasTime(Boolean endHasTime)
	{
		this.endHasTime = endHasTime;
	}

	@Override
	public int compareTo(Event o)
	{
		int result = start.compareTo(o.start);
		if (result == 0)
		{
			return summary.compareTo(o.summary);
		}
		else
		{
			return result;
		}
	}
	
	
}
