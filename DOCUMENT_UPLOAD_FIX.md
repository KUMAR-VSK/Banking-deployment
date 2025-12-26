# Document Upload 403 Error Fix

This document outlines the fixes applied to resolve the "Failed to upload document: Request failed with status code 403" error.

## Root Causes Identified

1. **Security Configuration Issues**: The security filter chain wasn't properly configured for multipart file uploads
2. **Missing Error Handling**: No proper error handling for authentication and authorization failures
3. **CORS Configuration**: CORS settings needed improvement for file upload requests
4. **Frontend Error Handling**: Frontend wasn't handling 403 errors gracefully

## Fixes Applied

### 1. Backend Security Configuration (`SecurityConfig.java`)

**Changes Made:**
- Added explicit authentication requirement for `/user/documents/upload` endpoint
- Enhanced CORS configuration to include `HEAD` method and longer cache duration
- Improved security filter chain ordering

```java
// Added explicit authentication for document upload
.requestMatchers("/user/documents/upload").authenticated()

// Enhanced CORS configuration
configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
configuration.setMaxAge(3600L); // Cache preflight response for 1 hour
```

### 2. Enhanced Error Handling (`LoanController.java`)

**Changes Made:**
- Added comprehensive logging for debugging
- Implemented proper error responses with HTTP status codes
- Added validation for authentication, file content, and document type
- Improved error messages for better user experience

**Key Improvements:**
- Authentication validation before processing
- File validation (empty files, missing document type)
- Detailed error logging for troubleshooting
- Proper HTTP status codes (401, 403, 400, 500)

### 3. Frontend Error Handling (`UserDashboard.js`)

**Changes Made:**
- Added comprehensive error handling for different HTTP status codes
- Implemented proper authentication token validation
- Added user-friendly error messages
- Enhanced file input with proper accept attribute and hints

**Error Handling Logic:**
```javascript
switch (status) {
  case 401: // Authentication failed
  case 403: // Access denied
  case 400: // Bad request
  case 413: // File too large
  case 500: // Server error
}
```

### 4. Diagnostic Test Script (`test-document-upload.sh`)

**Purpose:**
- Automated testing of document upload functionality
- Step-by-step diagnosis of common issues
- Provides troubleshooting guidance

**Usage:**
```bash
chmod +x test-document-upload.sh
./test-document-upload.sh
```

## Common Issues and Solutions

### 403 Forbidden Errors

**Possible Causes:**
1. **Invalid or expired JWT token**
   - Solution: Re-login and get a new token
   - Check token expiration time

2. **Missing authentication header**
   - Solution: Ensure `Authorization: Bearer <token>` header is present
   - Verify token format is correct

3. **CORS preflight issues**
   - Solution: Check CORS configuration in `SecurityConfig.java`
   - Ensure frontend origin is allowed

4. **Role-based permission issue**
   - Solution: Verify user has USER or ADMIN role
   - Check that security configuration allows USER role for document upload
   - The endpoint `/user/documents/upload` requires `hasAnyRole("USER", "ADMIN")`

5. **Security filter chain misconfiguration**
   - Solution: Verify `/user/documents/upload` is properly authenticated
   - Check filter ordering

### 401 Unauthorized Errors

**Possible Causes:**
1. **No authentication token**
   - Solution: User needs to log in first
   - Check localStorage for token

2. **Invalid token format**
   - Solution: Verify token is properly formatted
   - Check JWT signature and claims

3. **User not found in database**
   - Solution: Verify user exists in database
   - Check username in token matches database

### 400 Bad Request Errors

**Possible Causes:**
1. **File too large (>10MB)**
   - Solution: Reduce file size or increase limit in `application.properties`

2. **Invalid file type**
   - Solution: Use PDF, JPG, or PNG files only
   - Check file extension and MIME type

3. **Missing document type**
   - Solution: Select a document type from dropdown
   - Verify documentType parameter is sent

## Testing the Fix

### Manual Testing Steps

1. **Start Backend Server:**
   ```bash
   cd Backend
   ./mvnw spring-boot:run
   ```

2. **Start Frontend:**
   ```bash
   cd frontend
   npm start
   ```

3. **Test Document Upload:**
   - Login to the application
   - Navigate to User Dashboard
   - Try uploading a document
   - Check for proper error messages if issues occur

### Automated Testing

Use the provided test script:
```bash
./test-document-upload.sh
```

## Debugging Tips

### Backend Logs
Check Spring Boot application logs for detailed error information:
```bash
# If using IDE, check console output
# Or check log files if configured
tail -f logs/application.log
```

### Browser Developer Tools
1. Open Network tab in browser dev tools
2. Attempt document upload
3. Check request headers and response
4. Look for CORS errors or authentication issues

### Common Log Messages to Look For

**Authentication Issues:**
- "User not found"
- "Authentication required"
- "Token validation failed"

**File Upload Issues:**
- "File processing error"
- "Empty file uploaded"
- "File too large"

**CORS Issues:**
- "CORS preflight response"
- "Origin not allowed"

## Prevention

### Best Practices

1. **Always validate authentication** before processing requests
2. **Implement proper error handling** with meaningful messages
3. **Use comprehensive logging** for debugging
4. **Test with various scenarios** (invalid tokens, large files, etc.)
5. **Monitor application logs** for recurring issues

### Configuration Checklist

- [ ] JWT secret is properly configured
- [ ] CORS origins include frontend URL
- [ ] File upload limits are appropriate
- [ ] Security filter chain is correctly ordered
- [ ] Error handling is comprehensive
- [ ] Role-based access control is properly configured
- [ ] Users have appropriate roles (USER or ADMIN) for document upload

## Support

If issues persist after applying these fixes:

1. Run the diagnostic script: `./test-document-upload.sh`
2. Check backend logs for detailed error messages
3. Verify frontend console for JavaScript errors
4. Ensure all configuration files are properly set up

For additional help, refer to the Spring Security and JWT documentation.
