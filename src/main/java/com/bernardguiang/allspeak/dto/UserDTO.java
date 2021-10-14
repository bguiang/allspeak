package com.bernardguiang.allspeak.dto;

import com.bernardguiang.allspeak.model.User;

import lombok.Data;

@Data
public class UserDTO {
	private String username;
	private String color;
	
	public UserDTO(User user) {
		username = user.getUsername();
		color = user.getColor();
	}
}
