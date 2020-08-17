alter table covid19.diagnostico_accion add column id_paciente integer references covid19.paciente(id);

alter table covid19.diagnostico_accion alter valor type varchar(512);

INSERT INTO covid19admin.configuracion (id_configuracion, estado, nombre_variable, valor_variable) VALUES (16, 'ACTIVO', 'covid19.agendados.lastId', '0');

alter table covid19.registro_ubicacion add column actividad_identificada varchar(50);

alter table covid19.registro_ubicacion add column tipo_evento varchar(50);