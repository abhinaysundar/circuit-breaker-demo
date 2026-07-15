# Circuit Breaker Banking Demo

A Spring Boot microservices project demonstrating the **Circuit Breaker pattern** using **Resilience4J** in a banking domain.

## Architecture

```
┌──────────────────┐     HTTP      ┌─────────────────────┐
│  account-service │ ────────────▶ │  transaction-service │
│    (port 8081)   │               │    (port 8082)       │
│                  │               └─────────────────────┘
│  @CircuitBreaker │     HTTP      ┌─────────────────────┐
│  + fallback      │ ────────────▶ │ notification-service │
└──────────────────┘               │    (port 8083)       │
                                    └─────────────────────┘
```

### Services

| Service | Port | Role |
|---|---|---|
| **account-service** | 8081 | Core banking service. Creates accounts, processes transfers. Wraps downstream calls with CircuitBreaker. |
| **transaction-service** | 8082 | Records debit/credit transactions. Supports failure/slow simulation. |
| **notification-service** | 8083 | Sends transfer alerts. Supports failure/slow simulation. |

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Cloud 2023.0.1
- Resilience4J Circuit Breaker
- Spring Data JPA + H2 (in-memory)
- Springdoc OpenAPI (Swagger)

## How to Run

```bash
# Build all services
mvn clean package

# Start each service in separate terminals
java -jar account-service/target/account-service-1.0.0.jar
java -jar transaction-service/target/transaction-service-1.0.0.jar
java -jar notification-service/target/notification-service-1.0.0.jar
```

## API Endpoints

### account-service (8081)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/{accountNumber}` | Get account by number |
| POST | `/api/accounts` | Create account |
| POST | `/api/accounts/transfer` | Transfer funds |
| GET | `/actuator/circuitbreakers` | Circuit breaker states |

### transaction-service (8082)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/transactions` | List all transactions |
| POST | `/api/transactions` | Record a transaction |
| POST | `/api/transactions/failure?enabled=true` | Toggle failure mode |
| POST | `/api/transactions/slow?enabled=true` | Toggle slow mode |
| GET | `/api/transactions/status` | Check current mode |

### notification-service (8083)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notifications` | List all notifications |
| POST | `/api/notifications` | Send a notification |
| POST | `/api/notifications/failure?enabled=true` | Toggle failure mode |
| POST | `/api/notifications/slow?enabled=true` | Toggle slow mode |
| GET | `/api/notifications/status` | Check current mode |

## Testing Circuit Breaker

```bash
# 1. Create two accounts
curl -X POST "http://localhost:8081/api/accounts" -H "Content-Type: application/json" -d "{\"accountHolder\":\"Alice\",\"initialDeposit\":1000}"
curl -X POST "http://localhost:8081/api/accounts" -H "Content-Type: application/json" -d "{\"accountHolder\":\"Bob\",\"initialDeposit\":500}"

# Note the account numbers from the response

# 2. Enable failure on transaction-service
curl -X POST "http://localhost:8082/api/transactions/failure?enabled=true"

# 3. Trigger transfers - after 3 failures the circuit opens
curl -X POST "http://localhost:8081/api/accounts/transfer" -H "Content-Type: application/json" -d "{\"fromAccount\":\"ACC001\",\"toAccount\":\"ACC002\",\"amount\":100}"

# 4. Check circuit breaker state
curl "http://localhost:8081/actuator/circuitbreakers"

# 5. Disable failure mode (circuit auto-recovers after 10s in OPEN state)
curl -X POST "http://localhost:8082/api/transactions/failure?enabled=false"
```

## Swagger UI

| Service | URL |
|---|---|
| account-service | http://localhost:8081/swagger-ui/index.html |
| transaction-service | http://localhost:8082/swagger-ui/index.html |
| notification-service | http://localhost:8083/swagger-ui/index.html |
