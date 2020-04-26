//package com.example.demochat.controller;
//
//import java.util.List;
//
//import com.example.demochat.model.Message;
//import com.example.demochat.repository.MessageRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.util.MimeTypeUtils;
//import java.util.HashMap;
//import java.util.Map;
//@RestController
//@RequestMapping("/api/v1/messages")
//public class MessageController {
//
//    @Autowired
//    private MessageRepository messagerep;
//
//    @Autowired
//    private SimpMessageSendingOperations op;
//
//    @GetMapping("/")
//    public List<Message> getAllMessages() {
//        return messagerep.findAll();
//    }
//
//    @MessageMapping("/message")
//    public void sendMessage(Message m) {
//        //send message using the broker
//        System.out.println(m);
//        System.out.println(m.getId());
//        System.out.println(m.getBody());
//        System.out.println(m.getFrom());
//        System.out.println(m.getTo());
//        System.out.println(m.getTimestamp());
//
//        Map<String,Object> map = new HashMap<>();
//        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
//        op.convertAndSend("/queue/" + m.getTo(), m, map);
//
//        //save in db
//        messagerep.save(m);
//    }
//
//    @PostMapping("/send")
//    public Message sendMessageHTTP(@RequestBody Message m) {
//        System.out.println(m);
//        System.out.println(m.getId());
//        System.out.println(m.getBody());
//        System.out.println(m.getFrom());
//        System.out.println(m.getTo());
//        System.out.println(m.getTimestamp());
//        return messagerep.save(new Message(m.getBody(), m.getFrom(), m.getTo(), m.getTimestamp()));
////        return m;
//    }
//}