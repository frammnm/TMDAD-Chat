package com.example.demochat.service;

import com.example.demochat.configuration.JwtTokenUtil;
import com.example.demochat.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class WebSocketAuthenticatorService {

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    private MyUserDetailsService userService;

    // This method MUST return a UsernamePasswordAuthenticationToken instance, the spring security chain is testing it
    // with 'instanceof' later on. So don't use a subclass of it or any other class
    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(final String  token) throws AuthenticationException {
        try{
            CustomUserDetails cud = userService.loadUserByUsername(tokenUtil.extractUsername(token));
            return new UsernamePasswordAuthenticationToken(
                    cud.getUsername(),
                    null,
                    Collections.singleton((GrantedAuthority) () -> cud.getRole()) // MUST provide at least one role
            );
        }catch (Exception e){
            throw new BadCredentialsException("Bad credentials for user");
        }
    }
}

