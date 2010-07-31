package net.bzzt.ical.aggregator.service.impl;

import java.io.Serializable;
import java.util.Date;

public class EventFilter implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Date fromDate = new Date();
	
	public Date toDate;
	
	public boolean resolveDuplicates = true;
	
	public boolean noHidden = true;
	
	public Integer maxRecurrence;
	
	public EventFilter(Date date, int maxRecurrence)
	{
		this.maxRecurrence = maxRecurrence;
		this.fromDate = date;
		this.toDate = date;
	}

	public EventFilter(Integer maxRecurrence)
	{
		this.maxRecurrence = maxRecurrence;
	}

}
