create table covid19admin.login_automatico
(
    id                 bigserial                                       not null,
    id_usuario         bigint                                          not null,
    token              varchar(255)                                    not null,
    estado             varchar(25) default 'activo'::character varying not null,
    timestamp_creacion timestamp                                       not null
);

INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (12, 'ACTIVO', 'covid19.onetimetoken.seconds_duration', '300');
