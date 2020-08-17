alter table covid19.paciente add fin_previsto_aislamiento timestamp without time zone;

alter table covid19.paciente add fin_aislamiento timestamp without time zone;

alter table covid19.paciente add clasificacion_paciente varchar(255) default 'A';

create table covid19.historico_clinico
(
    id             serial    not null
        constraint historico_clinico_pkey
            primary key,
    id_paciente    integer   not null
        constraint historico_clinico_id_paciente_fkey
            references covid19.paciente(id),
    id_usuario     integer   not null
        constraint historico_clinico_id_usuario_fkey
            references covid19admin.usuario(id),
    id_registro    integer
        constraint historico_clinico_id_registro_fkey
            references covid19.registro(id),
    fecha_registro timestamp not null
);

create table covid19.historico_clinico_datos
(
    id                   serial  not null
        constraint historico_clinico_datos_pkey
            primary key,
    id_historico_clinico integer not null
        constraint historico_clinico_datos_id_historico_clinico_fkey
            references covid19.historico_clinico(id),
    nombre_dato          varchar(255),
    valor_dato           varchar(255)
);