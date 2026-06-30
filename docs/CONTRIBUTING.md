# Contributing to STADI

First of all, thank you for taking the time to contribute to LendingAPI.

Our goal is to build  Simplified Java-based lending application to automate loan management processes, including product creation, loan disbursement, repayment handling, and notifications while maintaining enterprise-grade software engineering standards.

---

# Development Philosophy

LendingAPI follows the following principles:

- Clean Architecture
- SOLID Principles
- Domain Driven Design
- RESTful APIs
- Test Driven Development where practical
- Security First
- Developer Experience

Every pull request should improve one or more of these principles.

---

# Branch Strategy

We follow GitHub Flow.

```

main
│
├── develop
│
├── feature/authentication
├── feature/payments
├── feature/leaderboard
├── feature/institutions
├── feature/mobile-api

```

Never push directly to main.

---

# Branch Naming

Feature

```

feature/payment-service

```

Bug Fix

```

fix/payment-callback

```

Hotfix

```

hotfix/login-issue

```

Refactor

```

refactor/auth-service

```

Documentation

```

docs/readme-update

```

---

# Commit Messages

We follow Conventional Commits.

Examples

```

feat(payment): add STK push endpoint

```

```

fix(jwt): validate refresh token expiry

```

```

refactor(auth): simplify token validation

```

```

docs(readme): update installation steps

```

---

# Pull Request Checklist

Before submitting a PR ensure:

- Code compiles
- Tests pass
- No failing GitHub Actions
- Swagger documentation updated
- DTOs documented
- Database migrations included (Flyway)
- No secrets committed

---

# Coding Standards

Java

- Java 25
- Spring Boot 4
- Constructor Injection
- Lombok
- No field injection

Always use

```

@RequiredArgsConstructor

```

instead of

```

@Autowired

```

---

# Package Structure

```

controller
service
repository
entity
dto
mapper
config
security
exception
util
validation

```

Never create random packages.

---

# API Standards

Every endpoint should return

```

ApiResponse<T>

```

Errors must return

```

ApiError

```

All exceptions should be handled by

```

GlobalExceptionHandler

```

---

# Validation

Always validate incoming DTOs using

```

@Valid

```

Never validate inside controllers.

---

# Logging

Use SLF4J.

Good

```

log.info()

log.warn()

log.error()

```

Avoid

```

System.out.println()

```

---

# Database

Use

- Flyway
- UUID only where necessary
- BIGSERIAL IDs
- Soft deletes where appropriate
- Audit fields

Every entity should contain

```

createdAt

updatedAt

createdBy

updatedBy

```

where applicable.

---

# Security

Never

- commit secrets
- expose stack traces
- expose passwords
- trust client input

JWT authentication protects every secured endpoint.

---

# Testing

Preferred

- JUnit 5
- Mockito
- Testcontainers

Every service should eventually have

- Unit Tests
- Integration Tests

---

# Docker

Application should always be runnable using

```

docker compose up

```

No contributor should require manual environment setup.

---

# CI Pipeline

Every Pull Request triggers

- Build
- Unit Tests
- Docker Build
- Security Checks (future)

No failing builds may be merged.

---

# Code Review

We review for

- Correctness
- Simplicity
- Readability
- Security
- Performance
- Maintainability

---

# Our Goal

Build software that could comfortably pass an engineering review at companies like Microsoft, Google, Amazon, Stripe or Network International.