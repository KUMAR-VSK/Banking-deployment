package com.example.Bank_Loan_Management.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Bank_Loan_Management.entity.Document;
import com.example.Bank_Loan_Management.entity.LoanApplication;
import com.example.Bank_Loan_Management.entity.User;
import com.example.Bank_Loan_Management.repository.DocumentRepository;
import com.example.Bank_Loan_Management.repository.LoanApplicationRepository;
import com.example.Bank_Loan_Management.repository.UserRepository;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    private final Path root = Paths.get("uploads");

    public DocumentService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public Document uploadDocument(User user, MultipartFile file, String documentType) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = root.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath.toString());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus(Document.Status.UPLOADED);

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByLoanApplication(Long loanApplicationId) {
        return documentRepository.findByLoanApplicationId(loanApplicationId);
    }

    public List<Document> getDocumentsByUser(User user) {
        return documentRepository.findByUser(user);
    }

    public Document verifyDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        document.setStatus(Document.Status.VERIFIED);
        Document saved = documentRepository.save(document);

        // Check if all documents for this user are verified
        List<Document> userDocuments = documentRepository.findByUser(document.getUser());
        boolean allVerified = userDocuments.stream()
                .allMatch(doc -> doc.getStatus() == Document.Status.VERIFIED);

        if (allVerified) {
            // Update all loan applications for this user to mark documents as verified
            List<LoanApplication> userApplications = loanApplicationRepository.findByUser(document.getUser());
            for (LoanApplication application : userApplications) {
                application.setDocumentsVerified(true);
                loanApplicationRepository.save(application);
            }
        }

        return saved;
    }

    public Document rejectDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        document.setStatus(Document.Status.REJECTED);
        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}
