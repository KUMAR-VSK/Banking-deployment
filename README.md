# Bank Loan Management System

This repository contains the source code for the Bank Loan Management System, a secure enterprise application designed to coordinate and manage the application, review, and approval lifecycle of retail and commercial bank loans.

---

## Architectural Workflow

The application is built on a decoupled client-server architecture consisting of a Spring Boot REST API backend and a React Single-Page Application (SPA) frontend. Communication is conducted via JSON payloads secured by stateless JSON Web Token (JWT) authentication.

### Application Lifecycle

The following sequence diagram models the flow of operations from initial user registration through document verification, automated credit scoring, and final manager approval:

```mermaid
sequenceDiagram
    autonumber
    actor U as Applicant (User)
    actor LM as Loan Manager
    actor M as Branch Manager
    participant FE as React Frontend
    participant BE as Spring Boot Backend
    database DB as MySQL Database

    %% Registration & Authentication
    U->>FE: Enter registration/login credentials
    FE->>BE: POST /auth/register & /auth/login
    BE->>DB: Query or save user credentials
    BE-->>FE: Return JWT containing role claims
    FE->>FE: Persist JWT in LocalStorage

    %% Application Steps
    U->>FE: Execute loan calculator simulation
    FE->>FE: Calculate monthly EMI rates
    U->>FE: Upload required verification documents
    FE->>BE: POST /api/user/documents/upload (Multipart file)
    BE->>BE: Validate file specifications (Max 10MB)
    BE->>DB: Persist document metadata (Status: PENDING)
    BE-->>FE: Return upload confirmation
    U->>FE: Submit loan application
    FE->>BE: POST /api/user/loans/apply
    BE->>BE: Verify existence of required documents
    BE->>DB: Persist loan record (Status: APPLIED)
    BE-->>FE: Return application confirmation

    %% Review Workflow
    LM->>FE: Review pending loan records
    FE->>BE: GET /api/loans (Fetch records with APPLIED status)
    BE-->>FE: Return filtered loan records
    LM->>FE: Verify documents and approve verification step
    FE->>BE: PUT /api/loans/{id}/verify
    BE->>DB: Update loan status to VERIFIED
    BE-->>FE: Return verification update confirmation

    %% Approval Workflow
    M->>FE: Access manager approval console
    FE->>BE: GET /api/loans (Fetch records with VERIFIED status)
    BE-->>FE: Return verified records
    M->>FE: Execute risk assessment and approve or reject application
    FE->>BE: PUT /api/loans/{id}/approve or /api/loans/{id}/reject
    BE->>BE: Determine and apply final interest rates
    BE->>DB: Persist final status (APPROVED or REJECTED)
    BE-->>FE: Update ledger balances
```

---

## Technical Stack and Component Architecture

### Frontend Application
*   **Core Library:** React 18 using functional components and standard hooks (`useState`, `useEffect`).
*   **Routing:** `react-router-dom` (v6) implementing role-based route protection and redirection.
*   **API Client:** `axios` configured with request interceptors to inject JWT Authorization headers.
*   **Data Visualization:** `react-chartjs-2` for dashboard metrics and telemetry display.
*   **Styling:** Tailwind CSS combined with standard stylesheet overrides for dark and light mode configurations.

### Backend Application
*   **Framework:** Spring Boot 3.2.3 running on Java 17.
*   **Security:** Spring Security configured with stateless JWT authentication filters.
*   **Data Persistence:** Spring Data JPA with Hibernate object-relational mapping.
*   **Database Management Systems:**
    *   **Production Environment:** Aiven Managed MySQL cloud instances with HikariCP connection pooling.
    *   **Testing and Local Environments:** H2 In-Memory Database with console access enabled.
*   **Utilities:** Project Lombok for boilerplate reduction and Jakarta Validation for field constraints.

---

## Directory Organization

The project is structured into distinct backend and frontend directories as outlined below:

