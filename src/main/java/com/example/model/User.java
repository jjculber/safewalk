package com.example.model;

public class User {

	private long id;
	private String email;
	private String firstName;
	private String lastName;
	private String confirmKey;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getConfirmKey() {
		return confirmKey;
	}

	public void setConfirmKey(String confirmKey) {
		this.confirmKey = confirmKey;
	}

}
