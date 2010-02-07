package net.bzzt.ical.aggregator.web;

import org.apache.wicket.PageParameters;

/**
 * Homepage
 */
public class HomePage extends AggregatorLayoutPage {

	private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
    	init();
    }

	private void init() {

		add(new FeedSelection("feedSelection"));

		add(new EventListPanel("calendar"));
	}
	
	
}
