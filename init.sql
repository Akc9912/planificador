CREATE SCHEMA IF NOT EXISTS planner;
USE planner;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN','USER') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    cambiarPass BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

-- Tabla para herencia JOINED: Admin
CREATE TABLE admin (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- Tabla para herencia JOINED: NormalUser
CREATE TABLE normal_user (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE materias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    estado ENUM('CURSANDO','APROBADA','SIN_CURSAR','CURSADA_APROBADA','DEBO_FINAL') NOT NULL DEFAULT 'SIN_CURSAR',
    color CHAR(7) NOT NULL, -- color en hex
    promocionable BOOLEAN NOT NULL DEFAULT FALSE,
    nota_promocion DECIMAL(5,2) NULL,
    calificacion DECIMAL(5,2) NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    INDEX idx_materias_usuario (id_usuario),
    INDEX idx_materias_estado (estado)
) ENGINE=InnoDB;

CREATE TABLE horario_por_materia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_materia INT NOT NULL,
    dia ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    FOREIGN KEY (id_materia) REFERENCES materias(id),
    INDEX idx_hpm_materia (id_materia),
    INDEX idx_hpm_dia (dia)
) ENGINE=InnoDB;


CREATE TABLE evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    color CHAR(7) NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    INDEX idx_evento_usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE horario_por_evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inicio DATETIME NOT NULL,
    fin DATETIME NOT NULL,
    id_evento INT NOT NULL,
    FOREIGN KEY (id_evento) REFERENCES evento(id),
    INDEX idx_hpe_evento (id_evento),
    INDEX idx_hpe_inicio (inicio),
    INDEX idx_hpe_fin (fin)
) ENGINE=InnoDB;

-- Tabla intermedia para vincular eventos con materias (muchos a muchos)
CREATE TABLE evento_materia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_evento INT NOT NULL,
    id_materia INT NOT NULL,
    FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE recordatorio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    color CHAR(7) NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    INDEX idx_recordatorio_usuario (id_usuario)
) ENGINE=InnoDB;

-- Tabla intermedia para vincular recordatorios con materias (muchos a muchos)

CREATE TABLE recordatorio_materia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_recordatorio INT NOT NULL,
    id_materia INT NOT NULL,
    UNIQUE KEY uq_recordatorio_materia (id_recordatorio, id_materia),
    FOREIGN KEY (id_recordatorio) REFERENCES recordatorio(id) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE user_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    theme ENUM('DARK','LIGHT','SYSTEM') NOT NULL DEFAULT 'SYSTEM',
    notificaciones BOOLEAN NOT NULL DEFAULT TRUE,
    formato_hora BOOLEAN NOT NULL DEFAULT FALSE,
    inicio_planner INT NOT NULL DEFAULT 8,
    fin_planner INT NOT NULL DEFAULT 20,
    primer_dia ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL DEFAULT 'LUNES',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    INDEX idx_settings_usuario (id_usuario)
) ENGINE=InnoDB;


CREATE TABLE auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    tabla_afectada VARCHAR(50) NOT NULL,
    id_registro INT,
    valor_anterior TEXT,
    valor_nuevo TEXT,
    detalle TEXT,
    ip VARCHAR(45),
    user_agent VARCHAR(255),
    updated_by INT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (updated_by) REFERENCES usuarios(id),
    INDEX idx_auditoria_usuario (id_usuario)
) ENGINE=InnoDB;

-- Índices adicionales sugeridos
CREATE INDEX idx_user_settings_theme ON user_settings(theme);
CREATE INDEX idx_user_settings_notificaciones ON user_settings(notificaciones);
CREATE INDEX idx_materias_calificacion ON materias(calificacion);
CREATE INDEX idx_auditoria_accion ON auditoria(accion);
CREATE INDEX idx_auditoria_tabla_afectada ON auditoria(tabla_afectada);


-- soporte (reporte de problemas y sugerencia)

create table soporte(
    id int AUTO_INCREMENT PRIMARY KEY,
    id_usuario int not null,
    tipo enum('REPORTE_PROBLEMA','SUGERENCIA')
    titulo varchar(50) not null,
    mensaje text not null,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
) ENGINE=InnoDB;



