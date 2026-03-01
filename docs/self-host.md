# Self-host (Docker)

Este modo sobe o sistema completo localmente com persistência.

## Pré-requisitos

- Docker Desktop
- Docker Compose v2

## Setup rápido

```bash
bash scripts/setup.sh --mode selfhost --up
```

## Serviços

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- pgAdmin: http://localhost:5050

## Persistência

Volumes usados:

- `postgres_data`
- `redis_data`

Evite usar `docker compose down -v` se quiser manter dados.

## Subir e parar manualmente

```bash
docker compose up -d
docker compose --profile selfhost up -d --build

docker compose down
```

## Variáveis de ambiente

Copie e ajuste:

```bash
cp .env.example .env
cp frontend/.env.example frontend/.env
```

Campos essenciais para ambiente real:

- `CLERK_JWKS_URI`
- `CLERK_ISSUER`
- `OPENROUTER_API_KEY`
- `R2_*`

## Testes

```bash
bash run-all-tests.sh
```
