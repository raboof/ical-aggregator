package net.bzzt.ical.aggregator.web.opensocial;

import net.bzzt.ical.aggregator.web.EventListPanel;
import net.bzzt.ical.aggregator.web.WeekView;
import net.bzzt.ical.aggregator.web.WicketApplication;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public class OpenSocialPage extends WebPage
{
	public OpenSocialPage()
	{
		WebMarkupContainer modulePrefs = new WebMarkupContainer("modulePrefs");
		modulePrefs.add(new AttributeModifier("title", true, new Model<String>(WicketApplication.getTitle())));
//		modulePrefs.add(new AttributeModifier("description", true, new Model<String>(WicketApplication.getTitle())));
		modulePrefs.add(new AttributeModifier("author", true, new Model<String>("Arnout Engelen")));
		modulePrefs.add(new AttributeModifier("author_email", true, new Model<String>("arnout.engelen@gmail.com")));
//		modulePrefs.add(new AttributeModifier("thumbnail", true, new Model<String>(WicketApplication.getTitle())));
//		modulePrefs.add(new AttributeModifier("screenshot", true, new Model<String>(WicketApplication.getTitle())));
		modulePrefs.add(new AttributeModifier("height", true, new Model<Integer>(380)));
		
		add(modulePrefs);
		
		add(new Label("contentPrefix", "<![CDATA[").setEscapeModelStrings(false));
		
		WebMarkupContainer content = new EventListPanel("content");
		add(content);
		
		add(new Label("contentPostfix", "]]>").setEscapeModelStrings(false));
	}
	
}
