package net.bzzt.ical.aggregator;

import junit.framework.TestCase;
import net.bzzt.ical.aggregator.web.HomePage;
import net.bzzt.ical.aggregator.web.WicketApplication;

import org.apache.wicket.util.tester.WicketTester;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends TestCase
{
	private WicketTester tester;

	@Override
	public void setUp()
	{
//		WicketApplication application = new WicketApplication();
//		tester = new WicketTester(application);
	}
	
	public void testTest()
	{
		
	}

//	public void testRenderMyPage()
//	{
//		//start and render the test page
//		tester.startPage(HomePage.class);
//
//		//assert rendered page class
//		tester.assertRenderedPage(HomePage.class);
//
//		//assert rendered label component
//		tester.assertLabel("message", "If you see this message wicket is properly configured and running");
//	}
}
