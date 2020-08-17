create table covid19.reporte_salud_paciente
(
    id                               bigserial not null
        constraint reporte_salud_paciente_pk
            primary key,
    timestamp_creacion               timestamp not null,
    como_te_sentis                   varchar(255),
    signos_sintomas_descritos        varchar(255),
    id_registro_formulario           bigint    not null
        constraint reporte_salud_paciente_registro_formulario_id_fk
            references covid19.registro_formulario,
    como_te_sentis_con_relacion_ayer varchar(255),
    congestion_nasal                 varchar(255),
    secrecion_nasal                  varchar(255),
    dolor_garganta                   varchar(255),
    tos                              varchar(255),
    percibe_olores                   varchar(255),
    percibe_sabores                  varchar(255),
    dificultad_respirar              varchar(255),
    sentis_fiebre                    varchar(255),
    sintomas_empeoraron              varchar(255),
    sintomas_mejoraron               varchar(255),
    desde_cuando_olores              varchar(255),
    desde_cuando_sabores             varchar(255),
    desde_cuando_fiebre              varchar(255),
    tomaste_temperatura              varchar(255),
    fiebre_ayer                      varchar(255),
    sentis_triste_desanimado         varchar(255),
    sentis_angustia                  varchar(255)
);

create unique index reporte_salud_paciente_id_uindex
    on covid19.reporte_salud_paciente (id);

