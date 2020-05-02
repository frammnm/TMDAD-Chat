package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.Message;
import com.example.demochat.repository.MessageRepository;
import com.example.demochat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.util.MimeTypeUtils;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @MessageMapping("/message")
    public void sendMessage(Message m) {
        messageService.sendMessage(m);
    }

    @MessageMapping("/messageAll")
    public void broadcastMessage(Message m) {
        messageService.broadcastMessage(m);
    }

    @PostMapping("/send")
    public Message sendMessageHTTP(@RequestBody Message m) {
        return messageService.sendMessageHTTP(m);
    }
}
