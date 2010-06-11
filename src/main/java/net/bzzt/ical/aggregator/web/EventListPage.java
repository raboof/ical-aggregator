package net.bzzt.ical.aggregator.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Homepage
 */
public class EventListPage extends AggregatorLayoutPage {

	private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public EventListPage(final PageParameters parameters) {
    	init();
    }

	private void init() {

		refresh(null);
	}

	@Override
	public void refresh(AjaxRequestTarget target) {
		EventListPanel eventListPanel = new EventListPanel("calendar");
		eventListPanel.setOutputMarkupId(true);
		addOrReplace(eventListPanel);
	
		if (target != null)
		{
			target.addComponent(eventListPanel);
		}
	}
	
	
}
