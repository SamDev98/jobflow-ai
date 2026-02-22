# DEPLOY_GUIDE — JobFlow AI

Guia dos passos manuais necessários para colocar o projeto em produção.
Estratégia: **Fly.io** (backend) + **Vercel** (frontend) + **Supabase** (PostgreSQL) + **Upstash** (Redis) + **GitHub Actions** (CI/CD).

---

## 1. GitHub — Criar repositório e fazer push

```bash
# Na raiz do projeto
cd /caminho/para/jobflow-ai
git init
git add .
git commit -m "feat: initial JobFlow AI scaffold"

# Criar repo no GitHub (via CLI ou UI)
gh repo create jobflow-ai --public --source=. --push

# Ou manualmente
git remote add origin https://github.com/<seu-usuario>/jobflow-ai.git
git push -u origin main
```

---

## 2. Supabase — PostgreSQL gerenciado

1. Acesse https://supabase.com → **New project**
2. Escolha: nome `jobflow`, região **South America (São Paulo)**
3. Anote a senha do banco gerada automaticamente
4. Em **Settings → Database → Connection String → (Mode: Transaction)**
   - Copie a string no formato: `postgresql://postgres.<ref>:<senha>@aws-0-sa-east-1.pooler.supabase.com:6543/postgres`
5. Adicione `?sslmode=require` ao final da string
6. Esta será sua `DATABASE_URL` (sem `jdbc:`; o backend adiciona automaticamente no profile `prod`)

> O Flyway irá criar as tabelas automaticamente no primeiro deploy.

---

## 3. Upstash — Redis gerenciado com TLS

1. Acesse https://upstash.com → **Create Database**
2. Tipo: **Redis**, Região: **Brazil (South America)**
3. Em **Details → REST URL / Endpoint**
   - Vá em **Connect** → copie a URL no formato `rediss://:<senha>@<host>:<porta>`
4. Esta será sua `REDIS_URL`

---

## 4. Fly.io — Deploy do backend

### 4.1 Instalar flyctl

```bash
brew install flyctl  # macOS
# ou: curl -L https://fly.io/install.sh | sh
```

### 4.2 Login e criar app

```bash
flyctl auth login

cd jobflow-ai/backend
flyctl launch --no-deploy --name jobflow-api --region gru
# Se perguntar sobre fly.toml existente: responda "Y" para usar o existente
```

### 4.3 Definir secrets (variáveis de produção)

```bash
flyctl secrets set \
  POSTGRES_HOST="aws-0-sa-east-1.pooler.supabase.com" \
  POSTGRES_PORT="6543" \
  POSTGRES_DB="postgres" \
  POSTGRES_USER="postgres.<ref>" \
  POSTGRES_PASSWORD="<senha-supabase>" \
   DATABASE_URL="postgresql://postgres.<ref>:<senha>@aws-0-sa-east-1.pooler.supabase.com:6543/postgres?sslmode=require" \
  REDIS_URL="rediss://:<senha-upstash>@<host-upstash>:<porta>" \
  CLERK_JWKS_URI="https://<clerk-domain>/.well-known/jwks.json" \
  CLERK_ISSUER="https://<clerk-domain>" \
  OPENROUTER_API_KEY="<sua-chave>" \
  R2_ACCESS_KEY_ID="<r2-key>" \
  R2_SECRET_ACCESS_KEY="<r2-secret>" \
  R2_BUCKET_NAME="jobflow-files" \
  R2_ENDPOINT="https://<account-id>.r2.cloudflarestorage.com" \
  R2_PUBLIC_URL="https://pub-<id>.r2.dev" \
  SENDGRID_API_KEY="<sendgrid-key>" \
  MAIL_FROM="noreply@<seu-dominio>" \
  TELEGRAM_BOT_TOKEN="<bot-token>"
```

> Evite definir `SPRING_DATASOURCE_URL`/`SPRING_FLYWAY_URL` com formato `jdbc:postgresql://user:pass@host...`.
> O backend em `prod` já monta o JDBC corretamente a partir de `POSTGRES_HOST/PORT/DB/USER/PASSWORD`.

> Para obter `CLERK_JWKS_URI` e `CLERK_ISSUER`: no Clerk Dashboard → **API Keys** → copie o **Frontend API URL** e adicione `/.well-known/jwks.json`.

### 4.4 Primeiro deploy

```bash
flyctl deploy --remote-only
```

