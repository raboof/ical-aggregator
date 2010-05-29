package net.bzzt.ical.aggregator.web.addthis;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * TODO: make 'addthis username' configurable and optional.
 * 
 * @author arnouten
 */
public class AddThisPanel extends Panel implements IHeaderContributor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String FACEBOOK = "facebook";
	
	public static final String TWITTER = "twitter";
	
	public static final String HYVES = "hyves";
	
	public static final String EMAIL = "email";
	
	public static final String FAVORITES = "favorites";
	
	public static final String PRINT = "print";
	
	private final String username;
	
	public AddThisPanel(String id, String title, String description, String url, String username)
	{
		this(id, title, description, url, Arrays.asList(new String[] {FACEBOOK, TWITTER, HYVES, EMAIL, FAVORITES }), username);
	}

	public AddThisPanel(String id, final String title, final String description, final String url, List<String> targets, String username)
	{
		super(id);
		
		this.username = username;
		
		WebMarkupContainer expanded = new WebMarkupContainer("expanded");
		addMetadata(expanded, title, description, url);
		String bookmarkUrl = "http://addthis.com/bookmark.php?v=250";
		if (StringUtils.isNotBlank(username))
		{
			bookmarkUrl += "&amp;username=" + username;
		}
		expanded.add(new AttributeModifier("href", new Model<String>(bookmarkUrl)));
		add(expanded);
		
		add(new ListView<String>("targets", targets)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item)
			{
				addMetadata(item, title, description, url);
				item.add(new AttributeModifier("class", new Model<String>("addthis_button_" + item.getModelObject())));
			}
			
		});
	}

	private void addMetadata(WebMarkupContainer link, String title, String description, String url)
	{
		if (StringUtils.isNotBlank(title))
		{
			link.add(new AttributeModifier("addthis:title", true, new Model<String>(title)));
		}
		if (StringUtils.isNotBlank(url))
		{
			link.add(new AttributeModifier("addthis:url", true, new Model<String>(url)));
		}
		if (StringUtils.isNotBlank(description))
		{
			link.add(new AttributeModifier("addthis:description", true, new Model<String>(description)));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		String jsUrl = "http://s7.addthis.com/js/250/addthis_widget.js#domready=1";
		if (StringUtils.isNotBlank(username))
		{
			jsUrl += "#username=" + username;
		}
		response.renderOnLoadJavascript("var script = '" + jsUrl + "'; if (window.addthis){ window.addthis = null; } $.getScript( script );");
	}

}
