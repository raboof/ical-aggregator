package net.bzzt.ical.aggregator.web;

import java.io.Serializable;

import net.bzzt.ical.aggregator.model.User;
import net.bzzt.ical.aggregator.service.UserService;
import net.bzzt.ical.aggregator.web.admin.EditPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class LoginPanel extends Panel
{

	public class LoginForm extends Form<LoginModel>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LoginForm(String id)
		{
			super(id, new CompoundPropertyModel<LoginModel>(new LoginModel()));
			
			add(new TextField<String>("username").setRequired(true));
			add(new PasswordTextField("password"));
			add(new SubmitLink("register")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/* (non-Javadoc)
				 * @see org.apache.wicket.markup.html.form.SubmitLink#onSubmit()
				 */
				@Override
				public void onSubmit()
				{
					LoginModel loginModel = (LoginModel) getForm().getModelObject();
					if (StringUtils.isBlank(loginModel.username))
					{
						throw new IllegalStateException();
					}
					if (StringUtils.isBlank(loginModel.password))
					{
						throw new IllegalStateException();
					}
					User user = userService.register(loginModel.username, loginModel.password);
					if (user == null)
					{
						error("Registration failed");
					}
					else
					{
						AggregatorSession.get().login(user);
						setResponsePage(EditPage.class);
					}
				}
				
			});
			add(new SubmitLink("login")
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/* (non-Javadoc)
				 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
				 */
				@Override
				public void onSubmit()
				{
					User user = userService.find(getModelObject().username, getModelObject().password);
					if (user == null)
					{
						error("Login incorrect");
					}
					else
					{
						AggregatorSession.get().login(user);
						setResponsePage(EditPage.class);
					}
				}
			});
		}
		
	

	}

	public static class LoginModel implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String username;
		
		private String password;

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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean(name="userService")
	private UserService userService;
	
	public LoginPanel(String id)
	{
		super(id);
		User loggedIn = AggregatorSession.get().getLoggedInUser();
		
		WebMarkupContainer loggedInAs = new WebMarkupContainer("loggedInAs");
		Form<LoginModel> loginForm = new LoginForm("loginForm");
		if (loggedIn == null)
		{
			loggedInAs.setVisible(false);
			
		}
		else
		{
			loggedInAs.add(new Label("name", loggedIn.getUsername()));
			loggedInAs.add(new Link<Void>("logout")
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					AggregatorSession.get().invalidateNow();
					setResponsePage(WicketApplication.get().getHomePage());
				}
				
			});
			loginForm.setVisible(false);
        }
		add(loggedInAs);
		add(loginForm);
	}

}
