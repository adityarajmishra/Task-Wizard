Task Wizard - Spring Boot Task Management System
================================================

A robust and scalable task management system built with Spring Boot, implementing clean architecture and RESTful API design principles.

Project Overview
----------------

Task Wizard is a comprehensive task management system that allows users to:

-   Create and manage users
-   Create, assign, and track tasks
-   Monitor task status and progress
-   Handle task assignments and updates

### Key Features

-   Clean, layered architecture
-   RESTful API design
-   Comprehensive error handling
-   Input validation
-   Thread-safe implementation
-   Extensive test coverage
-   OpenAPI documentation
-   Scalable and maintainable codebase

Technical Stack
---------------

-   Java 17
-   Spring Boot 3.2.0
-   Spring Security
-   Spring Validation
-   Lombok
-   JUnit 5
-   Mockito
-   JaCoCo for test coverage
-   OpenAPI (Swagger) for documentation

Project Structure
-----------------
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── taskwizard/
│   │           ├── TaskWizardApplication.java
│   │           ├── common/
│   │           │   ├── domain/
│   │           │   ├── exception/
│   │           │   ├── validation/
│   │           │   └── aspect/
│   │           ├── config/
│   │           ├── user/
│   │           │   ├── controller/
│   │           │   ├── domain/
│   │           │   ├── dto/
│   │           │   ├── repository/
│   │           │   └── service/
│   │           └── task/
│   │               ├── controller/
│   │               ├── domain/
│   │               ├── dto/
│   │               ├── repository/
│   │               └── service/
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/
            └── taskwizard/
                ├── user/
                └── task/
```

Prerequisites
-------------

-   JDK 17 or higher
-   Maven 3.6 or higher
-   Git

Getting Started
---------------

1.  Clone the repository:

`git clone https://github.com/yourusername/task-wizard.git
cd task-wizard`

1.  Build the project:

`./mvnw clean install`

1.  Run the application:

`./mvnw spring-boot:run`

The application will be available at `http://localhost:8080`

API Documentation
-----------------

Access the Swagger UI at: `http://localhost:8080/swagger-ui.html`

### Available Endpoints

#### User Management

-   POST /api/v1/users - Create a new user
-   GET /api/v1/users - Get all users
-   GET /api/v1/users/{id} - Get user by ID
-   DELETE /api/v1/users/{id} - Delete user

#### Task Management

-   POST /api/v1/tasks - Create a new task
-   GET /api/v1/tasks - Get all tasks
-   GET /api/v1/tasks/{id} - Get task by ID
-   GET /api/v1/tasks/user/{userId} - Get tasks by user
-   PATCH /api/v1/tasks/{id} - Update task status
-   DELETE /api/v1/tasks/{id} - Delete task

Testing
-------

### Running Tests

1.  Run all tests:

`./mvnw test`

1.  Run tests with coverage report:

`./mvnw clean verify`

1.  Run specific test class:

`./mvnw test -Dtest=TaskServiceTest`

1.  Run specific test method:

`./mvnw test -Dtest=TaskServiceTest#createTask_Success`

### Test Coverage Report

After running tests with coverage, find the detailed report at:

`target/site/jacoco/index.html`

Development Guidelines
----------------------

### Code Style

-   Follow Google Java Style Guide
-   Use meaningful names for classes, methods, and variables
-   Write clear and concise comments
-   Keep methods focused and small

### Git Workflow

1.  Create feature branch from main
2.  Make changes and write tests
3.  Ensure all tests pass
4.  Create pull request
5.  Code review and merge

### Adding New Features

1.  Define the API endpoint in controller
2.  Create/update domain models
3.  Implement service logic
4.  Write comprehensive tests
5.  Update documentation

Error Handling
--------------

The application uses a global exception handler that returns appropriate HTTP status codes:

-   400 Bad Request - Invalid input
-   404 Not Found - Resource not found
-   409 Conflict - Resource conflict (e.g., duplicate email)
-   500 Internal Server Error - Unexpected errors

Configuration
-------------

### Application Properties



```# Server Configuration
server.port=8080
server.servlet.context-path=/api/v1

# Logging
logging.level.com.taskwizard=DEBUG

# OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

Performance Considerations
--------------------------

-   In-memory data storage with thread-safe implementation
-   Efficient error handling and validation
-   Optimized database queries (when implemented)
-   Proper connection pooling

Future Enhancements
-------------------

1.  Database Integration
    -   Replace in-memory storage with persistent storage
    -   Add database migrations
    -   Implement caching
2.  Security Enhancements
    -   JWT authentication
    -   Role-based access control
    -   API rate limiting
3.  Additional Features
    -   Task categories and tags
    -   Task priorities
    -   Due dates and reminders
    -   File attachments
    -   Email notifications

Contributing
------------

1.  Fork the repository
2.  Create your feature branch
3.  Commit your changes
4.  Push to the branch
5.  Create a pull request

License
-------

This project is licensed under the MIT License - see the LICENSE file for details

Support
-------

For support and questions, please open an issue in the GitHub repository.