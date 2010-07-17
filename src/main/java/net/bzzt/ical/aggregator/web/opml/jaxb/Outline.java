package net.bzzt.ical.aggregator.web.opml.jaxb;

import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;

public class Outline
{
	@XmlAttribute
	public String text;
	
	@XmlAttribute
	public String type;
	
	@XmlAttribute
	public URL xmlUrl;
	
	@XmlAttribute(namespace="http://arnout.engelen.eu/ical-aggregator")
	public String shortName;

	@XmlAttribute
	public String description;

	@XmlAttribute
	public URL htmlUrl;

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
	
	public Outline(String text, String type, URL xmlUrl, String description, URL htmlUrl, String shortName)
	{
		this(text, type, xmlUrl, description, htmlUrl);
		this.shortName = shortName;
	}
}
