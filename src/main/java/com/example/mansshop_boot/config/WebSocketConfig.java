package com.example.mansshop_boot.config;

import com.example.mansshop_boot.config.jwt.JWTTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final JWTTokenProvider tokenProvider;

	@Value("#{jwt['token.all.prefix']}")
	private String tokenPrefix;

	@Value("#{jwt['token.access.header']}")
	private String accessHeader;

	@Value("#{jwt['cookie.ino.header']}")
	private String inoHeader;

	public WebSocketConfig(JWTTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.addInterceptors(
						new HandshakeInterceptor() {
							@Override
							public boolean beforeHandshake(ServerHttpRequest request,
														   ServerHttpResponse response,
														   WebSocketHandler wsHandler,
														   Map<String, Object> attributes) throws Exception {
								if(request instanceof ServletServerHttpRequest) {
									HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

									Cookie[] cookies = servletRequest.getCookies();

									if(cookies != null) {
										for(Cookie cookie : cookies) {
											if(cookie.getName().equals(inoHeader))
												attributes.put("ino", cookie.getValue());
										}
									}else
										log.warn("WebSocket Connection cookie is null");
								}

								return true;
							}

							@Override
							public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
						}
				)
				.withSockJS()
				.setSessionCookieNeeded(true)
				.setHeartbeatTime(25000)
				.setDisconnectDelay(30000);
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue");

		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(
				new ChannelInterceptor() {
					@Override
					public Message<?> preSend(Message<?> message, MessageChannel channel) {
						StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

						if(accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
							String token = accessor.getFirstNativeHeader(accessHeader);
							String inoValue = (String) accessor.getSessionAttributes().get("ino");

							if (token != null) {
								String tokenValue = token.replace(tokenPrefix, "");
								String userId = tokenProvider.verifyAccessToken(tokenValue, inoValue);

								if(userId != null && !userId.equals("WRONG_TOKEN") && !userId.equals("TOKEN_EXPIRATION") && !userId.equals("TOKEN_STEALING")) {
									accessor.setUser(() -> userId);
									log.info("WebSocket Principal set : {}", userId);
								}
							}
						}

						return message;
					}
				}
		);
	}
}
