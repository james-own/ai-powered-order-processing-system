# 🚀 AI-Powered Order Processing System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-latest-black.svg)](https://kafka.apache.org/)
[![Grafana](https://img.shields.io/badge/Grafana-latest-orange.svg)](https://grafana.com/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> **A production-grade event-driven microservices architecture demonstrating AI integration, hexagonal design patterns, and comprehensive observability.**

Built as a portfolio project to showcase modern backend engineering practices, this system implements intelligent order processing with real-time fraud detection, semantic search capabilities, and full-stack observability using industry-standard tools.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Observability](#observability)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## 🎯 Overview

This project demonstrates a **microservices-based order processing system** enhanced with AI capabilities, implementing:

- **Event-Driven Architecture** using Apache Kafka for asynchronous communication
- **Hexagonal Architecture** (Ports & Adapters) for clean separation of concerns
- **AI Integration** featuring fraud detection and LLM-powered customer support
- **Full Observability Stack** with metrics, logs, and distributed tracing
- **Production-Ready Patterns** including circuit breakers, retry mechanisms, and health checks

### Business Context

The system simulates an e-commerce order processing pipeline where:
1. Customers place orders through a REST API
2. AI analyzes orders for potential fraud
3. Inventory is validated asynchronously via event messaging
4. Customers receive real-time notifications
5. An AI chatbot answers customer queries about their orders

---

## 🏗️ Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Client Applications                          │
│                    (Web, Mobile, Third-party APIs)                  │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │   API Gateway       │
                    │  (Future: Kong/NGINX)│
                    └──────────┬──────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
┌───────▼────────┐    ┌────────▼────────┐   ┌───────▼────────┐
│ Order Service  │    │Inventory Service│   │Notification Svc│
│   (Port 8081)  │    │   (Port 8082)   │   │  (Port 8083)   │
│                │    │                 │   │                │
│ • REST API     │    │ • Stock Mgmt    │   │ • Email/SMS    │
│ • Fraud AI     │    │ • Validation    │   │ • AI Chatbot   │
│ • GraphQL      │    │                 │   │                │
└───────┬────────┘    └────────┬────────┘   └───────┬────────┘
        │                      │                      │
        └──────────────────────┼──────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │   Apache Kafka      │
                    │  Message Broker     │
                    └─────────────────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
┌───────▼────────┐    ┌────────▼────────┐   ┌───────▼────────┐
│  PostgreSQL    │    │   Prometheus    │   │     Loki       │
│   (Database)   │    │   (Metrics)     │   │    (Logs)      │
└────────────────┘    └─────────────────┘   └────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │      Grafana        │
                    │  (Visualization)    │
                    └─────────────────────┘
```

### Hexagonal Architecture

Each microservice follows the **Hexagonal Architecture** pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                    ADAPTERS (Input/Driving)                  │
│          REST Controllers, Kafka Consumers, gRPC             │
└──────────────────────────┬──────────────────────────────────┘
                           │ Ports (Interfaces)
┌──────────────────────────▼──────────────────────────────────┐
│                     APPLICATION CORE                         │
│              Pure Business Logic (Framework-Free)            │
│    • Use Cases    • Domain Models    • Business Rules       │
└──────────────────────────┬──────────────────────────────────┘
                           │ Ports (Interfaces)
┌──────────────────────────▼──────────────────────────────────┐
│                   ADAPTERS (Output/Driven)                   │
│       PostgreSQL, Kafka Producers, AI APIs, External APIs    │
└─────────────────────────────────────────────────────────────┘
```

**Benefits:**
- ✅ **Testability**: Business logic isolated from infrastructure
- ✅ **Maintainability**: Clear boundaries between layers
- ✅ **Flexibility**: Easy to swap implementations (PostgreSQL → MongoDB)
- ✅ **Framework Independence**: Core logic has zero Spring dependencies

---

## ✨ Key Features

### 🤖 AI-Powered Capabilities

#### 1. Fraud Detection Engine
- Real-time anomaly detection using machine learning
- Risk scoring based on order patterns, amount, and velocity
- Automatic flagging of suspicious transactions
- Integration with Claude API for enhanced pattern recognition

#### 2. LLM-Powered Customer Support Chatbot
- Natural language query processing using Anthropic Claude
- Answers questions like "Where is my order?" or "Can I cancel order #12345?"
- Function calling to fetch real-time order status from database
- Context-aware responses with order history

#### 3. Semantic Search (Future Enhancement)
- Vector embeddings for product and order search
- "Search by meaning" not just keywords
- PostgreSQL pgvector integration

### 🔄 Event-Driven Architecture

- **Asynchronous Communication**: Microservices communicate via Kafka topics
- **Event Sourcing Ready**: All state changes published as events
- **Scalability**: Services can be scaled independently
- **Resilience**: Services remain decoupled; failures are isolated

### 📊 Full-Stack Observability

#### Metrics (Prometheus)
- Request rates, latency percentiles (p50, p95, p99)
- Custom business metrics (orders created, fraud detected)
- JVM metrics (heap usage, GC pauses, thread pools)
- Kafka consumer lag monitoring

#### Logs (Loki)
- Structured JSON logging with correlation IDs
- Centralized log aggregation across all services
- Query logs by service, trace ID, or severity level

#### Traces (Tempo)
- Distributed tracing across microservices
- End-to-end request visualization
- Performance bottleneck identification
- Trace correlation with logs and metrics

### 🛡️ Resilience Patterns

- **Circuit Breakers**: Prevent cascading failures (Resilience4j)
- **Retry Mechanisms**: Automatic retry with exponential backoff
- **Bulkheads**: Isolate thread pools for critical operations
- **Health Checks**: Kubernetes-ready liveness/readiness probes

---

## 🛠️ Tech Stack

### Backend Framework
- **Java 17** - Modern LTS version with Records, Pattern Matching
- **Spring Boot 3.2.2** - Production-grade framework
- **Spring Data JPA** - Database abstraction
- **Spring Kafka** - Event streaming integration

### Messaging & Events
- **Apache Kafka** - Distributed event streaming platform
- **Zookeeper** - Kafka cluster coordination

### AI & Machine Learning
- **Spring AI** - LLM integration framework
- **Anthropic Claude API** - Advanced language model
- **OpenAI API** - Alternative LLM provider (configurable)

### Data Storage
- **PostgreSQL 15** - Primary relational database
- **pgvector** - Vector similarity search extension (future)

### Observability Stack
- **Prometheus** - Metrics collection and alerting
- **Grafana** - Visualization and dashboards
- **Loki** - Log aggregation system
- **Tempo** - Distributed tracing backend
- **Promtail** - Log shipping agent

### Build & Development
- **Gradle 8.x** - Build automation
- **Lombok** - Boilerplate code reduction
- **Testcontainers** - Integration testing with Docker
- **JUnit 5** - Testing framework

### DevOps & Infrastructure
- **Docker & Docker Compose** - Containerization
- **Kubernetes** - Container orchestration (deployment ready)
- **GitHub Actions** - CI/CD pipeline (future)

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** - [Download](https://adoptium.net/)
- **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop)
- **PostgreSQL** - Running locally or via Docker
- **Git** - Version control
- **IDE** - IntelliJ IDEA (recommended), VS Code, or Eclipse

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/jameskreye/ai-powered-order-processing-system.git
cd ai-powered-order-processing-system
```

#### 2. Set Up Environment Variables

Create `.env` file in the root directory:

```env
# Database
POSTGRES_DB=orderdb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=yourpassword

# AI APIs (get keys from providers)
ANTHROPIC_API_KEY=your_claude_api_key_here
OPENAI_API_KEY=your_openai_api_key_here

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:29092
```

#### 3. Create PostgreSQL Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE orderdb;

# Exit
\q
```

#### 4. Start Infrastructure Services

```bash
# Start Kafka, Zookeeper, and Observability Stack
docker-compose up -d

# Verify all services are running
docker-compose ps
```

Expected output:
```
NAME                SERVICE             STATUS
kafka               kafka               running
zookeeper           zookeeper           running
kafka-ui            kafka-ui            running
prometheus          prometheus          running
loki                loki                running
tempo               tempo               running
grafana             grafana             running
```

#### 5. Build All Microservices

```bash
# Build all services
./gradlew build

# Or build individually
./gradlew :order-service:build
./gradlew :inventory-service:build
./gradlew :notification-service:build
```

#### 6. Run Microservices

**Option A: Command Line (separate terminals)**

```bash
# Terminal 1 - Order Service
cd order-service
./gradlew bootRun

# Terminal 2 - Inventory Service
cd inventory-service
./gradlew bootRun

# Terminal 3 - Notification Service
cd notification-service
./gradlew bootRun
```

**Option B: IntelliJ IDEA**

1. Open project in IntelliJ
2. Gradle will auto-import all modules
3. Create run configurations for each service
4. Run all simultaneously

#### 7. Verify Services

Check health endpoints:

```bash
# Order Service
curl http://localhost:8081/actuator/health

# Inventory Service
curl http://localhost:8082/actuator/health

# Notification Service
curl http://localhost:8083/actuator/health
```

All should return: `{"status":"UP"}`

### Quick Test

Create your first order:

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-123",
    "productId": "product-456",
    "quantity": 2,
    "totalAmount": 99.99
  }'
```

Check Kafka UI to see events: http://localhost:8080

---

## 📁 Project Structure

```
ai-powered-order-processing-system/
│
├── observability/                      # Observability configurations
│   ├── prometheus/
│   │   └── prometheus.yml             # Metrics scraping config
│   ├── loki/
│   │   └── loki-config.yml            # Log aggregation config
│   ├── tempo/
│   │   └── tempo.yml                  # Distributed tracing config
│   └── grafana/
│       ├── provisioning/              # Auto-provisioned datasources
│       └── dashboards/                # Pre-built dashboards
│
├── order-service/                      # Microservice 1
│   ├── src/main/java/com/jameson/orderservice/
│   │   ├── domain/                    # 🎯 Core business logic
│   │   │   ├── model/                 # Domain entities (pure Java)
│   │   │   ├── port/                  # Interfaces (in & out)
│   │   │   └── service/               # Business rules
│   │   ├── application/               # Use case implementations
│   │   ├── adapter/                   # Infrastructure adapters
│   │   │   ├── in/rest/              # REST controllers
│   │   │   └── out/persistence/      # Database adapters
│   │   └── infrastructure/            # Framework configuration
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── logback-spring.xml
│   └── build.gradle
│
├── inventory-service/                  # Microservice 2
│   └── [Similar hexagonal structure]
│
├── notification-service/               # Microservice 3
│   └── [Similar hexagonal structure]
│
├── docker-compose.yml                  # Infrastructure services
├── settings.gradle                     # Multi-project Gradle config
├── .gitignore
├── LICENSE
└── README.md                          # This file
```

### Hexagonal Structure Deep Dive

```
domain/
├── model/              # Pure domain entities (no annotations!)
│   ├── Order.java
│   ├── OrderStatus.java
│   └── FraudScore.java
├── port/
│   ├── in/            # Input ports (use cases)
│   │   ├── CreateOrderUseCase.java
│   │   └── GetOrderUseCase.java
│   └── out/           # Output ports (dependencies)
│       ├── OrderRepository.java
│       ├── OrderEventPublisher.java
│       └── FraudDetectionService.java
└── service/           # Business logic implementation
    └── OrderDomainService.java
```

---

## 📊 Observability

### Access Dashboards

Once all services are running:

| Service | URL | Credentials | Purpose |
|---------|-----|-------------|---------|
| **Grafana** | http://localhost:3000 | admin / admin | Dashboards, metrics, logs, traces |
| **Prometheus** | http://localhost:9090 | - | Metrics queries |
| **Kafka UI** | http://localhost:8080 | - | Kafka topic monitoring |

### Pre-built Dashboards

Navigate to **Grafana → Dashboards**:

1. **System Overview** - All services at a glance
2. **Order Service Metrics** - Request rates, latency, errors
3. **Kafka Metrics** - Consumer lag, throughput
4. **JVM Metrics** - Heap, GC, threads across all services

### Custom Metrics

The system exposes business metrics:

```
# Total orders created
orders.created

# Fraudulent orders detected
fraud.detected

# Order confirmation rate
orders.confirmed

# Average fraud detection time
fraud.detection.time
```

Query in Prometheus:
```promql
# Order creation rate (per minute)
rate(orders_created_total[5m])

# 95th percentile latency for order creation
histogram_quantile(0.95, orders_create_seconds_bucket)
```

### Distributed Tracing

View end-to-end traces in Grafana:

1. Navigate to **Explore**
2. Select **Tempo** datasource
3. Search by Trace ID or service name
4. See request flow across all microservices

---

## 📚 API Documentation

### Order Service (Port 8081)

#### Create Order

```http
POST /api/orders
Content-Type: application/json

{
  "customerId": "customer-123",
  "productId": "product-456",
  "quantity": 2,
  "totalAmount": 99.99
}
```

Response:
```json
{
  "id": 1,
  "customerId": "customer-123",
  "productId": "product-456",
  "quantity": 2,
  "totalAmount": 99.99,
  "status": "PENDING",
  "fraudScore": {
    "score": 0.15,
    "riskLevel": "LOW"
  },
  "createdAt": "2026-02-10T10:30:00Z"
}
```

#### Get Order

```http
GET /api/orders/{id}
```

#### Chat with AI Bot

```http
POST /api/chat
Content-Type: application/json

{
  "message": "Where is my order #123?"
}
```

Response:
```json
{
  "response": "Your order #123 is currently in transit and expected to arrive on February 12th. The tracking number is 1Z999AA10123456784."
}
```

### Interactive API Documentation

Swagger UI available at:
- **Order Service**: http://localhost:8081/swagger-ui.html
- **Inventory Service**: http://localhost:8082/swagger-ui.html

---

## 💻 Development

### Code Style

This project follows:
- **Google Java Style Guide**
- **Clean Code principles** by Robert C. Martin
- **SOLID principles** for object-oriented design

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "feat: add fraud detection ML model"

# Push and create PR
git push origin feature/your-feature-name
```

### Commit Message Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add new feature
fix: bug fix
docs: documentation changes
refactor: code refactoring
test: adding tests
chore: build process or auxiliary tool changes
```

---

## 🧪 Testing

### Run All Tests

```bash
# All services
./gradlew test

# Single service
./gradlew :order-service:test

# With coverage report
./gradlew test jacocoTestReport
```

### Test Structure

```
src/test/java/
├── domain/         # Unit tests (no Spring, pure Java)
├── application/    # Use case tests (minimal mocking)
└── adapter/        # Integration tests (with Testcontainers)
```

### Integration Tests

Uses **Testcontainers** for real PostgreSQL and Kafka:

```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    
    @Test
    void shouldCreateOrderAndPublishEvent() {
        // Test with real database and Kafka
    }
}
```

---

## 🚢 Deployment

### Docker Deployment

Build Docker images:

```bash
# Build all service images
docker build -t order-service:latest ./order-service
docker build -t inventory-service:latest ./inventory-service
docker build -t notification-service:latest ./notification-service
```

### Kubernetes Deployment (Future)

Kubernetes manifests in `k8s/` directory:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/deployments/
kubectl apply -f k8s/services/
```

