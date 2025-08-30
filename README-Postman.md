# StateFin API Postman Collection

This directory contains Postman collection and environment files for comprehensive testing of the StateFin User Management API.

## Files Included

- **`StateFin-API.postman_collection.json`** - Complete API collection with all endpoints
- **`StateFin-API.postman_environment.json`** - Environment variables for testing
- **`README-Postman.md`** - This documentation file

## Quick Start

1. **Import Collection & Environment**
   - Open Postman
   - Import `StateFin-API.postman_collection.json`
   - Import `StateFin-API.postman_environment.json`
   - Select "StateFin API Environment" in the top-right corner

2. **Start the Application**
   ```bash
   # Using Docker (recommended)
   docker-compose up -d
   
   # Or using Maven
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Authentication Flow**
   - Go to "Authentication" → "Login"
   - Use default credentials: `admin` / `Admin123!`
   - Access token will be automatically saved to environment variables
   - All subsequent requests will use this token automatically

## Default Test Users

The application comes with three pre-configured users:

| Username | Password    | Role    | Permissions |
|----------|-------------|---------|-------------|
| admin    | Admin123!   | ADMIN   | Full access to all operations |
| manager  | Manager123! | MANAGER | User management permissions |
| user     | User123!    | USER    | Basic user access |

## Collection Structure

### 🔐 Authentication
- **POST** `/api/auth/login` - User authentication
- **POST** `/api/auth/register` - New user registration
- **POST** `/api/auth/refresh` - Token refresh

### 👤 User Management
- **GET** `/api/users/me` - Current user profile
- **GET** `/api/users` - List all users (paginated)
- **GET** `/api/users/{id}` - Get user by ID
- **PUT** `/api/users/{id}` - Update user
- **DELETE** `/api/users/{id}` - Soft delete user
- **POST** `/api/users/{userId}/roles/{roleId}` - Assign role
- **DELETE** `/api/users/{userId}/roles/{roleId}` - Remove role

### 🏷️ Role Management
- **GET** `/api/roles` - List all roles (paginated)
- **GET** `/api/roles/{id}` - Get role by ID
- **POST** `/api/roles` - Create new role
- **PUT** `/api/roles/{id}` - Update role
- **DELETE** `/api/roles/{id}` - Delete role
- **GET** `/api/roles/{id}/permissions` - Get role permissions
- **POST** `/api/roles/{roleId}/permissions/{permissionId}` - Assign permission
- **DELETE** `/api/roles/{roleId}/permissions/{permissionId}` - Remove permission

### 🔒 Permission Management
- **GET** `/api/permissions` - List all permissions (paginated)
- **GET** `/api/permissions/{id}` - Get permission by ID
- **POST** `/api/permissions` - Create new permission
- **PUT** `/api/permissions/{id}` - Update permission
- **DELETE** `/api/permissions/{id}` - Delete permission
- **GET** `/api/permissions/resources` - List available resources
- **GET** `/api/permissions/actions` - List available actions

## Environment Variables

The environment includes the following variables:

| Variable | Default Value | Description |
|----------|---------------|-------------|
| baseUrl | http://localhost:8080 | API base URL |
| accessToken | (auto-populated) | JWT access token |
| refreshToken | (auto-populated) | JWT refresh token |
| userId | 1 | Default user ID for testing |
| roleId | 1 | Default role ID for testing |
| permissionId | 1 | Default permission ID for testing |

## Automated Features

### Pre-request Scripts
- Auto-sets default environment variables
- Provides warnings for missing authentication
- Sets default test IDs

### Test Scripts
- Automatically saves JWT tokens after login/refresh
- Extracts and saves resource IDs from responses
- Validates response times (< 2000ms)
- Provides helpful error messages for 401/403 responses

### Response Validation
- Status code validation
- Token presence verification
- Permission error handling
- Performance monitoring

## Testing Workflows

### 1. Basic Authentication Test
1. Run "Authentication" → "Login"
2. Verify token is saved in environment
3. Run "User Management" → "Get Current User"

### 2. Full CRUD Operations
1. Login as admin
2. Create a new role: "Role Management" → "Create Role"
3. Create a new permission: "Permission Management" → "Create Permission"
4. Assign permission to role
5. Register a new user
6. Assign role to user

### 3. Permission Testing
1. Login as different users (admin, manager, user)
2. Test various endpoints to verify permission restrictions
3. Observe 403 Forbidden responses for insufficient permissions

## Common Request Bodies

### Login Request
```json
{
    "username": "admin",
    "password": "Admin123!"
}
```

### User Registration
```json
{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "NewUser123!",
    "firstName": "New",
    "lastName": "User"
}
```

### Role Creation
```json
{
    "name": "NEW_ROLE",
    "description": "A new role for testing"
}
```

### Permission Creation
```json
{
    "name": "CUSTOM_READ",
    "description": "Custom read permission",
    "resource": "CUSTOM",
    "action": "READ"
}
```

## Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Ensure you've logged in and token is set
   - Check if token has expired (24 hours)
   - Use refresh token endpoint

2. **403 Forbidden**
   - Login with appropriate user role
   - Check required permissions in API documentation

3. **404 Not Found**
   - Verify the application is running on port 8080
   - Check if resource IDs exist in the system

4. **Connection Refused**
   - Ensure application is running: `docker-compose up -d`
   - Check if port 8080 is available

### Environment Setup Verification
Run the health check endpoint manually:
```
GET http://localhost:8080/actuator/health
```

## API Documentation

When the application is running, you can access:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Support

For issues with the StateFin application itself, refer to the main project documentation. For Postman collection issues, verify:
1. Latest Postman version
2. Correct collection/environment import
3. Application running status
4. Network connectivity