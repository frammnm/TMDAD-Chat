package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.*;
import com.example.demochat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/")
    @JsonView(AppViews.Public.class)
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @MessageMapping("/message")
    public void sendMessage(Message m) {
        messageService.sendMessage(m);
    }

    @MessageMapping("/groupMessage")
    public void sendGroupMessage(Message m) {
        messageService.sendGroupMessage(m);
    }

    @MessageMapping("/messageAll")
    public void broadcastMessage(Message m) {
        messageService.broadcastMessage(m);
    }
}
