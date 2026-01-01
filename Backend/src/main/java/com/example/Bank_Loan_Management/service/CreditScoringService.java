package com.example.Bank_Loan_Management.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Bank_Loan_Management.entity.InterestRate;
import com.example.Bank_Loan_Management.repository.InterestRateRepository;

@Service
public class CreditScoringService {

    private final InterestRateRepository interestRateRepository;

    public CreditScoringService(InterestRateRepository interestRateRepository) {
        this.interestRateRepository = interestRateRepository;
    }

    public int calculateCreditScore(BigDecimal amount, Integer term, String purpose) {
        // Simple mock credit scoring logic
        // In a real application, this would involve external APIs, user history, etc.

        int baseScore = 500; // Minimum score

        // Amount factor: higher amount reduces score
        if (amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            baseScore -= 50;
        } else if (amount.compareTo(BigDecimal.valueOf(5000)) > 0) {
            baseScore -= 25;
        }

        // Term factor: longer term increases score slightly
        if (term > 24) {
            baseScore += 20;
        } else if (term > 12) {
            baseScore += 10;
        }

        // Purpose factor: some purposes are riskier
        if ("business".equalsIgnoreCase(purpose)) {
            baseScore -= 30;
        } else if ("personal".equalsIgnoreCase(purpose)) {
            baseScore += 10;
        }

        // Ensure score is between 300 and 850
        return Math.max(300, Math.min(850, baseScore));
    }

    public boolean isEligible(int creditScore, BigDecimal amount) {
        // Simple eligibility: score > 400 and amount < 50000
        return creditScore > 400 && amount.compareTo(BigDecimal.valueOf(50000)) < 0;
    }

    public BigDecimal getInterestRate(String purpose) {
        Optional<InterestRate> interestRateOpt = interestRateRepository.findByPurpose(purpose.toLowerCase());
        if (interestRateOpt.isPresent()) {
            return interestRateOpt.get().getRate();
        }
        // Fallback to default rates if not set in DB
        switch (purpose.toLowerCase()) {
            case "home purchase": return BigDecimal.valueOf(8.5);
            case "car purchase": return BigDecimal.valueOf(9.5);
            case "education": return BigDecimal.valueOf(7.5);
            case "business": return BigDecimal.valueOf(10.5);
            case "personal": return BigDecimal.valueOf(12.0);
            case "health": return BigDecimal.valueOf(8.0);
            case "travel": return BigDecimal.valueOf(11.0);
            case "wedding": return BigDecimal.valueOf(9.0);
            case "home renovation": return BigDecimal.valueOf(8.75);
            case "debt consolidation": return BigDecimal.valueOf(11.5);
            default: return BigDecimal.valueOf(10.0);
        }
    }
}
