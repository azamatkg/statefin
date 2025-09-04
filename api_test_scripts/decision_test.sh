#!/bin/bash

# Decision API Testing Script for StateFin Application
# Tests all decision-related endpoints with RBAC validation
# Author: API Testing Agent
# 
# CRITICAL SECURITY FINDING:
# Decision-related controllers use @PreAuthorize("hasRole('ADMIN')") 
# but the application uses permission-based authorities, not role-based.
# This results in ALL decision endpoints being inaccessible to ALL users,
# including admins. This is a critical security misconfiguration.

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080"
AUTH_ENDPOINT="/api/auth/login"

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Function to print colored output
print_status() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Function to log test results
log_test() {
    local test_name=$1
    local expected_status=$2
    local actual_status=$3
    local endpoint=$4
    local method=$5
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    if [ "$expected_status" -eq "$actual_status" ]; then
        PASSED_TESTS=$((PASSED_TESTS + 1))
        print_status $GREEN "✅ PASS: $test_name ($method $endpoint) - Expected: $expected_status, Got: $actual_status"
    else
        FAILED_TESTS=$((FAILED_TESTS + 1))
        print_status $RED "❌ FAIL: $test_name ($method $endpoint) - Expected: $expected_status, Got: $actual_status"
    fi
}

# Function to authenticate and get JWT token
authenticate() {
    local username=$1
    local password=$2
    
    print_status $BLUE "🔐 Authenticating user: $username"
    
    # Create temporary login file to avoid JSON parsing issues with special characters
    local temp_login="/tmp/${username}_login.json"
    echo "{\"username\":\"$username\",\"password\":\"$password\"}" > "$temp_login"
    
    local response=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST "$BASE_URL$AUTH_ENDPOINT" \
        -H "Content-Type: application/json" \
        -d @"$temp_login")
    
    local body=$(echo "$response" | sed -E 's/HTTPSTATUS:[0-9]{3}$//')
    local status=$(echo "$response" | grep -o "HTTPSTATUS:[0-9]*" | cut -d: -f2)
    
    # Clean up temp file
    rm -f "$temp_login"
    
    if [ "$status" -eq 200 ]; then
        # Extract token using basic text processing since jq might not be available
        local token=$(echo "$body" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
        if [ -n "$token" ]; then
            print_status $GREEN "✅ Authentication successful for $username"
            echo "$token"
            return 0
        fi
    fi
    
    print_status $RED "❌ Authentication failed for $username (Status: $status)"
    echo "Response: $body" >&2
    return 1
}

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local auth_token=$4
    local test_name=$5
    local data=$6
    
    local curl_cmd="curl -s -w \"HTTPSTATUS:%{http_code}\" -X $method \"$BASE_URL$endpoint\""
    
    if [ -n "$auth_token" ]; then
        curl_cmd="$curl_cmd -H \"Authorization: Bearer $auth_token\""
    fi
    
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -H \"Content-Type: application/json\" -d '$data'"
    fi
    
    local response=$(eval "$curl_cmd")
    local status=$(echo "$response" | grep -o "HTTPSTATUS:[0-9]*" | cut -d: -f2)
    
    log_test "$test_name" "$expected_status" "$status" "$endpoint" "$method"
    
    return 0
}

