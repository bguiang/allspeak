package com.bernardguiang.allspeak.model;

//TODO: use lombok
public class User implements Comparable<User>{
	private String username;
	private String sessionId;
	private String color;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public int compareTo(User u) {
	    if (username == null || u.getUsername() == null) {
	        return 0;
	      }
	    return username.compareTo(u.getUsername());
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", sessionId=" + sessionId + ", color=" + color + "]";
	}
	
	
}
