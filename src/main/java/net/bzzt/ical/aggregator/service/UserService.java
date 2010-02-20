package net.bzzt.ical.aggregator.service;

import net.bzzt.ical.aggregator.model.User;

public interface UserService
{

	void saveOrUpdate(User user);

	User find(String username, String password);

	User register(String username, String password);

}
