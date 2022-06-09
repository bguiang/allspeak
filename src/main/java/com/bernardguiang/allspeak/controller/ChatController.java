package com.bernardguiang.allspeak.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bernardguiang.allspeak.model.Message;

@Controller
public class ChatController {
	
	private SimpMessagingTemplate simpMessagingTemplate;
	
	public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@MessageMapping("/message") // /app/message
	@SendTo("/chatroom/public")
	private Message receivePublicMessage(@Payload Message message) {
		System.out.println(message.getMessage());
		return message;
	}
	
	// When sending a private message to a specific user
	// we need to dynamically create a "topic"
	// To do that, we use SimpMessagingTemplate
	@MessageMapping("/private-message") // /app/message
	private Message receivePrivateMessage(@Payload Message message) {
		System.out.println(message.getMessage());
		// convertAndSendToUser will use the prefix defined in WebsocketConfig
		simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message); // /user/{username}
		
		return message;
	}
}
