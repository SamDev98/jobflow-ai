# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**JobFlow AI** — Multi-tenant SaaS para gestão de candidaturas de emprego.
Stack: Java 21 + Spring Boot 3.2 (backend) / React 18 + TypeScript + Vite (frontend) / PostgreSQL + Redis.

```
jobflow-ai/
├── backend/    # Spring Boot API (porta 8080)
└── frontend/   # React SPA (porta 5173)
```

## Commands

### Infra (rodar sempre primeiro)
```bash
docker compose up -d          # Sobe PostgreSQL 16 + Redis 7 + pgAdmin
docker compose down           # Para os containers
docker compose logs -f        # Logs em tempo real
# pgAdmin: http://localhost:5050 (admin@jobflow.dev / admin)
```

### Backend
```bash
cd backend
./mvnw spring-boot:run                        # Roda com perfil dev
./mvnw test                                   # Todos os testes
./mvnw test -Dtest=ApplicationServiceTest     # Teste específico
./mvnw verify                                 # Testes + JaCoCo coverage
./mvnw clean package -DskipTests             # Build JAR
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Frontend
```bash
cd frontend
npm install           # Instalar dependências
npm run dev           # Dev server (http://localhost:5173)
npm run build         # Build de produção
npm run lint          # ESLint
npm run preview       # Preview do build de produção
```

## Architecture

### Backend — Fluxo de uma requisição
```
HTTP Request
  → ClerkJwtFilter       # Valida JWT via JWKS, popula SecurityContext
  → Controller           # Recebe DTO, chama service
  → Service              # Lógica de negócio, usa repositories
  → Repository           # Spring Data JPA → PostgreSQL
  → Response DTO         # Mapeado manualmente (sem MapStruct)
```

**Pacotes principais:**
- `config/` — SecurityConfig (Spring Security + JWKS), RedisConfig, FeignConfig, OpenApiConfig
- `entity/` — JPA entities com Lombok. `JobApplication` (não `Application` — conflito com java.lang). Soft delete via `deletedAt`.
- `repository/` — Spring Data JPA. Queries nativas quando necessário. Sempre filtrar `deleted_at IS NULL`.
- `service/` — Toda lógica de negócio aqui. `LLMOrchestrator` centraliza chamadas ao OpenRouter com cache Redis (TTL 7 dias). `StorageService` abstrai Cloudflare R2.
- `client/` — Feign clients para OpenRouter.
- `exception/` — `GlobalExceptionHandler` via `@RestControllerAdvice` trata todas as exceções.
- `util/PromptTemplates.java` — Todos os prompts LLM como constantes.

**Auth:** Clerk JWT validado via JWKS endpoint público. `ClerkJwtFilter` extrai `sub` (clerk user ID) e popula `SecurityContext`. `SecurityUtils.getCurrentUserId()` recupera em qualquer ponto.

**Banco:** Flyway migrations em `src/main/resources/db/migration/`. Nunca editar migrations já aplicadas — criar nova versão.

### Frontend — Fluxo de dados
```
Page Component
  → Custom Hook (useApplications, useResume...)
  → TanStack Query (cache + refetch)
  → lib/api.ts (axios com interceptor JWT Clerk)
  → Backend API
```

**Estrutura:**
- `lib/api.ts` — instância axios com `baseURL=VITE_API_URL`. Interceptor injeta token Clerk em cada request.
- `types/index.ts` — tipos TypeScript compartilhados (Application, Resume, Stage, etc.)
- `hooks/` — Toda lógica de servidor via TanStack Query. Um hook por recurso.
- `components/kanban/` — `KanbanBoard` usa `@dnd-kit`. Drag entre colunas dispara `PATCH /applications/:id`.
- `components/resume/` — Upload `.docx` + textarea JD → `POST /resumes/optimize` → `DiffViewer`.
- `pages/` — Composição de components. Sem lógica de servidor nos pages.

## Key Design Decisions

- `JobApplication` em vez de `Application` para evitar conflito com `java.lang`
- Feign client para OpenRouter — permite mock fácil em testes
- `StorageService` como abstração sobre S3/R2 — troca de provider sem afetar services
- Redis TTL 7 dias para respostas LLM — reduz custo de tokens significativamente
- Paginação com `Pageable` do Spring em todos os endpoints de listagem
- Frontend não tem estado global (sem Redux) — TanStack Query é a fonte da verdade
