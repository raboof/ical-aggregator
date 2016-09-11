package net.bzzt.ical.aggregator.service.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.bzzt.ical.aggregator.model.Right;
import net.bzzt.ical.aggregator.model.User;
import net.bzzt.ical.aggregator.service.UserService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.SUPPORTS)
public class UserServiceImpl implements UserService {
	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);
	
	@PersistenceContext(unitName="aggregatorPersistenceUnit")
	private EntityManager em;

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveOrUpdate(User user) {
		if (user.getId() != null)
		{
			user = em.merge(user);
		}
		em.persist(user);
	}

	@Override
	public User find(String username, String password)
	{
		Query query2 = em.createQuery("select username, password from Users u");
		LOG.info(query2.getResultList());

		Query query = em.createQuery("select u from Users u where username = :username and password = :password");
		query.setParameter("username", username);
		query.setParameter("password", password);
		
		@SuppressWarnings("unchecked")
		List<User> resultList = query.getResultList();
		
		if (resultList.isEmpty())
		{
			return null;
		}
		else if (resultList.size() != 1)
		{
			throw new IllegalStateException(String.valueOf(resultList.size()));
		}
		else
		{
			return resultList.get(0);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public User register(@Nonnull String username, @Nonnull String password)
	{
		User user = new User(username, password);
		em.persist(user);
		return user;
	}

	@Override
	public boolean hasRight(User loggedInUser, Right addEventDirect)
	{
		return loggedInUser != null && "raboof".equals(loggedInUser.getUsername());
	}
}
