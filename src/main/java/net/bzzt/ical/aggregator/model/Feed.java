package net.bzzt.ical.aggregator.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.bzzt.ical.aggregator.web.model.Identifiable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Feed implements Serializable, Identifiable<Long>, Comparable<Feed> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	public Long id;

	@Column(nullable=false)
	public String name;
	
	@Column(nullable=false,unique=true)
	public String shortName;
	
	/**
	 * Feeds zonder URL hebben alleen handmatige events
	 */
	@Column(nullable=true)
	public URL url;
	
	public Date lastUpdate;
	
	public Integer lastUpdateEvents = 0;
	
	public Integer lastUpdateAdded = 0;
	
	public String lastUpdateError;
	
	private Integer prio = 50;
	
	private Boolean showByDefault = true;

	/** bijv. homepage van de bron */
	public URL link;
	
	public String description;

	public Feed() { }
	
	public Feed(String name, String shortName) {
		this.shortName = shortName;
		this.name = name;
	}

	public Feed(String name, String shortName, URL url, URL link, String description, Integer prio, Boolean showByDefault)
	{
		this.name = name;
		this.shortName = shortName;
		this.url = url;
		this.link = link;
		this.description = description;
		this.prio = prio;
		this.showByDefault = showByDefault;
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

	@Override
	public int compareTo(Feed o)
	{
		return name.compareTo(o.name);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName()
	{
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}
	
	
}
