package com.example.demochat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.host}")
    private String springStompHost;

    @Value("${spring.rabbitmq.port}")
    private int springStompPort;

    @Value("${spring.rabbitmq.username}")
    private String springStompUsername;

    @Value("${spring.rabbitmq.password}")
    private String springStompPassword;

    @Value("${spring.rabbitmq.virtual-host}")
    private String springStompVHost;

    @Autowired
    private AuthChannelInterceptorImpl authChannelInterceptorImpl;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        //config.enableSimpleBroker("/topic");

        config.enableStompBrokerRelay("/queue", "/topic")
                .setUserDestinationBroadcast("/topic/unresolved.user.dest")
                .setUserRegistryBroadcast("/topic/registry.broadcast")
                .setRelayHost(springStompHost)
                .setRelayPort(springStompPort)
                .setClientLogin(springStompUsername)
                .setClientPasscode(springStompPassword)
                .setSystemLogin(springStompUsername)
                .setSystemPasscode(springStompPassword)
                //CloudAMQP
                .setVirtualHost(springStompVHost);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorImpl);
    }
}
