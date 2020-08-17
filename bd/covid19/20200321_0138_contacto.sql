CREATE TABLE covid19.contacto
(
	id bigserial not null
	constraint contacto_id_pk
            primary key,
	nombres varchar(100) not null,
	apellidos varchar(100) not null,
	nro_documento varchar(20) not null,
	telefono varchar(20),
	domicilio varchar(255),
	fecha_ultimo_contacto date,
	tipo varchar(100),
	timestamp_creacion timestamp not null,
	id_paciente bigint not null
        constraint contacto_paciente_id_fk
            references covid19.paciente
);

create unique index contacto_id_index
	on covid19.contacto (id);
