package net.bzzt.ical.aggregator.util;

import java.util.Collection;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

public class CategoryHelper
{
	public static <T, U> MultiMap categorize(Collection<T> items, Categorizer<T, U> categorizer)
	{
		MultiMap result = new MultiHashMap();
		for (T item : items)
		{
			result.put(categorizer.getCategory(item), item);
		}
		return result;
	}
}
