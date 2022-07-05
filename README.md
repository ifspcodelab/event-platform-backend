# Event Platform Back-end
Back-end for IFSP SPO Event Platform

## How to run

1. Clone this repo

2. Install Open JDK 17

3. Install PostgreSQL 14

4. Set env vars:

- `DATASOURCE_URL`: PostgreSQL Datasource URL ex: `jdbc:postgresql://localhost:5432/event_platform`
- `DATASOURCE_USERNAME`: PostgreSQL database username
- `DATASOURCE_PASSWORD`: PostgreSQL database password

5. Run with gradle wrapper:
```
./gradlew bootRun
```

## Tech Stack

- Java application - JDK 17
- Spring Boot 2.7.1
- PostgreSQL 14