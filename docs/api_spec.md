# Order Processing — API Specification

> [!NOTE]
> **Base URL:** `http://localhost:8080`
> **Auth:** All Order endpoints require a JWT `Bearer` token in the `Authorization` header.
> Register & Login endpoints are public.

---

## Table of Contents

1. [Authentication](#1-authentication)
   - [Register](#11-register)
   - [Login](#12-login)
2. [Orders](#2-orders)
   - [Create Order](#21-create-order)
   - [List Orders](#22-list-orders-paginated)
   - [Get Order by ID](#23-get-order-by-id)
   - [Update Order Status](#24-update-order-status)
3. [Enums & Constants](#3-enums--constants)
4. [Mock Data Examples](#4-mock-data-examples)

---

## 1. Authentication

### 1.1 Register

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/v1/auth/register` |
| **Auth** | None |
| **Content-Type** | `application/json` |

#### Request Body

```json
{
  "email": "customer@example.com",
  "password": "secret123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | `string` | ✅ | Non-blank, valid email, max 255 chars |
| `password` | `string` | ✅ | Non-blank, 6–40 chars |

#### Responses

| Status | Body | Condition |
|--------|------|-----------|
| **200 OK** | `MessageResponse` | Registration successful |
| **400 Bad Request** | `MessageResponse` | Email already taken / validation error |

**200 — Success:**
```json
{
  "message": "User registered successfully!"
}
```

**400 — Duplicate email:**
```json
{
  "message": "Error: Email is already in use!"
}
```

---

### 1.2 Login

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/v1/auth/login` |
| **Auth** | None |
| **Content-Type** | `application/json` |

#### Request Body

```json
{
  "email": "customer@example.com",
  "password": "secret123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | `string` | ✅ | Non-blank |
| `password` | `string` | ✅ | Non-blank |

#### Responses

| Status | Body | Condition |
|--------|------|-----------|
| **200 OK** | `JwtResponse` | Credentials valid |
| **401 Unauthorized** | `MessageResponse` | Invalid credentials |

**200 — Success:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBleGFtcGxlLmNvbSIsImlhdCI6MTcxMTI1MDAwMCwiZXhwIjoxNzExMzM2NDAwfQ.abc123",
  "tokenType": "Bearer",
  "id": 1,
  "email": "customer@example.com",
  "role": "ROLE_CUSTOMER"
}
```

**401 — Bad credentials:**
```json
{
  "message": "Bad credentials: Bad credentials"
}
```

---

## 2. Orders

> [!IMPORTANT]
> All Order endpoints require the `Authorization: Bearer <token>` header.
> Tokens are obtained from the [Login](#12-login) endpoint.

---

### 2.1 Create Order

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/v1/orders` |
| **Auth** | `Bearer` token — **CUSTOMER** role only |
| **Content-Type** | `application/json` |

#### Request Body — `OrderRequest`

```json
{
  "items": [
    {
      "productId": "PROD-001",
      "quantity": 2,
      "price": 29.99
    },
    {
      "productId": "PROD-042",
      "quantity": 1,
      "price": 149.00
    }
  ]
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `items` | `OrderItemRequest[]` | ✅ | Non-empty list, each item validated |

**`OrderItemRequest`**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `productId` | `string` | ✅ | Non-blank |
| `quantity` | `integer` | ✅ | ≥ 1 |
| `price` | `number` (decimal) | ✅ | ≥ 0.01 |

#### Responses

| Status | Body | Condition |
|--------|------|-----------|
| **200 OK** | `OrderResponse` | Order created successfully |
| **400 Bad Request** | `MessageResponse` | Validation / business rule error |
| **401 Unauthorized** | — | Missing or invalid token |
| **403 Forbidden** | — | User does not have CUSTOMER role |

**200 — Success:**
```json
{
  "id": 101,
  "status": "PENDING",
  "totalAmount": 208.98,
  "items": [
    {
      "id": 1001,
      "productId": "PROD-001",
      "quantity": 2,
      "priceAtPurchase": 29.99
    },
    {
      "id": 1002,
      "productId": "PROD-042",
      "quantity": 1,
      "priceAtPurchase": 149.00
    }
  ],
  "createdAt": "2026-03-24T04:30:00",
  "updatedAt": "2026-03-24T04:30:00"
}
```

**400 — Business rule violation:**
```json
{
  "message": "Inventory check failed for product PROD-001"
}
```

---

### 2.2 List Orders (Paginated)

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/v1/orders` |
| **Auth** | `Bearer` token — **CUSTOMER** or **ADMIN** |

#### Query Parameters

| Param | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `status` | `string` (enum) | ❌ | — | Filter by `OrderStatus` |
| `page` | `integer` | ❌ | `0` | Zero-based page index |
| `size` | `integer` | ❌ | `20` | Page size |

> [!TIP]
> - **ADMIN** users see *all* orders in the system.
> - **CUSTOMER** users see *only their own* orders.

#### Example Request

```
GET /api/v1/orders?status=PENDING&page=0&size=10
Authorization: Bearer eyJhbGci...
```

#### Response — `Page<OrderResponse>`

| Status | Body | Condition |
|--------|------|-----------|
| **200 OK** | `Page<OrderResponse>` | Always returned |

**200 — Success:**
```json
{
  "content": [
    {
      "id": 101,
      "status": "PENDING",
      "totalAmount": 208.98,
      "items": [
        {
          "id": 1001,
          "productId": "PROD-001",
          "quantity": 2,
          "priceAtPurchase": 29.99
        },
        {
          "id": 1002,
          "productId": "PROD-042",
          "quantity": 1,
          "priceAtPurchase": 149.00
        }
      ],
      "createdAt": "2026-03-24T04:30:00",
      "updatedAt": "2026-03-24T04:30:00"
    },
    {
      "id": 102,
      "status": "PENDING",
      "totalAmount": 59.98,
      "items": [
        {
          "id": 1003,
          "productId": "PROD-007",
          "quantity": 2,
          "priceAtPurchase": 29.99
        }
      ],
      "createdAt": "2026-03-23T18:15:30",
      "updatedAt": "2026-03-23T18:15:30"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

---

### 2.3 Get Order by ID

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/v1/orders/{id}` |
| **Auth** | `Bearer` token — **CUSTOMER** (own orders) or **ADMIN** (any order) |

#### Path Parameters

| Param | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `long` | ✅ | Order ID |

#### Responses

| Status | Body | Condition |
|--------|------|-----------|
| **200 OK** | `OrderResponse` | Order found and user is authorized |
| **403 Forbidden** | `MessageResponse` | Customer trying to access another user's order |
| **404 Not Found** | — (empty body) | Order does not exist |

**200 — Success:**
```json
{
  "id": 101,
  "status": "PROCESSING",
  "totalAmount": 208.98,
  "items": [
    {
      "id": 1001,
      "productId": "PROD-001",
      "quantity": 2,
      "priceAtPurchase": 29.99
    },
    {
      "id": 1002,
      "productId": "PROD-042",
      "quantity": 1,
      "priceAtPurchase": 149.00
    }
  ],
  "createdAt": "2026-03-24T04:30:00",
  "updatedAt": "2026-03-24T05:00:00"
}
```

**403 — Forbidden:**
```json
{
  "message": "You do not have permission to view this order"
}
```

**404 — Not Found:**
```
(empty response body)
```

---

### 2.4 Update Order Status

| | |
|---|---|
| **Method** | `PATCH` |
| **URL** | `/api/v1/orders/{id}` |
| **Auth** | `Bearer` token — **CUSTOMER** or **ADMIN** (role-based state-transitions) |
| **Content-Type** | `application/json` |

#### Path Parameters

| Param | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `long` | ✅ | Order ID |

#### Request Body — `UpdateOrderStatusRequest`

```json
{
  "status": "SHIPPED"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `status` | `string` (enum) | ✅ | Must be a valid `OrderStatus` value |

#### Responses

| Status | Body | Condition |
|--------|------|-----------|
| **200 OK** | `MessageResponse` | Status updated successfully |
| **400 Bad Request** | `MessageResponse` | Invalid argument (e.g. invalid enum value) |
| **403 Forbidden** | `MessageResponse` | User not authorized for this transition |
| **404 Not Found** | — (empty body) | Order does not exist |
| **409 Conflict** | `MessageResponse` | Invalid state transition (e.g. DELIVERED → PENDING) |

**200 — Success:**
```json
{
  "message": "Order status updated to SHIPPED"
}
```

**403 — Forbidden:**
```json
{
  "message": "You do not have permission to update this order"
}
```

**409 — Invalid Transition:**
```json
{
  "message": "Cannot transition from DELIVERED to PENDING"
}
```

**400 — Bad Request:**
```json
{
  "message": "Invalid status value"
}
```

---

## 3. Enums & Constants

### `OrderStatus`

```
PENDING → PROCESSING → SHIPPED → DELIVERED
                 ↘                  ↘
               CANCELLED          CANCELLED
```

| Value | Description |
|-------|-------------|
| `PENDING` | Order placed, awaiting processing |
| `PROCESSING` | Order is being prepared |
| `SHIPPED` | Order has been shipped |
| `DELIVERED` | Order delivered to customer |
| `CANCELLED` | Order was cancelled |

### `Role`

| Value | API Representation | Permissions |
|-------|-------------------|-------------|
| `CUSTOMER` | `ROLE_CUSTOMER` | Create orders, view own orders, cancel own orders |
| `ADMIN` | `ROLE_ADMIN` | View all orders, update any order status |

---

## 4. Mock Data Examples

### 4.1 Mock Users

```json
[
  {
    "id": 1,
    "email": "admin@example.com",
    "password": "admin123",
    "role": "ADMIN"
  },
  {
    "id": 2,
    "email": "alice@example.com",
    "password": "alice123",
    "role": "CUSTOMER"
  },
  {
    "id": 3,
    "email": "bob@example.com",
    "password": "bob12345",
    "role": "CUSTOMER"
  }
]
```

### 4.2 Mock JWT Response (after login)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMTI1MDAwMCwiZXhwIjoxNzExMzM2NDAwfQ.mock_signature",
  "tokenType": "Bearer",
  "id": 2,
  "email": "alice@example.com",
  "role": "ROLE_CUSTOMER"
}
```

### 4.3 Mock Orders

```json
[
  {
    "id": 101,
    "status": "PENDING",
    "totalAmount": 208.98,
    "items": [
      { "id": 1001, "productId": "PROD-001", "quantity": 2, "priceAtPurchase": 29.99 },
      { "id": 1002, "productId": "PROD-042", "quantity": 1, "priceAtPurchase": 149.00 }
    ],
    "createdAt": "2026-03-24T04:30:00",
    "updatedAt": "2026-03-24T04:30:00"
  },
  {
    "id": 102,
    "status": "PROCESSING",
    "totalAmount": 59.98,
    "items": [
      { "id": 1003, "productId": "PROD-007", "quantity": 2, "priceAtPurchase": 29.99 }
    ],
    "createdAt": "2026-03-23T18:15:30",
    "updatedAt": "2026-03-23T19:00:00"
  },
  {
    "id": 103,
    "status": "SHIPPED",
    "totalAmount": 499.99,
    "items": [
      { "id": 1004, "productId": "PROD-100", "quantity": 1, "priceAtPurchase": 499.99 }
    ],
    "createdAt": "2026-03-22T10:00:00",
    "updatedAt": "2026-03-23T08:30:00"
  },
  {
    "id": 104,
    "status": "DELIVERED",
    "totalAmount": 75.50,
    "items": [
      { "id": 1005, "productId": "PROD-003", "quantity": 3, "priceAtPurchase": 15.00 },
      { "id": 1006, "productId": "PROD-015", "quantity": 1, "priceAtPurchase": 30.50 }
    ],
    "createdAt": "2026-03-20T14:45:00",
    "updatedAt": "2026-03-22T16:00:00"
  },
  {
    "id": 105,
    "status": "CANCELLED",
    "totalAmount": 19.99,
    "items": [
      { "id": 1007, "productId": "PROD-055", "quantity": 1, "priceAtPurchase": 19.99 }
    ],
    "createdAt": "2026-03-21T09:00:00",
    "updatedAt": "2026-03-21T09:30:00"
  }
]
```

### 4.4 Mock Paginated Response

```json
{
  "content": [
    {
      "id": 101,
      "status": "PENDING",
      "totalAmount": 208.98,
      "items": [
        { "id": 1001, "productId": "PROD-001", "quantity": 2, "priceAtPurchase": 29.99 },
        { "id": 1002, "productId": "PROD-042", "quantity": 1, "priceAtPurchase": 149.00 }
      ],
      "createdAt": "2026-03-24T04:30:00",
      "updatedAt": "2026-03-24T04:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "empty": true, "sorted": false, "unsorted": true },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 5,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": { "empty": true, "sorted": false, "unsorted": true },
  "numberOfElements": 5,
  "first": true,
  "empty": false
}
```

### 4.5 TypeScript Interfaces (for Front-End)

```typescript
// ─── Enums ──────────────────────────────────────────
enum OrderStatus {
  PENDING = "PENDING",
  PROCESSING = "PROCESSING",
  SHIPPED = "SHIPPED",
  DELIVERED = "DELIVERED",
  CANCELLED = "CANCELLED",
}

enum Role {
  CUSTOMER = "ROLE_CUSTOMER",
  ADMIN = "ROLE_ADMIN",
}

// ─── Auth ───────────────────────────────────────────
interface LoginRequest {
  email: string;
  password: string;
}

interface SignupRequest {
  email: string;   // max 255, valid email
  password: string; // 6–40 chars
}

interface JwtResponse {
  accessToken: string;
  tokenType: "Bearer";
  id: number;
  email: string;
  role: string; // "ROLE_CUSTOMER" | "ROLE_ADMIN"
}

interface MessageResponse {
  message: string;
}

// ─── Orders ─────────────────────────────────────────
interface OrderItemRequest {
  productId: string;
  quantity: number; // ≥ 1, integer
  price: number;    // ≥ 0.01, decimal
}

interface OrderRequest {
  items: OrderItemRequest[]; // non-empty
}

interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

interface OrderItemResponse {
  id: number;
  productId: string;
  quantity: number;
  priceAtPurchase: number;
}

interface OrderResponse {
  id: number;
  status: OrderStatus;
  totalAmount: number;
  items: OrderItemResponse[];
  createdAt: string; // ISO 8601 datetime, e.g. "2026-03-24T04:30:00"
  updatedAt: string;
}

// ─── Spring Page wrapper ────────────────────────────
interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

interface Page<T> {
  content: T[];
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  sort: Sort;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
```
