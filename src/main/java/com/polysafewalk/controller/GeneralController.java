package com.polysafewalk.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.polysafewalk.model.Log;
import com.polysafewalk.model.User;
import com.polysafewalk.service.AreaService;

@Controller
public class GeneralController {

	@Autowired
	private AreaService areaService;
	
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

		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Log log = areaService.getLog(user.getId());
		map.put("currentRoute", log);
		
		if (user.getConfirmKey() != null && !user.getConfirmKey().isEmpty()) {
			return "redirect:/confirmPending";
		}
		return "userHome";
	}

	@RequestMapping("/confirmPending")
	@Secured("ROLE_USER")
	public String confirmPending(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		return "confirmPending";
	}

	@RequestMapping("/signupform")
	public String signupform(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		return "signupform";
	}

	@RequestMapping("/cancel")
	@Secured("ROLE_USER")
	public String cancel(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
	public String select(@RequestParam long fromArea, @RequestParam long toArea,
			Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");

		map.put("routes", areaService.getRoutes(fromArea, toArea));

		return "select";
	}

	@RequestMapping("/selectRoute")
	@Secured("ROLE_USER")
	public String selectRoute(@RequestParam long route,
			Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {

		map.put("title", "PolySafeWalk");
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		areaService.createLog(user.getId(), route);
		
		return "selectThanks";
	}
	
	

}
