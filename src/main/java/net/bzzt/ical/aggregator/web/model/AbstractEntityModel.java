package net.bzzt.ical.aggregator.web.model;

import java.io.Serializable;

import javax.persistence.EntityNotFoundException;


import org.apache.wicket.model.IModel;

public abstract class AbstractEntityModel<T extends Identifiable< ? >> implements IModel<T>
{
    private final Class clazz;
    private Serializable id;
 
    private T entity;
 
    public AbstractEntityModel(T entity)
    {
        clazz = entity.getClass();
        id = entity.getId();
        this.entity = entity;
    }
 
    public AbstractEntityModel(Class< ? extends T> clazz, Serializable id)
    {
        this.clazz = clazz;
        this.id = id;
    }
 
    public T getObject()
    {
        if (entity == null)
        {
            if (id != null)
            {
                entity = load(clazz, id);
                if (entity == null)
                {
                    throw new EntityNotFoundException("" + clazz + id);
                }
            }
        }
        return entity;
    }
 
    public void detach()
    {
        if (entity != null)
        {
            if (entity.getId() != null)
            {
                id = entity.getId();
                entity = null;
            }
        }
    }
 
    protected abstract T load(Class<T> clazz, Serializable id);
 
    public void setObject(T object)
    {
        throw new UnsupportedOperationException(getClass() +
                " does not support #setObject(T entity)");
    }
}