package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import com.example.demochat.service.GroupService;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    @Autowired
    private GroupRepository groups;

    @Autowired
    private UserRepository users;

    @Autowired
    private GroupService groupService;

    @GetMapping("/")
    @JsonView(AppViews.Public.class)
    public  ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }


    @PostMapping("/create")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("#u.getOwner().getId() == authentication.getPrincipal().getId() or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<Group> createGroup(@RequestBody Group u) {

        if (u.getName().equals("all")  ||  u.getName().equals("metrics.trending")  || u.getName().equals("metrics.chat")) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(groupService.createGroup(u.getName(), u.getOwner()));
    }

    @GetMapping("/{id}")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("authentication.getPrincipal().belongsToGroup(#id) or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<Group> getGroup(@PathVariable long id) {

        Group group = groupService.getGroupById(id);

        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(group);
    }

    @PutMapping("/{id}")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("#g.getOwner().getId() == authentication.getPrincipal().getId() or authentication.getPrincipal().getRole() =='ADMIN'")
    public  ResponseEntity<Group> updateGroup(@RequestBody Group g) {

        Group oldGroup = groupService.getGroupById(g.getId());
        if (oldGroup == null) {
            return ResponseEntity.notFound().build(); //.body("Username not found")
        }

        try {
            Group group = groupService.updateGroup(g);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); //"Wrong Object"
        }
    }

    @GetMapping("/{id}/messages")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("authentication.getPrincipal().belongsToGroup(#id) or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<List<Message>> getGroupMessages(@PathVariable long id) {

        Group group = groupService.getGroupById(id);

        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(group.getMessages());
    }

    @PutMapping("/addMember")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("authentication.getPrincipal().isGroupOwner(#req.getGroup_id()) or authentication.getPrincipal().getRole() =='ADMIN'")
    public ResponseEntity<Group> addMemberToGroup(@RequestBody AddMemberRequest req) {

        Group group = groupService.addMemberToGroup(req.getGroup_id(), req.getMember_id());

        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    @JsonView(AppViews.Public.class)
    @PreAuthorize("authentication.getPrincipal().isGroupOwner(#id) or authentication.getPrincipal().getRole() =='ADMIN'")
    public  ResponseEntity<?> deleteGroup(@PathVariable long id) {

        Group group = groupService.getGroupById(id);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        groupService.deleteGroup(id);

        Group groupDeleted = groupService.getGroupById(id);
        if (groupDeleted == null) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}