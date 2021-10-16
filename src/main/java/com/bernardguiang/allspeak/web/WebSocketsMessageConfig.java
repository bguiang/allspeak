package com.bernardguiang.allspeak.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketsMessageConfig implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		registry.addEndpoint("/allspeak").setAllowedOrigins(
				"http://localhost:3000", // For Testing With the React Frontend
				"http://localhost:8080", 
				"https://localhost:8080");
		
		/*
		 * https://stomp-js.github.io/guide/stompjs/rx-stomp/ng2-stompjs/using-stomp-
		 * with-sockjs.html Spring Spring tutorials implicitly suggest that you need
		 * SockJS to use STOMP. That is incorrect, you only need SockJS if you need to
		 * support old browsers URL protocol conventions are different for WebSockets
		 * (ws:/wss:) and SockJS (http: or https:). Internal handshake sequences are
		 * different - so, some brokers will use different end points for both
		 * protocols. Neither of these allow custom headers to be set during the HTTP
		 * handshake.
		 */
		//registry.addEndpoint("/allspeak").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic");
		
		/*
		 * TODO: be able to send messages to specific userr. For the purpose of sending
		 * the user's servername back to the specific user. Cannot do this currently due
		 * to frontend STOMP client being unable to access session ID.
		 * https://newbedev.com/spring-websocket-sendtosession-send-message-to-specific-
		 * session https://www.baeldung.com/spring-websockets-send-message-to-user
		 */
		//		registry.setUserDestinationPrefix("/user");
	}

	
}
