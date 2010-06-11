package net.bzzt.ical.aggregator.web.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.bzzt.ical.aggregator.util.Categorizer;

import org.apache.commons.collections.MultiHashMap;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;

public abstract class CategorizedList<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends ListView<T1> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MultiHashMap objectMap = new MultiHashMap();
	
	public CategorizedList(String id, List<T2> objects,
			Categorizer<T2, T1> categorizer) {
		super(id); 
		for (T2 object : objects)
		{
			objectMap.put(categorizer.getCategory(object), object);
		}
	
		@SuppressWarnings("unchecked")
		ArrayList<T1> keys = new ArrayList<T1>(objectMap.keySet());
		Collections.sort(keys);
		setModel(new Model<ArrayList<T1>>(keys));
	}

	@Override
	protected void populateItem(ListItem<T1> item) {
		populateCaption(item);
		
		@SuppressWarnings("unchecked")
		ArrayList<? extends T2> sublist = (ArrayList<? extends T2>) objectMap.get(item.getDefaultModelObject());
		Collections.sort(sublist);
		item.add(new PropertyListView<T2>("children", new Model<ArrayList<? extends T2>>(sublist)){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<T2> item) {
				populateChild(item);
			}});
	}

	protected abstract void populateCaption(ListItem<T1> item);

	protected abstract void populateChild(ListItem<T2> item);
}
