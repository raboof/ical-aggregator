package net.bzzt.ical.aggregator.web;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.bzzt.ical.aggregator.web.admin.EditPage;
import net.bzzt.ical.aggregator.web.ical.UpcomingEventsIcalPage;
import net.bzzt.ical.aggregator.web.opensocial.OpenSocialPage;
import net.bzzt.ical.aggregator.web.opml.OpmlPage;
import net.bzzt.ical.aggregator.web.rss.UpcomingEventsFeedPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.time.Time;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see net.bzzt.ical.aggregator.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	/**
	 * Constructor
	 */
	public WicketApplication() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init() {
		addComponentInstantiationListener(new SpringComponentInjector(this));
		
		getMarkupSettings().setStripWicketTags(true);
		
		mount(new MixedParamUrlCodingStrategy("/day", DayView.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/login", LoginPage.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/edit", EditPage.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/week", WeekView.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/feeds/upcoming/rss", UpcomingEventsFeedPage.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/feeds/upcoming/ical", UpcomingEventsIcalPage.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/feeds/opml", OpmlPage.class, new String[0]));
		mount(new MixedParamUrlCodingStrategy("/opensocial/week.xml", OpenSocialPage.class, new String[0]));
		
		super.init();
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new AggregatorSession(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Application#newConverterLocator()
	 */
	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator locator = new ConverterLocator();
		locator.set(URL.class, new IConverter() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Object convertToObject(String value, Locale locale) {
				if (StringUtils.isBlank(value)) {
					return null;
				}

				try {
					return new URL(value);
				} catch (Exception e) {
					throw new ConversionException(e);
				}
			}

			public String convertToString(Object value, Locale locale) {
				return ((URL) value).toExternalForm();
			}

		});

		locator.set(Time.class, new IConverter() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object convertToObject(String value, Locale locale) {
				return null;
			}

			@Override
			public String convertToString(Object value, Locale locale) {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				return format
						.format(new Date(((Time) value).getMilliseconds()));
			}
		});

		return locator;
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<? extends AggregatorLayoutPage> getHomePage() {
		return HomePage.class;
	}

	public static String getTitle()
	{
		String title = System.getProperty("title");
		if (title == null)
		{
			title = "ICalendar Aggregator";
		}
		return title;
	}

	public static String getLink()
	{
		HttpServletRequest httpServletRequest = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
		int serverPort = httpServletRequest.getServerPort();
		String scheme = httpServletRequest.getScheme();
		String portExtension = "";
		if (!("http".equalsIgnoreCase(scheme) && serverPort == 80 || "https".equalsIgnoreCase(scheme)
			&& serverPort == 443))
		{
			portExtension = ":" + serverPort;
		}
		String url = scheme + "://" + httpServletRequest.getServerName() + portExtension;
//			+ httpServletRequest.getContextPath();
		return url;
	}

}
