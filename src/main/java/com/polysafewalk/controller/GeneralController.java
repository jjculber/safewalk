package com.polysafewalk.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.polysafewalk.service.UserService;

@Controller
public class GeneralController {

	@Autowired
	private AreaService areaService;
	
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
		Log log = areaService.getLog(user.getId());
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
		Log log = areaService.getLog(user.getId());
		areaService.deleteLog(log);

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

		map.put("routes", areaService.getRoutes(fromArea, toArea));

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
		areaService.createLog(user.getId(), route);
		
		Route selectedRoute = areaService.getRoute(route);
		
		Date time = selectedRoute.getDateTime();
		TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
		Calendar c = Calendar.getInstance(tz);
		c.set(Calendar.HOUR_OF_DAY, time.getHours());
		c.set(Calendar.MINUTE, time.getMinutes());
		c.add(Calendar.MINUTE, -10);
		areaService.scheduleNotification(user.getId(), route, c.getTime());

		return "selectThanks";
	}

}
