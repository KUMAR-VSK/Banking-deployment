package com.example.Bank_Loan_Management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendNotification(String topic, String message) {
        if (kafkaTemplate != null) {
            kafkaTemplate.send(topic, message);
        } else {
            System.out.println("Kafka not available, logging notification: " + message);
        }
    }

    public void sendLoanStatusUpdate(Long userId, String status) {
        String message = String.format("Loan application for user %d has been %s", userId, status);
        sendNotification("loan-notifications", message);
    }
}
