package com.bernardguiang.allspeak.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.bernardguiang.allspeak.dto.UserDTO;
import com.bernardguiang.allspeak.model.ChatMessage;
import com.bernardguiang.allspeak.model.Destination;
import com.bernardguiang.allspeak.model.MessageType;
import com.bernardguiang.allspeak.model.User;

//@Component
@Service
public class WebSocketService {
	
	private final SimpMessagingTemplate sendingOperations;
	private Map<String, Destination> languageDestinationMap;
	private Map<String, User> userMap;
	
	@Autowired
	public WebSocketService(final SimpMessagingTemplate sendingOperations) {
		this.sendingOperations = sendingOperations;
		languageDestinationMap = new HashMap<>();
		userMap = new HashMap<>();
	}
	
	public void addUserSession(String username, String sessionId) {
		
		User user = new User();
		user.setSessionId(sessionId);
		user.setColor(generateRandomDarkColorHSL());
		user.setUsername(username);
		userMap.put(username, user);
		
		List<UserDTO> usersList = createUserList(userMap.values());
		sendingOperations.convertAndSend("/topic/users", usersList);
	}
	
	/* TODO: handle the same usernames by adding a random id suffix. 
	 * Cannot do this currently due to frontend STOMP client being unable to access session ID 
	 * which is needed to send a message to specific users
	 */
//	public String addUserSession(String username, String sessionId) {
//		
//		String usernameStored = username;
//		
//		User user = new User();
//		user.setSessionId(sessionId);
//		user.setColor(generateRandomDarkColorHSL());
//		
//		boolean stored = false;
//		Random random = new Random();
//		while(!stored) {
//			String id = String.format("%04d", random.nextInt(10000));
//			usernameStored = username + "#" +  id;
//			
//			if(!userMap.containsKey(usernameStored)) {
//				user.setUsername(usernameStored);
//				userMap.put(usernameStored, user);
//				stored = true;
//			}
//		}
//		
//		List<UserDTO> usersList = createUserList(userMap.values());
//		sendingOperations.convertAndSend("/topic/users", usersList);
//		
//		return usernameStored;
//	}
	
	public List<Destination> getActiveDestinations() {
		return new ArrayList<>(languageDestinationMap.values());
	}
	
	public List<UserDTO> getUsers() {
		return createUserList(userMap.values());
	}
	
	@EventListener
	public void handleWebSocketConnectListener(final SessionConnectedEvent event) {
		String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
		System.out.println("New Session Connected: " + sessionId);
	}
	
	@EventListener
	public void handleSessionSubscribeEvent(final SessionSubscribeEvent event) {
		GenericMessage message = (GenericMessage) event.getMessage();
		String simpDestination = (String) message.getHeaders().get("simpDestination");
		String simpSessionId = (String) message.getHeaders().get("simpSessionId");

		System.out.println("Session " + simpSessionId + " subscribed to : " + simpDestination);
		
		// Exclude "/topic/public" from language destinations
		if(!simpDestination.equalsIgnoreCase("/topic/public") && !simpDestination.equalsIgnoreCase("/topic/users")) {
			System.out.println("Not Public, add destination subscription");
			if(languageDestinationMap.containsKey(simpDestination)) {
				Destination destination = languageDestinationMap.get(simpDestination);
				destination.getSessions().add(simpSessionId);
			}
			else {
				Destination destination = new Destination();
				destination.setAddress(simpDestination);
				String language = simpDestination.substring(7); // skip "/topic/"
				destination.setLanguageCode(language);
				destination.getSessions().add(simpSessionId);
				languageDestinationMap.put(simpDestination, destination);
			}
		}
		else {
			System.out.println("Is Public, skip destination subscription");
		}
	}
	
	@EventListener
	public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
		
		GenericMessage message = (GenericMessage) event.getMessage();
		String simpDestination = (String) message.getHeaders().get("simpDestination");
		String simpSessionId = (String) message.getHeaders().get("simpSessionId");
		
		final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		
		final String username = (String) headerAccessor.getSessionAttributes().get("username");
		System.out.println("User " + username + " with session: " + simpSessionId + " disconnected");

		userMap.remove(username);
		List<UserDTO> usersList = createUserList(userMap.values());
		sendingOperations.convertAndSend("/topic/users", usersList);
		
		if(languageDestinationMap.containsKey(simpDestination)) {
			Destination destination = languageDestinationMap.get(simpDestination);
			Set<String> sessions = destination.getSessions();
			sessions.remove((simpSessionId));
			
			if(sessions.isEmpty())
				languageDestinationMap.remove(simpDestination);
		}
		
		final ChatMessage chatMessage = ChatMessage.builder()
				.type(MessageType.DISCONNECT)
				.content(username + " has left the chat")
				.build();
		
		sendingOperations.convertAndSend("/topic/public", chatMessage);
	}
	
	
	private List<UserDTO> createUserList(Collection<User> usersSet) {
		List<User> usersListAlphabetical = new ArrayList<>();
		usersListAlphabetical.addAll(usersSet);
		Collections.sort(usersListAlphabetical);
		
		List<UserDTO> usersListDTO = new ArrayList<>();
		for(User u : usersListAlphabetical) {
			System.out.println(u.toString());
			usersListDTO.add(new UserDTO(u));
		}
		
		return usersListDTO;
	}
	
	private String generateRandomDarkColorHSL() {
		Random rand = new Random();
		int upperbound = 361; // generate a hue value with the range of 0-360
		int hue = rand.nextInt(upperbound);
		
		// lightness is set to 45% to prevent very bright colors from being generated
		return "hsl(" + hue + ", 100%, 45%)";
	}
}
