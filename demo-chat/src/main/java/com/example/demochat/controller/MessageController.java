package com.example.demochat;

import java.util.List;

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