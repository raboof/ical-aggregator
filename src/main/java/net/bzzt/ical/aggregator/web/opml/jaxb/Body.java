package net.bzzt.ical.aggregator.web.opml.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class Body
{
	@Nonnull
	public List<Outline> outline = new ArrayList<Outline>();
}