Aguarde e confirme com:

```bash
flyctl status
flyctl logs
# Endpoint estará em: https://jobflow-api.fly.dev
```

### 4.5 CORS — adicionar o domínio Vercel

Após o deploy do frontend (passo 5), volte ao `SecurityConfig.java` e confirme que a regex
`*.vercel.app` cobre seu domínio. Se usar domínio customizado, adicione-o à lista de `allowedOriginPatterns`.

---

## 5. Vercel — Deploy do frontend

### 5.1 Via UI (recomendado na primeira vez)

1. Acesse https://vercel.com → **Add New Project**
2. Importe o repositório GitHub `jobflow-ai`
3. **Root Directory**: `frontend`
4. **Framework Preset**: Vite
5. Adicione as variáveis de ambiente:
   | Variável | Valor |
   |----------|-------|
   | `VITE_CLERK_PUBLISHABLE_KEY` | `pk_live_...` (da Clerk → Production instance) |
   | `VITE_API_URL` | `https://jobflow-api.fly.dev` |
6. Clique **Deploy**

### 5.2 Obter IDs para GitHub Actions

Após o deploy inicial:

```bash
npm i -g vercel
vercel login
vercel link  # dentro da pasta frontend/
# Anote: VERCEL_ORG_ID e VERCEL_PROJECT_ID (exibidos ou em .vercel/project.json)
```

---

## 6. GitHub Secrets — Configurar CI/CD

No repositório GitHub → **Settings → Secrets and variables → Actions → New repository secret**:

| Secret                       | Valor                                           |
| ---------------------------- | ----------------------------------------------- |
| `FLY_API_TOKEN`              | `flyctl auth token`                             |
| `VERCEL_TOKEN`               | Vercel → **Account Settings → Tokens** → Create |
| `VERCEL_ORG_ID`              | Do passo 5.2                                    |
| `VERCEL_PROJECT_ID`          | Do passo 5.2                                    |
| `VITE_CLERK_PUBLISHABLE_KEY` | Chave de produção do Clerk                      |
| `VITE_API_URL`               | `https://jobflow-api.fly.dev`                   |

Com isso, cada `git push` para `main`:

- Se mudou `backend/**` → testa + deploya no Fly.io
- Se mudou `frontend/**` → builda + deploya no Vercel

---

## 7. Clerk — Configurar instância

> **Atenção:** O Clerk não permite domínios `*.fly.dev` em instâncias de **Produção**. Para testar o deploy sem um domínio próprio, continue usando a instância de **Desenvolvimento**.

1. No Clerk Dashboard:
   - Se for usar domínio customizado: Vá em **Production** e siga as instruções.
   - Se for usar os domínios gratuitos: Use a instância **Development**.
2. Em **Settings → Allowed Origins**:
   - Adicione `https://jobflow-api.fly.dev`
   - Adicione `https://<seu-projeto>.vercel.app`
3. Atualize as chaves (`VITE_CLERK_PUBLISHABLE_KEY`) no frontend e GitHub Secrets se elas mudarem.

> A instância de desenvolvimento do Clerk funciona perfeitamente para testes em "staging" (Vercel/Fly.io), mas exibirá um aviso de "Development Mode" no rodapé.

---

## 8. Validar o deploy completo

```bash
# Backend health
curl https://jobflow-api.fly.dev/actuator/health

# Swagger em produção
open https://jobflow-api.fly.dev/swagger-ui.html

# Frontend
open https://<seu-projeto>.vercel.app
```

Fluxo de teste:

1. Crie conta via Clerk no frontend de produção
2. Abra DevTools → Console: `await window.__clerk.session.getToken()`
3. Use o token no Swagger para testar endpoints
4. Arraste cards no Kanban → verifique PATCH no banco Supabase

---

## Custos estimados (tier gratuito)

| Serviço    | Plano gratuito                   |
| ---------- | -------------------------------- |
| Fly.io     | 3 VMs shared, 160GB banda        |
| Supabase   | 500MB banco, 2GB storage         |
| Upstash    | 10.000 req/dia, 256MB            |
| Vercel     | Unlimited para projetos pessoais |
| Clerk      | 10.000 MAUs                      |
| OpenRouter | Gemini 2.0 Flash grátis          |

> Para zero custo: mantenha `auto_stop_machines = true` no `fly.toml` (a VM dorme após inatividade).
