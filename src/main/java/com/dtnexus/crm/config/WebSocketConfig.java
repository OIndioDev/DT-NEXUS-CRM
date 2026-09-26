package com.dtnexus.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @SuppressWarnings({ "deprecation", "null" })
	@Override
    public void configureMessageBroker(@Nullable @NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @SuppressWarnings({ "deprecation", "null" })
	@Override
    public void registerStompEndpoints(@Nullable @NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat").withSockJS();
    }
}