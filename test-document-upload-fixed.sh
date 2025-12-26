#!/bin/bash

echo "=== Document Upload Test Script (Fixed) ==="
echo "Testing document upload functionality with proper user setup..."

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

# 1. Check if backend is running
echo "1. Checking if backend is running..."
if curl -s http://localhost:8080/auth/user > /dev/null; then
    print_status "Backend is running"
else
    print_error "Backend is not running. Please start the Spring Boot application."
    echo "   Run: cd Backend && mvn spring-boot:run"
    exit 1
fi

# 2. Check if frontend is running
echo "2. Checking if frontend is running..."
if curl -s http://localhost:3000 > /dev/null; then
    print_status "Frontend is running"
else
    print_warning "Frontend is not running. Starting frontend..."
    cd frontend && npm start &
    sleep 5
fi

# 3. Create test user if it doesn't exist
echo "3. Setting up test user..."
TEST_USER="testuser"
TEST_PASS="testuser"
TEST_EMAIL="testuser@example.com"

# Try to login first (this will create the testuser automatically in AuthService)
echo "   Attempting to login/create testuser..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\", \"password\":\"$TEST_PASS\"}")

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    print_status "Successfully logged in as $TEST_USER"
    print_status "Token: $TOKEN"
else
    print_error "Failed to login/create testuser"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

# 4. Test document upload
echo "4. Testing document upload..."
# Create a test document
echo "   Creating test document..."
echo "This is a test document for upload testing." > test-document.txt

# Upload the document
UPLOAD_RESPONSE=$(curl -s -X POST http://localhost:8080/user/documents/upload \
    -H "Authorization: Bearer $TOKEN" \
    -F "file=@test-document.txt" \
    -F "documentType=IDENTITY")

if echo "$UPLOAD_RESPONSE" | grep -q "fileName"; then
    print_status "Document uploaded successfully!"
    echo "   Response: $UPLOAD_RESPONSE"
else
    print_error "Document upload failed"
    echo "   Response: $UPLOAD_RESPONSE"
fi

# 5. Test fetching documents
echo "5. Testing document retrieval..."
FETCH_RESPONSE=$(curl -s -X GET http://localhost:8080/user/documents \
    -H "Authorization: Bearer $TOKEN")

if echo "$FETCH_RESPONSE" | grep -q "fileName"; then
    print_status "Documents retrieved successfully!"
    echo "   Response: $FETCH_RESPONSE"
else
    print_error "Document retrieval failed"
    echo "   Response: $FETCH_RESPONSE"
fi

# 6. Cleanup
echo "6. Cleaning up..."
rm -f test-document.txt

echo ""
echo "=== Test Complete ==="
echo "If all tests passed, the document upload 403 error should be fixed!"
