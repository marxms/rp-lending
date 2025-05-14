# RP Lending API

## Project Description
**RP Lending API** is an application developed in **Java** using the **Micronaut** framework. The goal of the project is to provide a REST API to manage digital wallets, transactions, and financial transfers. The application uses a PostgreSQL database and is containerized with Docker.

---

## Build Requirements
- **Java 21** (JDK 21)
- **Maven 3.9+**
- **Docker** and **Docker Compose**

---

## Main Endpoints
### POST /rp-lending-api/wallet: Create a new wallet.
### GET /rp-lending-api/wallet/{walletKey}: Retrieve a wallet by key.
### GET /rp-lending-api/wallet/{walletKey}/history: Retrieve a wallet's balance history.
### POST /rp-lending-api/wallet/transaction: Register a transaction.
### POST /rp-lending-api/wallet/transfer: Perform a transfer.
<hr></hr>
Docker Containers  
PostgreSQL: Main database.  
Adminer: Database admin interface. (PORT: 8085)  
RP Lending API: Main application.  
<hr></hr>

## Main Libraries Used
- **Micronaut Framework**: Project foundation for building REST APIs.
- **Hibernate JPA**: Persistence management and object-relational mapping.
- **Testcontainers**: Integration testing with Docker containers.
- **MapStruct**: Object mapping.
- **PostgreSQL Driver**: Database connectivity for PostgreSQL.
- **Logback**: Logging configuration.
- **Lombok**: Reduces boilerplate code.

---

## Build Tutorial

### 1. Clone the Repository
```bash
git clone https://github.com/marxms/rp-lending-api.git
cd rp-lending-api
```

### 2. Set Up the Environment
Make sure the following requirements are installed:

- Java 21: Set the `JAVA_HOME` environment variable to point to JDK 21.
- Maven: Verify installation with `mvn -v`.
- Docker: Verify installation with `docker -v`.

### 3. Run the Application
Start the necessary services (PostgreSQL, Adminer, and the application) with the command:

```bash
docker-compose up --build
```

