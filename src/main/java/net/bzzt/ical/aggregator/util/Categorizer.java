package net.bzzt.ical.aggregator.util;

public interface Categorizer<T1, T2> {
	T2 getCategory(T1 object);
}
