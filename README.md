# 🏦 Vaultflow

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-6DB33F?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-336791?style=flat-square&logo=postgresql)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat-square&logo=springsecurity)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square&logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

> A production-ready **Finance Dashboard REST API** built with Spring Boot. Provides secure, role-based access to financial records, user management, and analytics summaries for a dashboard frontend.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Local Setup](#-local-setup)
- [PostgreSQL Setup](#-postgresql-setup)
- [Authentication](#-authentication--jwt-usage)
- [API Endpoints](#-api-endpoints)
- [Filtering & Pagination](#-filtering--pagination)
- [Dashboard Responses](#-dashboard-response-examples)
- [Error Handling](#-error-handling)
- [Validation Rules](#-validation-rules)
- [Soft Delete Behavior](#-soft-delete-behavior)
- [Assumptions](#-assumptions)
- [Design Decisions](#-design-decisions)

---

## 🔍 Overview

**Vaultflow** is a backend-only REST API that powers a financial dashboard system. It handles:

- Secure **user registration and login** using JWT tokens
- **Role-based access control** with three permission levels (VIEWER, ANALYST, ADMIN)
- Full **CRUD operations** for financial records (income and expenses)
- **Dashboard analytics** — summaries, category breakdowns, monthly/weekly trends
- **Soft delete** everywhere — nothing is ever permanently removed
- Dynamic **filtering and pagination** for all financial records

This project has **no frontend**. It is designed to be consumed by any frontend application or tested via Swagger UI or Postman.

---

## 🛠 Tech Stack

| Category         | Technology                              | Version    |
|------------------|-----------------------------------------|------------|
| Language         | Java                                    | 17         |
| Framework        | Spring Boot                             | 3.5.13     |
| Security         | Spring Security + JJWT                  | 0.11.5     |
| Database         | PostgreSQL                              | Latest     |
| ORM              | Spring Data JPA + Hibernate             | 6.6        |
| Validation       | Spring Boot Validation (Jakarta)        | Included   |
| API Docs         | Springdoc OpenAPI (Swagger UI)          | 2.3.0      |
| Build Tool       | Apache Maven                            | Latest     |
| Utilities        | Lombok                                  | Latest     |
| Dev Tools        | Spring Boot DevTools                    | Included   |

---

## 📁 Project Structure

```
src/main/java/com/finance/vaultflow/
├── VaultflowApplication.java         → Application entry point
│
├── config/
│   ├── SecurityConfig.java           → Spring Security + JWT filter chain setup
│   └── SwaggerConfig.java            → OpenAPI Bearer auth configuration
│
├── controller/
│   ├── AuthController.java           → POST /api/auth/**  (public)
│   ├── UserController.java           → /api/users/**      (Admin only)
│   ├── FinancialRecordController.java → /api/records/**   (role-based)
│   └── DashboardController.java      → /api/dashboard/**  (all authenticated)
│
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── FinancialRecordService.java
│   └── DashboardService.java
│
├── repository/
│   ├── UserRepository.java
│   ├── FinancialRecordRepository.java
│   └── RecordSpecification.java      → JPA Specification for dynamic filtering
│
├── model/
│   ├── User.java
│   └── FinancialRecord.java
│
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── UserResponse.java
│   ├── CreateUserRequest.java
│   ├── UpdateUserRequest.java
│   ├── UpdateRoleRequest.java
│   ├── UpdateStatusRequest.java
│   ├── RecordRequest.java
│   ├── RecordResponse.java
│   ├── PagedResponse.java
│   ├── DashboardSummaryResponse.java
│   ├── CategoryTotalResponse.java
│   ├── MonthlyTrendResponse.java
│   └── WeeklyTrendResponse.java
│
├── exception/
│   ├── AppException.java
│   └── GlobalExceptionHandler.java
│
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
│
└── enums/
    ├── Role.java                     → VIEWER, ANALYST, ADMIN
    ├── UserStatus.java               → ACTIVE, INACTIVE
    └── TransactionType.java          → INCOME, EXPENSE
```

---

## ✅ Prerequisites

Before running this project, ensure you have the following installed:

- **Java 17** — [Download here](https://adoptium.net/)
- **Maven 3.8+** — [Download here](https://maven.apache.org/download.cgi)
- **PostgreSQL 14+** — [Download here](https://www.postgresql.org/download/)
- **Git** — [Download here](https://git-scm.com/)

Verify your installations:

```bash
java -version        # Should print: java version "17.x.x"
mvn -version         # Should print: Apache Maven 3.x.x
psql --version       # Should print: psql (PostgreSQL) 14.x or higher
```

---

## 🚀 Local Setup

### Step 1 — Clone the Repository

```bash
git clone https://github.com/your-username/vaultflow.git
cd vaultflow
```

### Step 2 — Configure `application.properties`

Open `src/main/resources/application.properties` and fill in your values:

```properties
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/vaultflow
spring.datasource.username=YOUR_POSTGRES_USERNAME
spring.datasource.password=YOUR_POSTGRES_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=your_secret_key_must_be_at_least_32_characters_long
jwt.expiration=86400000

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

> ⚠️ **Important:** `jwt.secret` must be at least 32 characters. Use a random string such as `03f8b1c0d49e2a5f7c8b6e1a3d0f9e2c` for development.

### Step 3 — Create the Database

```bash
psql -U postgres
CREATE DATABASE vaultflow;
\q
```

### Step 4 — Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application starts at: **`http://localhost:8080`**

Swagger UI is available at: **`http://localhost:8080/swagger-ui.html`**

---

## 🐘 PostgreSQL Setup

If you are setting up PostgreSQL from scratch:

```sql
-- Connect as superuser
psql -U postgres

-- Create a dedicated user for the app
CREATE USER vaultflow_user WITH PASSWORD 'strongpassword';

-- Create the database
CREATE DATABASE vaultflow;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE vaultflow TO vaultflow_user;

\q
```

Then update your `application.properties`:

```properties
spring.datasource.username=vaultflow_user
spring.datasource.password=strongpassword
```

> Hibernate will **automatically create and update tables** on first run via `ddl-auto=update`. No manual schema creation is needed.

### Database Schema

#### Table: `users`

| Column       | Type             | Constraints                        |
|--------------|------------------|------------------------------------|
| id           | UUID             | Primary Key, auto-generated        |
| name         | VARCHAR(100)     | NOT NULL                           |
| email        | VARCHAR          | UNIQUE, NOT NULL                   |
| password     | VARCHAR          | NOT NULL (BCrypt hashed)           |
| role         | ENUM             | VIEWER, ANALYST, ADMIN             |
| status       | ENUM             | ACTIVE, INACTIVE                   |
| created_at   | TIMESTAMP        |                                    |
| updated_at   | TIMESTAMP        |                                    |
| deleted_at   | TIMESTAMP        | Nullable — soft delete marker      |

#### Table: `financial_records`

| Column       | Type             | Constraints                        |
|--------------|------------------|------------------------------------|
| id           | UUID             | Primary Key, auto-generated        |
| user_id      | UUID             | Foreign Key → users.id             |
| amount       | DECIMAL(15,2)    | NOT NULL, must be > 0              |
| type         | ENUM             | INCOME, EXPENSE                    |
| category     | VARCHAR(100)     | e.g. Salary, Food, Rent, Travel    |
| date         | DATE             | NOT NULL, cannot be a future date  |
| notes        | TEXT             | Nullable, max 500 characters       |
| created_at   | TIMESTAMP        |                                    |
| updated_at   | TIMESTAMP        |                                    |
| deleted_at   | TIMESTAMP        | Nullable — soft delete marker      |

---

## 🔐 Authentication & JWT Usage

### Authentication Flow

1. **Register** to get a JWT token immediately:
```bash
POST /api/auth/register
```
```json
{
  "name": "Surya",
  "email": "surya@example.com",
  "password": "password123"
}
```
> New users always receive the **VIEWER** role by default.

2. **Login** to get a JWT token:
```bash
POST /api/auth/login
```
```json
{
  "email": "surya@example.com",
  "password": "password123"
}
```

Both endpoints return:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXJ5YUBleGFtcGxlLmNvbSIs..."
}
```

3. **Use the token** in all subsequent requests via the `Authorization` header:

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." http://localhost:8080/api/dashboard/summary
```

### JWT Details

| Property   | Value                        |
|------------|------------------------------|
| Algorithm  | HMAC-SHA256                  |
| Expiry     | 24 hours (86,400,000 ms)     |
| Claims     | `email` (subject), `role`    |
| Secret     | Configured in `application.properties` |

### Using Swagger UI

1. Open `http://localhost:8080/swagger-ui.html`
2. Call `POST /api/auth/login` and copy the returned token
3. Click the **"Authorize"** button at the top right
4. Enter: `Bearer YOUR_TOKEN_HERE`
5. All subsequent requests will include the token automatically

---

## 📡 API Endpoints

### 🔓 Auth — Public

| Method | Endpoint              | Description              |
|--------|-----------------------|--------------------------|
| POST   | `/api/auth/register`  | Register, returns JWT    |
| POST   | `/api/auth/login`     | Login, returns JWT       |

---

### 👤 Users — Admin Only

| Method | Endpoint                      | Description                     |
|--------|-------------------------------|---------------------------------|
| GET    | `/api/users`                  | Get all users (paginated)       |
| GET    | `/api/users/{id}`             | Get user by ID                  |
| POST   | `/api/users`                  | Create user with any role       |
| PUT    | `/api/users/{id}`             | Update user name and email      |
| PATCH  | `/api/users/{id}/role`        | Change user role                |
| PATCH  | `/api/users/{id}/status`      | Activate or deactivate user     |
| DELETE | `/api/users/{id}`             | Soft delete user                |

**Example — Create User (Admin):**
```bash
POST /api/users
Authorization: Bearer <admin_token>
```
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "securepass",
  "role": "ANALYST"
}
```

---

### 💰 Financial Records — Role-Based

| Method | Endpoint              | Role Required    | Description                        |
|--------|-----------------------|------------------|------------------------------------|
| GET    | `/api/records`        | ALL              | Get all records (paginated, filterable) |
| GET    | `/api/records/{id}`   | ALL              | Get single record by ID            |
| POST   | `/api/records`        | ANALYST, ADMIN   | Create a new financial record      |
| PUT    | `/api/records/{id}`   | ANALYST, ADMIN   | Update an existing record          |
| DELETE | `/api/records/{id}`   | ADMIN            | Soft delete a record               |

**Example — Create Record (Analyst or Admin):**
```bash
POST /api/records
Authorization: Bearer <token>
```
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-04-01",
  "notes": "Monthly salary payment"
}
```

---

### 📊 Dashboard — All Authenticated Users

| Method | Endpoint                              | Description                            |
|--------|---------------------------------------|----------------------------------------|
| GET    | `/api/dashboard/summary`              | Total income, expenses, net balance    |
| GET    | `/api/dashboard/by-category`          | Totals grouped by category             |
| GET    | `/api/dashboard/recent`               | Last 10 transactions                   |
| GET    | `/api/dashboard/trends/monthly`       | Income vs expense per month            |
| GET    | `/api/dashboard/trends/weekly`        | Income vs expense per week             |

---

### 🔒 Role-Based Access Control

| Action                        | VIEWER | ANALYST | ADMIN |
|-------------------------------|:------:|:-------:|:-----:|
| Login / Register              | ✓      | ✓       | ✓     |
| View financial records        | ✓      | ✓       | ✓     |
| View dashboard summaries      | ✓      | ✓       | ✓     |
| Search and filter records     | ✓      | ✓       | ✓     |
| Create financial records      | ✗      | ✓       | ✓     |
| Update financial records      | ✗      | ✓       | ✓     |
| Soft delete records           | ✗      | ✗       | ✓     |
| Create and manage users       | ✗      | ✗       | ✓     |
| Update user roles and status  | ✗      | ✗       | ✓     |
| Soft delete users             | ✗      | ✗       | ✓     |

> Unauthorized access returns `403 Forbidden` with a JSON error body.

---

## 🔎 Filtering & Pagination

`GET /api/records` supports the following query parameters:

| Parameter   | Type    | Default | Description                                      |
|-------------|---------|---------|--------------------------------------------------|
| `page`      | Integer | 0       | Page number (zero-indexed)                       |
| `size`      | Integer | 10      | Number of records per page                       |
| `sortBy`    | String  | `date`  | Field to sort by                                 |
| `sortDir`   | String  | `desc`  | Sort direction: `asc` or `desc`                 |
| `type`      | String  | —       | Filter by `INCOME` or `EXPENSE`                  |
| `category`  | String  | —       | Filter by category (e.g. `Salary`, `Food`)       |
| `startDate` | String  | —       | Filter from date in `yyyy-MM-dd` format          |
| `endDate`   | String  | —       | Filter to date in `yyyy-MM-dd` format            |
| `search`    | String  | —       | Keyword search across `category` and `notes`     |

**Example Request:**
```bash
GET /api/records?type=EXPENSE&category=Food&startDate=2024-01-01&endDate=2024-03-31&page=0&size=5
Authorization: Bearer <token>
```

**Paginated Response Format:**
```json
{
  "data": [ ... ],
  "page": 0,
  "size": 5,
  "totalElements": 45,
  "totalPages": 9
}
```

> **Implementation:** Filtering is powered by the JPA Specification pattern (`RecordSpecification.java`), which allows any combination of filters to be applied dynamically at runtime without changing repository code.

---

## 📈 Dashboard Response Examples

**`GET /api/dashboard/summary`**
```json
{
  "totalIncome": 85000.00,
  "totalExpenses": 42000.00,
  "netBalance": 43000.00,
  "currency": "USD"
}
```

**`GET /api/dashboard/by-category`**
```json
[
  { "category": "Salary", "total": 80000.00 },
  { "category": "Food",   "total": 12000.00 },
  { "category": "Rent",   "total": 18000.00 }
]
```

**`GET /api/dashboard/trends/monthly`**
```json
[
  { "month": "2024-01", "income": 10000.00, "expense": 6000.00 },
  { "month": "2024-02", "income": 12000.00, "expense": 7500.00 }
]
```

**`GET /api/dashboard/trends/weekly`**
```json
[
  { "week": "2024-01", "income": 2500.00, "expense": 1500.00 },
  { "week": "2024-02", "income": 3000.00, "expense": 1800.00 }
]
```

---

## ❌ Error Handling

All errors are handled globally via `@RestControllerAdvice` and return a consistent JSON format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be greater than 0",
  "timestamp": "2024-04-04T10:30:00"
}
```

| HTTP Status | Meaning                          | Trigger                            |
|-------------|----------------------------------|------------------------------------|
| 400         | Bad Request                      | Validation failure                 |
| 401         | Unauthorized                     | Missing or invalid/expired JWT     |
| 403         | Forbidden                        | Insufficient role for this action  |
| 404         | Not Found                        | Resource does not exist            |
| 409         | Conflict                         | Duplicate email on registration    |
| 500         | Internal Server Error            | Unexpected server-side error       |

---

## ✔️ Validation Rules

### User

| Field      | Rule                                          |
|------------|-----------------------------------------------|
| `name`     | Not blank, max 100 characters                 |
| `email`    | Valid email format, not blank                 |
| `password` | Not blank, minimum 8 characters               |
| `role`     | Must be `VIEWER`, `ANALYST`, or `ADMIN`       |

### Financial Record

| Field      | Rule                                          |
|------------|-----------------------------------------------|
| `amount`   | Not null, must be greater than 0              |
| `type`     | Not null, must be `INCOME` or `EXPENSE`       |
| `category` | Not blank, max 100 characters                 |
| `date`     | Not null, cannot be a future date             |
| `notes`    | Optional, max 500 characters                  |

---

## 🗑 Soft Delete Behavior

This project **never hard-deletes** any data. Instead:

1. When a user or record is "deleted", the `deleted_at` timestamp is set to the current time.
2. All entities are annotated with `@Where(clause = "deleted_at IS NULL")`.
3. This means **all queries automatically exclude deleted records** — no extra filtering code is needed anywhere.
4. Deleted data is preserved in the database for audit and recovery purposes.

---

## 📌 Assumptions

- All monetary amounts are stored and returned in **USD**.
- Users who self-register always receive the **VIEWER** role. Only an Admin can upgrade a user's role.
- Only **ADMIN** users can change roles, statuses, or delete users.
- **Users cannot delete their own account** to prevent accidental lockout.
- Financial records are **not user-scoped** — all authenticated users can view all records in the system.
- Soft delete is applied everywhere; **no hard deletes** are performed at any level.
- A valid, non-expired **JWT is required** on every protected request.
- The app uses **stateless authentication** — no sessions or cookies are used.

---

## 🧠 Design Decisions

### 1. Soft Delete via `deleted_at` Timestamp
A timestamp is used instead of a boolean `is_deleted` flag. This preserves the exact time of deletion for audit trails. The `@Where` JPA annotation handles exclusion of deleted rows automatically across all queries.

### 2. JPA Specification for Dynamic Filtering
`RecordSpecification.java` implements the JPA `Specification` pattern, allowing any combination of filter parameters (type, category, date range, keyword) to be combined at runtime. This keeps the repository interface clean and avoids creating dozens of custom query methods.

### 3. Stateless JWT Authentication
No server-side sessions or session storage are used. Every request carries its own signed JWT. This makes the application **horizontally scalable** — multiple instances can run without needing to share session state.

### 4. Role Enforcement at Controller Level
`@PreAuthorize` annotations enforce role-based access directly on controller methods. This keeps security rules visible at the API boundary and keeps the service layer free of authorization logic.

### 5. UUID Primary Keys
All entities use UUID primary keys instead of auto-incremented integers. This avoids exposing sequential IDs (a security concern), prevents ID enumeration attacks, and supports future distributed deployments where multiple database nodes may generate IDs simultaneously.

### 6. Strict Layered Architecture
- **Controllers** handle HTTP only (parsing requests, returning responses).
- **Services** handle all business logic (calculations, validation, decisions).
- **Repositories** handle all database access (no logic, just data in/out).

This separation makes each layer independently testable and maintainable.
