package net.bzzt.ical.aggregator.web.opml.jaxb;

import java.io.Serializable;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;

public class Outline implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	public String text;
	
	@XmlAttribute
	public String type;
	
	@XmlAttribute
	public URL xmlUrl;
	
	@XmlAttribute
	public String description;

	@XmlAttribute
	public URL htmlUrl;

	@XmlAttribute(namespace="http://arnout.engelen.eu/ical-aggregator")
	public String shortName;
	
	@XmlAttribute(namespace="http://arnout.engelen.eu/ical-aggregator")
	public Integer priority;

	@XmlAttribute(namespace="http://arnout.engelen.eu/ical-aggregator")
	public Boolean showByDefault;

	public Outline()
	{
		
	}

	public Outline(String text, String type, URL xmlUrl, String description, URL htmlUrl)
	{
		super();
		this.text = text;
		this.type = type;
		this.xmlUrl = xmlUrl;
		this.description = description;
		this.htmlUrl = htmlUrl;
	}
	
	public Outline(String text, String type, URL xmlUrl, String description, URL htmlUrl, String shortName, Integer priority, Boolean showByDefault)
	{
		this(text, type, xmlUrl, description, htmlUrl);
		this.shortName = shortName;
		this.priority = priority;
		this.showByDefault = showByDefault;
	}
}
