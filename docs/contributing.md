# Contribuição

## Fluxo recomendado

1. Crie branch de feature
2. Faça mudanças pequenas e focadas
3. Rode testes locais
4. Abra PR com resumo objetivo

## Padrões

- Evitar mudanças amplas sem necessidade
- Preservar contratos de API e tipos
- Atualizar docs junto com mudanças funcionais

## Checklist de PR

- [ ] Build do frontend
- [ ] Testes backend/frontend relevantes
- [ ] Sem segredos em arquivos versionados
- [ ] Documentação atualizada

## Estratégia de commits

Para manter histórico legível, prefira commits semânticos:

- `feat(frontend): ...`
- `fix(backend): ...`
- `docs: ...`
- `chore(docker): ...`

Para limpeza antes de release OSS, use branch dedicada com squash/rebase interativo.
