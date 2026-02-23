# Mapeamento de Requisitos — JobFlow AI

Este documento detalha os requisitos funcionais e não funcionais do sistema JobFlow AI, abrangendo tanto o ecossistema Backend quanto o Frontend.

## 1. Requisitos Funcionais (RF)

### 1.1. Gestão de Candidaturas (Pipeline)

- **RF-001**: O sistema deve permitir criar, visualizar, editar e excluir candidaturas a vagas de emprego (_Job Applications_).
- **RF-002**: Deve haver uma visualização em Kanban para organizar as candidaturas por estágios (Applied, Interviewing, Offer, etc.).
- **RF-003**: O usuário deve poder mover candidaturas entre colunas via Drag-and-Drop, atualizando o status no banco de dados.
- **RF-004**: Cada candidatura deve suportar soft delete (campo `deletedAt`).

### 1.2. Otimização de Currículos (AI)

- **RF-005**: O sistema deve permitir o upload de currículos (.docx) ou texto puro.
- **RF-006**: O sistema deve integrar-se com LLMs (via OpenRouter) para comparar o currículo com uma descrição de vaga e sugerir melhorias.
- **RF-007**: Deve exibir um _Diff Viewer_ no frontend comparando a versão original e a otimizada.

### 1.3. Ferramentas de Sucesso (Tools)

- **RF-008**: **Pesquisa Salarial**: O sistema deve pesquisar faixas salariais para cargos e tecnologias específicas usando IA.
- **RF-009**: **Preparação para Entrevista**: O sistema deve gerar simulações de perguntas e respostas personalizadas para cada vaga/empresa.
- **RF-010**: **Quiz de Entrevista**: Interface interativa no frontend para praticar perguntas típicas de processos seletivos.

### 1.4. Usuário e Autenticação

- **RF-011**: O sistema deve autenticar usuários via Clerk (OAuth/Social Login).
- **RF-012**: O sistema deve gerenciar perfis de usuários, incluindo preferências e planos (Tier: FREE, PRO).
- **RF-013**: Cada usuário deve ter acesso estritamente aos seus próprios dados (isolamento de tenant via `clerkId`).

### 1.5. Dashboard e Analytics

- **RF-014**: O sistema deve fornecer métricas consolidadas (Total de aplicações, taxas de conversão por estágio).
- **RF-015**: Deve exibir um log de atividades recentes (_History_) de todas as ações importantes realizadas pelo usuário.

---

## 2. Requisitos Não Funcionais (RNF)

### 2.1. Arquitetura e Performance

- **RNF-001**: **Backend**: Java 21 + Spring Boot 3.5.x.
- **RNF-002**: **Frontend**: React 18 + TypeScript + Vite.
- **RNF-003**: **Caching**: Utilizar Redis para cachear respostas de LLM por até 7 dias, reduzindo custos de tokens e tempo de resposta.
- **RNF-004**: **Banco de Dados**: PostgreSQL 16+ hospedado no Supabase.
- **RNF-005**: **Pool de Conexões**: Configurado para funcionar com o PGBouncer do Supabase (Transaction Mode) usando `prepareThreshold=0`.

### 2.2. Segurança e Disponibilidade

- **RNF-006**: **Segurança de JWT**: Validação manual no Backend com cache local de JWKS para evitar ataques de força bruta e reduzir latência.
- **RNF-007**: **Infraestrutura**: Backend hospedado no Fly.io com escalonamento horizontal e Frontend no Vercel.
- **RNF-008**: **Storage**: Uploads de arquivos intermediados via Cloudflare R2 (S3 Compatible).

### 2.3. UX/UI e Manutenibilidade

- **RNF-009**: **Interface Responsiva**: O sistema deve ser totalmente funcional em dispositivos móveis e desktop.
- **RNF-010**: **Acessibilidade**: Implementar suporte a Dark Mode.
- **RNF-011**: **Código Limpo**: Utilizar migrations (Flyway) para qualquer alteração de esquema de banco de dados.
- **RNF-012**: **Automação**: Todo push na branch `main` deve disparar o deploy automático via GitHub Actions.

---

## 3. Tecnologias Utilizadas

| Camada                     | Tecnologia                       |
| :------------------------- | :------------------------------- |
| **Linguagem Backend**      | Java 21                          |
| **Framework Web**          | Spring Boot 3.5                  |
| **Auth Provider**          | Clerk                            |
| **Banco de Dados**         | PostgreSQL (Supabase)            |
| **AI Orchestration**       | OpenRouter (Feign Client)        |
| **Cache / Mensageria**     | Redis                            |
| **Interface**              | React + Tailwind CSS + Shadcn UI |
| **Gestão de Estado/Cache** | TanStack Query                   |
| **CI/CD**                  | GitHub Actions                   |
| **Infra Cloud**            | Fly.io (API) e Vercel (UI)       |
