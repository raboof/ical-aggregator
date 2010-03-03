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
	
	public EventFilter(Date date)
	{
		this.fromDate = date;
		this.toDate = date;
	}

	public EventFilter()
	{
	}

}
