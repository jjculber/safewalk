package com.polysafewalk.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Route {

	private long id;
	private Area fromArea;
	private Area toArea;
	private Date time;
	private int number;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Area getFromArea() {
		return fromArea;
	}

	public void setFromArea(Area fromArea) {
		this.fromArea = fromArea;
	}

	public Area getToArea() {
		return toArea;
	}

	public void setToArea(Area toArea) {
		this.toArea = toArea;
	}

	public String getTime() {
		return new SimpleDateFormat("h:mm a").format(time);
	}
	
	public Date getDateTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
