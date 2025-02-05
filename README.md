# E-Book API

The **E-Book API** is a Spring Boot-based backend service that facilitates the management of books and user interactions, such as sharing, retrieving, and maintaining book details. This API also handles user authentication and authorization.

---

## Features

- **User Management**:  
  - User authentication and registration.  
  - Role-based access control (e.g., `USER` role).  

- **Book Management**:  
  - Add, update, retrieve, and share books.  
  - Fetch paginated lists of books.  
  - Manage book metadata such as title, author, synopsis, and more.  

- **Secure Endpoints**:  
  - Endpoints are secured using JSON Web Tokens (JWT) for authentication.

---

## Prerequisites

Ensure you have the following installed:

1. **Java 17** or higher.
2. **Maven** for dependency management.
3. **Docker** for containerization and streamlined deployment.
4. **Angular** for UI.

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/MatheshRavichandrann/e-book.git
cd book-network
```

---

### 2. Configure the Application

Update the `src/main/resources/application.yml` file with your database configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database_name
    username: your_database_user
    password: your_database_password
  jpa:
    hibernate:
      ddl-auto: update
```

---

### 3. Run the Backend Service

**Using Maven**:

```bash
mvn clean install
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`.

---

### 4. Using Docker

If you prefer using Docker for both the backend and database setup:

#### Step 1: Build Docker Images

Add a `Dockerfile` to the backend:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/book-network.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Step 2: Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/booknetwork
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      - database

  database:
    image: postgres:15
    container_name: postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: booknetwork
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
```

#### Step 3: Run Docker Compose:

```bash
docker-compose up --build
```

---

### 5. Run the Frontend (Optional)

If you are using the `book-network-ui` (Angular):

```bash
cd book-network-ui
npm install
ng serve
```

Access the frontend at `http://localhost:4200`.

---

## API Documentation

API endpoints are documented using Swagger. Once the application is running, you can access the API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Project Structure

```plaintext
book-network/
├── src/
│   ├── main/
│   │   ├── java/com/mugiwara/book/
│   │   │   ├── auth/         # Authentication logic
│   │   │   ├── book/         # Book domain logic
│   │   │   ├── file/         # File handling (e.g., book covers)
│   │   │   ├── config/       # Application configuration
│   │   │   ├── handler/      # Global exception handling
│   │   └── resources/
│   │       ├── application.yml  # Application properties
│   │       ├── banner.txt       # Custom banner
├── pom.xml               # Maven configuration
├── Dockerfile            # Dockerfile for backend
├── docker-compose.yml    # Docker Compose file
├── README.md             # Project README file
└── book-network-ui/      # Optional Angular frontend
```

---

## Key Endpoints

### Authentication

| Method | Endpoint        | Description                  |
|--------|-----------------|------------------------------|
| POST   | `/auth/login`   | Log in a user and get a JWT. |
| POST   | `/auth/register`| Register a new user.         |

### Books

| Method | Endpoint           | Description                  |
|--------|--------------------|------------------------------|
| GET    | `/books`           | Get a paginated list of books. |
| POST   | `/books`           | Add a new book.             |
| GET    | `/books/{id}`      | Get details of a specific book. |
| DELETE | `/books/{id}`      | Delete a book by ID.        |

---

## Common Errors

### 1. `service not initialized in the default constructor`
Ensure all services are properly annotated with `@RequiredArgsConstructor` or autowired.

### 2. Database Connection Errors
Check your `application.yml` for the correct database URL, username, and password.

---

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch.
3. Commit your changes.
4. Create a pull request.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## Contact

For queries or support, contact the maintainer:

- Email: [mathesh1907@gmail.com](mailto:mathesh1907@gmail.com)
```
