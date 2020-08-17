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


