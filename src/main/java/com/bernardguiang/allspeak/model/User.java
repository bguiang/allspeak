package com.bernardguiang.allspeak.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Comparable<User>{
	private String username;
	private String sessionId;
	private String color;
	
	
	@Override
	public int compareTo(User u) {
	    if (username == null || u.getUsername() == null) {
	        return 0;
	      }
	    return username.compareTo(u.getUsername());
	}
}
