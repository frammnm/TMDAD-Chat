package com.example.demochat.service;

import com.example.demochat.model.Message;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages();
    void sendMessage(Message m);
    void broadcastMessage(Message m);
    void sendGroupMessage(Message m);
}
