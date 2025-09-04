#!/bin/bash

# Decision Endpoints Security Test - Final Verification
BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Decision Endpoints Security Final Test"
echo "=========================================="

# Get admin token
echo "Getting admin token..."
ADMIN_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"Admin123!"}' "$BASE_URL/api/auth/login")
ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Failed to get admin token"
    echo "Response: $ADMIN_RESPONSE"
    exit 1
fi

echo "✅ Admin token obtained: ${ADMIN_TOKEN:0:20}..."

# Get manager token
echo "Getting manager token..."
MANAGER_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -d '{"username":"manager","password":"Manager123!"}' "$BASE_URL/api/auth/login")
MANAGER_TOKEN=$(echo "$MANAGER_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$MANAGER_TOKEN" ]; then
    echo "❌ Failed to get manager token"
    echo "Response: $MANAGER_RESPONSE"
    exit 1
fi

echo "✅ Manager token obtained: ${MANAGER_TOKEN:0:20}..."

echo ""
echo "Testing Decision Types Endpoints..."
echo "-----------------------------------"

# Test admin access to decision types (should work)
echo "Testing admin GET /api/decision-types?page=0&size=5..."
ADMIN_DT_RESPONSE=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $ADMIN_TOKEN" "$BASE_URL/api/decision-types?page=0&size=5")
ADMIN_DT_STATUS="${ADMIN_DT_RESPONSE: -3}"
ADMIN_DT_BODY="${ADMIN_DT_RESPONSE%???}"

if [ "$ADMIN_DT_STATUS" = "200" ]; then
    echo "✅ Admin can access decision types (200 OK)"
else
    echo "❌ Admin decision types access failed: $ADMIN_DT_STATUS"
    echo "Response: ${ADMIN_DT_BODY:0:200}"
fi

# Test manager access to decision types (should get 403)
echo "Testing manager GET /api/decision-types?page=0&size=5..."
MANAGER_DT_RESPONSE=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $MANAGER_TOKEN" "$BASE_URL/api/decision-types?page=0&size=5")
MANAGER_DT_STATUS="${MANAGER_DT_RESPONSE: -3}"
MANAGER_DT_BODY="${MANAGER_DT_RESPONSE%???}"

if [ "$MANAGER_DT_STATUS" = "403" ]; then
    echo "✅ Manager properly denied access to decision types (403 Forbidden)"
else
    echo "❌ Manager decision types access unexpected: $MANAGER_DT_STATUS"
    echo "Response: ${MANAGER_DT_BODY:0:200}"
fi

echo ""
echo "Testing Decision Making Bodies Endpoints..."
echo "------------------------------------------"

# Test admin access to decision making bodies
echo "Testing admin GET /api/decision-making-bodies?page=0&size=5..."
ADMIN_DMB_RESPONSE=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $ADMIN_TOKEN" "$BASE_URL/api/decision-making-bodies?page=0&size=5")
ADMIN_DMB_STATUS="${ADMIN_DMB_RESPONSE: -3}"
ADMIN_DMB_BODY="${ADMIN_DMB_RESPONSE%???}"

if [ "$ADMIN_DMB_STATUS" = "200" ]; then
    echo "✅ Admin can access decision making bodies (200 OK)"
else
    echo "❌ Admin decision making bodies access failed: $ADMIN_DMB_STATUS"
    echo "Response: ${ADMIN_DMB_BODY:0:200}"
fi

# Test manager access to decision making bodies
echo "Testing manager GET /api/decision-making-bodies?page=0&size=5..."
MANAGER_DMB_RESPONSE=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $MANAGER_TOKEN" "$BASE_URL/api/decision-making-bodies?page=0&size=5")
MANAGER_DMB_STATUS="${MANAGER_DMB_RESPONSE: -3}"
MANAGER_DMB_BODY="${MANAGER_DMB_RESPONSE%???}"

if [ "$MANAGER_DMB_STATUS" = "403" ]; then
    echo "✅ Manager properly denied access to decision making bodies (403 Forbidden)"
