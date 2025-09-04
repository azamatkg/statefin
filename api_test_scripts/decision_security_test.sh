#!/bin/bash

# Decision Endpoints Security Test Script
# Tests the new decision permissions and RBAC implementation

BASE_URL="http://localhost:8080"
RESULTS_FILE="decision_security_test_results.txt"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ "$2" = "PASS" ]; then
        echo -e "${GREEN}[PASS]${NC} $1"
    elif [ "$2" = "FAIL" ]; then
        echo -e "${RED}[FAIL]${NC} $1"
    else
        echo -e "${YELLOW}[INFO]${NC} $1"
    fi
}

# Function to test authentication
test_authentication() {
    local username=$1
    local password=$2
    local user_type=$3
    
    print_status "Testing authentication for $user_type user ($username)" "INFO"
    
    local response=$(curl -s -w "%{http_code}" -X POST \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\"}" \
        "$BASE_URL/api/auth/login")
    
    local status_code="${response: -3}"
    local body="${response%???}"
    
    if [ "$status_code" = "200" ]; then
        local token=$(echo "$body" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
        if [ -n "$token" ]; then
            print_status "Authentication successful for $user_type" "PASS"
            echo "$token"
            return 0
        else
            print_status "Authentication failed - no token received for $user_type" "FAIL"
            return 1
        fi
    else
        print_status "Authentication failed for $user_type (HTTP $status_code)" "FAIL"
        return 1
    fi
}

# Function to test endpoint access
test_endpoint() {
    local method=$1
    local endpoint=$2
    local token=$3
    local expected_status=$4
    local user_type=$5
    local data=$6
    
    print_status "Testing $method $endpoint for $user_type (expecting $expected_status)" "INFO"
    
    local curl_cmd="curl -s -w \"%{http_code}\" -X $method -H \"Authorization: Bearer $token\""
    
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -H \"Content-Type: application/json\" -d '$data'"
    fi
    
    curl_cmd="$curl_cmd $BASE_URL$endpoint"
    
    local response=$(eval $curl_cmd)
    local status_code="${response: -3}"
    local body="${response%???}"
    
    if [ "$status_code" = "$expected_status" ]; then
        print_status "$method $endpoint - $user_type: $status_code (Expected $expected_status)" "PASS"
        echo "Response body: $body" | head -c 200
        echo ""
        return 0
    else
        print_status "$method $endpoint - $user_type: $status_code (Expected $expected_status)" "FAIL"
        echo "Response body: $body" | head -c 200
        echo ""
        return 1
    fi
}

# Clear results file
> "$RESULTS_FILE"

echo "======================================"
echo "Decision Endpoints Security Test"
echo "======================================"

# Test authentication for all users
print_status "Step 1: Testing Authentication" "INFO"
echo ""

ADMIN_TOKEN=$(test_authentication "admin" "Admin123!" "admin")
if [ $? -ne 0 ]; then
    print_status "Failed to get admin token - aborting tests" "FAIL"
    exit 1
fi

MANAGER_TOKEN=$(test_authentication "manager" "Manager123!" "manager")
if [ $? -ne 0 ]; then
    print_status "Failed to get manager token - aborting tests" "FAIL"
    exit 1
fi

USER_TOKEN=$(test_authentication "user" "User123!" "user")
if [ $? -ne 0 ]; then
    print_status "Failed to get user token - aborting tests" "FAIL"
    exit 1
fi

echo ""
print_status "Step 2: Testing Decision Endpoints Access" "INFO"
echo ""

# Test endpoints that should work for admin (200) and fail for manager/user (403)
ENDPOINTS=(
    "GET /api/decisions"
    "GET /api/decision-types"
    "GET /api/decision-making-bodies"
)

ADMIN_TESTS=0
ADMIN_PASS=0
MANAGER_TESTS=0
MANAGER_PASS=0
USER_TESTS=0
USER_PASS=0

for endpoint_info in "${ENDPOINTS[@]}"; do
    method=$(echo $endpoint_info | cut -d' ' -f1)
    endpoint=$(echo $endpoint_info | cut -d' ' -f2)
    
    # Test admin access (should get 200)
    test_endpoint "$method" "$endpoint" "$ADMIN_TOKEN" "200" "admin"
    if [ $? -eq 0 ]; then ((ADMIN_PASS++)); fi
    ((ADMIN_TESTS++))
    
    # Test manager access (should get 403)
    test_endpoint "$method" "$endpoint" "$MANAGER_TOKEN" "403" "manager"
    if [ $? -eq 0 ]; then ((MANAGER_PASS++)); fi
    ((MANAGER_TESTS++))
    
    # Test user access (should get 403)
    test_endpoint "$method" "$endpoint" "$USER_TOKEN" "403" "user"
    if [ $? -eq 0 ]; then ((USER_PASS++)); fi
    ((USER_TESTS++))
    
    echo ""
done

echo ""
print_status "Step 3: Testing POST Endpoints (Admin Only)" "INFO"
echo ""

# Test POST endpoints with admin (should work)
DECISION_TYPE_DATA='{"name":"Test Decision Type","description":"Test description for decision type"}'
test_endpoint "POST" "/api/decision-types" "$ADMIN_TOKEN" "201" "admin" "$DECISION_TYPE_DATA"
if [ $? -eq 0 ]; then ((ADMIN_PASS++)); fi
((ADMIN_TESTS++))

DECISION_BODY_DATA='{"name":"Test Decision Making Body","description":"Test description for decision making body"}'
test_endpoint "POST" "/api/decision-making-bodies" "$ADMIN_TOKEN" "201" "admin" "$DECISION_BODY_DATA"
if [ $? -eq 0 ]; then ((ADMIN_PASS++)); fi
((ADMIN_TESTS++))

# Test POST endpoints with manager (should get 403)
test_endpoint "POST" "/api/decision-types" "$MANAGER_TOKEN" "403" "manager" "$DECISION_TYPE_DATA"
if [ $? -eq 0 ]; then ((MANAGER_PASS++)); fi
((MANAGER_TESTS++))

test_endpoint "POST" "/api/decision-making-bodies" "$MANAGER_TOKEN" "403" "manager" "$DECISION_BODY_DATA"
if [ $? -eq 0 ]; then ((MANAGER_PASS++)); fi
((MANAGER_TESTS++))

# Test POST endpoints with user (should get 403)
test_endpoint "POST" "/api/decision-types" "$USER_TOKEN" "403" "user" "$DECISION_TYPE_DATA"
if [ $? -eq 0 ]; then ((USER_PASS++)); fi
((USER_TESTS++))

test_endpoint "POST" "/api/decision-making-bodies" "$USER_TOKEN" "403" "user" "$DECISION_BODY_DATA"
if [ $? -eq 0 ]; then ((USER_PASS++)); fi
((USER_TESTS++))

echo ""
echo "======================================"
echo "Test Results Summary"
echo "======================================"

print_status "Admin User: $ADMIN_PASS/$ADMIN_TESTS tests passed" "INFO"
print_status "Manager User: $MANAGER_PASS/$MANAGER_TESTS tests passed" "INFO"
print_status "Regular User: $USER_PASS/$USER_TESTS tests passed" "INFO"

TOTAL_TESTS=$((ADMIN_TESTS + MANAGER_TESTS + USER_TESTS))
TOTAL_PASS=$((ADMIN_PASS + MANAGER_PASS + USER_PASS))

echo ""
if [ $TOTAL_PASS -eq $TOTAL_TESTS ]; then
    print_status "ALL TESTS PASSED! Security fix is working correctly." "PASS"
    print_status "✓ Admin has full access to decision endpoints" "PASS"
    print_status "✓ Manager/User are properly denied access (403)" "PASS"
    print_status "✓ Decision permissions are working as expected" "PASS"
else
    print_status "Some tests failed. Security fix may need additional work." "FAIL"
    print_status "Total: $TOTAL_PASS/$TOTAL_TESTS tests passed" "FAIL"
fi

echo ""
print_status "Test completed. Check above for detailed results." "INFO"