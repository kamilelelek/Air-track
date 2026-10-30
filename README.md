# Air Track

**Author:** Kamil Lelek

Air Track is a web application that monitors air quality across Poland. It collects particulate matter readings (PM2.5 and PM10) from public monitoring stations and shows them on an interactive map, with every station rated by its current air quality level.

This repository contains the whole application, split into three parts: a data collector, a REST API and a React frontend. The project is still under active development, so changes may occur between versions.

## Key Features

- **Automated Data Collection:** A dedicated collector service fetches the latest readings from the OpenAQ v3 API every 30 minutes. It keeps only the stations that measure PM2.5 or PM10, drops readings older than 48 hours and skips measurements that are already stored, so the database stays clean and free of duplicates.
- **Rate-Limit Aware Client:** Requests to OpenAQ are throttled, and HTTP 429 responses are retried after the delay given in the `X-Ratelimit-Reset` header. A failure at a single station is logged and the collection moves on to the next one.
- **Air Quality Rating:** Every station gets a quality level (good, moderate, poor) based on GIOŚ index bands. When a station measures both pollutants, it is rated by the worse of PM2.5 and PM10.
- **REST API:** Endpoints for the station list with latest readings, station details, paginated measurement history and the latest measurements across all stations.
- **Interactive Map:** A Leaflet map on the CARTO Positron basemap, with color-coded markers and a sidebar showing station details and measurements.
- **Serverless Scheduling:** In production the collector runs as a one-shot job on GitHub Actions. Locally it runs as a long-lived service with Spring's scheduler.
- **Versioned Database Schema:** All schema changes are managed with Flyway migrations.

## Architecture

| Module | Description | Deployment |
|---|---|---|
| `collector-service` | Fetches data from OpenAQ and saves it to the database | GitHub Actions (cron, every 30 min) |
| `api-service` | REST API that serves stations and measurements | Render (Docker web service) |
| `air-track-FE` | React single-page application with the map | Render (static site) |
| Database | PostgreSQL | Neon (production), Docker Compose (local) |

## Technology Stack

- **Core:** Java 17, Spring Boot 4.1
- **Build Tool:** Maven (Maven Wrapper included)
- **Database:** PostgreSQL, Flyway migrations
- **Data Access:** Spring Data JPA (Hibernate)
- **External API:** OpenAQ v3, consumed with Spring `RestClient`
- **API:** REST (Spring WebMVC)
- **Boilerplate:** Lombok
- **Frontend:** React 19, TypeScript, Vite, Leaflet (react-leaflet), Axios
- **Infrastructure:** Docker, Docker Compose, GitHub Actions, Render, Neon
- **Testing:** JUnit 5, Mockito

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/locations` | All stations with their latest PM readings and air quality level |
| GET | `/api/v1/locations/{id}` | Details of a single station |
| GET | `/api/v1/locations/{id}/measurements` | Paginated measurement history of a station (default 20 per page, newest first) |
| GET | `/api/v1/measurements/latest` | Latest measurements across all stations |

## Getting Started

To run the application locally, you need a PostgreSQL database, an OpenAQ API key and a CARTO basemap API key.

### 1. Environment Configuration

Both backend services read their configuration from environment variables:

```
DB_URL=jdbc:postgresql://localhost:5433/airtrack
DB_USER=your_postgres_user
DB_PASSWORD=your_postgres_password

OPENAQ_API_KEY=your_openaq_api_key
```

`OPENAQ_API_KEY` is only needed by `collector-service`. To use a hosted database such as Neon, set `DB_URL` to its JDBC address with `?sslmode=require` at the end.

Create an `air-track-FE/.env` file for the frontend:

```
VITE_CARTO_API_KEY=your_carto_api_key
```

### 2. Starting the Database

A local PostgreSQL instance is defined in `compose.yaml` in the repository root. It is exposed on port `5433`. Put `DB_USER` and `DB_PASSWORD` in a `.env` file next to `compose.yaml`, then run:

```
docker compose up -d
```

### 3. Running the Application

Start the services in this order. The collector applies the Flyway migrations, and the API validates the schema on startup.

```
cd collector-service
./mvnw spring-boot:run
```

```
cd api-service
./mvnw spring-boot:run
```

```
cd air-track-FE
npm install
npm run dev
```

The API listens on port `8085`, and the frontend is available at `http://localhost:5173`. The Vite dev server forwards `/api/v1` requests to the API.

To run the collector once and exit, as it runs on GitHub Actions, pass `--collector.run-once=true`.

### 4. Running Tests

Run the test suite of each backend service from its directory:

```
./mvnw test
```
