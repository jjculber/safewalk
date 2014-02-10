package com.example.model;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {

	private long id;
	private Date time;
	private String fromAreaName;
	private String toAreaName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTime() {
		return new SimpleDateFormat("h:mm a").format(time);
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getFromAreaName() {
		return fromAreaName;
	}

	public void setFromAreaName(String fromAreaName) {
		this.fromAreaName = fromAreaName;
	}

	public String getToAreaName() {
		return toAreaName;
	}

	public void setToAreaName(String toAreaName) {
		this.toAreaName = toAreaName;
	}
}