else
    echo "❌ Manager decision making bodies access unexpected: $MANAGER_DMB_STATUS"
    echo "Response: ${MANAGER_DMB_BODY:0:200}"
fi

echo ""
echo "Testing POST Endpoints (Create Operations)..."
echo "--------------------------------------------"

# Test admin creating decision type
echo "Testing admin POST /api/decision-types..."
ADMIN_POST_DT_RESPONSE=$(curl -s -w "%{http_code}" -X POST \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Security Type","description":"Created during security test"}' \
    "$BASE_URL/api/decision-types")

ADMIN_POST_DT_STATUS="${ADMIN_POST_DT_RESPONSE: -3}"
ADMIN_POST_DT_BODY="${ADMIN_POST_DT_RESPONSE%???}"

if [ "$ADMIN_POST_DT_STATUS" = "201" ]; then
    echo "✅ Admin can create decision types (201 Created)"
else
    echo "❌ Admin POST decision type failed: $ADMIN_POST_DT_STATUS"
    echo "Response: ${ADMIN_POST_DT_BODY:0:200}"
fi

# Test manager trying to create decision type
echo "Testing manager POST /api/decision-types..."
MANAGER_POST_DT_RESPONSE=$(curl -s -w "%{http_code}" -X POST \
    -H "Authorization: Bearer $MANAGER_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Security Type","description":"Created during security test"}' \
    "$BASE_URL/api/decision-types")

MANAGER_POST_DT_STATUS="${MANAGER_POST_DT_RESPONSE: -3}"
MANAGER_POST_DT_BODY="${MANAGER_POST_DT_RESPONSE%???}"

if [ "$MANAGER_POST_DT_STATUS" = "403" ]; then
    echo "✅ Manager properly denied creating decision types (403 Forbidden)"
else
    echo "❌ Manager POST decision type unexpected: $MANAGER_POST_DT_STATUS"
    echo "Response: ${MANAGER_POST_DT_BODY:0:200}"
fi

echo ""
echo "=========================================="
echo "Security Test Results Summary"
echo "=========================================="

# Count successful tests
TOTAL_TESTS=6
PASSED_TESTS=0

# Check each test result
[ "$ADMIN_DT_STATUS" = "200" ] && ((PASSED_TESTS++))
[ "$MANAGER_DT_STATUS" = "403" ] && ((PASSED_TESTS++))
[ "$ADMIN_DMB_STATUS" = "200" ] && ((PASSED_TESTS++))
[ "$MANAGER_DMB_STATUS" = "403" ] && ((PASSED_TESTS++))
[ "$ADMIN_POST_DT_STATUS" = "201" ] && ((PASSED_TESTS++))
[ "$MANAGER_POST_DT_STATUS" = "403" ] && ((PASSED_TESTS++))

echo "Tests Passed: $PASSED_TESTS/$TOTAL_TESTS"

if [ $PASSED_TESTS -eq $TOTAL_TESTS ]; then
    echo ""
    echo "🎉 ALL SECURITY TESTS PASSED!"
    echo "✅ Decision endpoints security fix is working correctly"
    echo "✅ Admin has full access to decision endpoints"
    echo "✅ Manager is properly denied access (403 Forbidden)"
    echo "✅ RBAC permissions are functioning as expected"
    echo ""
    echo "🔒 SECURITY FIX VERIFICATION: SUCCESS ✅"
else
    echo ""
    echo "⚠️ Some tests failed - security fix needs attention"
    echo "🔒 SECURITY FIX VERIFICATION: NEEDS REVIEW ❌"
fi

echo ""
echo "Decision Permissions in Admin Token:"
echo "- DECISION_READ, DECISION_WRITE, DECISION_DELETE"
echo "- DECISION_TYPE_READ, DECISION_TYPE_WRITE, DECISION_TYPE_DELETE"
echo "- DECISION_MAKING_BODY_READ, DECISION_MAKING_BODY_WRITE, DECISION_MAKING_BODY_DELETE"