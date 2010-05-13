package net.bzzt.ical.aggregator.web.admin;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;

import net.bzzt.ical.aggregator.model.Right;
import net.bzzt.ical.aggregator.service.UserService;
import net.bzzt.ical.aggregator.web.AggregatorLayoutPage;
import net.bzzt.ical.aggregator.web.AggregatorSession;
import net.bzzt.ical.aggregator.web.EventVerificationPage;

public class EditPage extends AggregatorLayoutPage
{
	@SpringBean
	private UserService userService;

	public EditPage()
	{
		add(new BookmarkablePageLink<Void>("manageFeeds", ManageFeeds.class));
		add(new BookmarkablePageLink<Void>("addEvent", EventDetailPage.class));
		add(new BookmarkablePageLink<Void>("verifyEvents", EventVerificationPage.class).setVisible(userService
			.hasRight(AggregatorSession.get().getLoggedInUser(), Right.VERIFY_EVENTS)));
	}

}
