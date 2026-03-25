# Order Processing System (MVP)

A robust Spring Boot backend serving as the central engine for an E-commerce Order Processing System.

## Features
- **User Authentication**: Secure JWT-based login and registration. Includes role-based checks for `CUSTOMER` and `ADMIN` users.
- **Authentication Helper**: The logic for retrieving database entities from security context is centralized in `AuthenticationHelper`.
- **Caching Engine**: Spring Cache abstraction with Caffeine cache-aside strategy ensures sub-millisecond user retrieval with a 1-minute TTL.
- **Order Management**: Create, view, update, and cancel bounded orders. Implements strict tenancy rules (Customers view their own orders; Admins can view all). Order responses include user ID and email for administrative visibility.
- **External Mock Mocks**: Simulates responses from imaginary external Inventory and Payment processors securely isolated using `Service` facades.
- **Pagination**: Safe scaling of internal system lists via parameterization across `GET` order endpoints (`?page=0&size=20`).
- **Scheduled Status Update**: Background `PENDING` -> `PROCESSING` state machine executing on a fixed 5-minute schedule.

## Tech Stack
- **Framework**: JVM Java 17+, Spring Boot 3.x
- **Database**: PostgreSQL with Hibernate / Spring Data JPA
- **Security**: Spring Security + JWT
- **Build Tool**: Maven

## Architecture & Code Standards
- Code quality is strictly enforced via **Spotless** and **Checkstyle** embedded directly into the Maven lifecycle.
- **JaCoCo** confirms extensive Code coverage.

## Running the Application
Ensure PostgreSQL is running locally, and adjust database configurations in `application.yml` accordingly.

### Pre-flight Check
To run a full check including formatting (Spotless), static analysis (Checkstyle), and unit tests:
```bash
./mvnw clean verify
```

### Start the Service
```bash
./mvnw spring-boot:run
```


## API Documentation

Interactive API documentation is available at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

