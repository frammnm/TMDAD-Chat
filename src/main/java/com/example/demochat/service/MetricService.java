package com.example.demochat.service;

import com.example.demochat.model.Message;

import java.util.List;

public interface MetricService {
    public abstract void processMessageSent(Message m);
}
