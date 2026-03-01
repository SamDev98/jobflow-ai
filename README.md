# JobFlow AI

Plataforma para gestão de candidaturas com pipeline Kanban, otimização de currículo, preparação de entrevistas e pesquisa salarial.

## Modos de uso

- **Demo pública (frontend-only):** interface com dados mockados para portfolio.
- **Self-host completo:** frontend + backend + PostgreSQL + Redis via Docker.

## Quickstart

### 1) Demo frontend local (sem backend)

```bash
cd frontend
cp .env.example .env
# ajuste: VITE_DEMO_MODE=true
npm install
npm run dev
```

### 2) Self-host com Docker

```bash
bash scripts/setup.sh --mode selfhost --up
```

App: http://localhost:5173  
API: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

## Documentação

- [Guia de demo](docs/demo.md)
- [Guia self-host](docs/self-host.md)
- [Arquitetura](docs/architecture.md)
- [Segurança e privacidade](docs/security-privacy.md)
- [Contribuição](docs/contributing.md)

## Stack

- Backend: Java 21 + Spring Boot 3.5 + PostgreSQL + Redis
- Frontend: React 18 + TypeScript + Vite + TanStack Query
- Auth: Clerk
- AI: OpenRouter (com fallback demo mockado)

## Objetivo do projeto

Este repositório prioriza:

- demonstração clara de capacidade técnica (portfolio),
- execução previsível em Docker para qualquer pessoa clonar,
- separação entre demo pública e ambiente real self-host.
