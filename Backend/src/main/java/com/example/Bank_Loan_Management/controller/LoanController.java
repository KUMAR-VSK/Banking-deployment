package com.example.Bank_Loan_Management.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Bank_Loan_Management.entity.Document;
import com.example.Bank_Loan_Management.entity.LoanApplication;
import com.example.Bank_Loan_Management.entity.User;
import com.example.Bank_Loan_Management.repository.UserRepository;
import com.example.Bank_Loan_Management.service.DocumentService;
import com.example.Bank_Loan_Management.service.LoanService;

@RestController
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;
    private final DocumentService documentService;

    public LoanController(LoanService loanService, UserRepository userRepository, DocumentService documentService) {
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    // User endpoints
    @PostMapping("/user/documents/upload")
    public ResponseEntity<Document> uploadDocument(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam("file") MultipartFile file,
                                                   @RequestParam("documentType") String documentType) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Document document = documentService.uploadDocument(user, file, documentType);
            return ResponseEntity.ok(document);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/documents")
    public ResponseEntity<List<Document>> getMyDocuments(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Document> documents = documentService.getDocumentsByUser(user);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/user/loans/apply")
    public ResponseEntity<LoanApplication> applyForLoan(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody LoanApplicationRequest request) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Found user: " + user.getId() + ", " + user.getUsername());

        // Check if user has uploaded required documents
        List<Document> userDocuments = documentService.getDocumentsByUser(user);
        if (userDocuments.isEmpty()) {
            throw new RuntimeException("Please upload required documents before applying for loan");
        }

        // Check if all documents are verified
        boolean allDocumentsVerified = userDocuments.stream()
                .allMatch(doc -> doc.getStatus() == Document.Status.VERIFIED);
        if (!allDocumentsVerified) {
            throw new RuntimeException("All documents must be verified before applying for loan");
        }

        LoanApplication application = loanService.applyForLoan(user, request.getAmount(), request.getTerm(), request.getPurpose());
        System.out.println("Created application with user_id: " + application.getUser().getId());
        return ResponseEntity.ok(application);
    }

    @GetMapping("/user/loans")
    public ResponseEntity<List<LoanApplication>> getMyLoans(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<LoanApplication> loans = loanService.getLoansByUser(user);
        return ResponseEntity.ok(loans);
    }

    // Admin endpoints
    @GetMapping("/admin/loans")
    public ResponseEntity<List<LoanApplication>> getAllLoans() {
        List<LoanApplication> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/admin/loans/status/{status}")
    public ResponseEntity<List<LoanApplication>> getLoansByStatus(@PathVariable String status) {
        LoanApplication.Status enumStatus = LoanApplication.Status.valueOf(status.toUpperCase());
        List<LoanApplication> loans = loanService.getLoansByStatus(enumStatus);
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/admin/loans/verify/{id}")
    public ResponseEntity<LoanApplication> verifyLoan(@PathVariable Long id) {
        LoanApplication application = loanService.verifyLoan(id);
        return ResponseEntity.ok(application);
    }

    @PostMapping("/admin/loans/decide/{id}")
    public ResponseEntity<LoanApplication> approveOrRejectLoan(@PathVariable Long id) {
        LoanApplication application = loanService.approveOrRejectLoan(id);
        return ResponseEntity.ok(application);
    }

    @PostMapping("/admin/loans/approve/{id}")
    public ResponseEntity<LoanApplication> approveLoan(@PathVariable Long id) {
        LoanApplication application = loanService.approveLoan(id);
        return ResponseEntity.ok(application);
    }

    @PostMapping("/admin/loans/reject/{id}")
    public ResponseEntity<LoanApplication> rejectLoan(@PathVariable Long id) {
        LoanApplication application = loanService.rejectLoan(id);
        return ResponseEntity.ok(application);
    }

    public static class LoanApplicationRequest {
        private BigDecimal amount;
        private Integer term;
        private String purpose;

        // getters and setters
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Integer getTerm() { return term; }
        public void setTerm(Integer term) { this.term = term; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
}
