package com.example.demochat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/")
public class MessageController {

    @Autowired
    private MessageRepository messagerep;

    @GetMapping("messages")
    public List<Message> getAlMessages() {
        return messagerep.findAll();
    }

}