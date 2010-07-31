package net.bzzt.ical.aggregator.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.support.PropertyComparator;

public class PropertiesComparator<T> implements Comparator<T>
{
	private final List<PropertyComparator> comparators;
	
	public PropertiesComparator(String... properties)
	{
		comparators = new ArrayList<PropertyComparator>();
		for (String property : properties)
		{
			comparators.add(new PropertyComparator(property, false, true));
		}
	}

	@Override
	public int compare(T o1, T o2)
	{
		for (PropertyComparator comparator : comparators)
		{
			int result = comparator.compare(o1, o2);
			if (result != 0)
			{
				return result;
			}
		}
		return 0;
	}

}
