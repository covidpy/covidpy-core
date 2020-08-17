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
    id_configuracion bigint       not null
        constraint configuracion_pkey
            primary key,
    estado           varchar(255),
    nombre_variable  varchar(255) not null,
    valor_variable   varchar(255) not null
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

create table covid19.registro
(
    id                        serial       not null
        constraint registro_pkey
            primary key,
    codigo_verificacion       varchar(50)  not null,
    estado                    varchar(255) not null,
    fecha_creacion            timestamp(0) not null,
    fecha_ultima_modificacion timestamp(0),
    responsable_registro      varchar(255) not null,
    tipo_registro             varchar(255) not null,
    id_usuario                integer      not null
        constraint registro_id_usuario_fkey
            references covid19admin.usuario
);

create table covid19.paciente
(
    id                 bigserial not null
        constraint paciente_pk
            primary key,
    id_usuario         integer
        constraint paciente_usuario_id_fk
            references covid19admin.usuario
            on delete restrict,
    inicio_seguimiento timestamp,
    fin_seguimiento    timestamp,
    inicio_aislamiento timestamp
);

create unique index paciente_id_uindex
    on covid19.paciente (id);

create unique index paciente_id_usuario_uindex
    on covid19.paciente (id_usuario);

create table covid19.registro_formulario
(
    id                              serial       not null
        constraint registro_formulario_pkey
            primary key,
    id_registro                     integer      not null
        constraint registro_formulario_id_registro_fkey
            references covid19.registro,
    id_paciente                     integer
        constraint registro_formulario_id_paciente_fkey
            references covid19.paciente,
    registro_formulario_acompanante boolean      not null,
    nombre                          varchar(255) not null,
    estado                          varchar(255) not null,
    fecha_creacion                  timestamp(0) not null,
    fecha_ultima_modificacion       timestamp(0)
);

create table covid19.form_seccion_datos_basicos
(
    id                        serial       not null
        constraint form_seccion_datos_basicos_pkey
            primary key,
    id_registro_formulario    integer      not null
        constraint form_seccion_datos_basicos_id_registro_formulario_fkey
            references covid19.registro_formulario,
    nombre                    varchar(255) not null,
    apellido                  varchar(255) not null,
    pais_nacionalidad         varchar(255),
    ciudad_nacimiento         varchar(255),
    tipo_documento            varchar(50)  not null,
    numero_documento          varchar(50)  not null,
    fecha_nacimiento          date,
    sexo                      varchar(1),
    numero_celular            varchar(255) not null,
    numero_celular_verificado varchar(50)  not null,
    numero_telefono           varchar(50),
    correo_electronico        varchar(255),
    direccion_domicilio       varchar(255),
    residente_paraguay        boolean,
    pais_emisor_documento     varchar(255),
    id_usuario                integer,
    contrasenha               varchar(50),
    ciudad_domicilio          varchar(255),
    departamento_domicilio    varchar(255)
);

create table covid19.form_seccion_datos_clinicos
(
    id                                         serial                not null
        constraint form_seccion_datos_clinicos_pkey
            primary key,
    id_registro_formulario                     integer               not null
        constraint form_seccion_datos_clinicos_id_registro_formulario_fkey
            references covid19.registro_formulario,
    ocupacion                                  varchar(50),
    enfermedad_base_cardiopatia_cronica        boolean,
    enfermedad_base_pulmonar_cronico           boolean,
    enfermedad_base_asma                       boolean,
    enfermedad_base_diabetes                   boolean,
    enfermedad_base_renal_cronico              boolean,
    enfermedad_base_inmunodeprimido            boolean,
    enfermedad_base_neurologica                boolean,
    enfermedad_base_sindromedown               boolean,
    enfermedad_base_obesidad                   boolean,
    enfermedad_base_hepatica_grave             boolean,
    evaluacion_riesgo_usomedicamento           boolean,
    evaluacion_riesgo_medicamento              varchar(255),
    evaluacion_riesgo_vive_solo                boolean,
    evaluacion_riesgo_tiene_habitacion_propria boolean,
    sintomas_fiebre                            boolean,
    sintomas_fiebre_valor                      varchar(255),
    sintomas_tos                               boolean,
    sintomas_dificultad_respirar               boolean,
    sintomas_dif_respirar_dolor_garganta       boolean,
    sintomas_dif_respirar_cansancio_caminar    boolean,
    sintomas_dif_respirar_falta_aire           boolean,
    sintomas_dif_respirar_rinorrea             boolean,
    sintomas_dif_respirar_congestion_nasal     boolean,
    sintomas_diarrea                           boolean,
    sintomas_otros                             varchar(255),
    declaracion_agreement                      boolean default false not null,
    id_usuario                                 integer
);

