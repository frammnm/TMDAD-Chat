package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.util.MimeTypeUtils;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository users;

    @Autowired
    private SimpMessageSendingOperations op;

    @GetMapping("/")
    public List<User> getAllUsers() {
        return users.findAll();
    }

    @PostMapping("/signup")
    public User createUser(@RequestBody User u) {
        return users.save(new User(u.getUsername(), u.getPassword()));
    }


//    @PostMapping("/login")
//    public User createUser(@RequestBody Login lg) {
//        return users.findById(lg.getUsername(), lg.getPassword());
//    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return users.findById(id).orElse(null);
    }

    @GetMapping("/byUsername/{username}")
    public User getUser(@PathVariable String username) {
        return users.findByUsername(username);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User u) {
        return users.save(u);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        users.deleteById(id);
    }
}