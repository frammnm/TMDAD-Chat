package com.example.demochat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
        /*
        config.enableStompBrokerRelay("/topic")
                .setRelayHost("rattlesnake.rmq.cloudamqp.com")
                .setRelayPort(61613)
                .setSystemLogin("ioaosasv")
                .setSystemPasscode("0xE-99cOXc4shQndNSyyQIQp1wmycwL9")
                .setVirtualHost("ioaosasv");
         */
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").withSockJS();
    }

}