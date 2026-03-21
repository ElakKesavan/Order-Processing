# E-Commerce Order Processing System

A robust, enterprise-ready E-commerce Order Processing backend built with Java 17 and Spring Boot 3.

## 🚀 Features

* **Order Management:** Place, retrieve, list, and cancel orders efficiently.
* **Security & Authentication:** JWT-based stateless authentication with strict Role-Based Access Control (Admin vs. Customer).
* **Automated Background Processing:** Scheduled temporal jobs automatically transition `PENDING` orders to `PROCESSING`.
* **Data Integrity:** Real-time PostgreSQL database interactions optimized via Spring Data JPA.
* **Mock Integrations:** Standalone localized mock clients imitating Inventory and Payment gateway behaviors.

## 🛠️ Technology Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Data Access:** Spring Data JPA / Hibernate
* **Database:** PostgreSQL
* **Security:** Spring Security + JSON Web Tokens (JWT)
* **Code Quality:** Spotless, Checkstyle, JaCoCo
* **Build Tool:** Maven

## 📦 Getting Started

### Prerequisites
* Java 17 JDK or higher
* Maven 3.6+
* PostgreSQL 14+

### Installation & Setup

1. **Clone the repository** (or navigate to the project root directory).
2. **Configure Database:**
   Ensure your local PostgreSQL server is running. Create an empty database named `order_processing`. Inside your project, verify `src/main/resources/application.yml` matches your database credentials.
3. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```
4. **Access the API:**
   The server natively starts on `http://localhost:8080`.

## 📖 API Documentation (Overview)

### 🔐 Authentication
* `POST /api/v1/auth/register` - Create a new user (Restricted to `CUSTOMER` role)
* `POST /api/v1/auth/login` - Authenticate and yield a bearer token

### 📦 Orders
* `POST /api/v1/orders` - Place a new order with items (Customer restricted)
* `GET /api/v1/orders` - Unified endpoint for listing orders (Roles dictate response behavior via JWT)
* `GET /api/v1/orders/{id}` - Fetch explicit order details
* `PATCH /api/v1/orders/{id}/status` - Override an order's lifecycle status (Admin restricted)
* `POST /api/v1/orders/{id}/cancel` - Request cancellation of a `PENDING` order (Customer restricted)

## ⏳ Background Orchestration
The system incorporates an automated Spring `@Scheduled` orchestrator that periodically seeks out `PENDING` orders idling for more than 5 minutes and advances them sequentially to `PROCESSING`.
