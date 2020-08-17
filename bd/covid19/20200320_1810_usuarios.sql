create schema covid19admin;

create table covid19admin.usuario
(
    id              bigserial    not null
        constraint usuario_pkey
            primary key,
    activo          boolean,
    username        varchar(255) not null,
    nombre          varchar(255) not null,
    apellido        varchar(255) not null,
    cedula          varchar(255) not null
        constraint cedula_uk
            unique,
    email           varchar(255)
        constraint email_usuario_uq
            unique,
    password        varchar(255),
    telefono        varchar(255),
    token_reset     varchar(250),
    estado_contacto boolean,
    celular         varchar(100)
);

create table covid19admin.configuracion
(
    id_configuracion bigint                not null
        constraint configuracion_pkey
            primary key,
    estado           varchar(255),
    nombre_variable  varchar(255)          not null,
    valor_variable   varchar(255)          not null
);

create table covid19admin.rol
(
    id          bigserial    not null
        constraint rol_pkey
            primary key,
    activo      boolean,
    descripcion varchar(255),
    nombre      varchar(255) not null
);

create unique index uk_nombre_rol
    on covid19admin.rol (nombre)
    where (activo = true);

create table covid19admin.permiso
(
    id          bigserial    not null
        constraint permiso_pkey
            primary key,
    descripcion varchar(255),
    nombre      varchar(255) not null
        constraint uk_nombre_permiso
            unique
);

create table covid19admin.rol_permiso
(
    rol_id     bigint not null
        constraint rol_permiso_rol_fk
            references covid19admin.rol,
    permiso_id bigint not null
        constraint rol_permiso_permiso_fk
            references covid19admin.permiso,
    constraint rol_permiso_pkey
        primary key (rol_id, permiso_id)
);

create table covid19admin.rol_usuario
(
    usuario_id bigint not null
        constraint rol_usuario_usuario_fk
            references covid19admin.usuario,
    rol_id     bigint not null
        constraint rol_usuario_fk
            references covid19admin.rol,
    constraint rol_usuario_pkey
        primary key (usuario_id, rol_id)
);