```bash
Bank-Loan-Management/
├── Backend/                            # Maven Spring Boot Project Root
│   ├── src/main/java/com/example/Bank_Loan_Management/
│   │   ├── config/                     # Security configuration classes
│   │   │   ├── JwtAuthenticationFilter.java  # Stateless token interceptor filter
│   │   │   └── SecurityConfig.java           # Security filter chain rules
│   │   ├── controller/                 # REST endpoints
│   │   │   ├── AuthController.java           # Authentication and registration services
│   │   │   └── LoanController.java           # Loan transaction management APIs
│   │   ├── entity/                     # Database entities
│   │   │   ├── User.java                     # User credentials and schema
│   │   │   ├── LoanApplication.java          # Loan parameters and states
│   │   │   ├── Document.java                 # Associated file attachments
│   │   │   └── InterestRate.java             # System interest settings
│   │   ├── repository/                 # Data access repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── LoanApplicationRepository.java
│   │   │   ├── DocumentRepository.java
│   │   │   └── InterestRateRepository.java
│   │   ├── service/                    # Business logic implementations
│   │   │   ├── AuthService.java              # User lookup and token compilation
│   │   │   ├── LoanService.java              # Transaction workflow coordination
│   │   │   ├── DocumentService.java          # Local storage and type checks
│   │   │   ├── CreditScoringService.java     # Automated risk scoring logic
│   │   │   └── NotificationService.java      # Dispatch alert and email notifications
│   │   ├── util/                       # Helper and security utilities
│   │   │   └── JwtUtil.java                  # JWT building and parsing logic
│   │   └── BankLoanManagementApplication.java # Core entry point class
│   ├── src/main/resources/
│   │   └── application.properties       # Environment properties configuration
│   └── pom.xml                         # Project Object Model dependencies
│
└── frontend/                           # React Application Root
    ├── public/                         # Public templates and static files
    ├── src/
    │   ├── components/                 # Component files
    │   │   ├── Login.js                      # Authentication form layout
    │   │   ├── Register.js                   # Account creation form layout
    │   │   ├── UserDashboard.js              # Client interface component
    │   │   ├── LoanManagerDashboard.js       # First-tier review dashboard
    │   │   ├── ManagerDashboard.js           # Executive decision dashboard
    │   │   ├── AdminDashboard.js             # User directory and system configuration
    │   │   └── IntegratedLoanCalculator.js   # Amortization visual calculator
    │   ├── styles/                     # Supplementary CSS
    │   │   ├── auth.css
    │   │   └── components.css
    │   ├── App.js                      # Application initialization and routes
    │   ├── api.js                      # Axios instance initialization
    │   ├── index.js                    # Core React renderer
    │   └── index.css                   # General layout and theme sheets
    ├── package.json                    # Package properties and scripts
    └── vercel.json                     # Vercel deployment configurations
```

---

## Role Definitions and Dashboard Operations

Access control lists are enforced on the client side via React route guards and on the server side via Spring Security rules.

| Role | Target UI Dashboard Component | Operations and Capabilities |
| :--- | :--- | :--- |
| **`USER`** | [UserDashboard.js](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/src/components/UserDashboard.js) | • Compute loan amortization statistics using the calculator<br>• Upload Identity, Income, and Bank Statement documents<br>• Submit loan applications<br>• Inspect the execution status of active loan applications |
| **`LOAN_MANAGER`** | [LoanManagerDashboard.js](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/src/components/LoanManagerDashboard.js) | • Retrieve applications in `APPLIED` status<br>• Inspect uploaded verification documents<br>• Transition loan status to `VERIFIED` or mark documents as `REJECTED` |
| **`MANAGER`** | [ManagerDashboard.js](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/src/components/ManagerDashboard.js) | • Access loan applications in `VERIFIED` status<br>• Review credit score metrics generated by the system<br>• Terminate workflows with `APPROVED` or `REJECTED` statuses |
| **`ADMIN`** | [AdminDashboard.js](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/src/components/AdminDashboard.js) | • Register new users and modify roles<br>• View system-wide metrics (Active balances, user directories)<br>• Configure base interest rates and global system constants |

---

## Technical Modifications and Defect Resolutions

