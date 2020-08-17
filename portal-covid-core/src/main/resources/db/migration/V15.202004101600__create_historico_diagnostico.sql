create table covid19.historico_diagnostico
(
	id serial PRIMARY KEY,
	id_paciente integer references covid19.paciente(id) not null,
	id_usuario integer references covid19admin.usuario(id) not null,
	resultado_diagnostico varchar(50) not null,
	fecha_diagnostico timestamp without time zone not null,
	fin_previsto_aislamiento timestamp without time zone,
	fecha_modificacion timestamp without time zone not null
);