### Environment Variables

Required for production:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/orderdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# AI
ANTHROPIC_API_KEY=${CLAUDE_API_KEY}

# Observability
MANAGEMENT_OTLP_TRACING_ENDPOINT=http://tempo:4318/v1/traces
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Guidelines

- Follow the existing code style
- Write tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Contact

**Jameson Michel**

- GitHub: [@jameskreye](https://github.com/jameskreye)
- LinkedIn: [jamesonm](https://www.linkedin.com/in/jamesonm/)
- Email: jamesonmichel16@gmail.com

---

## 🙏 Acknowledgments

- **Spring Team** - For the amazing Spring Boot framework
- **Apache Kafka** - For reliable event streaming
- **Anthropic** - For Claude AI API
- **Grafana Labs** - For the observability stack
- **Tech Community** - For inspiration and knowledge sharing

---

## 🗺️ Roadmap

### Phase 1 - Core Features (Current)
- [x] Event-driven microservices architecture
- [x] Hexagonal architecture implementation
- [x] Basic fraud detection
- [x] Full observability stack
- [ ] AI chatbot integration

### Phase 2 - Advanced AI
- [ ] ML-based fraud detection with model training
- [ ] Semantic search with vector embeddings
- [ ] Predictive inventory management
- [ ] Sentiment analysis on customer feedback

### Phase 3 - Scalability
- [ ] Kubernetes deployment
- [ ] Auto-scaling based on metrics
- [ ] Multi-region support
- [ ] Event replay capabilities

### Phase 4 - Advanced Features
- [ ] GraphQL API gateway
- [ ] Real-time analytics dashboard
- [ ] A/B testing framework
- [ ] Feature flags system

---

## 📈 Performance Metrics

Target performance benchmarks:

| Metric | Target | Current |
|--------|--------|---------|
| Order Creation Latency (p95) | < 100ms | TBD |
| Fraud Detection Time (p95) | < 50ms | TBD |
| Event Processing Latency | < 10ms | TBD |
| System Availability | 99.9% | TBD |
| Request Throughput | 10,000 req/s | TBD |

---

<div align="center">

**⭐ If you find this project useful, please consider giving it a star!**

Made with ❤️ by Jameson Michel

</div>
