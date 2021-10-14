package com.bernardguiang.allspeak.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Destination {
	private String address;
	private Set<String> sessions = new HashSet<>();
	private String languageCode;
}
