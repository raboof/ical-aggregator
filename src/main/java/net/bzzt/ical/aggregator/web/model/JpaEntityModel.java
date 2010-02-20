package net.bzzt.ical.aggregator.web.model;

import java.io.Serializable;

import javax.persistence.Persistence;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;

import net.bzzt.ical.aggregator.model.Feed;
import net.bzzt.ical.aggregator.service.FeedService;
import net.bzzt.ical.aggregator.service.impl.FeedServiceImpl;

public class JpaEntityModel<T extends Identifiable<?>> extends AbstractEntityModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private FeedService feedService;
	
	public JpaEntityModel(T entity) {
		super(entity);
		InjectorHolder.getInjector().inject(this);
	}

	public JpaEntityModel(Class<T> clazz, Serializable id)
	{
		super(clazz, id);
		InjectorHolder.getInjector().inject(this);
	}

	@Override
	protected T load(Class<T> clazz, Serializable id) {
		return feedService.load(clazz, id);
	}

}