create table covid19.form_seccion_datos_ingresopais
(
    id                      serial      not null
        constraint form_seccion_datos_ingresopais_pkey
            primary key,
    id_registro_formulario  integer     not null
        constraint form_seccion_datos_ingresopais_id_registro_formulario_fkey
            references covid19.registro_formulario,
    tipo_empresa_transporte varchar(255),
    transporte_no_asiento   varchar(255),
    fecha_partida           date,
    fecha_llegada           timestamp,
    pais_origen             varchar(255),
    ciudad_origen           varchar(255),
    ocupacion               varchar(50) not null,
    paises_circulacion      varchar(255),
    id_usuario              integer
);

create table covid19.paciente_datos_personales_basicos
(
    id                        serial       not null
        constraint paciente_datos_personales_basicos_pkey
            primary key,
    id_paciente               integer      not null
        constraint paciente_datos_personales_basicos_id_paciente_fkey
            references covid19.paciente,
    nombre                    varchar(255) not null,
    apellido                  varchar(255) not null,
    pais_nacionalidad         varchar(255),
    ciudad_nacimiento         varchar(255),
    pais_emisor_documento     varchar(255),
    tipo_documento            varchar(50)  not null,
    numero_documento          varchar(50)  not null,
    fecha_nacimiento          date,
    sexo                      varchar(1),
    numero_celular            varchar(255) not null,
    numero_celular_verificado varchar(50)  not null,
    numero_telefono           varchar(49),
    correo_electronico        varchar(255),
    direccion_domicilio       varchar(255),
    residente_paraguay        boolean,
    ciudad_domicilio          varchar(255),
    departamento_domicilio    varchar(255)
);

create table covid19.historico_clinico
(
    id             serial    not null
        constraint historico_clinico_pkey
            primary key,
    id_paciente    integer   not null
        constraint historico_clinico_id_paciente_fkey
            references covid19.paciente,
    id_usuario     integer   not null
        constraint historico_clinico_id_usuario_fkey
            references covid19.paciente,
    id_registro    integer
        constraint historico_clinico_id_registro_fkey
            references covid19.registro,
    fecha_registro timestamp not null
);

create table covid19.paciente_estado_salud
(
    id                                      bigserial             not null
        constraint paciente_estado_salud_pkey
            primary key,
    id_paciente                             integer               not null
        constraint paciente_estado_salud_id_paciente_fkey
            references covid19.paciente,
    id_historico_clinico                    integer               not null
        constraint paciente_estado_salud_id_historico_clinico_fkey
            references covid19.historico_clinico,
    clasificacion_paciente                  varchar(255)          not null,
    ultimo_reporte_fecha                    timestamp             not null,
    ultimo_reporte_tipo                     varchar(255)          not null,
    ultimo_registro_tipo                    varchar(255)          not null,
    sintomas_fiebre                         boolean default false not null,
    sintomas_fiebre_ultima_medicion         varchar(255),
    sintomas_tos                            boolean default false not null,
    sintomas_dificultad_respirar            boolean default false not null,
    sintomas_dif_respirar_dolor_garganta    boolean default false not null,
    sintomas_dif_respirar_cansancio_caminar boolean default false not null,
    sintomas_dif_respirar_falta_aire        boolean default false not null,
    sintomas_dif_respirar_rinorrea          boolean default false not null,
    sintomas_dif_respirar_congestion_nasal  boolean default false not null,
    sintomas_diarrea                        boolean,
    sintomas_otros                          varchar(255)
);

