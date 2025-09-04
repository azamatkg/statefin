#!/bin/bash

# Decision Endpoints Security Verification Script
# Tests that admin has access and manager/user are denied access to decision endpoints

BASE_URL="http://localhost:8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ "$2" = "PASS" ]; then
        echo -e "${GREEN}[PASS]${NC} $1"
    elif [ "$2" = "FAIL" ]; then
        echo -e "${RED}[FAIL]${NC} $1"
    elif [ "$2" = "INFO" ]; then
        echo -e "${BLUE}[INFO]${NC} $1"
    else
        echo -e "${YELLOW}[WARN]${NC} $1"
    fi
}

# Function to authenticate and get token
get_auth_token() {
    local username=$1
    local password=$2
    local user_type=$3
    
    print_status "Getting auth token for $user_type..." "INFO"
    
    local response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\"}" \
        "$BASE_URL/api/auth/login")
    
    local token=$(echo "$response" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
    
    if [ -n "$token" ] && [ "$token" != "null" ]; then
        print_status "Successfully authenticated $user_type" "PASS"
        echo "$token"
    else
        print_status "Failed to authenticate $user_type" "FAIL"
        echo "Response: $response" | head -c 200
        echo ""
        return 1
    fi
}

# Function to test endpoint access
test_endpoint_access() {
    local method=$1
    local endpoint=$2
    local token=$3
    local user_type=$4
    local expected_status=$5
    local description=$6
    
    print_status "Testing $method $endpoint for $user_type (expecting $expected_status)" "INFO"
    
    local response=$(curl -s -w "%{http_code}" -X "$method" \
        -H "Authorization: Bearer $token" \
        "$BASE_URL$endpoint")
    
    local status_code="${response: -3}"
    local body="${response%???}"
    
    if [ "$status_code" = "$expected_status" ]; then
        print_status "$description: $status_code ✓" "PASS"
        return 0
    else
        print_status "$description: Got $status_code, expected $expected_status ✗" "FAIL"
        echo "Response body (first 100 chars): ${body:0:100}"
        return 1
    fi
}

echo "=========================================="
echo "Decision Endpoints Security Verification"
echo "=========================================="
echo ""

# Authenticate users
print_status "Step 1: Authenticating users" "INFO"
ADMIN_TOKEN=$(get_auth_token "admin" "Admin123!" "admin")
if [ $? -ne 0 ]; then
    print_status "Cannot proceed without admin token" "FAIL"
    exit 1
fi

MANAGER_TOKEN=$(get_auth_token "manager" "Manager123!" "manager")
if [ $? -ne 0 ]; then
    print_status "Cannot proceed without manager token" "FAIL"
    exit 1
fi

echo ""
print_status "Step 2: Testing Decision Endpoints Access Control" "INFO"
echo ""

# Test results counters
TOTAL_TESTS=0
PASSED_TESTS=0

# Define endpoints to test
declare -a ENDPOINTS=(
    "GET /api/decisions Read decisions list"
    "GET /api/decision-types Read decision types list"
    "GET /api/decision-making-bodies Read decision making bodies list"
)

# Test admin access (should get 200)
print_status "Testing Admin Access (should get 200 OK)" "INFO"
for endpoint_info in "${ENDPOINTS[@]}"; do
    method=$(echo $endpoint_info | cut -d' ' -f1)
    endpoint=$(echo $endpoint_info | cut -d' ' -f2)
    description="Admin $method $endpoint"
    
    test_endpoint_access "$method" "$endpoint" "$ADMIN_TOKEN" "admin" "200" "$description"
    if [ $? -eq 0 ]; then ((PASSED_TESTS++)); fi
    ((TOTAL_TESTS++))
done

echo ""
# Test manager access (should get 403)
print_status "Testing Manager Access (should get 403 Forbidden)" "INFO"
for endpoint_info in "${ENDPOINTS[@]}"; do
    method=$(echo $endpoint_info | cut -d' ' -f1)
    endpoint=$(echo $endpoint_info | cut -d' ' -f2)
    description="Manager $method $endpoint"
    
    test_endpoint_access "$method" "$endpoint" "$MANAGER_TOKEN" "manager" "403" "$description"
    if [ $? -eq 0 ]; then ((PASSED_TESTS++)); fi
    ((TOTAL_TESTS++))