### 1. Document Upload HTTP 403 (Forbidden) Resolution
An issue was resolved where document upload requests returned an HTTP `403 Forbidden` status response code despite valid user authentication.
*   **Root Cause:** The JWT token generated during registration or login did not include the user's role payload. Consequently, the stateless authentication mechanism failed to assign the necessary roles to the authentication object within the security context, causing requests to violate endpoint role restrictions.
*   **Resolution:**
    *   Modified [JwtUtil.java](file:///Users/kumarvs/Downloads/Bank-Loan-Management/Backend/src/main/java/com/example/Bank_Loan_Management/util/JwtUtil.java) to append user roles as claims during token assembly.
    *   Updated [AuthService.java](file:///Users/kumarvs/Downloads/Bank-Loan-Management/Backend/src/main/java/com/example/Bank_Loan_Management/service/AuthService.java) to supply user role data to the token generator.
    *   Enhanced [JwtAuthenticationFilter.java](file:///Users/kumarvs/Downloads/Bank-Loan-Management/Backend/src/main/java/com/example/Bank_Loan_Management/config/JwtAuthenticationFilter.java) to extract the role claim and construct the appropriate Spring Security authority mappings using the `ROLE_` prefix.

### 2. User Interface Contrast and Accessibility Fixes
*   **Issue:** The initial style layout defaulted to dark theme variables. When running in light theme mode, headings, table cells, and form fields had insufficient color contrast, violating accessibility standards.
*   **Resolution:** Performed a systematic review of [index.css](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/src/index.css) to explicitly declare light mode color tokens, configure clear visual hierarchies for tables, design dark-gradient headers for grid views, and store theme settings in `localStorage` inside [App.js](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/src/App.js).

---

## Installation and Local Running Guide

### Prerequisites
*   **Java Development Kit (JDK 17)** or higher.
*   **Node.js** (v18.x or v20.x recommended) and **npm**.
*   **Maven** 3.8+ (or use the packaged wrapper `./mvnw`).

---

### Environment Configuration

Establish a `.env` file within the `frontend/` directory with the following content:
```env
REACT_APP_API_BASE_URL=http://localhost:8080
```

Configure the application database parameters inside [application.properties](file:///Users/kumarvs/Downloads/Bank-Loan-Management/Backend/src/main/resources/application.properties). For local testing using an in-memory database:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

For connection to a persistent **MySQL Database**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bank_loan?createDatabaseIfNotExist=true
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

---

### Step 1: Start the Spring Boot Backend

Navigate to the Backend directory and run the initialization commands:
```bash
cd Backend
./mvnw clean spring-boot:run
```
The backend API server will initialize on port **`8080`**. Verification can be performed by visiting the H2 dashboard at `http://localhost:8080/h2-console`.

---

### Step 2: Start the React Frontend

Navigate to the frontend directory, install package dependencies, and run the development build:
```bash
cd frontend
npm install
npm start
```
The client dashboard interface will deploy locally on port **`3000`**.

---

## API Verification and Testing Operations

### 1. User Registration
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testuser","email":"testuser@example.com"}'
```

### 2. Authentication and JWT Extraction
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testuser"}'
```
Response payload contains the JWT token and user metadata:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzM...",
  "userId": 1,
  "username": "testuser",
  "role": "USER"
}
```

### 3. File Upload Execution
```bash
curl -X POST http://localhost:8080/api/user/documents/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@test-document.pdf" \
  -F "documentType=IDENTITY"
```

### 4. Loan Application Submission
```bash
curl -X POST http://localhost:8080/api/user/loans/apply \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount": 500000, "term": 24, "purpose": "Business Expansion"}'
```

---

## Production Deployment Configuration

The repository is configured for automated cloud builds and deployments:
1.  **Frontend:** Handled by Vercel utilizing the [frontend/vercel.json](file:///Users/kumarvs/Downloads/Bank-Loan-Management/frontend/vercel.json) configuration to proxy backend requests to Render.
2.  **Backend:** Configured for Render/Railway using the multi-stage [Dockerfile](file:///Users/kumarvs/Downloads/Bank-Loan-Management/Dockerfile) and [render.yaml](file:///Users/kumarvs/Downloads/Bank-Loan-Management/render.yaml) configuration files.
