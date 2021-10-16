package com.bernardguiang.allspeak.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.bernardguiang.allspeak.dto.UserDTO;

class WebSocketServiceTest {

	private WebSocketService underTest;
	private SimpMessagingTemplate sendingOperations;
	
	@BeforeEach
	void setUp() throws Exception {
		sendingOperations = Mockito.mock(SimpMessagingTemplate.class);
		underTest = new WebSocketService(sendingOperations);
	}

	@Test
	void itShouldAddUserSession() {
		// Given 
		String username = "user123";
		String sessionId = "asdf-asdf";
		ArgumentCaptor<List<UserDTO>> userListCaptor = ArgumentCaptor.forClass(List.class);
		
		// When
		underTest.addUserSession(username, sessionId);
		
		// Then
		Mockito.verify(sendingOperations).convertAndSend(Mockito.eq("/topic/users"), userListCaptor.capture());
		List<UserDTO> users = userListCaptor.getValue();
		assertEquals(1, users.size());
		
		UserDTO user = users.get(0);
		assertEquals("user123", user.getUsername());
		assertNotNull(user.getColor());
	}
	
	@Test
	void itshouldGetActiveDestinations() {
		// Given
		// When
		// Then
	}
	
	@Test
	void itShouldGetUsers() {
		// Given 
		String username = "user123";
		String sessionId = "asdf-asdf";
				
		// When
		doNothing().when(sendingOperations).convertAndSend(Mockito.anyString(), Mockito.anyList());
		underTest.addUserSession(username, sessionId);
		List<UserDTO> users = underTest.getUsers();
				
		// Then
		assertEquals(1, users.size());
				
		UserDTO user = users.get(0);
		assertEquals("user123", user.getUsername());
		assertNotNull(user.getColor());
	}

	@Test
	void itShouldHandleSessionSubscribeEvent() {
		// Given 
		SessionSubscribeEvent sessionSubscribeEvent = Mockito.mock(SessionSubscribeEvent.class);
		
		// When
		//when(sessionSubscribeEvent).getMessage(); // TODO: how to deal with the typcasting within the method call?
		
		// Then
	}
}
