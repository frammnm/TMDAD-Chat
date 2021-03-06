package com.example.demochat.service;

import com.example.demochat.model.*;
import com.example.demochat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${chat.rabbitmq.topic.broadcast}")
    private String broadcastTopic;

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
        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/topic/" + broadcastTopic, m, map);

        //save in db
        messages.save(m);
    }

    @Override
    @Transactional
    public void sendGroupMessage(Message m) {
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/topic/" + m.getSent_to(), m, map);

        Message savedMessage = messages.save(m);
        Group group = groups.findByName(m.getSent_to());

        List<Message> groupMessages = group.getMessages();
        groupMessages.add(savedMessage);
        group.setMessages(groupMessages);
        savedMessage.setType("Group");
        savedMessage.setGroup(group);
        messages.save(savedMessage);
    }
}
