package com.example.demochat.service;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
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
    private MessageRepository messages;

    @Autowired
    private GroupRepository groups;

    @Autowired
    private SimpMessageSendingOperations op;

    @Override
    public List<Message> getAllMessages() {
        return messages.findAll();
    }

    @Override
    public void sendMessage(Message m) {
        //send message using the broker
//        System.out.println(m);
//        System.out.println(m.getId());
//        System.out.println(m.getBody());
//        System.out.println(m.getFrom());
//        System.out.println(m.getTo());
//        System.out.println(m.getTimestamp());

        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/queue/" + m.getSent_to(), m, map);

        //save in db
        messages.save(m);
    }

    @Override
    public void broadcastMessage(Message m) {
        //send message using the broker
        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/topic/all", m, map);

        //save in db
        messages.save(m);
    }

    @Override
    public Message sendMessageHTTP(Message m) {
        return messages.save(new Message(m.getBody(), m.getSent_from(), m.getSent_to(), m.getTimestamp()));
//        return m;
    }

    public void sendGroupMessage(Message m) {
        //send message using the broker
//        Map<String,Object> map = new HashMap<>();
//        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
//        op.convertAndSend("/topic/"+m.getSent_to(), m, map);

        //save in db
//        System.out.println(m);
//        System.out.println(m.getId());
//        System.out.println(m.getBody());
//        System.out.println(m.getSent_from());
//        System.out.println(m.getSent_to());
//        System.out.println(m.getTimestamp());


        Message savedMessage = messages.save(m);
        Group group = groups.findByName(m.getSent_to());

        List<Message> groupMessages = group.getMessages();
        groupMessages.add(savedMessage);
        group.setMessages(groupMessages);
        savedMessage.setGroup(group);
        messages.save(savedMessage);

    }

    public Message sendGroupMessageHTTP(Message m) {
//        System.out.println(m);
//        System.out.println(m.getId());
//        System.out.println(m.getBody());
//        System.out.println(m.getSent_from());
//        System.out.println(m.getSent_to());
//        System.out.println(m.getTimestamp());
        //save in db
//        Message savedMessage = messages.save(m);
//        System.out.println("msgId:");
//        System.out.println(m.getId());
//
//        System.out.println("to:");
//        System.out.println(m.getSent_to());
//        Group group = groups.findByName(m.getSent_to());
//        System.out.println("grpId:");
//        System.out.println(group.getId());
//
//        List<Message> groupMessages = group.getMessages();
//
//        System.out.println("list:");
//        System.out.println(groupMessages);
//
//
//        groupMessages.add(m);
//        group.setMessages(groupMessages);
//        m.setGroup(group);
//        groups.save(group);
//        messages.save(savedMessage);
        return messages.save(m);

    }
}
