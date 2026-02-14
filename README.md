# Digital Café

Spring Boot web app for table booking, pre-order, and online payments.

## Requirements

- **Java 17**
- **Maven** (or use the included wrapper)

## How to run

From the project root (`digital-cafe/`):

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

Then open: **http://localhost:8080**

## Backend API (for React frontend)

This app is **backend only** for the API. Run React on **http://localhost:3000**. CORS is allowed for that origin.

**Base URL:** `http://localhost:8080`

### Auth APIs

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/signup` | `{ "email", "password", "name" }` | Register; returns JWT + user |
| POST | `/api/auth/login`  | `{ "email", "password" }`       | Login; returns JWT + user |

**Signup request:**
```json
{ "email": "user@example.com", "password": "secret123", "name": "John" }
```

**Login request:**
```json
{ "email": "user@example.com", "password": "secret123" }
```

**Success response (201 for signup, 200 for login):**
```json
{
  "token": "eyJhbGc...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "name": "John"
}
```

**Using the token in React:**  
For protected APIs, send the JWT in the header:
```
Authorization: Bearer <token>
```

Store `token` (e.g. in memory, localStorage, or a cookie) after login/signup and attach it to every request to `/api/*` (except `/api/auth/login` and `/api/auth/signup`).

## Project structure

```
digital-cafe/
├── pom.xml
├── mvnw, mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/cafe/digital_cafe/
│   │   │   ├── DigitalCafeApplication.java   # Entry point
│   │   │   ├── config/          # Security, CORS
│   │   │   ├── controller/      # HomeController, AuthController
│   │   │   ├── dto/             # LoginRequest, SignupRequest, AuthResponse
│   │   │   ├── entity/          # User
│   │   │   ├── repository/      # UserRepository
│   │   │   ├── security/        # JwtAuthFilter
│   │   │   └── service/         # AuthService, JwtService
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   │           └── index.html
│   └── test/
│       └── java/com/cafe/digital_cafe/
│           └── DigitalCafeApplicationTests.java
```

## Build

```bash
mvnw.cmd clean package
```

Run the JAR:

```bash
java -jar target/digital-cafe-0.0.1-SNAPSHOT.jar
```

run the proejct using this command 
.\mvnw.cmd spring-boot:run
