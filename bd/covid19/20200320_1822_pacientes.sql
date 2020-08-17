create table covid19.paciente
(
    id         bigserial not null
        constraint paciente_pk
            primary key,
    id_usuario integer
        constraint paciente_usuario_id_fk
            references covid19admin.usuario
            on delete restrict
);

create unique index paciente_id_uindex
    on covid19.paciente (id);

create unique index paciente_id_usuario_uindex
    on covid19.paciente (id_usuario);

