package com.example.demochat.service;

import com.example.demochat.model.*;

import java.util.List;

public interface GroupService {
    public abstract List<Group> getAllGroups();
    public abstract Group createGroup(String name, User owner);
    public abstract Group getGroupById(Long id);
    public abstract Group updateGroup(Group g);
//    public abstract List<Message> getGroupMessages(Long id);
    public abstract Group addMemberToGroup(long groupId, long userId);
    public abstract void deleteGroup(Long id);
}