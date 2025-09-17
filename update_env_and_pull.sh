#!/bin/bash

APP_DIR="/home/seba/planificador"
ENV_FILE="$APP_DIR/.env"

cd "$APP_DIR"

# Traer últimos cambios de GitHub
git reset --hard
git pull origin main

# Cargar variables de entorno desde .env
if [ -f "$ENV_FILE" ]; then
    export $(grep -v '^#' "$ENV_FILE" | xargs)
    echo "Variables de entorno cargadas desde $ENV_FILE"
else
    echo "No se encontró $ENV_FILE"
fi