package com.polysafewalk.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.polysafewalk.exception.UsernameNotUniqueException;
import com.polysafewalk.form.UserRegistrationForm;
import com.polysafewalk.model.Log;
import com.polysafewalk.model.Notification;
import com.polysafewalk.model.User;
import com.polysafewalk.service.LogService;
import com.polysafewalk.service.NotificationService;
import com.polysafewalk.service.RouteService;
import com.polysafewalk.service.UserService;

@Controller
public class UsersController {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private LogService logService;

	@Autowired
	private UserService userService;

	@Autowired
	private RouteService routeService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String registerUser(UserRegistrationForm userRegistrationForm,
			HttpServletRequest request) {

		String email = userRegistrationForm.getEmail();
		String regex = "^[a-zA-z0-9]{1,15}@calpoly\\.edu$";
		if (email == null || email.isEmpty() || !email.matches(regex)) {
			return "redirect:/signupform?error=Email must be a valid calpoly email address.";
		}

		try {
			String confirmKey = UUID.randomUUID().toString();
			userService.createNewUser(userRegistrationForm, confirmKey);
			String password = userRegistrationForm.getPassword();
			doAutoLogin(email, password, request);

			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			user.setConfirmKey(confirmKey);

			sendConfirmationEmail(user);

			return "redirect:/home";

		} catch (UsernameNotUniqueException e) {
			return "redirect:/?error=Email%20already%20signed%20up";
		}
	}

	@RequestMapping("/confirm/{confirmKey}")
	public String confirm(@PathVariable(value = "confirmKey") String confirmKey) {
		userService.confirmKey(confirmKey);

		try {
			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			if (user != null) {
				user.setConfirmKey(null);
			}
		} catch (Exception e) {
			// ignore
		}
		return "confirmSuccess";
	}

	private void doAutoLogin(String username, String password,
			HttpServletRequest request) {
		try {
			// Must be called from request filtered by Spring Security,
			// otherwise
			// SecurityContextHolder is not updated
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					username, password);
			token.setDetails(new WebAuthenticationDetails(request));
			Authentication authentication = userService.authenticateUser(token);
			SecurityContextHolder.getContext()
					.setAuthentication(authentication);
		} catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			e.printStackTrace();
		}
	}

	@Scheduled(fixedRate = 60000)
	public void walkNotificationTask() {
		// every 60 seconds

		// get unsent notifications
		List<Notification> notifications = notificationService
				.getNotifications();
		for (Notification note : notifications) {
			System.out.println("Notif id: " + note.getId());
			User user = userService.getUserById(note.getUserId());
			Log log = logService.getLog(user.getId());
			if (log != null && note.getRouteId() == log.getRouteId()) {
				System.out.println("sending email.");
				sendReminderEmail(note, user);
				notificationService.markNotificationSent(note, 1);
			} else {
				System.out.println("not valid anymore.");
				notificationService.markNotificationSent(note, 2);
			}

		}
	}

	private void sendReminderEmail(final Notification notification,
			final User user) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setTo(user.getEmail());
				message.setFrom("info@polysafewalk.com");
				message.setSubject("Walk Reminder");

				Map<String, Object> model = new HashMap<String, Object>();
				model.put("user", user);
				model.put("count",
						routeService.countWalkers(notification.getRouteId()));

				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, "walk-reminder.vm", model);
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}

	private void sendConfirmationEmail(final User user) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setTo(user.getEmail());
				message.setFrom("info@polysafewalk.com");
				message.setSubject("Thanks for signing up for PolySafeWalk!");

				Map<String, Object> model = new HashMap<String, Object>();
				model.put("user", user);
				model.put("confirmKey", user.getConfirmKey());

				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, "registration-confirmation.vm", model);
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}

}
