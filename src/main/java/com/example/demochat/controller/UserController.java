package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.*;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository users;

//    @Autowired
//    private SimpMessageSendingOperations op;

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
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(users.findAll());
    }

    @PostMapping("/signup")
    @JsonView(AppViews.Public.class)
    public ResponseEntity<User> createUser(@RequestBody User u) {
        String password = passwordEncoder.encode(u.getPassword());
        User user = users.save(new User(u.getUsername(), password));
        return ResponseEntity.ok(user);
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
    @JsonView(AppViews.Public.class) //getAuthorities
    @PreAuthorize("#id == authentication.getPrincipal().getId() or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<User> getUser(@PathVariable long id) {

        User user = users.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/byUsername/{username}")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("#username == authentication.getPrincipal().getUsername() or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = users.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build(); //.body("Username not found")
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("#u.getId() == authentication.getPrincipal().getId() or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<User> updateUser(@RequestBody User u, @PathVariable long id) {

        User oldUser = users.findById(id).orElse(null);
        if (oldUser == null) {
            return ResponseEntity.notFound().build(); //.body("Username not found")
        }

        String password = u.getPassword();
        u.setPassword(passwordEncoder.encode(password));

        try {
            u.setRole(oldUser.getRole());
            User user = users.save(u);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); //"Wrong Object"
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.getPrincipal().getId() or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {

        User user = users.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build(); //.body("Username not found")
        }

        users.deleteById(id);
        return ResponseEntity.ok().build();
    }
}