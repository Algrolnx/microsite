# README BY AI
# Microservices Architecture: Django, Redis, PostgreSQL, and Java Worker

This project is a Proof of Concept (PoC) demonstrating an **Event-Driven Microservices Architecture**. It showcases how a fast web framework (Django) can offload heavy background tasks to a heterogeneous worker (Java) using a message broker (Redis) and a relational database (PostgreSQL) for state persistence.

## 🏗️ Architecture Overview

The system consists of three main components running in Docker containers:

1.  **API Gateway (Django REST Framework)**
    *   Receives incoming HTTP requests.
    *   Creates a `TaskRequest` record in PostgreSQL with a `PENDING` status.
    *   Publishes the task ID and payload to a Redis queue.
    *   Returns a `202 Accepted` response instantly, never blocking the main thread.
2.  **Message Broker (Redis)**
    *   Acts as a reliable, in-memory queue (`task_queue`) to buffer tasks between the API and the worker.
3.  **Background Worker (Java/JDBC)**
    *   Continuously listens to the Redis queue using a blocking pop (`brpop`).
    *   Picks up tasks, simulates heavy processing (e.g., report generation).
    *   Connects directly to PostgreSQL via JDBC to update the task status to `SUCCESS`.

## 🚀 How to Run Locally

### Prerequisites
*   [Docker](https://www.docker.com/) and Docker Compose installed.

### Start the Services
1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd microsite
   ```
2. Build and start the containers in the background:
   ```bash
   docker compose up -d --build
   ```
3. Apply database migrations (required on first run):
   ```bash
   docker compose exec api python manage.py migrate
   ```

## 🧪 Testing the Pipeline

Send a POST request to the API to trigger a task:

```bash
curl -X POST http://localhost:8000/api/tasks/process/ \
     -H "Content-Type: application/json" \
     -d '{"message": "Generate monthly report"}'
```

Watch the worker process the task in real-time:
```bash
docker compose logs -f worker
```

## 🛠️ Technology Stack
*   **Backend API:** Python, Django, Django REST Framework
*   **Worker:** Java 17, Maven, Jedis, PostgreSQL JDBC
*   **Broker:** Redis
*   **Database:** PostgreSQL 15
*   **DevOps:** Docker, Docker Compose, GitHub Actions (CI)
