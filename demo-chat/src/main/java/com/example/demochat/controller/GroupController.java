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
@RequestMapping("/api/v1/groups")
public class GroupController {

    @Autowired
    private GroupRepository groups;

    @Autowired
    private UserRepository users;

    @Autowired
    private SimpMessageSendingOperations op;

    @GetMapping("/")
    public List<Group> getAllGroups() {
        return groups.findAll();
    }

    @PostMapping("/create")
    public Group createGroup(@RequestBody Group u) {
//        User owner = users.findById(u.getOwner()).orElse(null);
//        if (owner == null) { return null;};
//        Group newGroup = new Group(u.getName(), owner);
//        owner.setGroups(owner.getGroups().add(newGroup));
//        return groups.save(newGroup);
        System.out.println("####################################");
        System.out.println(u.getName());
        System.out.println(u);
        System.out.println("####################################");

        return groups.save(new Group(u.getName(), u.getOwner()));
    }

//    @GetMapping("/{id}")
//    public User getUser(@PathVariable long id) {
//        return users.findById(id).orElse(null);
//    }
//
//    @PutMapping("/{id}")
//    public User updateUser(@RequestBody User u) {
//        return users.save(u);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteUser(@PathVariable long id) {
//        users.deleteById(id);
//    }

}