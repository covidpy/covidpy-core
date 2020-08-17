alter table covid19.form_seccion_datos_basicos add inicio_aislamiento timestamp without time zone;

alter table covid19.form_seccion_datos_basicos add fecha_prevista_toma_muestra_laboratorial timestamp without time zone;

alter table covid19.historico_clinico_datos add tipo_clinico_dato varchar(255);

create table covid19.paciente_estado_salud
(
	id bigserial not null primary key,
	id_paciente integer not null REFERENCES covid19.paciente(id),
	id_historico_clinico integer not null REFERENCES covid19.historico_clinico(id),
	clasificacion_paciente character varying(255) not null,
	ultimo_reporte_fecha timestamp without time zone not null,
	ultimo_reporte_tipo character varying(255) not null, --auto-reporte, reporte-medico 
	ultimo_registro_tipo character varying(255) not null, --misma info que registro_tipo
	sintomas_fiebre boolean not null default false,
	sintomas_fiebre_ultima_medicion character varying(255),
	sintomas_tos boolean not null default false,
	sintomas_dificultad_respirar boolean not null default false,
	sintomas_dif_respirar_dolor_garganta boolean not null default false,
	sintomas_dif_respirar_cansancio_caminar boolean not null default false,
	sintomas_dif_respirar_falta_aire boolean not null default false,
	sintomas_dif_respirar_rinorrea boolean not null default false,
	sintomas_dif_respirar_congestion_nasal boolean not null default false,
	sintomas_diarrea boolean,
	sintomas_otros character varying(255)

);

INSERT INTO covid19admin.rol (id,activo,descripcion,nombre) VALUES (4,true,'Tecnico Salud','Tecnico Salud');