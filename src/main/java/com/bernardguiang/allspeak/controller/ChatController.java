package com.bernardguiang.allspeak.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bernardguiang.allspeak.dto.UserDTO;
import com.bernardguiang.allspeak.model.ChatMessage;
import com.bernardguiang.allspeak.model.Destination;
import com.bernardguiang.allspeak.model.MessageType;
import com.bernardguiang.allspeak.service.WebSocketService;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

@Controller
public class ChatController {
	
	@Autowired
	private WebSocketService webSocketService;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	// Requires environment variable "GOOGLE_API_KEY" if using API Key
	// or "GOOGLE_APPLICATION_CREDENTIALS" if using Service Account.
	// It depends on your Google Cloud Platform project's authentication setup
	// https://cloud.google.com/docs/authentication/getting-started
	private Translate translate = TranslateOptions.getDefaultInstance().getService();
	
	@GetMapping(path = "/languages", produces = "application/json")
	public @ResponseBody List<Language> getAvailableLanguages() {
		
		List<Language> languages = translate.listSupportedLanguages();
		
		return languages;
	}
	
	@GetMapping(path = "/users", produces = "application/json")
	public @ResponseBody List<UserDTO> getUsers() {
		
		return webSocketService.getUsers();
	}

	private void convertAndSendToActiveDestinations(ChatMessage chatMessage) {
		
		String originalContent = chatMessage.getContent();
		List<Destination> activeDestinations = webSocketService.getActiveDestinations();
		System.out.println("Destinations: " + activeDestinations.size());
		System.out.println(activeDestinations.toString());
		for(Destination destination : activeDestinations) {
			
			Translation translation = translate.translate(
					originalContent,
					Translate.TranslateOption.targetLanguage(destination.getLanguageCode()));
			chatMessage.setContent(translation.getTranslatedText());
			template.convertAndSend(destination.getAddress(), chatMessage);
		}
	}
	
	@MessageMapping("/chat.send")
	public void sendMessage(@Payload final ChatMessage chatMessage) {
		convertAndSendToActiveDestinations(chatMessage); 
	}
	
	@MessageMapping("/chat.newUser")
	@SendTo("/topic/public")
	public ChatMessage newUser(
			@Payload ChatMessage chatMessage, 
			SimpMessageHeaderAccessor headerAccessor
		) {
		
		String username = chatMessage.getSender();
		
		/* TODO: handle the same usernames entered by adding a random id suffix to the saved username
		 * Cannot do this currently due to frontend STOMP client being unable to access session ID 
		 * which is needed to send a message to specific users
		 */
//		String usernameStored = webSocketService.addUserSession(username, sessionId);

		String sessionId = headerAccessor.getSessionId();
		webSocketService.addUserSession(username, sessionId);
		
		headerAccessor.getSessionAttributes().put("username", username);
		chatMessage.setContent(username + " joined");
		chatMessage.setType(MessageType.CONNECT);
		chatMessage.setSender(username);
		
		return chatMessage;
	}
}
