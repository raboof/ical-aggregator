package net.bzzt.ical.aggregator.web;

import java.io.Serializable;

import net.bzzt.ical.aggregator.model.User;
import net.bzzt.ical.aggregator.service.UserService;

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

	public class LoginModel implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		String username;
		
		String password;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private UserService userService;
	
	public LoginPanel(String id)
	{
		super(id);
		User loggedIn = AggregatorSession.get().getLoggedInUser();
		
		WebMarkupContainer loggedInAs = new WebMarkupContainer("loggedInAs");
		Form<LoginModel> loginForm = new Form<LoginModel>("loginForm", new CompoundPropertyModel<LoginModel>(new LoginModel()))
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
			 */
			@Override
			protected void onSubmit()
			{
				User user = userService.find(getModelObject().username, getModelObject().password);
				if (user == null)
				{
					error("Login incorrect");
				}
				else
				{
					AggregatorSession.get().login(user);
					setResponsePage(HomePage.class);
				}
			}
			
			
		};
		if (loggedIn == null)
		{
			loggedInAs.setVisible(false);
			loginForm.add(new TextField<String>("username"));
			loginForm.add(new PasswordTextField("password"));
			loginForm.add(new SubmitLink("register")
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
					User user = userService.register(loginModel.username, loginModel.password);
					if (user == null)
					{
						error("Registration failed");
					}
					else
					{
						AggregatorSession.get().login(user);
					}
				}
				
			}.setDefaultFormProcessing(false));
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
					setResponsePage(HomePage.class);
				}
				
			});
			loginForm.setVisible(false);
        }
		add(loggedInAs);
		add(loginForm);
	}

}
