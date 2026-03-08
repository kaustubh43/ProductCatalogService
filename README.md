# Product Catalog Service

A robust Spring Boot microservice for product catalog management in an e-commerce ecosystem. This service handles product browsing, category management, search with pagination & sorting, and integrates with external APIs (FakeStore) and Redis caching.

## 🚀 Features

- **Product Browsing**: Browse products by different categories
- **Product Details**: Detailed product pages with images, descriptions, specifications, and pricing
- **Search**: Search products by keywords with pagination and multi-field sorting
- **Category Management**: Create and retrieve product categories
- **Role-Based Access**: Return product details based on user roles (Admin vs regular users)
- **Caching**: Redis integration for caching product data from external APIs
- **Service Discovery**: Integrated with Netflix Eureka for microservice architecture
- **External API Integration**: FakeStore API client for external product data
- **Soft Delete**: Products are soft-deleted by setting state to `DELETED`

## 🛠️ Tech Stack

- **Java**: 17
- **Spring Boot**: 3.5.6
- **Spring Data JPA**: 3.5.6 (Data persistence)
- **Spring Data Redis**: 3.5.7 (Caching)
- **MySQL**: Production database (Connector 9.4.0)
- **H2**: In-memory database for testing
- **Netflix Eureka Client**: 4.3.0 (Service Discovery)
- **Lombok**: For reducing boilerplate code
- **Maven**: Build and dependency management

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.6+** (or use the included Maven wrapper)
- **MySQL 8.0+** running locally
- **Eureka Server** running on port 8761 (for service discovery)
- **Redis** running on port 6379 (for caching)

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/kaustubh43/ProductCatalogService.git
cd ProductCatalogService
```

### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE productcatalogservice;
```

### 3. Update Configuration

Edit `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productcatalogservice
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. Build the Project

Using Maven wrapper (recommended):

```bash
./mvnw clean install
```

Or using Maven:

```bash
mvn clean install
```

### 5. Run the Application

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on **port 8082**.

## 📡 API Endpoints

### Product Endpoints

#### Get Product by ID
```http
GET /products/{id}
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "Galaxy Phone",
  "description": "Latest smartphone",
  "imageUrl": "http://img/1",
  "price": 999.99,
  "categoryDto": {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic items"
  }
}
```

#### Get All Products
```http
GET /products
```

**Response**: `200 OK` — Returns a list of all products.

#### Add Product
```http
POST /products/add
Content-Type: application/json

{
  "name": "Galaxy Phone",
  "description": "Latest smartphone",
  "imageUrl": "http://img/phone.jpg",
  "price": 999.99,
  "categoryDto": {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic items"
  }
}
```

**Response**: `201 CREATED`

#### Update Product (Replace)
```http
PUT /products/update/{id}
Content-Type: application/json

{
  "name": "Updated Phone",
  "description": "Updated description",
  "imageUrl": "http://img/phone-v2.jpg",
  "price": 1099.99,
  "categoryDto": {
    "id": 1,
    "name": "Electronics"
  }
}
```

**Response**: `200 OK`

#### Delete Product
```http
DELETE /products/{id}
```

**Response**: `200 OK` — Soft-deletes the product (sets state to `DELETED`).

#### Get Product Based on User Role
```http
GET /products/{productId}/{userId}
```

**Response**: `200 OK` — Returns product if user has access (Admin sees all; regular users see only `ACTIVE` products).

### Category Endpoints

#### Get Category by ID
```http
GET /category/{id}
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Electronic items"
}
```

#### Create Category
```http
POST /category
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic items"
}
```

**Response**: `201 CREATED`

#### Get All Categories
```http
GET /category
```

**Response**: `200 OK` — Returns a list of all categories.

### Search Endpoint

#### Search Products
```http
POST /search
Content-Type: application/json

{
  "query": "Galaxy phone",
  "pageSize": 4,
  "pageNumber": 0,
  "sortParameters": [
    {
      "sortCriteria": "price",
      "sortOrder": "ASCENDING"
    },
    {
      "sortCriteria": "id",
      "sortOrder": "DESCENDING"
    }
  ]
}
```

**Response**: `200 OK` — Returns paginated and sorted results.

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

Run specific test class:

```bash
./mvnw test -Dtest=ProductControllerTest
```

The project includes comprehensive tests for:
- Controllers (ProductController, CategoryController, SearchController)
- Services (StorageProductService, StorageCategoryService, StorageSearchService, FakeStoreProductService)
- Clients (FakeStoreApiClient)
- Models, DTOs, Configurations, and Exception handling

## 📁 Project Structure

```
ProductCatalogService/
├── src/
│   ├── main/
│   │   ├── java/org/ecommerce/productcatalogservice/
│   │   │   ├── clients/          # External API clients (FakeStore)
│   │   │   ├── configurations/   # Redis & RestTemplate configs
│   │   │   ├── controllers/      # REST controllers
│   │   │   ├── dtos/             # Data Transfer Objects
│   │   │   ├── exceptions/       # Global exception handler
│   │   │   ├── models/           # JPA entities (Product, Category, etc.)
│   │   │   ├── repositories/     # Spring Data JPA repositories
│   │   │   └── services/         # Business logic & service interfaces
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Unit tests (mirrors main structure)
├── .github/workflows/            # CI/CD pipeline
├── .mvn/wrapper/                 # Maven wrapper files
├── mvnw                          # Maven wrapper script (Unix)
├── mvnw.cmd                      # Maven wrapper script (Windows)
├── pom.xml                       # Maven dependencies
└── README.md
```

## 🔐 Role-Based Access

This service implements role-based product visibility:

- **Admin users**: Can view all products regardless of state
- **Regular users**: Can only view products with `ACTIVE` state
- User roles are fetched from the **UserAuthenticationService** via REST call

## 🌐 Service Discovery

The service registers with Netflix Eureka Server running at `http://localhost:8761/eureka/` for service discovery in a microservices architecture. The `RestTemplate` is `@LoadBalanced` for client-side load balancing.

## 📊 Database Schema

The application automatically creates/updates the database schema using Hibernate DDL with the following main entities:

- **Product**: Stores product details (name, description, image, price, state) with a ManyToOne relationship to Category
- **Category**: Defines product categories with a OneToMany relationship to Products
- **BaseModel**: Abstract base entity with `id`, `createdAt`, `lastUpdatedAt`, and `state` (audited via JPA Auditing)
- **State**: Enum — `ACTIVE`, `INACTIVE`, `DELETED`, `OUT_OF_STOCK`

## 🐛 Troubleshooting

### Common Issues

**Port 8082 already in use:**
```bash
# Change the port in application.properties
server.port=8083
```

**MySQL connection refused:**
- Ensure MySQL is running
- Verify database credentials
- Check if the database exists

**Eureka registration fails:**
- Ensure Eureka Server is running on port 8761
- Or disable Eureka by setting: `eureka.client.enabled=false`

**Redis connection refused:**
- Ensure Redis is running on port 6379
- Or exclude Redis auto-config for local dev

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of an e-commerce microservices ecosystem.

## 📧 Contact

**Author**: Kaustubh Ajgaonkar  
**Email**: kaustubhajgaonkar43@gmail.com  
**GitHub**: [@kaustubh43](https://github.com/kaustubh43)

## 🔄 CI/CD

This project uses GitHub Actions for continuous integration. The workflow includes:
- Building the project with Maven
- Running all unit tests
- Triggered on push and pull requests to `main`

---

**Note**: This is a microservice designed to work within a larger e-commerce ecosystem. Ensure all dependent services (Eureka Server, Redis, UserAuthenticationService) are running for full functionality.
