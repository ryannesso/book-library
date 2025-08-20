# **Library Management System**

A modern web application built with Spring Boot 3.4.4, providing a RESTful API for managing books and user accounts. The system supports CRUD operations, role-based access control, secure authentication, and integrates messaging and database migration frameworks.

Demo Video: https://www.youtube.com/watch?v=0qdUSKTKRzQ&ab_channel=ryan

## Features

### **Book Management**
- Borrow and Return: Users can check out and return books.
- Inventory Oversight: Admins manage book details and availability.

### **User Management**
- Profile Handling: Users can create and manage accounts.
- Borrowing Records: Users can view loan history and account activity.
- Roles & Access: Role-based access control (User/Admin).

### **Transaction Management**
- Activity Tracking: Records detailed borrow/return activities.
- Transaction Logs: Complete history of actions, dates, and book details.

### **Authentication & Security**
- JWT-Based Authentication: Secure authentication using JSON Web Tokens (JJWT).
- Role-Based Authorization: Different access levels for User and Admin.
- Spring Security: Password hashing and secure login via spring-boot-starter-security.

### **Persistence & Database**
- Spring Data JPA: ORM support with Hibernate.
- MySQL 8: Primary database.
- Flyway: Database migration and versioning.

### **Messaging & Event Handling**
- Spring Kafka: Event-driven architecture for asynchronous processing.
- Apache Kafka + Zookeeper: Broker and coordination for messaging.

### **Validation & Utilities**
- Jakarta Bean Validation + Hibernate Validator: Server-side validation of entities.
- MapStruct: DTO mapping between entity and request/response objects.
- Lombok: Boilerplate code reduction (getters/setters, constructors).

### **Development Tools**
- Spring Boot DevTools: Hot reload during development.
- WebSocket Support: Real-time updates using spring-boot-starter-websocket.

### **Tech Stack**
* Spring, Spring Boot, Security, Data JPA, MVC, WebSocket
* Hibernate
* Flyway
* Kafka
* MapStruct
* Lombok
* MySQL 8

## Getting Started

**Prerequisites**
- Java 21
- Maven
- MySQL 8
- Docker & Docker Compose

### **Running the Application**

Using Docker Compose
```
docker-compose up --build
```

This command will build and start all services: MySQL, Kafka, Zookeeper, backend, and frontend.

Database Configuration
The application uses MySQL 8. Database credentials and connection are configured in src/main/resources/application.properties.

# **Configuration**
Default Admin User
During startup, a default admin user is created with credentials that can be customized in the application.properties file:
```
app.admin.username=admin
app.admin.password=admin
app.admin.email=admin52@example.com
```

# Images
<img width="1882" height="972" alt="1 " src="https://github.com/user-attachments/assets/fb9c927e-e36f-43c8-8df3-73304dc9f367" />
<img width="1882" height="972" alt="2" src="https://github.com/user-attachments/assets/673244ae-8efe-4a41-a1e8-532c18541912" />
<img width="1882" height="972" alt="3" src="https://github.com/user-attachments/assets/101ac496-e9b6-450c-a00b-f7d8767f12c5" />
<img width="1882" height="972" alt="6" src="https://github.com/user-attachments/assets/28364d01-b585-4044-bd35-c25476cc8463" />
<img width="1882" height="972" alt="5" src="https://github.com/user-attachments/assets/13e06bdd-3191-4120-9cce-85da01809b73" />
<img width="1882" height="972" alt="4" src="https://github.com/user-attachments/assets/3bd1b789-fb19-42e9-bca4-dfb4d724d3e6" />
<img width="1882" height="972" alt="8" src="https://github.com/user-attachments/assets/39875b2a-0086-46ca-a0fd-e2507878c544" />
<img width="1882" height="972" alt="7" src="https://github.com/user-attachments/assets/88c61fd1-bf7d-4802-82b1-eaf52d407ab2" />

