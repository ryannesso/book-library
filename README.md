Library Management System

A modern web application built with Spring Boot, providing a RESTful API for managing books and user accounts. The application supports CRUD operations for books and users, role-based access control, and secure authentication.

Features

Book Management
- Borrow and Return: Users have the ability to check out and return books.
- Inventory Oversight: Admins oversee and maintain the details and availability of books.

User Management
- Profile Handling: Users can create and manage their personal accounts.
- Borrowing Records: Users can view their loan history and account activity.
- Roles & Access: The system supports various user roles (e.g., admin, user) with role-based permissions.

Transaction Management
- Activity Tracking: Records detailed information about the borrowing and returning of books.
- Transaction Logs: Allows viewing of complete transaction histories, including actions, dates, and book details.

Authentication
- JWT-Based Authentication: Secure authentication using JSON Web Tokens (JWT).
- Role-Based Authorization: Different access levels for User and Admin roles.

Getting Started

Prerequisites
- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- MySQL 8
- Docker & Docker Compose
- Lombok
- MapStruct

Running the Application
Using Docker
```
docker-compose up --build
```

Database
The application uses MySQL 8 as the database engine. Configuration is managed through the src/main/resources/application.properties file.
