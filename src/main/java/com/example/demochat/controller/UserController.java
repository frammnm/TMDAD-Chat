package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.*;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.util.MimeTypeUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demochat.service.MyUserDetailsService;
import com.example.demochat.configuration.JwtTokenUtil;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository users;

    @Autowired
    private SimpMessageSendingOperations op;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil TokenUtil;

    @GetMapping("/")
    @JsonView(AppViews.Public.class)
    public List<User> getAllUsers() {
        return users.findAll();
    }

    @PostMapping("/signup")
    public User createUser(@RequestBody User u) {

        String password = passwordEncoder.encode(u.getPassword());
        return users.save(new User(u.getUsername(), password));
    }


    @PostMapping("/signin")
    @JsonView(AppViews.Public.class)
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest auth) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            auth.getUsername(),
                            auth.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(auth.getUsername());
        final String token = TokenUtil.generateToken(userDetails);
        final User user = userDetails.getUser();
        return ResponseEntity.ok(new AuthenticationResponse(token, user));
    }

    @GetMapping("/{id}")
    @JsonView(AppViews.Public.class)
    public User getUser(@PathVariable long id) {
        return users.findById(id).orElse(null);
    }

    @GetMapping("/byUsername/{username}")
    @JsonView(AppViews.Public.class)
    public User getUser(@PathVariable String username) {
        return users.findByUsername(username);
    }

    @PutMapping("/{id}")
    @JsonView(AppViews.Public.class)
    public User updateUser(@RequestBody User u) {
        return users.save(u);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        users.deleteById(id);
    }
}