package com.example.demochat.configuration;

import com.example.demochat.service.MessageService;
import com.example.demochat.service.WebSocketAuthenticatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

@Configuration
public class AuthChannelInterceptorImpl implements ChannelInterceptor {

    @Autowired
    private WebSocketAuthenticatorService webSocketAuthenticatorService;

    @Autowired
    private MessageService messageService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) throws AuthenticationException {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            //Añadir las condiciones deseadas de seguridad y lanzar excepción si no se pasan
            //message.getHeaders().get("simpDestination") --> para ver el destino de la subscripción: "/topic/*"
            List<String> authorizations = accessor == null ? null : accessor.getNativeHeader("Authorization");
            String token = authorizations == null || authorizations.size() == 0 ? null : authorizations.get(0).substring(7);

            //Validate and convert to a Principal based on your own requirements e.g.
            try{
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFail(token);
                System.out.println(user);
                accessor.setUser(user);
            } catch (Exception e) {
                throw new BadCredentialsException("Bad credentials for user");
            }

            return message;

            // not documented anywhere but necessary otherwise NPE in StompSubProtocolHandler!
            //accessor.setLeaveMutable(true);
            //return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            System.out.println(accessor.getDestination());
            MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
            com.example.demochat.model.Message m = (com.example.demochat.model.Message) jacksonMessageConverter.fromMessage(message, com.example.demochat.model.Message.class);
            messageService.processMessageSent(m);
        }

        return message;
    }
}
