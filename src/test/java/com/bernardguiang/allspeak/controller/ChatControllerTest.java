package com.bernardguiang.allspeak.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.bernardguiang.allspeak.dto.UserDTO;
import com.bernardguiang.allspeak.model.ChatMessage;
import com.bernardguiang.allspeak.model.Destination;
import com.bernardguiang.allspeak.service.WebSocketService;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

class ChatControllerTest {

	private ChatController underTest;
	private WebSocketService webSocketService;
	private SimpMessagingTemplate simpMessagingTemplate;
	private static Translate translate;
	
	@BeforeEach
	void setUp() throws Exception {
		this.webSocketService = Mockito.mock(WebSocketService.class);
		this.simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
		
		translate = Mockito.mock(Translate.class);
		MockedStatic<TranslateOptions> translateOptionsMockStatic = Mockito.mockStatic(TranslateOptions.class);
		
		TranslateOptions mockTranslateOptions = Mockito.mock(TranslateOptions.class);
		translateOptionsMockStatic.when(() -> TranslateOptions.getDefaultInstance()).thenReturn(mockTranslateOptions);
		when(mockTranslateOptions.getService()).thenReturn(translate);
		
		underTest = new ChatController(webSocketService, simpMessagingTemplate);
		
		translateOptionsMockStatic.close();
	}

	@Test
	void itShouldGetAvailableLanguages() {
		// Given
		List<Language> languages = new ArrayList<Language>();
		
		// When
		when(translate.listSupportedLanguages()).thenReturn(languages);
		List<Language> languagesReceived = underTest.getAvailableLanguages();
		
		// Then
		assertNotNull(languagesReceived);
		assertEquals(languages, languagesReceived);
		
	}
	
	@Test
	void itShouldGetUsers() {
		// Given
		List<UserDTO> users = new ArrayList<>();
		
		// When
		when(webSocketService.getUsers()).thenReturn(users);
		List<UserDTO> usersReceived = underTest.getUsers();
		
		// Then
		assertNotNull(usersReceived);
		assertEquals(users, usersReceived);
	}

	@Test
	void itShouldSendMessageToActiveDestinations() {
		// Given
		Translation translation = Mockito.mock(Translation.class);
		
		ChatMessage chatMessage = ChatMessage.builder()
				.content("translate this")
				.sender("user1")
				.language("en")
				.build();
		List<Destination> activeDestinations = new ArrayList<>();
		Destination destination = new Destination();
		destination.setAddress("english");
		destination.setLanguageCode("en");
		activeDestinations.add(destination);
		
		// When	
		when(translate.translate(Mockito.anyString(), Mockito.any(TranslateOption.class))).thenReturn(translation);
		when(translation.getTranslatedText()).thenReturn("translated text");
		
		when(webSocketService.getActiveDestinations()).thenReturn(activeDestinations);
		underTest.sendMessageToActiveDestinations(chatMessage);
		
		// Then
		verify(simpMessagingTemplate, times(1)).convertAndSend(destination.getAddress(), chatMessage);
	}
	
	@Test
	void itShouldAddNewUser() {
		// Given
		SimpMessageHeaderAccessor headerAccessorMock = Mockito.mock(SimpMessageHeaderAccessor.class);
		ChatMessage chatMessage = ChatMessage.builder()
				.sender("user123")
				.build();
		
		// When
		when(headerAccessorMock.getSessionId()).thenReturn("session-123");
		ChatMessage messageSentToChannel = underTest.newUser(chatMessage, headerAccessorMock);
		
		// Then
		verify(webSocketService).addUserSession("user123", "session-123");
		assertEquals(messageSentToChannel, chatMessage);
	}
}
