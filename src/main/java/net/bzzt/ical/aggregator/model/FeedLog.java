package net.bzzt.ical.aggregator.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.bzzt.ical.aggregator.web.model.Identifiable;

/**
 * 
 * @author arnouten
 *
 */
@Entity
public class FeedLog implements Identifiable<Long> {
	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public Feed feed;
	
	public String data;
	
	public Date date;
	
	public Long getId() {
		return id;
	}
}
