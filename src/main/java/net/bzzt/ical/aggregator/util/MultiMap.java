package net.bzzt.ical.aggregator.util;

import java.util.List;
import java.util.Set;

public interface MultiMap<T,U> extends org.apache.commons.collections.MultiMap
{
	@Override
	public List<U> get(Object key);

	@Override
	Set<T> keySet();
}
