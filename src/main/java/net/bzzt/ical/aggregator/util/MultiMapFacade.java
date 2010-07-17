package net.bzzt.ical.aggregator.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;

@SuppressWarnings("unchecked")
public class MultiMapFacade<T, U> implements MultiMap<T, U>
{
	private final MultiHashMap multiMap;
	
	public MultiMapFacade(MultiHashMap multiHashMap)
	{
		this.multiMap = multiHashMap;
	}

	@Override
	public boolean containsValue(Object value)
	{
		return multiMap.containsValue(value);
	}

	@Override
	public List<U> get(Object key)
	{
		return (List<U>) multiMap.get(key);
	}

	@Override
	public Object put(Object key, Object value)
	{
		return multiMap.put(key, value);
	}

	@Override
	public U remove(Object key)
	{
		return (U) multiMap.remove(key);
	}

	@Override
	public U remove(Object key, Object item)
	{
		return (U) multiMap.remove(key, item); 
	}

	@Override
	public int size()
	{
		return multiMap.size();
	}

	@Override
	public Collection<U> values()
	{
		return multiMap.values();
	}

	@Override
	public void clear()
	{
		multiMap.clear();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return multiMap.containsKey(key);
	}

	@Override
	public Set<Entry<T,U>> entrySet()
	{
		return multiMap.entrySet();
	}

	@Override
	public boolean isEmpty()
	{
		return multiMap.isEmpty();
	}

	@Override
	public Set<T> keySet()
	{
		return multiMap.keySet();
	}

	@Override
	public void putAll(Map m)
	{
		multiMap.putAll(m);
	}

}
