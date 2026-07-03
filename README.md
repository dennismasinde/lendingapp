# Getting Started

## Clone the repository

```bash
git clone https://github.com/dennismasinde/lendingapp
cd lendingapp
```

---

## Run using Docker

```bash
docker compose up --build
```

Verify that the containers are running:

```bash
docker ps
```

Expected output:

```
CONTAINER ID   IMAGE                    PORTS
xxxxxxxxxxxx   lendingapp-lending-api   0.0.0.0:8088->8088
xxxxxxxxxxxx   postgres:17              0.0.0.0:5431->5432
```

---

# Docker Services

## PostgreSQL

| Property | Value       |
| -------- | ----------- |
| Database | lendingdb   |
| Username | lendinguser |
| Password | secret      |
| Port     | 5431        |

## Spring Boot API

Runs on

```
http://localhost:8088
```

---

# Connecting to PostgreSQL

Open a PostgreSQL shell:

```bash
docker exec -it lending-postgres psql -U lendinguser -d lendingdb
```

Useful commands

List all tables

```sql
\dt
```

Describe a table

```sql
\d customers
```

Exit

```sql
\q
```

---

# Flyway Database Migrations

Database versioning is managed using Flyway.

Current migrations:

| Version | Description    |
| ------- | -------------- |
| V1      | Initial Schema |
| V2      | Seed Data      |

Verify migrations:

```sql
SELECT installed_rank,
       version,
       description,
       success
FROM flyway_schema_history
ORDER BY installed_rank;
```

---

# Seed Data

The application is automatically seeded with realistic sample data.

| Entity                 | Records |
| ---------------------- | ------- |
| Customers              | 10      |
| Customer Loan Limits   | 10      |
| Loan Products          | 8       |
| Fees                   | 19      |
| Loans                  | 10      |
| Installments           | 48      |
| Repayments             | 14      |
| Loan Fees              | 0       |
| Notifications          | 9       |
| Notification Variables | 3       |

---

# Useful Database Queries

## Customers

```sql
SELECT *
FROM customers;
```

## Loans

```sql
SELECT
    l.id,
    c.first_name || ' ' || c.last_name AS customer,
    p.name,
    l.status,
    l.loan_type,
    l.principal_amount,
    l.outstanding_balance
FROM loans l
JOIN customers c
ON c.id = l.customer_id
JOIN loan_products p
ON p.id = l.product_id;
```

## Installments

```sql
SELECT
    loan_id,
    COUNT(*) AS installments,
    SUM(amount) AS total_amount
FROM installments
GROUP BY loan_id;
```

## Repayments

```sql
SELECT
    loan_id,
    COUNT(*) AS repayments,
    SUM(amount) AS amount_paid
FROM repayments
GROUP BY loan_id;
```

## Notifications

```sql
SELECT
    customer_id,
    type,
    channel,
    is_sent,
    is_read
FROM notifications;
```

---

# API Documentation

Swagger UI is available at

```
http://localhost:8088/swagger-ui/index.html
```

or

```
http://localhost:8088/swagger-ui.html
```

---

# Swagger Authentication

Spring Security protects all endpoints, including Swagger.

Use the default development user:

**Username**

```
user
```

Retrieve the generated password:

```bash
docker logs lending-api
```

Look for a line similar to:

```
Using generated security password:

xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

Use that password to authenticate in Swagger.

---

# REST API Base URL

```
http://localhost:8088
```

---

# Example Endpoints

```
GET    /api/v1/customers

GET    /api/v1/loans

GET    /api/v1/loan-products

GET    /api/v1repayments

GET    /api/v1notifications
```

---

# Database Model

The application currently manages:

* Customers
* Customer Loan Limits
* Loan Products
* Fees
* Loans
* Installments
* Repayments
* Loan Fees
* Notifications
* Notification Variables

Relationships are enforced using foreign keys and optimized using indexes.

---

# Useful Docker Commands

Start containers

```bash
docker compose up --build
```

Stop containers

```bash
docker compose down
```

View logs

```bash
docker logs lending-api
```

Follow logs

```bash
docker logs -f lending-api
```

Connect to PostgreSQL

```bash
docker exec -it lending-postgres psql -U lendinguser -d lendingdb
```

---

# Build Commands

Compile

```bash
mvn clean compile
```

Run tests

```bash
mvn test
```

Package

```bash
mvn clean package
```

Run locally

```bash
mvn spring-boot:run
```

---

# Current Status

* ✅ Spring Boot 4.1 application
* ✅ Java 25
* ✅ PostgreSQL 17
* ✅ Flyway migrations
* ✅ Docker Compose
* ✅ Spring Security
* ✅ Swagger/OpenAPI
* ✅ Seeded sample data
* ✅ Domain-oriented package structure
* ✅ Production-style relational schema

---

# Author

**Dennis Masinde**

Senior Platform & Backend Engineer

Java • Spring Boot • PostgreSQL • Docker • Kubernetes • Azure • DevOps

```
```
