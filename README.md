# user-management-BE

A User Management REST API built with Spring Boot 3, implementing JWT authentication and Role-Based Access Control (RBAC).

## Features

- **Spring Boot 3**: Modern Java framework with latest features
- **Spring Security**: JWT-based authentication with BCrypt password hashing
- **Role-Based Access Control (RBAC)**: 
  - ADMIN role: Full access (POST, PUT, DELETE, GET)
  - USER role: Read-only access (GET)
- **Jakarta Validation**: Input validation for emails and required fields
- **PostgreSQL**: Production-ready database
- **Layered Architecture**: Controller, Service, Repository, Entity pattern
- **Exception Handling**: Global exception handler with proper error responses

## Tech Stack

- Java 17
- Spring Boot 3.2.1
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Maven
- H2 Database (for testing)

## API Endpoints

### Authentication

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
```

### User Management

All user management endpoints require authentication via JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

#### Create User (ADMIN only)
```http
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "jane",
  "email": "jane@example.com",
  "password": "password123",
  "role": "USER"
}
```

#### Get User by ID (USER or ADMIN)
```http
GET /api/users/{id}
Authorization: Bearer <token>
```

#### Get All Users (USER or ADMIN)
```http
GET /api/users
Authorization: Bearer <token>
```

#### Update User (ADMIN only)
```http
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "jane_updated",
  "email": "jane.updated@example.com",
  "password": "newpassword123",
  "role": "ADMIN"
}
```

#### Delete User (ADMIN only)
```http
DELETE /api/users/{id}
Authorization: Bearer <token>
```

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

### Database Configuration

1. Create a PostgreSQL database:
```sql
CREATE DATABASE usermanagement;
```

2. Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/usermanagement
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Build and Run

1. Clone the repository:
```bash
git clone https://github.com/fauzaanirsyaadi/user-management-BE.git
cd user-management-BE
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Run Tests

```bash
mvn test
```

## Security

- Passwords are hashed using BCrypt before storage
- JWT tokens are used for authentication
- Token expiration: 24 hours (configurable in application.properties)
- CSRF protection is disabled (suitable for stateless API)
- Session management is stateless

## Validation Rules

- **Username**: Required, 3-50 characters, must be unique
- **Email**: Required, must be valid email format, must be unique
- **Password**: Required, minimum 6 characters
- **Role**: Required (USER or ADMIN)

## Error Responses

The API returns structured error responses:

```json
{
  "timestamp": "2026-01-18T13:28:51.837Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Username is already taken"
}
```

Validation errors:
```json
{
  "timestamp": "2026-01-18T13:28:51.837Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.