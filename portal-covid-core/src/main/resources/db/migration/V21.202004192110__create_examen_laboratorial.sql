create table covid19.examen_laboratorial
(
	id serial PRIMARY KEY,
	id_paciente integer references covid19.paciente(id) not null,
	id_usuario integer references covid19admin.usuario(id) not null,
	fecha_prevista_toma_muestra_laboratorial timestamp without time zone not null,
	identificador_externo varchar(50),
	resultado_diagnostico varchar(50),
	fecha_resultado_diagnostico timestamp without time zone,
	estado varchar(50) not null,
	fecha_notificacion_toma_muestra_laboratorial timestamp without time zone,
	fecha_notificacion_resultado timestamp without time zone
);