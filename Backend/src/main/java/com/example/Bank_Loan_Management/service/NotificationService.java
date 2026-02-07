package com.example.Bank_Loan_Management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendNotification(String topic, String message) {
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send(topic, message);
                System.out.println("Notification sent to Kafka: " + message);
            } else {
                System.out.println("Kafka not available, logging notification: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send notification to Kafka: " + e.getMessage());
            System.out.println("Logging notification instead: " + message);
        }
    }

    public void sendLoanStatusUpdate(Long userId, String status) {
        String message = String.format("Loan application for user %d has been %s", userId, status);
        sendNotification("loan-notifications", message);
    }
}
