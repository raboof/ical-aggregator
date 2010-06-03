package net.bzzt.ical.aggregator.web.model;

import java.io.Serializable;

import net.bzzt.ical.aggregator.service.FeedService;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class JpaEntityModel<T extends Identifiable<?>> extends AbstractEntityModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean(name="feedService")
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
	protected T load(Class<? extends T> clazz, Serializable id) {
		return feedService.load(clazz, id);
	}

}
