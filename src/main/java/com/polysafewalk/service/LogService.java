package com.polysafewalk.service;

import com.polysafewalk.model.Log;

public interface LogService {

	public void createLog(long userId, long routeId);
	public Log getLog(long userId);
	public void deleteLog(Log log);

}
