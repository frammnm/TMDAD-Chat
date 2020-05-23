package com.example.demochat.service;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demochat.service.MyUserDetailsService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groups;

    @Autowired
    private UserRepository users;

    @PersistenceContext
    private EntityManager em;

    @Override
    public  List<Group> getAllGroups() {
        return groups.findAll();
    };

    @Override
    public  Group createGroup(String name, User owner) {
        User user = users.findById(owner.getId()).orElse(null);

        if (user == null) {
            return null;
        }

        Group group = groups.save(new Group(name, user));
        group.setOwner(user);

        return group;
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
    @Transactional
    public  void deleteGroup(Long id) {

        Group currentGroup = groups.findById(id).orElse(null);
        for (User user : currentGroup.getMembers()) {
            List<Group> userGroups = user.getGroups();
            userGroups.remove(currentGroup);
            user.setGroups(userGroups);
        }

        User owner = currentGroup.getOwner();
        List<Group> ownerGroups = owner.getOwnedGroups();
        ownerGroups.remove(currentGroup);

        em.remove(currentGroup);
        em.flush();

    };

}