package com.polysafewalk.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.polysafewalk.model.Log;
import com.polysafewalk.model.Route;
import com.polysafewalk.model.User;
import com.polysafewalk.service.AreaService;
import com.polysafewalk.service.LogService;
import com.polysafewalk.service.NotificationService;
import com.polysafewalk.service.RouteService;
import com.polysafewalk.service.UserService;

@Controller
public class GeneralController {

	@Autowired
	private AreaService areaService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private UserService userService;

	@RequestMapping("/")
	public String home(
			@RequestParam(value = "error", required = false) String error,
			Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");
		map.put("error", error);

		return "home";
	}

	@RequestMapping("/home")
	@Secured("ROLE_USER")
	public String home(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Log log = logService.getLog(user.getId());
		map.put("currentRoute", log);
		map.put("user", user);

		if (user.getConfirmKey() != null && !user.getConfirmKey().isEmpty()) {
			return "redirect:/confirmPending";
		}
		return "userHome";
	}

	@RequestMapping("/confirmPending")
	@Secured("ROLE_USER")
	public String confirmPending(Map<String, Object> map,
			HttpServletRequest request, HttpServletResponse response) {

		User loggedIn = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		User user = userService.getUserById(loggedIn.getId());

		if (user != null && user.isActive()) {
			loggedIn.setConfirmKey(null);
			return "redirect:/home";
		}

		map.put("title", "PolySafeWalk");

		return "confirmPending";
	}

	@RequestMapping("/signupform")
	public String signupform(
			@RequestParam(value = "error", required = false) String error,
			Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {
		
		map.put("error", error);

		map.put("title", "PolySafeWalk");

		return "signupform";
	}

	@RequestMapping("/cancel")
	@Secured("ROLE_USER")
	public String cancel(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Log log = logService.getLog(user.getId());
		logService.deleteLog(log);

		return "redirect:/home";
	}

	@RequestMapping("/create")
	@Secured("ROLE_USER")
	public String create(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		map.put("fromAreas", areaService.getFromAreas());
		map.put("toAreas", areaService.getToAreas());

		return "create";
	}

	@RequestMapping("/select")
	@Secured("ROLE_USER")
	public String select(@RequestParam long fromArea,
			@RequestParam long toArea, Map<String, Object> map,
			HttpServletRequest request, HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		map.put("routes", routeService.getRoutes(fromArea, toArea));

		return "select";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping("/selectRoute")
	@Secured("ROLE_USER")
	public String selectRoute(@RequestParam long route,
			Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		logService.createLog(user.getId(), route);
		
		Route selectedRoute = routeService.getRoute(route);
		
		Date time = selectedRoute.getDateTime();
		
		DateTimeZone tz = DateTimeZone.forID("America/Los_Angeles");
		DateTime dt = new DateTime(tz).withHourOfDay(time.getHours()).withMinuteOfHour(time.getMinutes());
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(fmt.print(dt));
		
		dt = dt.minusMinutes(10);

		System.out.println(fmt.print(dt));
		
		notificationService.scheduleNotification(user.getId(), route, dt);

		return "selectThanks";
	}

}
