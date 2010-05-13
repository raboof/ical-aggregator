package net.bzzt.ical.aggregator.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.bzzt.ical.aggregator.web.model.Identifiable;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Feed implements Serializable, Identifiable<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	public Long id;

	@Column(nullable=false)
	public String name;
	
	@Column(nullable=false)
	public String shortName;
	
	/**
	 * Feeds zonder URL hebben alleen handmatige events
	 */
	@Column(nullable=true)
	public URL url;
	
	public Date lastUpdate;
	
	private Integer prio = 50;
	
	private Boolean showByDefault = true;

	public Feed() { }
	
	public Feed(String name, String shortName) {
		this.shortName = shortName;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public URL getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return name;
	}

	public Integer getPrio() {
		return prio;
	}
	
	public void setPrio(Integer prio)
	{
		if (prio == null)
		{
			this.prio = 50;
		}
		else
		{
			this.prio = prio;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Feed))
		{
			return false;
		}
		Feed feed = (Feed) obj;
		if (id != null)
		{
			return id.equals(feed.getId());
		}
		else
		{
			return this == obj;
		}
	}

	/**
	 * @return the showByDefault
	 */
	public Boolean getShowByDefault()
	{
		return showByDefault;
	}

	/**
	 * @param showByDefault the showByDefault to set
	 */
	public void setShowByDefault(Boolean showByDefault)
	{
		if (showByDefault == null)
		{
			this.showByDefault = true;
		}
		else
		{
			this.showByDefault = showByDefault;
		}
	}
	
	
}
