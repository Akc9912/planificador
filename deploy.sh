#!/bin/bash

# =========================
# Configuración
# =========================
PROJECT_DIR="/home/seba/planificador"
JAR_NAME="planificador-0.0.1-SNAPSHOT.jar"
PID_FILE="$PROJECT_DIR/api.pid"

# =========================
# Funciones
# =========================
stop_api() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null; then
            echo "Deteniendo API con PID $PID..."
            kill $PID
            sleep 5
        fi
        rm -f "$PID_FILE"
    fi
}

start_api() {
    echo "Iniciando API..."
    nohup java -jar "$PROJECT_DIR/target/$JAR_NAME" > "$PROJECT_DIR/api.log" 2>&1 &
    echo $! > "$PID_FILE"
    echo "API iniciada con PID $(cat $PID_FILE)"
}

update_code() {
    cd "$PROJECT_DIR" || exit
    echo "Actualizando código desde Git..."
    git reset --hard
    git pull origin main
}

build_project() {
    cd "$PROJECT_DIR" || exit
    echo "Construyendo proyecto..."
    mvn clean package -DskipTests
}

load_env() {
    set -o allexport
    source "$PROJECT_DIR/.env"
    set +o allexport
}

# =========================
# Script principal
# =========================
stop_api
update_code
build_project
load_env
start_api

echo "Despliegue completado."
