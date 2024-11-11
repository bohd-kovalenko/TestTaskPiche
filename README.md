# Banking Service

## Overview
This project is a REST API that simulates a bancing application that supports essential banking operations such as creating accounts, making deposits and withdrawals, and transferring funds between accounts.

## Technologies
Built using Java 21 and Spring (Web with virtual threads enabled for better concurrency, and Spring Data JPA). Gradle was used as the build and packaging tool. 
PostgreSQL was used as the primary data storage solution. The application was tested using JUnit 5, Mockito, and Testcontainers for comprehensive integration tests. 
Metrics are gathered with Prometheus, enabling future visualization with Grafana for detailed performance monitoring. Flyway was used for managing database migrations.

## Features (Each represented with REST endpoint)
### 1. Account Management
- **Create a New Account**: Create a new user account with an initial balance.
- **Get Account Details**: Retrieve account information using an account number.
- **List All Accounts**: View all available accounts in the system.

### 2. Account Transactions
- **Deposit Funds**: Deposit money into an account. Current balance is not shown because it is supposed that endpoint may be called 
- **Withdraw Funds**: Withdraw money from an account. Returns current account balance.
- **Transfer Funds**: Transfer money between two accounts. Returns current account balance of the transaction initiator.
Each transaction is splitted on to a debit and credit and stored in db for convenient data analysis.

## Installation and Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/banking-solution.git
   cd banking-solution
2. **Make sure You have installed docker and docker-compose on Your machine. If so, run the command**
   ```bash
   docker-compose up -d

## Data reviewing
1. Swagger UI is available under http://localhost:8080/api/swagger-ui/index.html#/
2. Prometheus UI is available under http://localhost:9090/
3. Test coverage is available after build of project under the path /build/reports/index.html . Project build may be performed from CLI by running from the root project folder the command 
```bash
./gradlew build
