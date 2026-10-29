# spring-boot-thymeleaf-midtrans

[![Java CI with Maven](https://github.com/hendisantika/spring-boot-thymeleaf-midtrans/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-boot-thymeleaf-midtrans/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot)

A sample Spring Boot + Thymeleaf web application showing how to integrate the
[Midtrans](https://midtrans.com) payment gateway using the official
[Midtrans Java library](https://github.com/Midtrans/midtrans-java). It covers **Snap**, **Core API**
(credit card and GoPay) and **Iris** (payouts).

## Screenshots

### Home

![Home page](img/home.png)

### Snap Demo

Choose which payment methods to enable and the Snap type (pop-up or redirect), then check out.

![Snap demo](img/snap.png)

### Core API Demo

Charge directly via the Core API using a credit card or GoPay.

![Core API demo](img/core-api.png)

### Mobile SDK Demo

Sandbox testing cards and the backend endpoint used by the Midtrans Android SDK sample app.

![Mobile SDK demo](img/mobile-sdk.png)

### JVM Workshop Checkout

![JVM Workshop checkout](img/workshop.png)

> The Iris Disbursement pages call the Midtrans Iris API on load, so they need valid sandbox keys to render.

## Tech Stack

| Component        | Version                     |
|------------------|-----------------------------|
| Java (JDK)       | 25                          |
| Spring Boot      | 4.1.x                       |
| Maven            | 3.10.x (via Maven Wrapper)  |
| Thymeleaf        | + Layout Dialect 4.0.1      |
| Midtrans Java    | 3.2.2                       |
| Database         | PostgreSQL 18               |
| Lombok           | managed by Spring Boot      |

## Prerequisites

- JDK 25 (e.g. [Eclipse Temurin](https://adoptium.net/))
- Docker (optional, for running PostgreSQL via `compose.yml`)
- A Midtrans sandbox account to obtain a **Server Key** and **Client Key**

## Getting Started

### 1. Clone the repository

```shell
git clone https://github.com/hendisantika/spring-boot-thymeleaf-midtrans.git
cd spring-boot-thymeleaf-midtrans
```

### 2. Start PostgreSQL

```shell
docker compose up -d
```

This starts PostgreSQL 18 with the `midtrans_db` database, which already matches the datasource in
`src/main/resources/application.yml`. To use a different database, override it with environment variables:

```shell
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_db
export SPRING_DATASOURCE_USERNAME=your_user
export SPRING_DATASOURCE_PASSWORD=your_password
```

### 3. Configure Midtrans keys

```shell
export MIDTRANS_SERVER_KEY=your-sandbox-server-key
export MIDTRANS_CLIENT_KEY=your-sandbox-client-key
```

### 4. Run the application

```shell
./mvnw spring-boot:run
```

Open <http://localhost:8080> in your browser.

### Build & test

```shell
./mvnw clean package
```

## Endpoints

| Area      | Method | Path                                                                   |
|-----------|--------|------------------------------------------------------------------------|
| Home      | GET    | `/`, `/mobile-sdk`, `/api/core-api`, `/api/checkout`                   |
| Snap      | GET    | `/snap`, `/workshop`                                                   |
| Snap      | POST   | `/snap/check-out`, `/checkout`                                         |
| Core API  | POST   | `/cards/charge`, `/gopay/charge`                                       |
| Core API  | POST   | `/transactions/status`, `/approve`, `/cancel`, `/expire`               |
| Core API  | POST   | `/notification`, `/notif/append1`, `/notif/append2`, `/notif/override1`, `/notif/override2` |
| Iris      | GET    | `/iris/ping`, `/iris/index`, `/iris/payouts`                           |
| Iris      | POST   | `/iris/payouts/create`, `/approve`, `/reject`, `/details`, `/iris/notifications` |

## Continuous Integration

GitHub Actions (`.github/workflows/maven.yml`) builds and tests the project on JDK 25 (Temurin) for every push and
pull request to `main`. On pushes to `main`, it also submits the Maven dependency graph to GitHub.

## Dependency Updates

Dependabot (`.github/dependabot.yml`) keeps the project up to date:

- **Maven**: daily; Spring Boot updates are grouped together, and other minor/patch bumps are grouped in one PR
- **GitHub Actions**: weekly, grouped into one PR
- **Docker Compose**: weekly, for the PostgreSQL image in `compose.yml`

## Author

**Hendi Santika**

- GitHub: [@hendisantika](https://github.com/hendisantika)
