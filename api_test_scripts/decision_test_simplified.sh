#!/bin/bash

# Simplified Decision API Testing Script
# Focus on critical security configuration issue

BASE_URL="http://localhost:8080"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${1}${2}${NC}"
}

print_status $BLUE "🚀 Decision API Security Configuration Test"
print_status $BLUE "==========================================="

# Authenticate admin
echo '{"username":"admin","password":"Admin123!"}' > /tmp/admin_login.json
ADMIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" -H "Content-Type: application/json" -d @/tmp/admin_login.json)
ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
    print_status $RED "❌ Admin authentication failed"
    exit 1
fi

print_status $GREEN "✅ Admin authenticated successfully"

# Test regular endpoint (should work)
print_status $YELLOW "\n🔍 Testing regular API endpoint (should work):"
STATUS=$(curl -s -w "%{http_code}" -o /dev/null -H "Authorization: Bearer $ADMIN_TOKEN" "$BASE_URL/api/users")
if [ "$STATUS" -eq 200 ]; then
    print_status $GREEN "✅ /api/users returns 200 - Normal permission-based auth works"
else
    print_status $RED "❌ /api/users returns $STATUS - Something is wrong"
fi

# Test decision endpoints (should fail due to security bug)
print_status $YELLOW "\n🔍 Testing decision API endpoints (should fail due to security bug):"

ENDPOINTS=(
    "/api/decisions"
    "/api/decision-types" 
    "/api/decision-making-bodies"
    "/api/decisions/search?searchTerm=test"
    "/api/decision-types/active"
    "/api/decision-making-bodies/active"
)

ALL_FAILED=true
for endpoint in "${ENDPOINTS[@]}"; do
    STATUS=$(curl -s -w "%{http_code}" -o /dev/null -H "Authorization: Bearer $ADMIN_TOKEN" "$BASE_URL$endpoint")
    if [ "$STATUS" -eq 403 ]; then
        print_status $GREEN "✅ $endpoint returns 403 (expected due to security bug)"
    else
        print_status $RED "❌ $endpoint returns $STATUS (unexpected!)"
        ALL_FAILED=false
    fi
done

print_status $BLUE "\n📊 Test Results:"
if [ "$ALL_FAILED" = true ]; then
    print_status $RED "🚨 CRITICAL SECURITY BUG CONFIRMED:"
    print_status $RED "   All decision endpoints are inaccessible due to incorrect @PreAuthorize configuration"
    print_status $RED "   Controllers use hasRole('ADMIN') but system uses permission-based authorities"
    print_status $YELLOW "   Fix: Change @PreAuthorize from hasRole('ADMIN') to hasAuthority('DECISION_READ'), etc."
else
    print_status $YELLOW "⚠️  Some endpoints returned unexpected status codes - needs investigation"
fi

# Cleanup
rm -f /tmp/admin_login.json

print_status $BLUE "\n🏁 Test completed"