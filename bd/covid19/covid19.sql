CREATE DATABASE covid19;

CREATE SCHEMA covid19;

CREATE TABLE covid19.registro
(
	id serial primary key,
	codigo_verificacion character varying(50) not null, --codigo verificacion para envio SMS
	estado character varying(255) not null, --pre-registro, registro
	fecha_creacion timestamp(0) without time zone not null,
	fecha_ultima_modificacion timestamp(0) without time zone,
	tipo_inicio character varying(255) not null, --para saber a partir de donde se origino
	tipo_registro character varying(255) not null
);

CREATE TABLE covid19.registro_formulario
(
	id serial primary key,
	id_registro integer not null REFERENCES covid19.registro(id),
	registro_formulario_acompanante boolean not null,
	nombre character varying(255) not null,
	estado  character varying(255) not null, --si se completo todo o no
	fecha_creacion timestamp(0) without time zone not null,
	fecha_ultima_modificacion timestamp(0) without time zone
);

CREATE TABLE covid19.form_seccion_datos_basicos_ingresopais
(
	id serial primary key,
	id_registro_formulario integer not null REFERENCES covid19.registro_formulario(id),
	nombre character varying(255) not null,
	apellido character varying(255) not null,
	pais_nacionalidad character varying(255),  --tabla paises en frontend
	ciudad_nacimiento character varying(255),
	tipo_documento varchar(50) not null,
	numero_documento varchar(50) not null,
	fecha_nacimiento date,
	sexo character varying(1),
	numero_celular character varying(255) not null,
	numero_celular_verificado character varying(50) not null, --no verificado, enviado, verificado
	numero_telefono character varying(50),
	correo_electronico character varying(255),
	direccion_domicilio character varying(255),
	residente_paraguay boolean,
	pais_emisor_documento character varying(255),
	id_usuario integer,
	contrasenha varchar(50) not null
);

CREATE TABLE covid19.form_seccion_datos_clinicos_ingresopais
(
	id serial PRIMARY KEY,
	id_registro_formulario integer not null REFERENCES covid19.registro_formulario(id),
	medio_transporte character varying(255), --(aereo/terrestre/otro-especificar)
	tipo_empresa_transporte character varying(255), --puede colocar otros
	transporte_no_asiento character varying(255),
	fecha_partida date,
	fechahora_llegada timestamp,
	pais_origen character varying(255),
	ciudad_origen character varying(255),
	ocupacion character varying(50) not null,
	paises_circulacion character varying(255), --paises donde visito ultimos 14 dias
	sintomas_fiebre boolean not null default false,
	sintomas_tos boolean not null default false,
	sintomas_dificultad_respirar boolean not null default false,
	sintomas_dolor_garganta boolean not null default false,
	sintomas_otro character varying(255),
	declaracion_agreement boolean not null default false,
	id_usuario integer
);