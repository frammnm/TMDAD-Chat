package com.example.demochat.controller;

import java.util.List;

import com.example.demochat.model.Message;
import com.example.demochat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageRepository messagerep;

    @GetMapping("/")
    public List<Message> getAlMessages() {
        return messagerep.findAll();
    }
}