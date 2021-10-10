package com.bernardguiang.allspeak.dto;

import com.bernardguiang.allspeak.model.User;

// TODO: use lombok
public class UserDTO {
	private String username;
	private String color;
	
	public UserDTO(User user) {
		username = user.getUsername();
		color = user.getColor();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	
}
