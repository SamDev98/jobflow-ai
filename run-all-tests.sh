#!/usr/bin/env bash
set -euo pipefail

# Cores para o output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "🚀 Iniciando fluxo de testes completo para JobFlow AI..."

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 1. Testes do Backend
echo -e "\n${GREEN}==> Executando testes do Backend (JUnit)...${NC}"
cd "$ROOT_DIR/backend"
if ./mvnw test; then
    echo -e "${GREEN}✅ Backend OK!${NC}"
else
    echo -e "${RED}❌ Falha nos testes do Backend!${NC}"
    exit 1
fi

# 2. Testes do Frontend
echo -e "\n${GREEN}==> Executando testes do Frontend (Vitest)...${NC}"
cd "$ROOT_DIR/frontend"
if npm test; then
    echo -e "${GREEN}✅ Frontend OK!${NC}"
else
    echo -e "${RED}❌ Falha nos testes do Frontend!${NC}"
    exit 1
fi

# 3. Verificação de Saúde (Opcional - se houver algo local rodando)
echo -e "\n${GREEN}==> Verificando Actuator Health...${NC}"
HEALTH_STATUS=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"UP"')
if [ "$HEALTH_STATUS" == '"status":"UP"' ]; then
    echo -e "${GREEN}✅ Actuator local está UP!${NC}"
else
    echo -e "${RED}⚠️ Actuator local não encontrado ou DOWN (ignorar se backend não estiver rodando).${NC}"
fi

echo -e "\n${GREEN}✨ Fluxo de testes concluído!${NC}"
