# Cart Service

The **Cart Service** is a Spring Boot microservice within the ECOMM e-commerce platform. It manages shopping cart state — for both authenticated users and anonymous guests — backed by Redis, with support for guest-to-user cart merging on login.

## Features

- **Guest & user carts** — anonymous shoppers get a cart tracked via a `GUEST_CART_ID` cookie; authenticated shoppers get a cart keyed by their JWT subject.
- **Redis-backed storage** — carts are stored in Redis with a TTL (7 minutes for guest carts, 30 minutes for user carts), so abandoned carts expire automatically.
- **Cart merge on login** — when a guest logs in, their guest cart is merged into their user cart via `POST /cart/merge`, combining quantities for shared items.
- **JWT-based auth** — secured as an OAuth2 resource server; only `/cart/merge` requires an authenticated (JWT) request, all other endpoints support both guest and authenticated access.
- **Service discovery** — registers with Eureka as `cart-service` for discovery by other services.

## API

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/cart/add` | Optional | Add an item to the cart (guest or user) |
| GET | `/cart/get` | Optional | Get the current cart |
| PATCH | `/cart/quantity` | Optional | Update the quantity of a cart item |
| DELETE | `/cart/remove/{productId}` | Optional | Remove an item from the cart |
| DELETE | `/cart/clear` | Optional | Clear the entire cart |
| POST | `/cart/merge` | Required | Merge the guest cart into the authenticated user's cart |

## Tech stack

- Java 17
- Spring Boot 3.3.3 (Web, Data Redis, OAuth2 Resource Server)
- Spring Cloud 2023.0.3 (Netflix Eureka Client)
- Redis
- Lombok

## Configuration

Key properties (`src/main/resources/application.properties`):

| Property | Description |
|---|---|
| `server.port` | `8085` |
| `eureka.instance.appname` / `virtual-host-name` | `cart-service` |
| `jwt.secret` | JWT signing secret, injected via `JWT_SECRET` environment variable |
| `spring.data.redis.host` / `port` | Redis connection (defaults to `localhost:6379`) |

## Running locally

```bash
# Ensure Redis is running locally on port 6379, and Eureka Service Discovery is up

export JWT_SECRET=your-shared-secret

./mvnw spring-boot:run
```

The service starts on `http://localhost:8085` and registers itself with Eureka as `cart-service`.

## Where it fits

The platform is split into independently deployable Spring Boot services, registered with and discovered through Eureka:

| Service | Responsibility
|---|---|
| [Auth Service](https://github.com/sarangKaliyath/ECOMM_Auth_ServiceApplication) | Identity, tokens, sessions |
| [Profile Service](https://github.com/sarangKaliyath/ECOMM_Profile_Service_Application) | User profile data (created reactively on signup) |
| [Product Service](https://github.com/sarangKaliyath/ECOMM_Product_ServiceApplication) | Product catalog |
| **Cart Service** [*(this repo)*](https://github.com/sarangKaliyath/ECOMM_Cart_Service_Application) | Shopping cart |
| [Ordering Service](https://github.com/sarangKaliyath/ECOMM_Ordering_Service_Application) | Order lifecycle |
| [Payment Service](https://github.com/sarangKaliyath/ECOMM_Payment_Gateway_Service_Application) | Payment processing |
| [Email Service](https://github.com/sarangKaliyath/ECOMM_Email_Service_Application) | Transactional email delivery |
| [Service Discovery](https://github.com/sarangKaliyath/ECOMM_Service_Discovery_Application) | Eureka registry |
