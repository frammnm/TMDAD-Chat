package com.example.demochat.service;

import com.example.demochat.model.*;

import java.util.List;

public interface GroupService {
    List<Group> getAllGroups();
    Group createGroup(String name, User owner);
    Group getGroupById(Long id);
    Group updateGroup(Group g);
//    List<Message> getGroupMessages(Long id);
    Group addMemberToGroup(long groupId, long userId);
    void deleteGroup(Long id);
}