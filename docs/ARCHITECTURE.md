# Architecture

## Overview

This lending application follows a layered Spring Boot architecture backed by PostgreSQL.

### Technology Stack

- Java 25
- Spring Boot 4.1
- Spring Data JPA
- Flyway
- PostgreSQL 17
- Spring Security
- Springdoc OpenAPI (Swagger)
- Docker & Docker Compose

## High-Level Components

- Customer Management
- Loan Products
- Loan Management
- Installments
- Repayments
- Fees
- Notifications

## Database Architecture

The ER diagram below represents the complete database model, including entities, relationships, primary keys, foreign keys, audit fields, and cardinality.

![Entity Relationship Diagram](lending_er_diag.png)

## Core Relationships

| Parent | Child | Cardinality |
|---------|-------|-------------|
| Customer | Customer Loan Limit | 1:1 |
| Customer | Loan | 1:N |
| Customer | Notification | 1:N |
| Customer | Repayment | 1:N |
| Loan Product | Loan | 1:N |
| Loan Product | Fee | 1:N |
| Loan | Installment | 1:N |
| Loan | Repayment | 1:N |
| Loan | Loan Fee | 1:N |
| Fee | Loan Fee | 1:N |
| Notification | Notification Variables | 1:N |

## Audit Fields

Every major entity contains:

- created_at
- updated_at
- created_by
- updated_by
- version

These provide traceability and optimistic locking support.
