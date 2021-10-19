package com.bernardguiang.allspeak.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.bernardguiang.allspeak.dto.UserDTO;
import com.bernardguiang.allspeak.model.ChatMessage;
import com.bernardguiang.allspeak.model.Destination;

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
		// Then
		List<Destination> destinations = underTest.getActiveDestinations();
		assertEquals(0, destinations.size());
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
		SessionSubscribeEvent sessionSubscribeEventMock = Mockito.mock(SessionSubscribeEvent.class);
		GenericMessage genericMessageMock = Mockito.mock(GenericMessage.class);
		MessageHeaders messageHeadersMock = Mockito.mock(MessageHeaders.class);
		
		// When
		when(sessionSubscribeEventMock.getMessage()).thenReturn(genericMessageMock); // TODO: how to deal with the typcasting within the method call?
		when(genericMessageMock.getHeaders()).thenReturn(messageHeadersMock);
		when(messageHeadersMock.get("simpDestination")).thenReturn("/topic/english");
		when(messageHeadersMock.get("simpSessionId")).thenReturn("session-123");
		underTest.handleSessionSubscribeEvent(sessionSubscribeEventMock);
		
		// Then
		List<Destination> destinations = underTest.getActiveDestinations();
		assertEquals(1, destinations.size());
		
		Destination destination = destinations.get(0);
		assertEquals("/topic/english", destination.getAddress());
		
		Set<String> sessions = destination.getSessions();
		assertEquals(1, sessions.size());
		assertTrue(sessions.contains("session-123"));
	}
	
	@Test
	void sessionSubscribeEventHandlerShouldAddNewSessionToExistingDestination() {
		// Given 
		SessionSubscribeEvent sessionSubscribeEventMock = Mockito.mock(SessionSubscribeEvent.class);
		GenericMessage genericMessageMock = Mockito.mock(GenericMessage.class);
		MessageHeaders messageHeadersMock = Mockito.mock(MessageHeaders.class);
		
		SessionSubscribeEvent sessionSubscribeEventMock2 = Mockito.mock(SessionSubscribeEvent.class);
		GenericMessage genericMessageMock2 = Mockito.mock(GenericMessage.class);
		MessageHeaders messageHeadersMock2 = Mockito.mock(MessageHeaders.class);
		
		// When
		when(sessionSubscribeEventMock.getMessage()).thenReturn(genericMessageMock); // TODO: how to deal with the typcasting within the method call?
		when(genericMessageMock.getHeaders()).thenReturn(messageHeadersMock);
		when(messageHeadersMock.get("simpDestination")).thenReturn("/topic/english");
		when(messageHeadersMock.get("simpSessionId")).thenReturn("session-111");
		
		when(sessionSubscribeEventMock2.getMessage()).thenReturn(genericMessageMock2); // TODO: how to deal with the typcasting within the method call?
		when(genericMessageMock2.getHeaders()).thenReturn(messageHeadersMock2);
		when(messageHeadersMock2.get("simpDestination")).thenReturn("/topic/english");
		when(messageHeadersMock2.get("simpSessionId")).thenReturn("session-222");
		
		underTest.handleSessionSubscribeEvent(sessionSubscribeEventMock);
		underTest.handleSessionSubscribeEvent(sessionSubscribeEventMock2);
		
		// Then
		List<Destination> destinations = underTest.getActiveDestinations();
		assertEquals(1, destinations.size());
		
		Destination destination = destinations.get(0);
		assertEquals("/topic/english", destination.getAddress());
		
		Set<String> sessions = destination.getSessions();
		assertEquals(2, sessions.size());
		assertTrue(sessions.contains("session-111"));
		assertTrue(sessions.contains("session-222"));
	}
	
	@Test
	void sessionSubscribeEventHandlerShouldNotAddTopicPublicToDestinations() {
		// Given 
		SessionSubscribeEvent sessionSubscribeEventMock = Mockito.mock(SessionSubscribeEvent.class);
		GenericMessage genericMessageMock = Mockito.mock(GenericMessage.class);
		MessageHeaders messageHeadersMock = Mockito.mock(MessageHeaders.class);
		
		// When
		when(sessionSubscribeEventMock.getMessage()).thenReturn(genericMessageMock); // TODO: how to deal with the typcasting within the method call?
		when(genericMessageMock.getHeaders()).thenReturn(messageHeadersMock);
		when(messageHeadersMock.get("simpDestination")).thenReturn("/topic/public");
		when(messageHeadersMock.get("simpSessionId")).thenReturn("session-123");
		underTest.handleSessionSubscribeEvent(sessionSubscribeEventMock);
		
		// Then
		List<Destination> destinations = underTest.getActiveDestinations();
		assertEquals(0, destinations.size());
	}
	
	@Test
	void itShouldHandleWebSocketDisconnectEvent() {
		// Given 
		String username = "user123";
		String sessionId = "session-123";
		
		SessionDisconnectEvent sessionDisconnectEventMock = Mockito.mock(SessionDisconnectEvent.class);
		GenericMessage genericMessageMock = Mockito.mock(GenericMessage.class);
		MessageHeaders messageHeadersMock = Mockito.mock(MessageHeaders.class);
		
		MockedStatic<StompHeaderAccessor> stompHeaderAccessorStaticMock = Mockito.mockStatic(StompHeaderAccessor.class);
		StompHeaderAccessor stompHeaderAccessorMock = Mockito.mock(StompHeaderAccessor.class);
		Map<String, Object> sessionAttributes = new HashMap<>();
		sessionAttributes.put("username", username);
		
		ArgumentCaptor<List<UserDTO>> userListCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<ChatMessage> disconnectMessageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
				
		SessionSubscribeEvent sessionSubscribeEventMock = Mockito.mock(SessionSubscribeEvent.class);
		GenericMessage genericMessageMock2 = Mockito.mock(GenericMessage.class);
		MessageHeaders messageHeadersMock2 = Mockito.mock(MessageHeaders.class);
				
		// When
		// ... Subscribing adds to the destinations
		when(sessionSubscribeEventMock.getMessage()).thenReturn(genericMessageMock2); // TODO: how to deal with the typcasting within the method call?
		when(genericMessageMock2.getHeaders()).thenReturn(messageHeadersMock2);
		when(messageHeadersMock2.get("simpDestination")).thenReturn("/topic/en");
		when(messageHeadersMock2.get("simpSessionId")).thenReturn("session-123");
		underTest.handleSessionSubscribeEvent(sessionSubscribeEventMock);
		
		// ... Add User adds to user-session list
		underTest.addUserSession(username, sessionId);
		
		// ... User disconnects
		when(sessionDisconnectEventMock.getMessage()).thenReturn(genericMessageMock); // TODO: how to deal with the typcasting within the method call?
		when(genericMessageMock.getHeaders()).thenReturn(messageHeadersMock);
		when(messageHeadersMock.get("simpDestination")).thenReturn("/topic/en");
		when(messageHeadersMock.get("simpSessionId")).thenReturn("session-123");
		
		stompHeaderAccessorStaticMock.when(() -> StompHeaderAccessor.wrap(Mockito.any())).thenReturn(stompHeaderAccessorMock);
		when(stompHeaderAccessorMock.getSessionAttributes()).thenReturn(sessionAttributes);
		
		underTest.handleWebSocketDisconnectListener(sessionDisconnectEventMock);
		
		// Then
		//... Users List After Adding User - underTest.addUserSession()
		Mockito.verify(sendingOperations, Mockito.times(2)).convertAndSend(Mockito.eq("/topic/users"), userListCaptor.capture());
		
		//... Message Sent to public After User Disconnects
		Mockito.verify(sendingOperations).convertAndSend(Mockito.eq("/topic/public"), disconnectMessageCaptor.capture());
		
		List<List<UserDTO>> userListValues = userListCaptor.getAllValues();
		assertEquals(2, userListValues.size());
		
		List<UserDTO> userListAfterAddingUser = userListValues.get(0);
		assertEquals(1, userListAfterAddingUser.size());
		UserDTO user = userListAfterAddingUser.get(0);
		assertEquals(username, user.getUsername());
		
		List<UserDTO> userListAfterUserDisconnects = userListValues.get(1);
		assertEquals(0, userListAfterUserDisconnects.size());
		
		ChatMessage disconnectMessage = disconnectMessageCaptor.getValue();
		assertEquals("user123 has left the chat", disconnectMessage.getContent());
	}
}
