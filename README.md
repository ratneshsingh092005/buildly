# Buildly

Buildly is a project management platform built using Spring Boot. It provides secure authentication, project management, role-based access control, AI-powered code generation, payment integration, and object storage support. The application follows clean architecture principles and demonstrates modern backend development practices using the Spring ecosystem.

---

## Overview

Buildly is designed to help teams manage projects efficiently while showcasing enterprise-level backend concepts such as authentication, authorization, AI integration, file storage, and payment processing.

---

## Features

### Authentication & Authorization

- User Registration
- User Login
- JWT-based Authentication
- Spring Security Integration
- Role-Based Access Control (RBAC)
- Protected REST APIs

### User Management

- User Profile Management
- Secure Access Control

### Project Management

- Create Projects
- Update Projects
- Delete Projects
- Manage Project Members

### AI Integration

- AI-powered Code Generation
- OpenAI Integration using Spring AI
- Tool Calling Support
- AI Chat Event Handling

### Payment Integration

- Stripe Webhook Integration
- Secure Payment Event Processing

### Object Storage

- File Upload using MinIO
- Object Storage Integration

### API Documentation

- Swagger UI
- OpenAPI Documentation

---

## Technology Stack

### Backend

- Java 17
- Spring Boot 4
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate Validator

### Database

- PostgreSQL

### AI

- Spring AI
- OpenAI

### Security

- JWT (JJWT)

### Object Storage

- MinIO

### Payment Gateway

- Stripe

### Documentation

- Swagger / OpenAPI

### Build Tool

- Maven

### Additional Libraries

- MapStruct
- Lombok

---


## Getting Started

### Prerequisites

- Java 17
- Maven
- PostgreSQL
- MinIO
- OpenAI API Key
- Stripe Account

---

### Clone the Repository

```bash
git clone https://github.com/ratneshsingh092005/buildly.git

cd buildly
```

---

### Configure PostgreSQL

Update your `application.properties` file.

```properties
spring.datasource.url=YOUR_DATABASE_URL
spring.datasource.username=YOUR_DATABASE_USERNAME
spring.datasource.password=YOUR_DATABASE_PASSWORD
```

---

### Configure JWT

```properties
jwt.secret=YOUR_SECRET_KEY
jwt.expiration=YOUR_EXPIRATION_TIME
```

---

### Configure OpenAI

```properties
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
```

---

### Configure MinIO

```properties
minio.url=YOUR_MINIO_URL
minio.access-key=YOUR_ACCESS_KEY
minio.secret-key=YOUR_SECRET_KEY
minio.bucket-name=YOUR_BUCKET_NAME
```

---

### Configure Stripe

```properties
stripe.secret-key=YOUR_SECRET_KEY
stripe.webhook-secret=YOUR_WEBHOOK_SECRET
```

---

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Authentication

Protected endpoints require a JWT access token.

Example:

```
Authorization: Bearer <JWT_TOKEN>
```

---

## Architecture

```
                    Client
                       │
                       ▼
                Spring Boot REST API
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
 Authentication     Projects      AI Services
        │              │              │
        └──────────────┼──────────────┘
                       │
                 Spring Data JPA
                       │
                  PostgreSQL

      Spring AI ─────────────► OpenAI

      Object Storage ─────────► MinIO

      Payment Processing ─────► Stripe
```

---

## Concepts Demonstrated

- RESTful API Development
- JWT Authentication
- Spring Security
- Role-Based Access Control (RBAC)
- Bean Validation
- DTO Mapping using MapStruct
- Exception Handling
- Layered Architecture
- File Storage Integration
- AI Integration using Spring AI
- Payment Webhook Handling
- Clean Code Practices

---

## Planned Enhancements

- Microservices Architecture
- API Gateway
- Apache Kafka
- Service Discovery
- Distributed Transactions
- Kubernetes Deployment
- CI/CD using GitHub Actions

---

## Author

**Ratnesh Singh**

GitHub: https://github.com/ratneshsingh092005/buildly

---