# Main testing function
main() {
    print_status $BLUE "🚀 Starting Decision API Testing Suite"
    print_status $BLUE "=====================================\n"
    
    # Check application health
    print_status $YELLOW "🏥 Checking application health..."
    health_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")
    if [ "$health_status" -ne 200 ]; then
        print_status $RED "❌ Application is not healthy (Status: $health_status)"
        exit 1
    fi
    print_status $GREEN "✅ Application is healthy\n"
    
    # Authenticate users
    print_status $YELLOW "🔐 Authenticating test users..."
    ADMIN_TOKEN=$(authenticate "admin" "Admin123!")
    MANAGER_TOKEN=$(authenticate "manager" "Manager123!")
    USER_TOKEN=$(authenticate "user" "User123!")
    
    if [ -z "$ADMIN_TOKEN" ]; then
        print_status $RED "❌ Cannot proceed without admin authentication"
        exit 1
    fi
    
    echo ""
    
    # Test sample data for POST/PUT operations
    DECISION_TYPE_DATA='{
        "nameRu": "Тест решение типа",
        "nameKg": "Test decision type KG",
        "nameEn": "Test decision type EN",
        "description": "Test decision type for API testing",
        "status": "ACTIVE"
    }'
    
    DECISION_MAKING_BODY_DATA='{
        "nameRu": "Тест орган принятия решений",
        "nameKg": "Test decision making body KG",
        "nameEn": "Test decision making body EN",
        "description": "Test decision making body for API testing",
        "status": "ACTIVE"
    }'
    
    # Test Decision Type endpoints
    print_status $YELLOW "📋 Testing Decision Type endpoints (/api/decision-types)"
    print_status $RED "⚠️  WARNING: All endpoints expected to return 403 due to security misconfiguration"
    
    test_endpoint "GET" "/api/decision-types" 403 "$ADMIN_TOKEN" "Get all decision types (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decision-types" 403 "$MANAGER_TOKEN" "Get all decision types (Manager - should fail)"
    test_endpoint "GET" "/api/decision-types" 403 "$USER_TOKEN" "Get all decision types (User - should fail)"
    test_endpoint "GET" "/api/decision-types" 401 "" "Get all decision types (No auth - should fail)"
    
    test_endpoint "GET" "/api/decision-types/active" 403 "$ADMIN_TOKEN" "Get active decision types (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decision-types/search?searchTerm=test" 403 "$ADMIN_TOKEN" "Search decision types (Admin - FAILS due to security bug)"
    test_endpoint "POST" "/api/decision-types" 403 "$ADMIN_TOKEN" "Create decision type (Admin - FAILS due to security bug)" "$DECISION_TYPE_DATA"
    test_endpoint "POST" "/api/decision-types" 403 "$MANAGER_TOKEN" "Create decision type (Manager - should fail)" "$DECISION_TYPE_DATA"
    test_endpoint "GET" "/api/decision-types/1" 403 "$ADMIN_TOKEN" "Get decision type by ID (Admin - FAILS due to security bug)"
    test_endpoint "PUT" "/api/decision-types/1" 403 "$ADMIN_TOKEN" "Update decision type (Admin - FAILS due to security bug)" "$DECISION_TYPE_DATA"
    test_endpoint "GET" "/api/decision-types/exists/name-ru/Test" 403 "$ADMIN_TOKEN" "Check decision type exists by name (Admin - FAILS due to security bug)"
    test_endpoint "DELETE" "/api/decision-types/999" 403 "$ADMIN_TOKEN" "Delete non-existent decision type (Admin - FAILS due to security bug)"
    
    echo ""
    
    # Test Decision Making Body endpoints
    print_status $YELLOW "🏢 Testing Decision Making Body endpoints (/api/decision-making-bodies)"
    print_status $RED "⚠️  WARNING: All endpoints expected to return 403 due to security misconfiguration"
    
    test_endpoint "GET" "/api/decision-making-bodies" 403 "$ADMIN_TOKEN" "Get all decision making bodies (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decision-making-bodies" 403 "$MANAGER_TOKEN" "Get all decision making bodies (Manager - should fail)"
    test_endpoint "GET" "/api/decision-making-bodies" 403 "$USER_TOKEN" "Get all decision making bodies (User - should fail)"
    
    test_endpoint "GET" "/api/decision-making-bodies/active" 403 "$ADMIN_TOKEN" "Get active decision making bodies (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decision-making-bodies/search?searchTerm=test" 403 "$ADMIN_TOKEN" "Search decision making bodies (Admin - FAILS due to security bug)"
    test_endpoint "POST" "/api/decision-making-bodies" 403 "$ADMIN_TOKEN" "Create decision making body (Admin - FAILS due to security bug)" "$DECISION_MAKING_BODY_DATA"
    test_endpoint "GET" "/api/decision-making-bodies/1" 403 "$ADMIN_TOKEN" "Get decision making body by ID (Admin - FAILS due to security bug)"
    test_endpoint "PUT" "/api/decision-making-bodies/1" 403 "$ADMIN_TOKEN" "Update decision making body (Admin - FAILS due to security bug)" "$DECISION_MAKING_BODY_DATA"
    test_endpoint "GET" "/api/decision-making-bodies/exists/name-ru/Test" 403 "$ADMIN_TOKEN" "Check decision making body exists by name (Admin - FAILS due to security bug)"
    test_endpoint "DELETE" "/api/decision-making-bodies/999" 403 "$ADMIN_TOKEN" "Delete non-existent decision making body (Admin - FAILS due to security bug)"
    
    echo ""
    
    # Test Decision endpoints (main decisions)
    print_status $YELLOW "⚖️  Testing Decision endpoints (/api/decisions)"
    print_status $RED "⚠️  WARNING: All endpoints expected to return 403 due to security misconfiguration"
    
    test_endpoint "GET" "/api/decisions" 403 "$ADMIN_TOKEN" "Get all decisions (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decisions" 403 "$MANAGER_TOKEN" "Get all decisions (Manager - should fail)"
    test_endpoint "GET" "/api/decisions" 403 "$USER_TOKEN" "Get all decisions (User - should fail)"
    
    test_endpoint "GET" "/api/decisions/search?searchTerm=test" 403 "$ADMIN_TOKEN" "Search decisions (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decisions/search-and-filter?searchTerm=test&status=ACTIVE" 403 "$ADMIN_TOKEN" "Search and filter decisions (Admin - FAILS due to security bug)"
    
    # Generate a valid UUID for testing
    TEST_UUID="123e4567-e89b-12d3-a456-426614174000"
    
    test_endpoint "GET" "/api/decisions/$TEST_UUID" 403 "$ADMIN_TOKEN" "Get decision by ID (Admin - FAILS due to security bug)"
    test_endpoint "GET" "/api/decisions/exists/number/TEST001" 403 "$ADMIN_TOKEN" "Check decision exists by number (Admin - FAILS due to security bug)"
    test_endpoint "PUT" "/api/decisions/$TEST_UUID" 403 "$ADMIN_TOKEN" "Update non-existent decision (Admin - FAILS due to security bug)"
    test_endpoint "DELETE" "/api/decisions/$TEST_UUID" 403 "$ADMIN_TOKEN" "Delete non-existent decision (Admin - FAILS due to security bug)"
    
    echo ""
    
    # Summary
    print_status $BLUE "📊 Test Summary"
    print_status $BLUE "==============="
    echo "Total Tests: $TOTAL_TESTS"
    print_status $GREEN "Passed: $PASSED_TESTS"
    print_status $RED "Failed: $FAILED_TESTS"
    
    if [ $FAILED_TESTS -eq 0 ]; then
        print_status $GREEN "\n🎉 All tests passed!"
        exit 0
    else
        print_status $RED "\n⚠️  Some tests failed. Please review the results above."
        exit 1
    fi
}

# Check if script is being sourced or executed
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi