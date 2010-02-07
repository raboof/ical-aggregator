package net.bzzt.ical.aggregator.web.model;

import java.io.Serializable;

import javax.persistence.Persistence;

import net.bzzt.ical.aggregator.service.impl.FeedServiceImpl;

public class JpaEntityModel<T extends Identifiable<?>> extends AbstractEntityModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JpaEntityModel(T entity) {
		super(entity);
	}

	@Override
	protected T load(Class clazz, Serializable id) {
		return (T) FeedServiceImpl.em.find(clazz, id);
	}

}
