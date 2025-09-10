create schema planner;
use planner;

create table usuarios(
	id int auto_increment primary key ,
    email varchar(100),
    pass varchar(100),
    nombre varchar(100),
    apellido varchar(100),
    rol enum ('ADMIN', 'USUARIO'),
	activo bool,
    cambiarPass bool
);

create table materias(
	id int auto_increment primary key,
    titulo varchar(100),
    activo bool,
    estado enum ('CURSANDO', 'SIN_CURSAR', 'DEBO_FINAL','APROBADA')
);

create table materias_por_usuario(
	id_usuario int,
    id_materia int,
    primary key (id_usuario, id_materia),
    foreign key (id_usuario) references usuarios(id),
    foreign key (id_materia) references materias(id)
);

create table horario_por_materia(
    id int auto_increment primary key,
    id_materia int,
    dia enum ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'),
    hora_inicio time,
    hora_fin time,
    foreign key (id_materia) references materias(id)
);