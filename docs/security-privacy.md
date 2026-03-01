# Segurança e Privacidade

## Diretrizes adotadas

- Isolamento por usuário via Clerk (`clerkId`)
- Não expor chaves sensíveis no frontend
- Uso de HTTPS em ambientes públicos
- Sanitização de logs para não registrar segredos

## Recomendações para self-host

- Defina segredos apenas em `.env` local (não versionar)
- Use rotação periódica de chaves (`OPENROUTER`, `R2`, `SENDGRID`)
- Restrinja CORS para domínios confiáveis
- Faça backup periódico do PostgreSQL

## Privacidade em features de IA

- Enviar ao provedor externo apenas o mínimo necessário
- Evitar persistir prompt completo quando não for necessário
- Informar claramente ao usuário quando estiver em modo demo
