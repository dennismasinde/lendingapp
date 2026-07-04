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

# Observability Stack

The application includes a production-style observability stack for monitoring, metrics, logging, alerting, and distributed tracing.

| Service | URL | Purpose |
|---------|-----|---------|
| Spring Boot Actuator | http://localhost:8088/actuator | Application health and metrics |
| Prometheus | http://localhost:9090 | Metrics collection and alert evaluation |
| Grafana | http://localhost:3000 | Dashboards and visualization |
| PostgreSQL Exporter | http://localhost:9187/metrics | PostgreSQL metrics |
| Loki | http://localhost:3100 | Log aggregation |
| Zipkin | http://localhost:9411 | Distributed tracing |

---

# Grafana

Grafana is pre-configured using provisioning.

Open:

```
http://localhost:3000
```

Default credentials:

```
Username: admin
Password: admin
```

The following data sources are automatically provisioned:

- Prometheus
- Loki

Available dashboards include metrics for:

- Spring Boot
- JVM
- HTTP Requests
- PostgreSQL
- Docker Containers
- System Health

---

# Prometheus

Prometheus collects metrics from:

- Lending API
- PostgreSQL Exporter

Open:

```
http://localhost:9090
```

Useful targets page:

```
http://localhost:9090/targets
```

All configured targets should display:

```
UP
```

Useful graph page:

```
http://localhost:9090/graph
```

Example queries:

Application availability

```promql
up
```

HTTP requests

```promql
http_server_requests_seconds_count
```

JVM Heap Usage

```promql
jvm_memory_used_bytes
```

CPU Usage

```promql
process_cpu_usage
```

Database Connections

```promql
hikaricp_connections_active
```

PostgreSQL Metrics

```promql
pg_up
```

---

# Alerting

Prometheus evaluates alert rules located at:

```
monitoring/prometheus/alert.rules.yml
```

Current alert:

| Alert | Description |
|--------|-------------|
| LendingApiDown | Fires when the Lending API cannot be scraped for more than 1 minute |

View alert status:

```
http://localhost:9090/alerts
```

Alert states:

- Inactive
- Pending
- Firing

---

# Spring Boot Actuator

The application exposes Actuator endpoints used by Prometheus.

Base URL:

```
http://localhost:8088/actuator
```

Useful endpoints:

Health

```
/actuator/health
```

Metrics

```
/actuator/metrics
```

Prometheus Metrics

```
/actuator/prometheus
```

Example:

```
http://localhost:8088/actuator/prometheus
```

---

# PostgreSQL Exporter

The PostgreSQL Exporter exposes database metrics consumed by Prometheus.

Metrics endpoint:

```
http://localhost:9187/metrics
```

Metrics include:

- Active connections
- Database size
- Transactions
- Locks
- Buffers
- Query statistics

---

# Loki

Loki provides centralized log aggregation.

Service URL:

```
http://localhost:3100
```

Logs can be viewed directly from Grafana using the preconfigured Loki data source.

---

# Zipkin

Zipkin provides distributed tracing for incoming requests.

Open:

```
http://localhost:9411
```

Once tracing is enabled in the application, request traces and latency breakdowns will be available here.

---

---

# Verifying the Monitoring Stack

Check that all monitoring containers are running:

```bash
docker ps
```

Expected containers:

- lending-api
- lending-postgres
- lending-postgres-exporter
- lending-prometheus
- lending-grafana
- lending-loki
- lending-promtail
- lending-zipkin

Verify API health:

```bash
curl http://localhost:8088/actuator/health
```

Verify Prometheus metrics:

```bash
curl http://localhost:8088/actuator/prometheus
```

Verify PostgreSQL exporter:

```bash
curl http://localhost:9187/metrics
```

Open Grafana:

```
http://localhost:3000
```

Ensure all Prometheus targets are **UP**:

```
http://localhost:9090/targets
```

Check active alerts:

```
http://localhost:9090/alerts
```

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

Platform & Backend Engineer
```
