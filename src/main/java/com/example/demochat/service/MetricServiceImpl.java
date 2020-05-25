package com.example.demochat.service;

import com.example.demochat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class MetricServiceImpl implements MetricService {

    @Autowired
    @Lazy
    private SimpMessageSendingOperations op;

    @Value("${metrics.rabbitmq.exchange}")
    String exchange;

    @Value("${metrics.rabbitmq.routingkey}")
    private String routingkey;

    @Override
    public void processMessageSent(Message m) {
        //send message using the broker
        System.out.println(m.getBody());
        System.out.println(m.getSent_from());
        System.out.println(m.getSent_to());
//        System.out.println(m.getGroup());

        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

        //Check if its group, change queue for topic
        op.convertAndSend("/queue/chat-metrics", m, map);
    }
}
