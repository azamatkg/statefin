# StateFin User Management System

A production-ready Spring Boot application for user management with comprehensive Role-Based Access Control (RBAC).

## Features

### Core Functionality
- ✅ User registration and authentication
- ✅ JWT-based stateless authentication
- ✅ Role-based access control (RBAC)
- ✅ Database-driven roles and permissions
- ✅ RESTful API with OpenAPI documentation
- ✅ Multi-profile configuration (dev/prod/test)

### Security
- ✅ BCrypt password encryption
- ✅ JWT token generation and validation  
- ✅ Method-level security annotations
- ✅ CORS configuration
- ✅ Global exception handling

### Data Management
- ✅ PostgreSQL for production
- ✅ H2 for testing
- ✅ JPA/Hibernate ORM
- ✅ Database migrations
- ✅ Initial data seeding

### DevOps & Production
- ✅ Docker containerization
- ✅ Docker Compose setup
- ✅ Health checks and metrics
- ✅ Structured logging
- ✅ Comprehensive testing

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose (optional)

### Running Locally

1. **Clone and build:**
```bash
git clone <repository-url>
cd statefin
./mvnw clean install
```

2. **Start with Docker (Recommended):**
```bash
docker-compose up -d
```

3. **Or run with local PostgreSQL:**
```bash
# Start PostgreSQL locally on port 5432
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

4. **Access the application:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Health Check: http://localhost:8080/actuator/health

### Default Users

The system creates default users on startup:

| Username | Password    | Role    | Permissions |
|----------|-------------|---------|-------------|
| admin    | Admin123!   | ADMIN   | All permissions |
| manager  | Manager123! | MANAGER | User management |
| user     | User123!    | USER    | Basic access |

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/register` - User registration

### User Management
- `GET /api/users/me` - Get current user profile
- `GET /api/users` - List all users (Admin)
- `GET /api/users/{id}` - Get user by ID (Admin)
- `PUT /api/users/{id}` - Update user (Admin)
- `DELETE /api/users/{id}` - Delete user (Admin)
- `POST /api/users/{userId}/roles/{roleId}` - Assign role (Admin)
- `DELETE /api/users/{userId}/roles/{roleId}` - Remove role (Admin)

### Role Management
- `GET /api/roles` - List all roles
- `GET /api/roles/{id}` - Get role by ID
- `POST /api/roles` - Create role
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `GET /api/roles/{id}/permissions` - Get role permissions
- `POST /api/roles/{roleId}/permissions/{permissionId}` - Assign permission
- `DELETE /api/roles/{roleId}/permissions/{permissionId}` - Remove permission

### Permission Management
- `GET /api/permissions` - List all permissions
- `GET /api/permissions/{id}` - Get permission by ID
- `POST /api/permissions` - Create permission
- `PUT /api/permissions/{id}` - Update permission
- `DELETE /api/permissions/{id}` - Delete permission
- `GET /api/permissions/resources` - Get available resources
- `GET /api/permissions/actions` - Get available actions

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection string | `jdbc:postgresql://localhost:5432/statefin_dev` |
| `DATABASE_USERNAME` | Database username | `statefin` |
| `DATABASE_PASSWORD` | Database password | `statefin123` |
| `JWT_SECRET` | JWT signing secret | Auto-generated |
| `ADMIN_USERNAME` | Initial admin username | `admin` |
| `ADMIN_PASSWORD` | Initial admin password | `Admin123!` |

### Profiles

- **dev**: Development with PostgreSQL, debug logging
- **prod**: Production optimized, requires environment variables
- **test**: Testing with H2 in-memory database

## Testing

Run all tests:
```bash
./mvnw test
```

Run with coverage:
```bash
./mvnw test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

## Database Schema

### Core Tables
- `users` - User accounts
- `roles` - Role definitions  
- `permissions` - Permission definitions
- `user_roles` - User-role assignments
- `role_permissions` - Role-permission assignments

### Initial Permissions
- **User Management**: `USER_READ`, `USER_WRITE`, `USER_DELETE`
- **Role Management**: `ROLE_READ`, `ROLE_WRITE`, `ROLE_DELETE`, `ROLE_MANAGE`
- **Permission Management**: `PERMISSION_READ`, `PERMISSION_WRITE`, `PERMISSION_DELETE`

## Security Model

The application implements a fine-grained RBAC system:

1. **Users** can have multiple **Roles**
2. **Roles** can have multiple **Permissions**
3. **Permissions** define specific actions on resources
4. Access is controlled at the method level using `@PreAuthorize`

Example permission check:
```java
@PreAuthorize("hasAuthority('USER_READ')")
public ResponseEntity<PagedResponse<UserResponse>> getAllUsers() {
    // Implementation
}
```

## Development

### Architecture
- **Layered Architecture**: Controllers → Services → Repositories → Entities
- **Clean Code**: SOLID principles, dependency injection
- **Security**: JWT authentication, method-level authorization
- **Error Handling**: Global exception handler with standardized responses

### Key Dependencies
- Spring Boot 3.2.1
- Spring Security 6.x
- Spring Data JPA
- PostgreSQL Driver
- JJWT 0.12.3
- SpringDoc OpenAPI 2.2.0

### Code Quality
- Lombok for boilerplate reduction
- Jakarta Validation for input validation
- Comprehensive logging with SLF4J
- Unit and integration tests
- Docker for consistent environments

## Deployment

### Docker Production Deployment

1. **Build image:**
```bash
docker build -t statefin-app .
```

2. **Run with external database:**
```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://your-db:5432/statefin \
  -e DATABASE_USERNAME=your-user \
  -e DATABASE_PASSWORD=your-password \
  -e JWT_SECRET=your-jwt-secret \
  statefin-app
```

### Health Monitoring
- Health endpoint: `/actuator/health`
- Metrics: `/actuator/metrics`
- Application info: `/actuator/info`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.