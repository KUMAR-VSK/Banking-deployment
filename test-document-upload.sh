#!/bin/bash

# Test script for document upload functionality
# This script helps diagnose 403 errors in document upload

echo "=== Document Upload Test Script ==="
echo "Testing document upload functionality..."
echo ""

# Configuration
BASE_URL="http://localhost:8080"
FRONTEND_URL="http://localhost:3000"
TEST_FILE="test-document.pdf"

# Check if backend is running
echo "1. Checking if backend is running..."
if curl -s "$BASE_URL/actuator/health" > /dev/null; then
    echo "✓ Backend is running"
else
    echo "✗ Backend is not running. Please start the Spring Boot application."
    echo "   Run: cd Backend && ./mvnw spring-boot:run"
    exit 1
fi

# Check if frontend is running
echo "2. Checking if frontend is running..."
if curl -s "$FRONTEND_URL" > /dev/null; then
    echo "✓ Frontend is running"
else
    echo "⚠ Frontend is not running (optional for API testing)"
fi

# Create a test PDF file if it doesn't exist
echo "3. Creating test document..."
if [ ! -f "$TEST_FILE" ]; then
    echo "Creating test PDF file..."
    echo "This is a test document for upload testing." > test-content.txt
    # Create a simple PDF using ghostscript or just use the text file
    if command -v gs &> /dev/null; then
        gs -sDEVICE=pdfwrite -dCompatibilityLevel=1.4 -dPDFSETTINGS=/screen \
           -dNOPAUSE -dQUIET -dBATCH -sOutputFile="$TEST_FILE" test-content.txt
    else
        # Fallback: create a simple text file with .pdf extension
        cp test-content.txt "$TEST_FILE"
        echo "Note: Created text file with .pdf extension (no ghostscript available)"
    fi
    rm -f test-content.txt
fi

echo "✓ Test document created: $TEST_FILE"

# Test authentication endpoint
echo "4. Testing authentication..."
echo "Please log in to get an authentication token."
echo "Visit: $FRONTEND_URL/login"
echo "After logging in, copy your JWT token from browser localStorage."
echo ""
read -p "Enter your JWT token: " JWT_TOKEN

if [ -z "$JWT_TOKEN" ]; then
    echo "✗ No token provided. Cannot test authenticated endpoints."
    exit 1
fi

# Test document upload
echo "5. Testing document upload..."
echo "Uploading test document..."

curl -X POST "$BASE_URL/user/documents/upload" \
     -H "Authorization: Bearer $JWT_TOKEN" \
     -F "file=@$TEST_FILE" \
     -F "documentType=IDENTITY" \
     -H "Accept: application/json" \
     -v

echo ""
echo "6. Testing document retrieval..."
curl -X GET "$BASE_URL/user/documents" \
     -H "Authorization: Bearer $JWT_TOKEN" \
     -H "Accept: application/json" \
     -v

echo ""
echo "=== Test Complete ==="
echo ""
echo "Common issues and solutions:"
echo "1. 403 Forbidden:"
echo "   - Check if JWT token is valid and not expired"
echo "   - Verify user has proper permissions"
echo "   - Check CORS configuration in SecurityConfig"
echo ""
echo "2. 401 Unauthorized:"
echo "   - Token is missing or invalid"
echo "   - User not found in database"
echo ""
echo "3. 400 Bad Request:"
echo "   - File size too large (>10MB)"
echo "   - Invalid file type"
echo "   - Missing document type"
echo ""
echo "4. 500 Internal Server Error:"
echo "   - Check backend logs for detailed error"
echo "   - Verify file upload directory exists"
echo ""
echo "To debug further, check the backend logs:"
echo "   tail -f Backend/logs/application.log"
echo ""
echo "Cleaning up test files..."
rm -f "$TEST_FILE"
