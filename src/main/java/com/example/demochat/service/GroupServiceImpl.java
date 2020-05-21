package com.example.demochat.service;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groups;

    @Autowired
    private UserRepository users;

    @Override
    public  List<Group> getAllGroups() {
        return groups.findAll();
    };

    @Override
    public  Group createGroup(String name, User owner) {
        return groups.save(new Group(name, owner));
    };

    @Override
    public  Group getGroupById(Long id) {
        return groups.findById(id).orElse(null);
    };

    @Override
    public  Group updateGroup(Group g) {
        return groups.save(g);
    };

//    @Override
//    public  List<Message> getGroupMessages(Long id) {
//
//    };

    @Override
    public  Group addMemberToGroup(long groupId, long userId) {

        Group currentGroup = groups.findById(groupId).orElse(null);
        User user = users.findById(userId).orElse(null);

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

    };

    @Override
    public  void deleteGroup(Long id) {

    };
}