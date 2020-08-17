alter table covid19.registro_ubicacion add column altitude_accuracy double precision;

alter table covid19.registro_ubicacion add column speed double precision;

alter table covid19.registro_ubicacion add column altitude double precision;

alter table covid19.registro_ubicacion add column accuracy double precision;

alter table covid19.registro_ubicacion add column tipo_registro_ubicacion varchar(50); --(manual, automatico)

alter table covid19.registro_ubicacion add column ip_cliente varchar(50);

alter table covid19.registro_ubicacion add column user_agent varchar(100);