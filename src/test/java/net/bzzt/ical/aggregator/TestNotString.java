package net.bzzt.ical.aggregator;

import junit.framework.TestCase;

public class TestNotString extends TestCase
{
	public String notString(String str) {
		  if (str.startsWith("not "))
		    return str.substring(4);
		  else
		    return "not " + str;
		}

	public void testNotString()
	{
		assertEquals("bad", notString("not bad"));
	}
}