create table covid19.historico_clinico_datos
(
    id                   serial  not null
        constraint historico_clinico_datos_pkey
            primary key,
    id_historico_clinico integer not null
        constraint historico_clinico_datos_id_historico_clinico_fkey
            references covid19.historico_clinico,
    nombre_dato          varchar(255),
    valor_dato           varchar(255)
);

create table covid19.censo_contacto
(
    id                    bigserial    not null
        constraint contacto_id_pk
            primary key,
    nombres               varchar(100) not null,
    apellidos             varchar(100) not null,
    nro_documento         varchar(20),
    telefono              varchar(20),
    domicilio             varchar(255),
    fecha_ultimo_contacto date,
    tipo                  varchar(100),
    timestamp_creacion    timestamp    not null,
    id_paciente           bigint       not null
        constraint contacto_paciente_id_fk
            references covid19.paciente
);

create unique index contacto_id_index
    on covid19.censo_contacto (id);

create table covid19.registro_ubicacion
(
    id                 bigserial        not null
        constraint registro_ubicacion_pk
            primary key,
    id_paciente        integer          not null
        constraint registro_ubicacion_paciente_id_fk
            references covid19.paciente,
    lat_reportado      double precision not null,
    long_reportado     double precision not null,
    timestamp_creacion timestamp        not null,
    lat_dispositivo    double precision,
    long_dispositivo   double precision
);

create unique index registro_ubicacion_id_uindex
    on covid19.registro_ubicacion (id);

create table covid19.notificacion
(
    id                 bigserial not null
        constraint notificacion_pk
            primary key,
    id_paciente        bigint    not null
        constraint notificacion_paciente_id_fk
            references covid19.paciente,
    fecha_notificacion timestamp not null,
    mensaje            varchar(250),
    visto              boolean   not null
);

create unique index notificacion_id_uindex
    on covid19.notificacion (id);

create table covid19admin.login_automatico
(
    id                 bigserial                                       not null,
    id_usuario         bigint                                          not null,
    token              varchar(255)                                    not null,
    estado             varchar(25) default 'activo'::character varying not null,
    timestamp_creacion timestamp                                       not null
);

create table covid19.pais
(
    id    varchar(64) not null
        constraint pais_pkey
            primary key,
    value varchar(64) not null
);


INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (5, 'ACTIVO', 'url.host', 'https://admin.paraguay.gov.py/');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (7, 'ACTIVO', 'adminportalpy.url', 'https://localhost:4200/#/');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (8, 'ACTIVO', 'adminportalpy.redis.host', 'localhost');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (9, 'ACTIVO', 'adminportalpy.redis.port', '6379');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (10, 'ACTIVO', 'adminportalpy.redis.timetolive', '60');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (11, 'ACTIVO', 'adminportalpy.redis.env', 'desarrollo');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (6, 'INACTIVO', 'adminportalpyA.url', 'https://admin.paraguay.gov.py/');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (2, 'ACTIVO', 'mail.smtp.port', '465');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (1, 'ACTIVO', 'mail.smtp.host', '');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (4, 'ACTIVO', 'mail.smtp.pass', '');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (3, 'ACTIVO', 'mail.smtp.user', '');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (12, 'ACTIVO', 'covid19.onetimetoken.seconds_duration', '300');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (13, 'ACTIVO', 'covid19.dominioapp', 'http://192.168.100.5:4200/');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (14, 'ACTIVO', 'covid19.nombre_app', 'Covid19 - Paraguay');
INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (15, 'ACTIVO', 'covid19.path_cambiar_clave', 'login/cambiar-clave');