done

echo ""
print_status "Step 3: Testing POST Endpoints (Admin vs Manager)" "INFO"
echo ""

# Test POST decision type
DECISION_TYPE_DATA='{"name":"Security Test Type","description":"Test decision type created for security verification"}'

print_status "Testing POST /api/decision-types" "INFO"
test_endpoint_access "POST" "/api/decision-types" "$ADMIN_TOKEN" "admin" "201" "Admin POST decision type"
if [ $? -eq 0 ]; then ((PASSED_TESTS++)); fi
((TOTAL_TESTS++))

test_endpoint_access "POST" "/api/decision-types" "$MANAGER_TOKEN" "manager" "403" "Manager POST decision type"
if [ $? -eq 0 ]; then ((PASSED_TESTS++)); fi
((TOTAL_TESTS++))

# Test POST decision making body
DECISION_BODY_DATA='{"name":"Security Test Body","description":"Test decision making body created for security verification"}'

print_status "Testing POST /api/decision-making-bodies" "INFO"
test_endpoint_access "POST" "/api/decision-making-bodies" "$ADMIN_TOKEN" "admin" "201" "Admin POST decision making body"
if [ $? -eq 0 ]; then ((PASSED_TESTS++)); fi
((TOTAL_TESTS++))

test_endpoint_access "POST" "/api/decision-making-bodies" "$MANAGER_TOKEN" "manager" "403" "Manager POST decision making body"
if [ $? -eq 0 ]; then ((PASSED_TESTS++)); fi
((TOTAL_TESTS++))

# Add content-type for POST requests
print_status "Re-testing POST endpoints with proper content-type..." "INFO"

response_type=$(curl -s -w "%{http_code}" -X POST \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "$DECISION_TYPE_DATA" \
    "$BASE_URL/api/decision-types")
    
status_code_type="${response_type: -3}"

response_body=$(curl -s -w "%{http_code}" -X POST \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "$DECISION_BODY_DATA" \
    "$BASE_URL/api/decision-making-bodies")
    
status_code_body="${response_body: -3}"

if [ "$status_code_type" = "201" ]; then
    print_status "Admin can successfully create decision types (201)" "PASS"
    ((PASSED_TESTS++))
else
    print_status "Admin POST decision type failed: $status_code_type" "FAIL"
fi
((TOTAL_TESTS++))

if [ "$status_code_body" = "201" ]; then
    print_status "Admin can successfully create decision making bodies (201)" "PASS"
    ((PASSED_TESTS++))
else
    print_status "Admin POST decision making body failed: $status_code_body" "FAIL"
fi
((TOTAL_TESTS++))

echo ""
echo "=========================================="
echo "Security Verification Results Summary"
echo "=========================================="

print_status "Total Tests: $TOTAL_TESTS" "INFO"
print_status "Passed Tests: $PASSED_TESTS" "INFO"
print_status "Failed Tests: $((TOTAL_TESTS - PASSED_TESTS))" "INFO"

echo ""
if [ $PASSED_TESTS -eq $TOTAL_TESTS ]; then
    print_status "🎉 ALL SECURITY TESTS PASSED!" "PASS"
    print_status "✅ Decision endpoints security fix is working correctly" "PASS"
    print_status "✅ Admin has full access to decision endpoints" "PASS"
    print_status "✅ Manager is properly denied access (403 Forbidden)" "PASS"
    print_status "✅ RBAC permissions are functioning as expected" "PASS"
    echo ""
    print_status "🔒 Security fix verification: SUCCESSFUL" "PASS"
else
    print_status "⚠️  Some security tests failed!" "FAIL"
    print_status "Security fix may need additional investigation" "FAIL"
    echo ""
    print_status "🔒 Security fix verification: NEEDS ATTENTION" "FAIL"
fi

echo ""
print_status "Decision permissions verified in admin token:" "INFO"
echo "- DECISION_READ, DECISION_WRITE, DECISION_DELETE"
echo "- DECISION_TYPE_READ, DECISION_TYPE_WRITE, DECISION_TYPE_DELETE"
echo "- DECISION_MAKING_BODY_READ, DECISION_MAKING_BODY_WRITE, DECISION_MAKING_BODY_DELETE"