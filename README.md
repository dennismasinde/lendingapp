Here's a single, clean **README** file without the sample **API** calls:

```markdown # Tezza Lending Application

A simplified Java-based lending application built with Spring Boot to automate loan management processes including product creation, loan disbursement, repayment handling, and notifications.

## 📋 Prerequisites

- **Docker** and **Docker Compose** (Recommended)
- **Git** (for cloning)
- **Postman** or **cURL** (for testing)

## 🚀 Quick Start

### 1. Clone and Run

```bash # Clone the repository git clone [https://github.com/dennismasinde/lendingapp.git](https://github.com/yourusername/lendingapp.git) cd lendingapp

# Start the application

docker-compose up --build ```

This will:
- Start PostgreSQL database
- Run Flyway migrations (create tables and seed data)
- Start the Spring Boot application on port **8088**

### 2. Verify it's running

```bash docker-compose ps ```

Expected output: ``` **NAME**                **SERVICE**             **STATUS**              **PORTS** lending-api         lending-api         Up (healthy)        0.0.0.0:**8088**->**8088**/tcp lending-postgres    postgres            Up (healthy)        0.0.0.0:**5431**->**5432**/tcp ```

## 🔐 Authentication

Spring Boot generates a default password on startup. Get it from the logs:

```bash docker logs lending-api | grep *password* ```

**Default Credentials:**
- Username: `user`
- Password: `[Generated password from logs]`

## 🔑 Access the API

### Swagger UI (Recommended)

``` [http://localhost:**8088**/swagger-ui.html](http://localhost:**8088**/swagger-ui.html) ```

**To authenticate:**
## Open Swagger UI
## Click the ***Authorize*** button (🔓 icon)
## Enter:
    - Password: `[Your generated password]`
## Click ***Authorize***
## You can now test all endpoints

### OpenAPI Specification

``` [http://localhost:**8088**/v3/api-docs](http://localhost:**8088**/v3/api-docs) ```

### Health Check

``` [http://localhost:**8088**/actuator/health](http://localhost:**8088**/actuator/health) ```

## 📊 Seed Data

The database is pre-seeded with:
- ✅ 10 Customers with various profiles
- ✅ Customer loan limits
- ✅ 8 Loan Products (Personal, Business, Emergency, Education, **SME**, Car, Salary Advance)
- ✅ 19 Fee configurations
- ✅ Loans in different states (**OPEN**, **OVERDUE**, **PENDING**, **APPROVED**, **CLOSED**, **CANCELLED**)
- ✅ Installment plans
- ✅ Repayment records
- ✅ Notifications

## 🛠️ Useful Commands

### Docker Commands

```bash # View all logs docker-compose logs -f

# View specific service logs

docker-compose logs -f lending-api docker-compose logs -f postgres

# Check application status

docker-compose ps

# Stop all services

docker-compose down

# Stop and remove volumes (reset database)

docker-compose down -v

# Restart services

docker-compose restart

# Rebuild after code changes

docker-compose up --build

# Execute commands in container

docker exec -it lending-api bash

# Check database connection

docker exec -it lending-postgres psql -U lendinguser -d lendingdb

# View container resource usage

docker stats

# View Spring logs with grep for password

docker logs lending-api 2>&1 | grep -i password ```

### Database Commands

```bash # Connect to PostgreSQL docker exec -it lending-postgres psql -U lendinguser -d lendingdb

# List tables

\l

# View schema

\dt

# Query customers

**SELECT** * **FROM** customers;

# Query loans

**SELECT** * **FROM** loans;

# Exit psql

\q ```

### Application Debugging

```bash # Check if application is running curl [http://localhost:**8088**/actuator/health](http://localhost:**8088**/actuator/health)

# View application properties

docker exec -it lending-api cat /app/config/application.properties

# View application logs with timestamps

docker-compose logs -f --timestamps lending-api

# Check memory usage

docker exec -it lending-api jcmd 1 VM.native_memory summary

# Force re-run migrations (if needed)

docker-compose down -v docker-compose up --build ```

## 🔧 Common Issues & Solutions

### 1. Port 8088 already in use

```bash # Change port in docker-compose.yaml ports: - ***8089**:**8088***  # Change to **8089**

# Or kill the process using port 8088

sudo lsof -i :**8088** sudo kill -9 [**PID**] ```

### 2. Spring Security password not shown

```bash # Look in container logs docker logs lending-api 2>&1 | grep *Using generated security password*

# Or check all logs for password

docker-compose logs lending-api | grep -i generated ```

### 3. Database connection failed

```bash # Check if PostgreSQL is running docker-compose ps postgres

# Check PostgreSQL logs

docker-compose logs postgres

# Restart PostgreSQL

docker-compose restart postgres ```

### 4. Flyway migration failed

```bash # Reset database and re-run migrations docker-compose down -v docker-compose up --build ```

### 5. Bean creation error

```bash # Check application logs for detailed error docker-compose logs lending-api | grep -A 10 *ERROR*

# Add debugging to application.properties

SPRING_DEBUG=true ```

## 📂 Project Structure

``` lendingapp/ ├── docker-compose.yaml          # Docker services ├── Dockerfile                   # Docker build config ├── pom.xml                      # Maven dependencies ├── src/ │   ├── main/ │   │   ├── java/ │   │   │   └── com/tezzasolutions/lendingapp/ │   │   │       ├── common/      # Shared components │   │   │       ├── customer/    # Customer module │   │   │       ├── loan/        # Loan module │   │   │       ├── fee/         # Fee module │   │   │       ├── repayment/   # Repayment module │   │   │       ├── notification/# Notification module │   │   │       └── repository/  # Data repositories │   │   └── resources/ │   │       └── db/migration/    # Flyway migrations │   └── test/                    # Test classes └── **README**.md                    # This file ```

## 📚 API Endpoints Overview

| Module | Base Path |
|--------|-----------|
| Customer Management | `/api/v1/customers` |
| Loan Product Management | `/api/v1/products` |
| Loan Management | `/api/v1/loans` |
| Fee Management | `/api/v1/fees` |
| Repayment Management | `/api/v1/repayments` |

Full **API** documentation available at: [http://localhost:**8088**/swagger-ui.html](http://localhost:**8088**/swagger-ui.html)

## 🎯 Quick Start Checklist

- [ ] Clone repository
- [ ] Run `docker-compose up --build`
- [ ] Wait for application to start (about 30 seconds)
- [ ] Find generated password in logs
- [ ] Open Swagger UI: [http://localhost:**8088**/swagger-ui.html](http://localhost:**8088**/swagger-ui.html)
- [ ] Click *Authorize* button
- [ ] Enter  password from logs
- [ ] Explore and test endpoints!

## 📞 Need Help?

Check the logs for errors: ```bash docker-compose logs -f lending-api ```

---

**❤️ Tezza Solutions Team** ```