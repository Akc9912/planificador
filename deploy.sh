#!/usr/bin/env bash

set -Eeuo pipefail

APP_DIR="${APP_DIR:-/home/seba/planificador}"
BRANCH="${BRANCH:-main}"
PID_FILE="$APP_DIR/api.pid"
LOG_FILE="$APP_DIR/api.log"
ENV_FILE="$APP_DIR/.env"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"
}

fail() {
    log "ERROR: $*"
    exit 1
}

usage() {
    cat <<EOF
Uso: $(basename "$0") [comando]

Comandos:
  deploy   Detiene API, actualiza codigo, compila e inicia (default)
  update   Solo actualiza codigo desde Git
  build    Solo compila proyecto
  start    Solo inicia API
  stop     Solo detiene API
  restart  Reinicia API (stop + start)
  status   Muestra estado actual
  logs     Muestra ultimas lineas del log (usa LOG_LINES=200)

Variables opcionales:
  APP_DIR=/home/seba/planificador
  BRANCH=main
  FORCE_RESET=1         # Permite reset --hard contra origin/
  LOG_LINES=100
EOF
}

ensure_app_dir() {
    [[ -d "$APP_DIR" ]] || fail "No existe APP_DIR: $APP_DIR"
}

resolve_jar_path() {
    local jar_path
    jar_path=$(find "$APP_DIR/target" -maxdepth 1 -type f -name "*.jar" ! -name "original-*.jar" | sort | tail -n 1)
    [[ -n "$jar_path" ]] || fail "No se encontro un JAR en $APP_DIR/target. Ejecuta build primero."
    echo "$jar_path"
}

load_env() {
    [[ -f "$ENV_FILE" ]] || fail "No se encontro archivo .env en $ENV_FILE"
    set -a
    # shellcheck disable=SC1090
    source "$ENV_FILE"
    set +a
    log "Variables de entorno cargadas desde $ENV_FILE"
}

update_code() {
    ensure_app_dir
    cd "$APP_DIR"

    log "Actualizando codigo desde origin/$BRANCH"

    if [[ "${FORCE_RESET:-0}" == "1" ]]; then
        git fetch origin "$BRANCH"
        git reset --hard "origin/$BRANCH"
        log "FORCE_RESET aplicado: reset --hard a origin/$BRANCH"
        return
    fi

    if ! git diff --quiet || ! git diff --cached --quiet; then
        fail "Hay cambios locales en $APP_DIR. Commit/stash antes de actualizar o usa FORCE_RESET=1"
    fi

    git pull --ff-only origin "$BRANCH"
}

build_project() {
    ensure_app_dir
    cd "$APP_DIR"
    load_env
    log "Compilando proyecto"

    if [[ -x "$APP_DIR/mvnw" ]]; then
        "$APP_DIR/mvnw" clean package -DskipTests
    else
        command -v mvn >/dev/null 2>&1 || fail "No se encontro mvn ni mvnw"
        mvn clean package -DskipTests
    fi
}

stop_api() {
    if [[ ! -f "$PID_FILE" ]]; then
        log "No hay PID file. API ya detenida."
        return
    fi

    local pid
    pid=$(cat "$PID_FILE")

    if [[ -z "$pid" ]]; then
        rm -f "$PID_FILE"
        log "PID file vacio. Se elimino $PID_FILE"
        return
    fi

    if kill -0 "$pid" >/dev/null 2>&1; then
        log "Deteniendo API con PID $pid"
        kill "$pid"

        for _ in {1..20}; do
            if ! kill -0 "$pid" >/dev/null 2>&1; then
                break
            fi
            sleep 1
        done

        if kill -0 "$pid" >/dev/null 2>&1; then
            log "La API no se detuvo a tiempo, enviando SIGKILL a $pid"
            kill -9 "$pid"
        fi
    else
        log "El proceso $pid no existe. Se limpiara el PID file."
    fi

    rm -f "$PID_FILE"
    log "API detenida"
}

start_api() {
    ensure_app_dir
    load_env

    if [[ -f "$PID_FILE" ]]; then
        local existing_pid
        existing_pid=$(cat "$PID_FILE")
        if [[ -n "$existing_pid" ]] && kill -0 "$existing_pid" >/dev/null 2>&1; then
            fail "La API ya esta corriendo con PID $existing_pid"
        fi
        rm -f "$PID_FILE"
    fi

    local jar_path
    jar_path=$(resolve_jar_path)

    log "Iniciando API con JAR: $jar_path"
    nohup java -jar "$jar_path" > "$LOG_FILE" 2>&1 &

    local new_pid=$!
    echo "$new_pid" > "$PID_FILE"
    sleep 1

    if kill -0 "$new_pid" >/dev/null 2>&1; then
        log "API iniciada con PID $new_pid"
        log "Log: $LOG_FILE"
    else
        rm -f "$PID_FILE"
        fail "No se pudo iniciar la API. Revisa $LOG_FILE"
    fi
}

status_api() {
    if [[ -f "$PID_FILE" ]]; then
        local pid
        pid=$(cat "$PID_FILE")
        if [[ -n "$pid" ]] && kill -0 "$pid" >/dev/null 2>&1; then
            log "API en ejecucion (PID $pid)"
            return
        fi
        log "PID file existe pero proceso no activo"
        return
    fi
    log "API detenida"
}

show_logs() {
    local lines="${LOG_LINES:-100}"
    [[ -f "$LOG_FILE" ]] || fail "No existe log file: $LOG_FILE"
    tail -n "$lines" "$LOG_FILE"
}

run_deploy() {
    stop_api
    update_code
    build_project
    start_api
    log "Despliegue completado"
}

main() {
    local command="${1:-deploy}"

    case "$command" in
    deploy)
        run_deploy
        ;;
    update)
        update_code
        ;;
    build)
        build_project
        ;;
    start)
        start_api
        ;;
    stop)
        stop_api
        ;;
    restart)
        stop_api
        start_api
        ;;
    status)
        status_api
        ;;
    logs)
        show_logs
        ;;
    -h | --help | help)
        usage
        ;;
    *)
        usage
        fail "Comando no valido: $command"
        ;;
    esac
}

main "$@"
