package com.polysafewalk.controller;

import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.polysafewalk.exception.UsernameNotUniqueException;
import com.polysafewalk.form.UserRegistrationForm;
import com.polysafewalk.model.Log;
import com.polysafewalk.model.Notification;
import com.polysafewalk.model.User;
import com.polysafewalk.service.AreaService;
import com.polysafewalk.service.UserService;

@Controller
public class UsersController {

	@Autowired
	private AreaService areaService;

	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender mailSender;

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

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
					false, "utf-8");
			String confirmUrl = "http://www.polysafewalk.com/confirm/"
					+ confirmKey;
			String htmlMsg = "Thank you for signing up!<br/><br/>"
					+ "Please confirm your email address:<br/>" + "<a href=\""
					+ confirmUrl + "\">" + confirmUrl + "</a><br/><br/>Thanks!";
			mimeMessage.setContent(htmlMsg, "text/html");
			helper.setTo(email);
			helper.setSubject("Thanks for signing up for PolySafeWalk!");
			helper.setFrom("info@polysafewalk.com");
			mailSender.send(mimeMessage);

			return "redirect:/home";

		} catch (UsernameNotUniqueException e) {
			return "redirect:/?error=Email%20already%20signed%20up";
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "redirect:/home";
	}

	@RequestMapping("/confirm/{confirmKey}")
	public String confirm(@PathVariable(value = "confirmKey") String confirmKey) {
		userService.confirmKey(confirmKey);
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (user != null) {
			user.setConfirmKey(null);
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
		List<Notification> notifications = areaService.getNotifications();
		for (Notification note : notifications) {
			System.out.println("Notif id: " + note.getId());
			User user = userService.getUserById(note.getUserId());
			Log log = areaService.getLog(user.getId());
			if (log != null && note.getRouteId() == log.getRouteId()) {
				System.out.println("sending email.");
				sendReminderEmail(note, user);
				areaService.markNotificationSent(note, 1);
			} else {
				System.out.println("not valid anymore.");
				areaService.markNotificationSent(note, 2);
			}

		}
	}

	private void sendReminderEmail(Notification notification, User user) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper;
			helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			String htmlMsg = "Your walk is scheduled to leave in less than 10 minutes."
					+ "<br/><br/>Thanks,<br/>PolySafeWalk";
			mimeMessage.setContent(htmlMsg, "text/html");
			helper.setTo(user.getEmail());
			helper.setSubject("Walk Reminder");
			helper.setFrom("info@polysafewalk.com");
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
