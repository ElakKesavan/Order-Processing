# Implementation Plan - Order Processing System

This document outlines the architecture and technical approach for the Order Processing System Minimum Viable Product (MVP), adhering to robust backend design principles.

## 1. High-Level Architecture
**Tech Stack**: Java 17+, Spring Boot 3.x, Spring Data JPA, Spring Security, PostgreSQL.
**Design Pattern**: Layered REST API Architecture (Controller -> Service -> Repository).
**Mocks**: External services (Inventory & Payment) will be mocked via standalone mock service classes resolving in-memory.

## 2. Database Schema Design (PostgreSQL)

We will use Hibernate to auto-generate the schema for the MVP, ensuring structural integrity via JPA annotations. Below is the mental model:

### `users`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY (Auto-increment) | Unique identifier |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | User's email |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `role` | VARCHAR(50) | NOT NULL | `CUSTOMER` or `ADMIN` |
| `created_at` | TIMESTAMP | NOT NULL | Creation time |

*(Index: `idx_users_email`)*

### `orders`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY (Auto-increment) | Unique identifier |
| `user_id` | BIGINT | FOREIGN KEY | Reference to `users.id` |
| `status` | VARCHAR(50) | NOT NULL | `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED` |
| `total_amount` | DECIMAL(10,2) | NOT NULL | Total cost of the order |
| `created_at` | TIMESTAMP | NOT NULL | Creation time |
| `updated_at` | TIMESTAMP | NOT NULL | Last update time |

*(Index on `status` and `created_at` to quickly and efficiently identify `PENDING` orders for the background job).*

### `order_items`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY (Auto-increment) | Unique identifier |
| `order_id` | BIGINT | FOREIGN KEY | Reference to `orders.id` |
| `product_id` | VARCHAR(100) | NOT NULL | The product identifier |
| `quantity` | INTEGER | NOT NULL | Quantity ordered |
| `price_at_purchase` | DECIMAL(10,2)| NOT NULL | Snapshot of the product price |

## 3. API Endpoints

### Authentication
* `POST /api/v1/auth/register` - Register a new user (`CUSTOMER` role only. `ADMIN` accounts are seeded/created by a SYSTEM user via separate flow).
* `POST /api/v1/auth/login` - Authenticate and receive a JWT.

### Orders
* `POST /api/v1/orders` (Auth: Customer) 
  * Payload: List of items `{ productId, quantity, price }`.
  * Flow: Calls Mock Inventory Service -> Calls Mock Payment Gateway -> Saves Order with status `PENDING` -> Returns Order details.
* `GET /api/v1/orders/{id}` (Auth: Customer/Admin)
  * Customers can only retrieve their own orders. Admins can retrieve any order.
* `GET /api/v1/orders` (Auth: Customer/Admin)
  * Unified endpoint to fetch orders (includes optional filtering `?status=PENDING`). 
  * Logic extracts JWT claims: if identity is `ADMIN`, all matching orders are returned; if `CUSTOMER`, orders are heavily restricted precisely to that customer.
* `PATCH /api/v1/orders/{id}/status` (Auth: Admin)
  * Updates the order status to `SHIPPED`, `DELIVERED`, etc.
* `POST /api/v1/orders/{id}/cancel` (Auth: Customer)
  * Cancels the order. Fails with HTTP 400 or HTTP 409 if status is not `PENDING`.

## 4. Mock Integrations
* **Inventory Service Mock**: Returns random/configured responses:
  1. `SUCCESS`
  2. `INSUFFICIENT_STOCK` (Order creation will fail)
  3. `SERVICE_UNAVAILABLE` (Order creation will fail gracefully)
* **Payment Service Mock**: Simply mimics a successful payment authorization.

## 5. Background Processing
A Spring `@Scheduled(fixedRate = 300000)` mechanism will be utilized to track order states:
* It will query the DB for orders where `status = 'PENDING'` and `created_at <= (NOW() - 5 minutes)`.
* It will update those orders to `PROCESSING` status within a transactional boundary.
* Note: This serves as a lightweight implementation until a decoupled message queue (e.g., RabbitMQ/Kafka) is introduced in a future iteration.

## 6. Verification Plan
* **Unit Tests**: Test `OrderService` verifying the core business logic (cancellation limits, status transitions, etc.).
* **Integration Tests**: Test repository/database layer interaction using Testcontainers if Docker is available.
* **Security Tests**: Validate WebMvc API endpoints verifying JWT Role constraints (Admin vs. Customer scope boundaries).

## 7. Documentation
* Create and maintain a comprehensive `README.md` containing features, tech stack overview, setup instructions, and the API endpoint summary for easy onboarding.

## 8. Code Quality & Standards
* **Spotless**: Enforces pristine code formatting. Bound to the Maven `check` phase.
* **Checkstyle**: Validates syntax against predefined coding standards. Bound to the Maven `check` phase.
* **JaCoCo**: Generates extensive test coverage reports to guarantee structural soundness. Setup in the `test` phase.
