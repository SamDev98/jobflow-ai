# HOW TO RUN — JobFlow AI

## Pré-requisitos

| Ferramenta | Versão mínima | Verificar |
|------------|--------------|-----------|
| Java | 21 | `java -version` |
| Maven | 3.9+ | `./mvnw -version` |
| Node.js | 18+ | `node -v` |
| Docker | 24+ | `docker -v` |
| Docker Compose | v2 | `docker compose version` |

---

## 1. Configuração inicial (fazer apenas uma vez)

### 1.1 Copiar o .env
```bash
cd jobflow-ai
cp .env.example .env
# Preencher as chaves (Clerk, OpenRouter, R2, SendGrid, Telegram)
```

### 1.2 Criar o .env do frontend
```bash
cp frontend/.env.example frontend/.env
# Preencher VITE_CLERK_PUBLISHABLE_KEY
```

---

## 2. Subir a infraestrutura (PostgreSQL + Redis)

```bash
# Na raiz do projeto
docker compose up -d

# Verificar se está saudável
docker compose ps
```

| Serviço | URL |
|---------|-----|
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |
| pgAdmin | http://localhost:5050 (admin@jobflow.dev / sam0492@) |

> Para parar: `docker compose down`
> Para apagar os dados: `docker compose down -v`

---

## 3. Rodar o Backend

O Spring Boot não lê `.env` automaticamente — é necessário exportar as variáveis antes:

```bash
# Exportar variáveis do .env para o shell (rodar uma vez por sessão)
export $(grep -v '^#' .env | grep -v '^$' | xargs)

# Entrar na pasta e iniciar
cd backend
./mvnw spring-boot:run
```

**Aguardar a mensagem:**
```
Started JobFlowApplication in X seconds
```

| Endpoint | URL |
|----------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs (JSON) | http://localhost:8080/api-docs |

---

## 4. Rodar o Frontend

```bash
cd frontend
npm install        # Apenas na primeira vez
npm run dev
```

Acesse: **http://localhost:5173**

> O login é via Clerk — faça o cadastro na primeira vez.

---

## 5. Testar o fluxo completo

### 5.1 Autenticação
1. Acesse http://localhost:5173
2. Crie uma conta pelo Clerk (email + senha)
3. Após login, o frontend chama automaticamente `POST /users/sync` para criar o usuário no banco

**Verificar no pgAdmin:**
```sql
SELECT * FROM users;
```

---

### 5.2 Applications CRUD (Kanban)

**Via Swagger UI** (http://localhost:8080/swagger-ui.html):
> Clique em **Authorize** → cole o JWT do Clerk (veja abaixo como obter)

**Obter JWT do Clerk no browser:**
1. Abra o DevTools → Console
2. Execute:
```js
await window.__clerk.session.getToken()
```
3. Copie o token e cole no Swagger

**Criar uma candidatura:**
```bash
curl -X POST http://localhost:8080/applications \
  -H "Authorization: Bearer <seu-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Nubank",
    "role": "Senior Backend Engineer",
    "stage": "APPLIED",
    "jobDescription": "We use Java, Kotlin, Kafka...",
    "techStack": ["Java", "Kafka", "PostgreSQL"]
  }'
```

**Via Frontend:**
- Acesse `/pipeline` e veja o Kanban
- Arraste cards entre colunas para mudar o stage

---

### 5.3 Resume Optimizer

**Via Swagger:**
```
POST /resumes/optimize
  - resume: <arquivo .docx>
  - jd: <texto da vaga>
```

**Via Frontend:**
- Acesse `/tools` → aba **Resume Optimizer**
- Upload de um `.docx` + cole a descrição da vaga
- O resultado mostra: ATS Score + bullets otimizados + links de download

> ⚠️ A conversão para PDF ainda é um placeholder. O endpoint retorna o `.docx` otimizado normalmente.

---

### 5.4 Salary Research

```bash
curl -X POST "http://localhost:8080/salary/research?jobTitle=Backend+Engineer&location=Brazil" \
  -H "Authorization: Bearer <seu-token>"
```

**Via Frontend:** `/tools` → aba **Salary Research**

---

### 5.5 Analytics

```bash
curl http://localhost:8080/analytics/by-stage \
  -H "Authorization: Bearer <seu-token>"
```

Retorna contagem de candidaturas por stage:
```json
{ "APPLIED": 3, "SCREENING": 1, "TECHNICAL": 0, "ONSITE": 0, "OFFER": 0, "REJECTED": 1 }
```

---

## 6. Rodar os testes

```bash
cd backend

# Todos os testes
./mvnw test

# Teste específico
./mvnw test -Dtest=ApplicationServiceTest

# Com relatório de cobertura (JaCoCo)
./mvnw verify
# Relatório em: target/site/jacoco/index.html
```

---

## 7. O que está implementado vs. pendente

### ✅ Funcionando
| Feature | Endpoints |
|---------|-----------|
| Auth via Clerk JWT | Automático |
| Applications CRUD | `GET/POST/PATCH/DELETE /applications` |
| Kanban drag & drop | Frontend `/pipeline` |
| Resume Optimizer (docx) | `POST /resumes/optimize` |
| Salary Research (LLM) | `POST /salary/research` |
| Analytics por stage | `GET /analytics/by-stage` |
| Dashboard | Frontend `/dashboard` |

### ⚠️ Parcialmente implementado
| Feature | Status |
|---------|--------|
| Resume → PDF | Placeholder — arquivo `.pdf` não é gerado, apenas o `.docx` |
| LLM cache (Redis) | Implementado, mas JWKS do Clerk é buscado a cada request (sem cache) |

### ❌ Ainda não implementado (próximos passos)
| Feature | O que falta |
|---------|-------------|
| **Interview Prep** | `InterviewPrepService`, `InterviewPrepRepository`, `InterviewPrepController` |
| **Notificações** | `NotificationService` com `@Scheduled` (follow-up, deadlines via email/Telegram) |
| **PDF real** | Integrar `Apache FOP` ou `LibreOffice headless` no `ResumeOptimizerService` |
| **Perfil do usuário** | Salvar `UserProfile` (anos de exp, tech stack) via Settings |
| **Dockerfile backend** | Containerizar o Spring Boot para deploy no Fly.io |

---

## 8. Troubleshooting

**Backend não conecta no banco:**
```bash
# Verificar se o postgres está up
docker compose ps
# Verificar se as variáveis foram exportadas
echo $POSTGRES_PASSWORD
```

**Erro 401 Unauthorized:**
- O JWT do Clerk expirou — gere um novo pelo Console do browser
- Verifique se `CLERK_JWKS_URI` e `CLERK_ISSUER` estão corretos no `.env`

**Frontend não aparece após login:**
- Verifique se `VITE_CLERK_PUBLISHABLE_KEY` está no `frontend/.env`
- Reinicie o `npm run dev` após alterar o `.env`

**Redis connection refused:**
```bash
docker compose up -d redis
# Verificar se REDIS_PASSWORD no .env bate com o do docker-compose.yml
```
