# Bank Loan Management - Deployment Guide

This guide covers deploying the Bank Loan Management application to Aiven, Vercel, and Render.

## Platform Overview

- **Backend**: Spring Boot (Java) - Deploy to Render or Aiven
- **Frontend**: React - Deploy to Vercel
- **Database**: MySQL - Use Aiven for managed database

## Environment Variables

### Backend (.env)
```bash
# Database Configuration
DATABASE_URL=jdbc:mysql://your-aiven-mysql-host:port/defaultdb?sslMode=REQUIRED
MYSQL_USER=avnadmin
MYSQL_PASSWORD=your-aiven-mysql-password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-at-least-32-characters
JWT_EXPIRATION=86400000

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://your-frontend-url.vercel.app,https://your-render-backend.onrender.com

# Server Configuration
PORT=8080

# File Upload Configuration
MULTIPART_ENABLED=true
MULTIPART_THRESHOLD=2KB
MULTIPART_MAX_FILE_SIZE=10MB
MULTIPART_MAX_REQUEST_SIZE=10MB
```

### Frontend (.env.production)
```bash
REACT_APP_API_BASE_URL=https://your-render-backend.onrender.com
```

## Deployment Steps

### 1. Aiven MySQL Database Setup

1. Create an Aiven account
2. Create a new MySQL service
3. Get connection details:
   - Host: `your-service.aivencloud.com`
   - Port: `23885` (example)
   - Database: `defaultdb`
   - Username: `avnadmin`
   - Password: `your-generated-password`

### 2. Render Backend Deployment

1. Connect your GitHub repository to Render
2. Create a new Web Service
3. Configure build settings:
   - Build Command: `cd Backend && mvn clean package`
   - Start Command: `java -jar Backend/target/Bank-Loan-Management-0.0.1-SNAPSHOT.jar`
4. Add environment variables from Backend (.env)
5. Deploy

### 3. Vercel Frontend Deployment

1. Connect your GitHub repository to Vercel
2. Create a new Project
3. Configure build settings:
   - Framework Preset: `Create React App`
   - Build Command: `npm run build`
   - Output Directory: `build`
   - Install Command: `npm install`
4. Add environment variables from Frontend (.env.production)
5. Deploy

## Production Configuration

### Backend Application Properties
```properties
# Production database (Aiven)
spring.datasource.url=${DATABASE_URL:jdbc:mysql://localhost:3306/banking_master?createDatabaseIfNotExist=true}
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:vsk@1234}

# Production server
server.port=${PORT:8080}

# Production JWT
jwt.secret=${JWT_SECRET:mySuperSecretKeyThatIsAtLeast32CharactersLong}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Production CORS
cors.allowed.origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

### Frontend API Configuration
```javascript
// src/api.js
const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
});
```

## Security Considerations

1. **JWT Secret**: Use a strong, unique secret in production
2. **CORS**: Only allow your frontend domain
3. **Database**: Use SSL connections with Aiven
4. **Environment Variables**: Never commit secrets to git
5. **File Uploads**: Implement proper validation and virus scanning

## Monitoring and Maintenance

1. **Aiven**: Monitor database performance and backups
2. **Render**: Monitor application logs and performance
3. **Vercel**: Monitor frontend performance and CDN
4. **Health Checks**: Implement health check endpoints

## Troubleshooting

### Common Issues

1. **CORS Errors**: Check CORS_ALLOWED_ORIGINS configuration
2. **Database Connection**: Verify Aiven connection details
3. **JWT Issues**: Ensure JWT_SECRET is consistent across deployments
4. **File Uploads**: Check file size limits and storage permissions

### Health Check Endpoints

- Backend: `GET /actuator/health`
- Frontend: `GET /` (should return React app)

## Scaling Considerations

1. **Database**: Use Aiven's scaling options
2. **Backend**: Scale Render instances based on load
3. **Frontend**: Vercel auto-scales with traffic
4. **Caching**: Implement Redis caching for better performance