# Spring Guardian UI

Modern dashboard for Spring Guardian reports.

## Run locally

Start the backend on port `8080`, then:

```bash
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

Vite proxies `/api` to `http://localhost:8080` during development.

## Build

```bash
npm run build
```

## Docker

The root `docker-compose.yml` builds this UI and serves it with Nginx on port `3000`.

```bash
docker compose up --build
```

Open:

```text
http://localhost:3000
```
