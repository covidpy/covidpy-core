create table covid19.notificacion
(
    id                 bigserial        not null
        constraint notificacion_pk
            primary key,
    id_paciente        bigint          not null
        constraint notificacion_paciente_id_fk
            references covid19.paciente,
    fecha_notificacion   timestamp not null,
    mensaje     varchar(250),
    visto boolean  not null
);

create unique index notificacion_id_uindex
    on covid19.notificacion (id);
