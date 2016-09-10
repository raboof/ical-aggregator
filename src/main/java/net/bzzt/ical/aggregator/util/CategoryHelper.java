package net.bzzt.ical.aggregator.util;

import java.util.Collection;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class CategoryHelper
{
	public static <T, U> ListValuedMap<U,T> categorize(Collection<T> items, Categorizer<T, U> categorizer)
	{
		ListValuedMap<U,T> result = new ArrayListValuedHashMap<U,T>();
		for (T item : items)
		{
			result.put(categorizer.getCategory(item), item);
		}
		return result;
	}
}
