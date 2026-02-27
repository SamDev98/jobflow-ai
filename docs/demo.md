# Demo (Frontend-only)

A demo pública roda **apenas frontend** com dados mockados.

## Objetivo

Mostrar UX e funcionalidades principais sem custo operacional de backend online.

## Configuração local da demo

```bash
cd frontend
cp .env.example .env
```

Defina no `.env`:

```env
VITE_DEMO_MODE=true
VITE_API_URL=http://localhost:8080
VITE_CLERK_PUBLISHABLE_KEY=pk_test_placeholder
```

> Em `DEMO_MODE=true`, a aplicação ignora backend e Clerk para navegação principal.

## Build de demo

```bash
cd frontend
npm install
npm run build
npm run preview
```

## Deploy no Vercel

Variáveis recomendadas:

- `VITE_DEMO_MODE=true`
- `VITE_API_URL=https://unused.local`
- `VITE_CLERK_PUBLISHABLE_KEY=pk_test_placeholder`

## Limitações intencionais

- Sem persistência real entre sessões.
- Sem autenticação real no fluxo demo.
- Respostas de IA são simuladas para portfolio.
