# Library Management System 📚

A backend-only, Dockerized Library Management System built with **Java (Jersey)**, **MySQL**, and **multi-container architecture** using **Docker Compose**.

---

## Tech Stack

* **Java 17**
* **JAX-RS (Jersey 3.x)**
* **MySQL 8**
* **Docker & Docker Compose**
* **Modular WAR builds** for `user` and `admin` services
* **JDBC for DB interaction**

---

## Architecture

The system is containerized into the following components:

```
library-management-system/
├── user-service       # Handles user registration, book search, borrow, return
├── admin-service      # Admin-only routes: add/update/delete books, promote users
├── mysql              # MySQL 8 with schema initialized via `init.sql`
├── Dockerfile         # Builds WAR into Tomcat for user/admin services
├── docker-compose.yml # Orchestrates multi-container setup
```

Each WAR is deployed in its own Tomcat container:

* `user.war` → handles `/users/*` routes
* `admin.war` → handles `/admin/*` routes

All containers communicate via Docker's internal bridge network.

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/shravan2193/library-management-system.git
cd library-management-system
```

### 2. Build and Run with Docker Compose

```bash
docker-compose up --build
```

This will:

* Build WARs using Maven
* Deploy them to separate Tomcat containers
* Initialize the MySQL database using `init.sql`

---

### 3. API Overview

#### User Endpoints (`/users`)

* `POST /users/signup` – Register a user
* `POST /users/login` – Authenticate
* `GET /users/books` – View all books
* `GET /users/books/search` – Search books
* `POST /users/borrow` – Borrow a book
* `POST /users/return` – Return a book
* `GET /users/{id}/borrow-history` – View borrow history

#### Admin Endpoints (`/admin`)

* `POST /admin/books/add` – Add book
* `PUT /admin/books/{id}/update` – Update book
* `DELETE /admin/books/{id}/delete` – Delete book
* `POST /admin/categories/add` – Add category
* `PUT /admin/users/{id}/promote` – Promote user to admin
* `DELETE /admin/users/{id}/delete` – Delete user
* `GET /admin/users` – View all users
* `GET /admin/borrow-records` – View all borrow logs

---

### 4. Database Schema

Defined in [`db/init.sql`](./db/init.sql), includes the following tables:

* `users`
* `books`
* `categories`
* `book_categories`
* `borrow_records`

---

## Testing APIs

You can test the APIs using:

* [Postman](https://www.postman.com/)
* `curl` from terminal
* Or integrate with a frontend in the future

Example POST request:

```json
POST /users/signup
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword"
}
```

---

## Future Enhancements

* CI/CD pipeline integration
* JWT-based authentication
* Kubernetes deployment
* Role-based access control (RBAC)
* Add frontend in React/Vue (optional)

---

## License

This project is licensed under the MIT License.

---
