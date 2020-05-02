package com.example.demochat.service;

import com.example.demochat.model.Message;
import com.example.demochat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messagerep;

    @Autowired
    private SimpMessageSendingOperations op;

    @Override
    public List<Message> getAllMessages() {
        return messagerep.findAll();
    }

    @Override
    public void sendMessage(Message m) {
        //send message using the broker
        System.out.println(m);
        System.out.println(m.getId());
        System.out.println(m.getBody());
        System.out.println(m.getFrom());
        System.out.println(m.getTo());
        System.out.println(m.getTimestamp());

        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/queue/" + m.getTo(), m, map);

        //save in db
        messagerep.save(m);
    }

    @Override
    public void broadcastMessage(Message m) {
        //send message using the broker
        System.out.println(m);
        System.out.println(m.getId());
        System.out.println(m.getBody());
        System.out.println(m.getFrom());
        System.out.println(m.getTo());
        System.out.println(m.getTimestamp());

        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/topic/all", m, map);

        //save in db
        messagerep.save(m);
    }

    @Override
    public Message sendMessageHTTP(Message m) {
        System.out.println(m);
        System.out.println(m.getId());
        System.out.println(m.getBody());
        System.out.println(m.getFrom());
        System.out.println(m.getTo());
        System.out.println(m.getTimestamp());
        return messagerep.save(new Message(m.getBody(), m.getFrom(), m.getTo(), m.getTimestamp()));
//        return m;
    }
}
