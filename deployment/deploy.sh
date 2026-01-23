#!/bin/bash

# Bank Loan Management Deployment Script
# This script helps deploy the application to Aiven, Vercel, and Render

set -e

echo "🚀 Bank Loan Management Deployment Script"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required tools are installed
check_requirements() {
    print_status "Checking requirements..."
    
    if ! command -v git &> /dev/null; then
        print_error "Git is not installed"
        exit 1
    fi
    
    if ! command -v docker &> /dev/null; then
        print_warning "Docker is not installed. Skipping Docker deployment"
    fi
    
    print_status "Requirements check complete"
}

# Build the application
build_application() {
    print_status "Building application..."
    
    # Build backend
    cd Backend
    ./mvnw clean package -DskipTests
    cd ..
    
    # Build frontend
    cd frontend
    npm install
    npm run build
    cd ..
    
    print_status "Application built successfully"
}

# Deploy to local Docker (for testing)
deploy_local() {
    print_status "Deploying to local Docker..."
    
    if command -v docker &> /dev/null; then
        docker-compose -f deployment/docker-compose.yml up -d
        print_status "Local deployment complete"
        print_status "Frontend: http://localhost:3000"
        print_status "Backend: http://localhost:8080"
        print_status "MySQL: localhost:3306"
    else
        print_warning "Docker not available, skipping local deployment"
    fi
}

# Generate deployment files
generate_deployment_files() {
    print_status "Generating deployment files..."
    
    # Create production environment file
    if [ ! -f deployment/.env.production ]; then
        cp deployment/.env.template deployment/.env.production
        print_status "Created .env.production template"
    fi
    
    print_status "Deployment files generated"
}

# Main deployment function
main() {
    case "$1" in
        "local")
            check_requirements
            build_application
            deploy_local
            ;;
        "production")
            check_requirements
            build_application
            generate_deployment_files
            print_status "Production deployment files ready"
            print_status "Please follow the deployment guide in deployment/README.md"
            ;;
        "clean")
            print_status "Cleaning up..."
            docker-compose -f deployment/docker-compose.yml down -v
            docker rmi bank-loan-backend bank-loan-frontend 2>/dev/null || true
            print_status "Cleanup complete"
            ;;
        *)
            echo "Usage: $0 {local|production|clean}"
            echo ""
            echo "Commands:"
            echo "  local       - Deploy to local Docker for testing"
            echo "  production  - Prepare production deployment files"
            echo "  clean       - Clean up local deployment"
            echo ""
            echo "For production deployment to Aiven, Vercel, and Render:"
            echo "1. Follow the guide in deployment/README.md"
            echo "2. Set up Aiven MySQL database"
            echo "3. Deploy backend to Render"
            echo "4. Deploy frontend to Vercel"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"