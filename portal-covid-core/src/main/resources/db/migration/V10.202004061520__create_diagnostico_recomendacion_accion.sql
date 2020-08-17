create table covid19.diagnostico_recomendacion
(
	id serial PRIMARY KEY,
	id_historico_clinico integer references covid19.historico_clinico(id) not null,
	recomendacion_tipo varchar(255) not null, -- salud_mental, sintomas, motivacional
	recomendacion_valor varchar(1024) not null --contenido de la recomendacion
);


create table covid19.diagnostico_accion
(
	id serial PRIMARY KEY,
	id_historico_clinico integer references covid19.historico_clinico(id), -- puede ser nulo
	id_diagnostico_recomendacion integer references covid19.diagnostico_recomendacion(id), -- puede ser nul pq tendremos examen laboratorial
	tipo_accion  varchar(255) not null, -- tipos accion: clasificacion_paciente, notificar_sms, frecuencia_reporte_salud, frecuencia_reporte_localizacion, notificar_mensaje, notificar_push, notificar_operador_salud
	valor varchar(255) not null, -- eventualmente, en algun momento tendremos q poner un JSON (caso notificar operador salud por ej)
	estado_ejecucion  varchar(10) not null, -- pendiente, ejecutado.
	resultado_ejecucion varchar(255), -- suceso o no
	fechahora_ejecucion timestamp without time zone
);
