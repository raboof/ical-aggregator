package net.bzzt.ical.aggregator.web;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
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
				// TODO Auto-generated method stub
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
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

}
