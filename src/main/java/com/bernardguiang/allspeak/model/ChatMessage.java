package com.bernardguiang.allspeak.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder // use builder pattern with lombok
public class ChatMessage {
	@Getter
	@Setter
	private MessageType type;
	@Setter
	@Getter
	private String content;
	@Setter
	@Getter
	private String language;
	@Getter
	@Setter
	private String sender;
	@Getter
	@Setter
	private String time;
}
