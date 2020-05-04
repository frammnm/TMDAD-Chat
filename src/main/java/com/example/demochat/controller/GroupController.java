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
        System.out.println("####################################");
        System.out.println(u.getName());
        System.out.println(u);
        System.out.println("####################################");

        return groups.save(new Group(u.getName(), u.getOwner()));
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable long id) {
        return groups.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Group updateGroup(@RequestBody Group g) {
        return groups.save(g);
    }

    @GetMapping("/{id}/messages")
    public List<Message> getGroupMessages(@PathVariable long id) {
        Group group = groups.findById(id).orElse(null);

        if (group == null) {
            return null;
        }

        return group.getMessages();
    }

    @PutMapping("/addMember")
    public Group addMemberToGroup(@RequestBody Map<String, Long> reqObject) {
        Group currentGroup = groups.findById(reqObject.get("group_id")).orElse(null);
        User user = users.findById(reqObject.get("member_id")).orElse(null);

        if ((user == null) || (currentGroup == null)) {
            return null;
        } else {
            List<User> currentMembers = currentGroup.getMembers();
            currentMembers.add(user);
            currentGroup.setMembers(currentMembers);

            List<Group> userGroups = user.getGroups();
            userGroups.add(currentGroup);
            user.setGroups(userGroups);
            users.save(user);
        }

        return groups.save(currentGroup);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable long id) {
        groups.deleteById(id);
    }
}