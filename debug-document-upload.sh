#!/bin/bash

echo "=== Debug Document Upload Test ==="
echo "Testing document upload with detailed debugging..."

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

# 1. Register test user
echo "1. Registering test user..."
TEST_USER="testuser"
TEST_PASS="testuser"
TEST_EMAIL="testuser@example.com"

REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/register \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\", \"password\":\"$TEST_PASS\", \"email\":\"$TEST_EMAIL\"}")

if echo "$REGISTER_RESPONSE" | grep -q "username"; then
    print_status "Successfully registered testuser"
else
    print_warning "User registration failed or user already exists"
    echo "   Response: $REGISTER_RESPONSE"
fi

# 2. Login to get JWT token
echo "2. Logging in to get JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$TEST_USER\", \"password\":\"$TEST_PASS\"}")

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    print_status "Successfully logged in as $TEST_USER"
    print_status "Token: $TOKEN"
else
    print_error "Failed to login"
    echo "   Response: $LOGIN_RESPONSE"
    exit 1
fi

# 3. Test current user endpoint
echo "3. Testing current user endpoint..."
CURRENT_USER_RESPONSE=$(curl -s -X GET http://localhost:8080/auth/user \
    -H "Authorization: Bearer $TOKEN")

if echo "$CURRENT_USER_RESPONSE" | grep -q "username"; then
    print_status "Current user endpoint working"
    echo "   Response: $CURRENT_USER_RESPONSE"
else
    print_error "Current user endpoint failed"
    echo "   Response: $CURRENT_USER_RESPONSE"
fi

# 4. Test document upload with verbose output
echo "4. Testing document upload with verbose output..."
# Create a test document
echo "   Creating test document..."
echo "This is a test document for upload testing." > test-document.txt

# Upload the document with verbose output
echo "   Uploading document..."
UPLOAD_RESPONSE=$(curl -s -v -X POST http://localhost:8080/user/documents/upload \
    -H "Authorization: Bearer $TOKEN" \
    -F "file=@test-document.txt" \
    -F "documentType=IDENTITY" 2>&1)

echo "   Upload response:"
echo "$UPLOAD_RESPONSE"

# 5. Test document retrieval
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
echo "=== Debug Complete ==="
