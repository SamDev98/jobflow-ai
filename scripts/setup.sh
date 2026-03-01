#!/usr/bin/env bash
set -euo pipefail

MODE="infra"
RUN_UP="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --mode)
      MODE="${2:-infra}"
      shift 2
      ;;
    --up)
      RUN_UP="true"
      shift
      ;;
    *)
      echo "Parâmetro inválido: $1"
      echo "Uso: bash scripts/setup.sh --mode [infra|selfhost|demo] [--up]"
      exit 1
      ;;
  esac
done

if [[ ! "$MODE" =~ ^(infra|selfhost|demo)$ ]]; then
  echo "Modo inválido: $MODE"
  exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker não encontrado. Instale Docker Desktop."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose não encontrado."
  exit 1
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "[setup] root: $ROOT_DIR"

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "[setup] .env criado a partir de .env.example"
else
  echo "[setup] .env já existe"
fi

if [[ ! -f frontend/.env ]]; then
  cp frontend/.env.example frontend/.env
  echo "[setup] frontend/.env criado"
else
  echo "[setup] frontend/.env já existe"
fi

if [[ "$MODE" == "demo" ]]; then
  if grep -q '^VITE_DEMO_MODE=' frontend/.env; then
    sed -i.bak 's/^VITE_DEMO_MODE=.*/VITE_DEMO_MODE=true/' frontend/.env && rm -f frontend/.env.bak
  else
    echo 'VITE_DEMO_MODE=true' >> frontend/.env
  fi
  echo "[setup] modo demo habilitado no frontend/.env"
fi

if [[ "$MODE" == "selfhost" ]]; then
  if grep -q '^VITE_DEMO_MODE=' frontend/.env; then
    sed -i.bak 's/^VITE_DEMO_MODE=.*/VITE_DEMO_MODE=false/' frontend/.env && rm -f frontend/.env.bak
  else
    echo 'VITE_DEMO_MODE=false' >> frontend/.env
  fi
  echo "[setup] modo selfhost habilitado no frontend/.env"
fi

if [[ "$RUN_UP" == "true" ]]; then
  case "$MODE" in
    infra)
      echo "[setup] subindo somente infraestrutura (postgres/redis/pgadmin)..."
      docker compose up -d
      ;;
    selfhost)
      echo "[setup] subindo stack completa com profile selfhost..."
      docker compose --profile selfhost up -d --build
      ;;
    demo)
      echo "[setup] subindo infraestrutura para apoiar desenvolvimento local..."
      docker compose up -d
      ;;
  esac

  echo "[setup] status dos serviços:"
  docker compose ps
fi

echo "[setup] concluído"
