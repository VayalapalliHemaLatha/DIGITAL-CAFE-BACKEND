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

### Razorpay payment (checkout with UPI QR, cards, etc.)

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| POST | `/api/orders/{orderId}/payment/create` | none | Create Razorpay order for the cafe order. Returns `keyId`, `razorpayOrderId`, `amountPaise`, `currency`, `receipt`, `companyName`. Requires auth. |
| POST | `/api/orders/{orderId}/payment/verify` | `{ "razorpayOrderId", "razorpayPaymentId", "razorpaySignature" }` | Verify payment after checkout success. Requires auth. |

**Create response (201):**
```json
{
  "keyId": "rzp_test_xxx",
  "razorpayOrderId": "order_xxx",
  "amountPaise": 15000,
  "currency": "INR",
  "receipt": "order_1",
  "companyName": "Digital Cafe"
}
```

**Verify request body:** (use the same names Razorpay returns in the success handler)
```json
{
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "razorpaySignature": "abc123..."
}
```

**Frontend flow (Razorpay Standard Checkout with UPI QR):**

1. Load script: `<script src="https://checkout.razorpay.com/v1/checkout.js"></script>`
2. When user clicks Pay on checkout page (e.g. `/customer/order/checkout/payment`):
   - Call `POST /api/orders/{orderId}/payment/create` with `Authorization: Bearer <token>`.
   - Use response: `keyId`, `razorpayOrderId`, `amountPaise`, `currency`, `companyName`.
3. Open Razorpay checkout:
   - `options = { key: keyId, amount: amountPaise, currency, order_id: razorpayOrderId, name: companyName, description: "Order payment", handler: function(response) { ... } }`
   - `new Razorpay(options); rzp1.open();`
4. In `handler(response)`: you get `response.razorpay_payment_id`, `response.razorpay_order_id`, `response.razorpay_signature`. Call `POST /api/orders/{orderId}/payment/verify` with body `{ razorpayOrderId: response.razorpay_order_id, razorpayPaymentId: response.razorpay_payment_id, razorpaySignature: response.razorpay_signature }`, then show success and redirect.
5. Razorpay checkout shows UPI, Cards, UPI QR, Netbanking, Wallet, Pay Later by default; UPI QR is under the UPI tab.

Credentials are in `application.properties` (`rzp.key_id`, `rzp.key_secret`). For production use env vars `RZP_KEY_ID` and `RZP_KEY_SECRET`. If you get "Razorpay authentication failed", get fresh Test Mode keys from https://dashboard.razorpay.com/app/keys and update the properties (no spaces, exact copy).

### Cart-style order (Swiggy/Zomato style)

Use this when the user selects items from the menu without choosing cafe/table first. Cafe is inferred from the items (all items must be from the same cafe). Table is optional; if omitted, the first table of that cafe is used.

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| POST | `/api/orders/from-cart` | `{ "items": [ { "menuItemId", "quantity" } ], "orderDate", "orderTime" }` | Create order from cart. Optional: `tableId`, `bookingId`. Requires auth. |

**Example request:**
```json
{
  "items": [
    { "menuItemId": 4, "quantity": 3 },
    { "menuItemId": 5, "quantity": 2 }
  ],
  "orderDate": "2026-03-15",
  "orderTime": "17:41"
}
```

Use menu item ids from `GET /api/menu` or `GET /api/cafes/{id}`. All items must belong to the same cafe.

**Order flow using menu API:**

1. Call `GET /api/menu` (with auth). Response example:
   ```json
   [
     { "id": 4, "name": "Americano", "description": "...", "price": 3.00, "category": "beverage", "available": true },
     { "id": 23, "name": "Americano", ... }
   ]
   ```
2. User adds items to cart (use the `id` from each object as `menuItemId`).
3. Call `POST /api/orders/from-cart` with:
   ```json
   {
     "items": [ { "menuItemId": 4, "quantity": 3 }, { "menuItemId": 23, "quantity": 1 } ],
     "orderDate": "2026-03-15",
     "orderTime": "17:49"
   }
   ```
4. Response includes the created order with `id`. Use that for payment: `POST /api/orders/{orderId}/payment/create`.

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
