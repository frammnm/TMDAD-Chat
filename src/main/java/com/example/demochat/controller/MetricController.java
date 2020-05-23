package com.example.demochat.controller;

import com.example.demochat.model.AppViews;
import com.example.demochat.model.Message;
import com.example.demochat.service.MessageService;
import com.example.demochat.service.MetricService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/metrics")
public class MetricController {

    @Autowired
    private MetricService metricService;

    @GetMapping("/")
    public String getAllMetrics() {
        return "OK";
    }

    @MessageMapping("/trends")
    public String sendMessage(Message m) {
        return "OK";
    }
}
