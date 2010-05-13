package net.bzzt.ical.aggregator.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.bzzt.ical.aggregator.web.model.Identifiable;

/** 'user' is not a valid database name in postgresql */
@Entity(name="Users")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable, Identifiable<Long>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	public Long id;
	
	@Column(unique=true, nullable=false)
	private String username;
	
	@Column(unique=false, nullable=false)
	private String password;

	public User() {};
	
	public User(@Nonnull String username2, @Nonnull String password2)
	{
		this.username = username2;
		if (password2 == null)
		{
			throw new IllegalArgumentException();
		}
		this.password = password2;
	}

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
}
