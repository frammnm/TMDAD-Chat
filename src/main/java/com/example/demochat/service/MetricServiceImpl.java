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

    @Value("${metrics.rabbitmq.queue}")
    private String metricQueue;

    @Override
    public void processMessageSent(Message m) {
        Map<String,Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        op.convertAndSend("/queue/" + metricQueue, m, map);
    }
}
