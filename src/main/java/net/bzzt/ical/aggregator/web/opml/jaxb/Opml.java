package net.bzzt.ical.aggregator.web.opml.jaxb;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="http://www.opml.org/spec2")
public class Opml
{
	@XmlAttribute
	public String version = "2.0";
	
	@Nonnull
	public Head head = new Head();
	
	@Nonnull
	public Body body = new Body();
}
