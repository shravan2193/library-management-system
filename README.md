# Library Management System

A backend microservices-based library management system built with Java (Jersey), MySQL, Docker, and Kubernetes. Designed for educational and portfolio purposes with modular components for user and admin functionality.

---

## Features

* User Registration and Login
* Book Search and Borrowing
* Book Return
* Admin Book Management (Add/Update/Delete)
* Role-based Authorization (planned)
* Stateless Architecture with JWT (planned)
* MySQL Database Initialization with Persistent Volumes

---

## Tech Stack

* Java 17, JAX-RS (Jersey 3.x)
* MySQL 8
* Docker, Docker Compose
* Kubernetes (Minikube)
* Maven for build
* ConfigMaps & Secrets for environment management

---

## Project Structure

```
library-management-system/
├── user-service/          # Java backend for user APIs
├── admin-service/         # Java backend for admin APIs
├── db/                    # SQL init scripts
├── k8/                    # Kubernetes YAMLs
│   ├── user/
│   ├── admin/
│   ├── mysql/
│   └── ingress/
└── Dockerfile             # Base Dockerfile (per service)
```

---

## Setup Instructions

### Docker (Local Dev)

```bash
# 1. Build Docker images
docker build -t user-service ./user-service
docker build -t admin-service ./admin-service

# 2. Start all services using Docker Compose
docker-compose up --build
```

### Kubernetes (Minikube)

```bash
# 1. Start Minikube
minikube start --driver=docker

# 2. Apply all K8s configs
kubectl apply -f k8/

# 3. Get service URL
minikube service user-service -n library-app
```

---

## API Endpoints

### User Service

* POST /users/register
* POST /users/login *(planned)*
* GET /books
* POST /borrow
* POST /return

### Admin Service

* POST /books/add
* PUT /books/{id}
* DELETE /books/{id}
* GET /users

---

## Future Enhancements

* JWT-based Authentication
* API Gateway/Ingress with TLS
* CI/CD Pipeline Integration
* Unit & Integration Testing
* AWS Migration with Terraform

---

## Author

Shravan S
Backend Developer & Cloud Architect in Progress
[https://github.com/shravan2193](https://github.com/shravan2193)
