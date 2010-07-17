package net.bzzt.ical.aggregator.util;

import java.util.Collection;

import org.apache.commons.collections.MultiHashMap;

public class CategoryHelper
{
	public static <T, U> MultiMap<U,T> categorize(Collection<T> items, Categorizer<T, U> categorizer)
	{
		MultiMap<U,T> result = new MultiMapFacade<U,T>(new MultiHashMap());
		for (T item : items)
		{
			result.put(categorizer.getCategory(item), item);
		}
		return result;
	}
}
