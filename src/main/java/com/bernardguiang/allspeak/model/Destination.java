package com.bernardguiang.allspeak.model;

import java.util.HashSet;
import java.util.Set;

//TODO: replace with lombok
public class Destination {
	private String address;
	private Set<String> sessions;
	private String languageCode;
	
	public Destination() {
		sessions = new HashSet<>();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Set<String> getSessions() {
		return sessions;
	}

	public void setSessions(Set<String> sessions) {
		this.sessions = sessions;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
}
