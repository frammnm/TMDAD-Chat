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
        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        String sendPath = "queue";
        //Check if its group, change queue for topic
        op.convertAndSend("/" + sendPath + "/" + m.getSent_to(), m, map);

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
    public void sendGroupMessage(Message m) {
        //send message using the broker
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/topic/" + m.getSent_to(), m, map);

        Message savedMessage = messages.save(m);
        Group group = groups.findByName(m.getSent_to());

        List<Message> groupMessages = group.getMessages();
        groupMessages.add(savedMessage);
        group.setMessages(groupMessages);
        savedMessage.setGroup(group);
        messages.save(savedMessage);
    }

    @Override
    public void processMessageSent(Message m) {
        //send message using the broker
        //System.out.println(m);
        System.out.println(m.getId());
        System.out.println(m.getBody());
        System.out.println(m.getSent_from());
        System.out.println(m.getSent_to());
        System.out.println(m.getTimestamp());
    }
}
