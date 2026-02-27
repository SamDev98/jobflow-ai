# Arquitetura

## Backend

Fluxo:

1. Filtro JWT Clerk valida token
2. Controller recebe DTO
3. Service aplica regra de negócio
4. Repository persiste no PostgreSQL

Princípios:

- Soft delete com `deletedAt`
- Flyway para versionamento de schema
- Redis para cache de respostas LLM

## Frontend

Fluxo:

1. Página renderiza componente
2. Hook usa TanStack Query
3. Camada API decide entre backend real e mock (`DEMO_MODE`)

## Modos operacionais

- **Demo:** frontend com `mockApi`
- **Real:** frontend -> backend Spring -> Postgres/Redis

## Decisões-chave

- Demo desacoplada de backend para reduzir custo de portfolio
- Self-host dockerizado para facilitar clonagem
- Contratos tipados para manter paridade entre `api` e `mockApi`
