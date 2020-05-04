package com.example.demochat.service;

import com.example.demochat.model.Message;

import java.util.List;

public interface MessageService {
    public abstract List<Message> getAllMessages();
    public abstract void sendMessage(Message m);
    public abstract void broadcastMessage(Message m);
    public abstract Message sendMessageHTTP(Message m);
    public abstract void sendGroupMessage(Message m);
    public abstract Message sendGroupMessageHTTP(Message m);
}
