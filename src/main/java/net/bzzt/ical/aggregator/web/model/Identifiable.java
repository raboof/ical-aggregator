package net.bzzt.ical.aggregator.web.model;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {
	T getId();
}
