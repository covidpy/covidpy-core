--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.22
-- Dumped by pg_dump version 12.2

-- Started on 2020-08-13 11:01:49

--
-- TOC entry 2305 (class 1262 OID 90192)
-- Name: covid19; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE covid19 WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Spanish_Paraguay.1252' LC_CTYPE = 'Spanish_Paraguay.1252';


\connect covid19

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;

--
-- TOC entry 9 (class 2615 OID 90193)
-- Name: audit; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA audit;


--
-- TOC entry 10 (class 2615 OID 90194)
-- Name: covid19; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA covid19;


--
-- TOC entry 11 (class 2615 OID 90195)
-- Name: covid19admin; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA covid19admin;


--
-- TOC entry 2 (class 3079 OID 90197)
-- Name: hstore; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;


--
-- TOC entry 2306 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION hstore; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION hstore IS 'data type for storing sets of (key, value) pairs';


--
-- TOC entry 295 (class 1255 OID 90317)
-- Name: install_logger(text, text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.install_logger(schema_name text, table_name text, log_truncate boolean DEFAULT false) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $$
DECLARE
	fq_table_name text = NULL;
BEGIN
	SELECT schema_name || '.' || table_name INTO fq_table_name; 

	-- check if the table exists and if it doesn't get an error
	EXECUTE 'SELECT ' || quote_literal(fq_table_name) || '::regclass';

	-- drop the trigger if it  already exists and re-create it
	-- this is easier than checking pg_triggers to see if the trigger exists
	EXECUTE 'DROP TRIGGER IF EXISTS auditing_mod_actions ON ' || fq_table_name;
	EXECUTE 'CREATE TRIGGER auditing_mod_actions AFTER INSERT OR UPDATE OR DELETE ' ||
			' ON ' || fq_table_name || ' FOR EACH ROW EXECUTE PROCEDURE logger();';
	
	IF (log_truncate) THEN
		EXECUTE 'DROP TRIGGER IF EXISTS auditing_truncate_actions ON ' || fq_table_name;
		EXECUTE 'CREATE TRIGGER auditing_truncate_actions AFTER TRUNCATE ' ||
				' ON ' || fq_table_name || ' FOR EACH STATEMENT EXECUTE PROCEDURE logger();';
	END IF;

	RETURN TRUE;
END;
$$;


--
-- TOC entry 296 (class 1255 OID 90318)
-- Name: logger(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.logger() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$DECLARE 
	hs_new hstore = NULL;
	hs_old hstore = NULL;
BEGIN
	-- Check that the trigger for the logger should be AFTER and FOR EACH ROW
	IF TG_WHEN = 'BEFORE' THEN
		RAISE EXCEPTION 'Trigger for logger should be AFTER';
	END IF; 

	IF TG_LEVEL = 'STATEMENT' AND TG_OP <> 'TRUNCATE' THEN
		RAISE EXCEPTION 'Trigger for logger should be FOR EACH ROW';
	END IF;

	-- Obtain the hstore versions of NEW and OLD, when appropiate
	IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
		SELECT hstore(new.*) INTO hs_new;
	END IF;

	IF TG_OP = 'DELETE' OR TG_OP = 'UPDATE' THEN
		SELECT hstore(old.*) INTO hs_old;
	END IF;

	INSERT INTO audit.audit_log(log_relid, log_client_addr, log_schema_name, log_table_name, log_operation, log_old_values, log_new_values) 
	WITH t_old(key, value) as (SELECT * FROM each(hs_old)),
	     t_new(key, value) as (SELECT * FROM each(hs_new))
	SELECT TG_RELID, inet_client_addr(), TG_TABLE_SCHEMA, TG_TABLE_NAME, TG_OP, 
		(SELECT hstore(array_agg(key), array_agg(value)) FROM t_old WHERE (key, value) NOT IN (SELECT key, value FROM t_new)),
		(SELECT hstore(array_agg(key), array_agg(value)) FROM t_new WHERE (key, value) NOT IN (SELECT key, value FROM t_old));

	RETURN NULL;
END;
$$;


--
-- TOC entry 175 (class 1259 OID 90319)
-- Name: audit_log; Type: TABLE; Schema: audit; Owner: -
--

CREATE TABLE audit.audit_log (
    log_id integer NOT NULL,
    log_relid oid NOT NULL,
    log_session_user text DEFAULT "session_user"() NOT NULL,
    log_when timestamp with time zone DEFAULT now() NOT NULL,
    log_client_addr inet,
    log_schema_name text,
    log_table_name text,
    log_operation text,
    log_old_values public.hstore,
    log_new_values public.hstore
);


--
-- TOC entry 176 (class 1259 OID 90327)
-- Name: audit_log_log_id_seq; Type: SEQUENCE; Schema: audit; Owner: -
--

CREATE SEQUENCE audit.audit_log_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2307 (class 0 OID 0)
-- Dependencies: 176
-- Name: audit_log_log_id_seq; Type: SEQUENCE OWNED BY; Schema: audit; Owner: -
--

ALTER SEQUENCE audit.audit_log_log_id_seq OWNED BY audit.audit_log.log_id;


--
-- TOC entry 177 (class 1259 OID 90329)
-- Name: censo_contacto; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.censo_contacto (
    id bigint NOT NULL,
    nombres character varying(100) NOT NULL,
    apellidos character varying(100) NOT NULL,
    nro_documento character varying(20),
    telefono character varying(20),
    domicilio character varying(255),
    fecha_ultimo_contacto date,
    tipo character varying(100),
    timestamp_creacion timestamp without time zone NOT NULL,
    id_paciente bigint NOT NULL,
    creado_por bigint NOT NULL,
    modificado_por bigint,
    fecha_modificacion timestamp without time zone
);


--
-- TOC entry 178 (class 1259 OID 90335)
-- Name: censo_contacto_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.censo_contacto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2308 (class 0 OID 0)
-- Dependencies: 178
-- Name: censo_contacto_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.censo_contacto_id_seq OWNED BY covid19.censo_contacto.id;


--
-- TOC entry 179 (class 1259 OID 90337)
-- Name: diagnostico_accion; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.diagnostico_accion (
    id integer NOT NULL,
    id_historico_clinico integer,
    id_diagnostico_recomendacion integer,
    tipo_accion character varying(255) NOT NULL,
    valor character varying(512) NOT NULL,
    estado_ejecucion character varying(10) NOT NULL,
    resultado_ejecucion character varying(255),
    fechahora_ejecucion timestamp without time zone,
    id_paciente integer
);


--
-- TOC entry 180 (class 1259 OID 90343)
-- Name: diagnostico_accion_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.diagnostico_accion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2309 (class 0 OID 0)
-- Dependencies: 180
-- Name: diagnostico_accion_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.diagnostico_accion_id_seq OWNED BY covid19.diagnostico_accion.id;


--
-- TOC entry 181 (class 1259 OID 90345)
-- Name: diagnostico_recomendacion; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.diagnostico_recomendacion (
    id integer NOT NULL,
    id_historico_clinico integer NOT NULL,
    recomendacion_tipo character varying(255) NOT NULL,
    recomendacion_valor character varying(1024) NOT NULL
);


--
-- TOC entry 182 (class 1259 OID 90351)
-- Name: diagnostico_recomendacion_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.diagnostico_recomendacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2310 (class 0 OID 0)
-- Dependencies: 182
-- Name: diagnostico_recomendacion_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.diagnostico_recomendacion_id_seq OWNED BY covid19.diagnostico_recomendacion.id;


--
-- TOC entry 183 (class 1259 OID 90353)
-- Name: examen_laboratorial; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.examen_laboratorial (
    id integer NOT NULL,
    id_paciente integer NOT NULL,
    id_usuario integer NOT NULL,
    fecha_prevista_toma_muestra_laboratorial timestamp without time zone NOT NULL,
    identificador_externo character varying(50),
    resultado_diagnostico character varying(50),
    fecha_resultado_diagnostico timestamp without time zone,
    estado character varying(50) NOT NULL,
    fecha_notificacion_toma_muestra_laboratorial timestamp without time zone,
    fecha_notificacion_resultado timestamp without time zone,
    local_toma_muestra character varying(150)
);


--
-- TOC entry 184 (class 1259 OID 90356)
-- Name: examen_laboratorial_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.examen_laboratorial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2311 (class 0 OID 0)
-- Dependencies: 184
-- Name: examen_laboratorial_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.examen_laboratorial_id_seq OWNED BY covid19.examen_laboratorial.id;


--
-- TOC entry 185 (class 1259 OID 90358)
-- Name: form_seccion_datos_basicos; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.form_seccion_datos_basicos (
    id integer NOT NULL,
    id_registro_formulario integer NOT NULL,
    nombre character varying(255) NOT NULL,
    apellido character varying(255) NOT NULL,
    pais_nacionalidad character varying(255),
    ciudad_nacimiento character varying(255),
    tipo_documento character varying(50) NOT NULL,
    numero_documento character varying(50) NOT NULL,
    fecha_nacimiento date,
    sexo character varying(1),
    numero_celular character varying(255) NOT NULL,
    numero_celular_verificado character varying(50) NOT NULL,
    numero_telefono character varying(50),
    correo_electronico character varying(255),
    direccion_domicilio character varying(255),
    residente_paraguay boolean,
    pais_emisor_documento character varying(255),
    id_usuario integer,
    contrasenha character varying(100),
    ciudad_domicilio character varying(255),
    departamento_domicilio character varying(255),
    inicio_aislamiento timestamp without time zone,
    fecha_prevista_toma_muestra_laboratorial timestamp without time zone
);


--
-- TOC entry 186 (class 1259 OID 90364)
-- Name: form_seccion_datos_basicos_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.form_seccion_datos_basicos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2312 (class 0 OID 0)
-- Dependencies: 186
-- Name: form_seccion_datos_basicos_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.form_seccion_datos_basicos_id_seq OWNED BY covid19.form_seccion_datos_basicos.id;


--
-- TOC entry 187 (class 1259 OID 90366)
-- Name: form_seccion_datos_clinicos; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.form_seccion_datos_clinicos (
    id integer NOT NULL,
    id_registro_formulario integer NOT NULL,
    ocupacion character varying(50),
    enfermedad_base_cardiopatia_cronica boolean,
    enfermedad_base_pulmonar_cronico boolean,
    enfermedad_base_asma boolean,
    enfermedad_base_diabetes boolean,
    enfermedad_base_renal_cronico boolean,
    enfermedad_base_inmunodeprimido boolean,
    enfermedad_base_neurologica boolean,
    enfermedad_base_sindromedown boolean,
    enfermedad_base_obesidad boolean,
    enfermedad_base_hepatica_grave boolean,
    evaluacion_riesgo_usomedicamento boolean,
    evaluacion_riesgo_medicamento character varying(255),
    evaluacion_riesgo_vive_solo boolean,
    evaluacion_riesgo_tiene_habitacion_propria boolean,
    sintomas_fiebre boolean,
    sintomas_fiebre_valor character varying(255),
    sintomas_tos boolean,
    sintomas_dificultad_respirar boolean,
    sintomas_dif_respirar_dolor_garganta boolean,
    sintomas_dif_respirar_cansancio_caminar boolean,
    sintomas_dif_respirar_falta_aire boolean,
    sintomas_dif_respirar_rinorrea boolean,
    sintomas_dif_respirar_congestion_nasal boolean,
    sintomas_diarrea boolean,
    sintomas_otros character varying(255),
    declaracion_agreement boolean DEFAULT false NOT NULL,
    id_usuario integer,
    enfermedad_base_hipertension_arterial boolean,
    enfermedad_base_autoinmune boolean,
    enfermedad_base_neoplasias boolean,
    enfermedad_base_epoc boolean
);


--
-- TOC entry 188 (class 1259 OID 90373)
-- Name: form_seccion_datos_clinicos_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.form_seccion_datos_clinicos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2313 (class 0 OID 0)
-- Dependencies: 188
-- Name: form_seccion_datos_clinicos_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.form_seccion_datos_clinicos_id_seq OWNED BY covid19.form_seccion_datos_clinicos.id;


--
-- TOC entry 189 (class 1259 OID 90375)
-- Name: historico_clinico; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.historico_clinico (
    id integer NOT NULL,
    id_paciente integer NOT NULL,
    id_usuario integer NOT NULL,
    id_registro integer,
    fecha_registro timestamp without time zone NOT NULL
);


--
-- TOC entry 190 (class 1259 OID 90378)
-- Name: historico_clinico_datos; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.historico_clinico_datos (
    id integer NOT NULL,
    id_historico_clinico integer NOT NULL,
    nombre_dato character varying(255),
    valor_dato character varying(255),
    tipo_clinico_dato character varying(255)
);


--
-- TOC entry 191 (class 1259 OID 90385)
-- Name: historico_clinico_datos_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.historico_clinico_datos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2314 (class 0 OID 0)
-- Dependencies: 191
-- Name: historico_clinico_datos_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.historico_clinico_datos_id_seq OWNED BY covid19.historico_clinico_datos.id;


--
-- TOC entry 192 (class 1259 OID 90387)
-- Name: historico_clinico_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.historico_clinico_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2315 (class 0 OID 0)
-- Dependencies: 192
-- Name: historico_clinico_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.historico_clinico_id_seq OWNED BY covid19.historico_clinico.id;


--
-- TOC entry 193 (class 1259 OID 90389)
-- Name: historico_diagnostico; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.historico_diagnostico (
    id integer NOT NULL,
    id_paciente integer NOT NULL,
    id_usuario integer NOT NULL,
    resultado_diagnostico character varying(50) NOT NULL,
    fecha_diagnostico timestamp without time zone NOT NULL,
    fin_previsto_aislamiento timestamp without time zone,
    fecha_modificacion timestamp without time zone NOT NULL
);


--
-- TOC entry 194 (class 1259 OID 90392)
-- Name: historico_diagnostico_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.historico_diagnostico_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2316 (class 0 OID 0)
-- Dependencies: 194
-- Name: historico_diagnostico_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.historico_diagnostico_id_seq OWNED BY covid19.historico_diagnostico.id;


--
-- TOC entry 195 (class 1259 OID 90394)
-- Name: notificacion; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.notificacion (
    id bigint NOT NULL,
    id_paciente bigint NOT NULL,
    fecha_notificacion timestamp with time zone NOT NULL,
    mensaje character varying(1024),
    visto boolean NOT NULL,
    remitente character varying(255)
);


--
-- TOC entry 196 (class 1259 OID 90400)
-- Name: notificacion_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.notificacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2317 (class 0 OID 0)
-- Dependencies: 196
-- Name: notificacion_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.notificacion_id_seq OWNED BY covid19.notificacion.id;


--
-- TOC entry 197 (class 1259 OID 90402)
-- Name: paciente; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.paciente (
    id bigint NOT NULL,
    id_usuario integer,
    inicio_seguimiento timestamp without time zone,
    fin_seguimiento timestamp without time zone,
    inicio_aislamiento timestamp without time zone,
    fin_previsto_aislamiento timestamp without time zone,
    fin_aislamiento timestamp without time zone,
    clasificacion_paciente character varying(255) DEFAULT 'A'::character varying,
    resultado_ultimo_diagnostico character varying(50),
    fecha_ultimo_diagnostico timestamp without time zone,
    tiene_sintomas character varying(2),
    fecha_inicio_sintoma timestamp without time zone,
    fecha_exposicion timestamp without time zone
);


--
-- TOC entry 198 (class 1259 OID 90406)
-- Name: paciente_datos_personales_basicos; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.paciente_datos_personales_basicos (
    id integer NOT NULL,
    id_paciente integer NOT NULL,
    nombre character varying(255) NOT NULL,
    apellido character varying(255) NOT NULL,
    pais_nacionalidad character varying(255),
    ciudad_nacimiento character varying(255),
    pais_emisor_documento character varying(255),
    tipo_documento character varying(50) NOT NULL,
    numero_documento character varying(50) NOT NULL,
    fecha_nacimiento date,
    sexo character varying(1),
    numero_celular character varying(255) NOT NULL,
    numero_celular_verificado character varying(50) NOT NULL,
    numero_telefono character varying(49),
    correo_electronico character varying(255),
    direccion_domicilio character varying(255),
    residente_paraguay boolean,
    ciudad_domicilio character varying(255),
    departamento_domicilio character varying(255)
);


--
-- TOC entry 199 (class 1259 OID 90412)
-- Name: paciente_datos_personales_basicos_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.paciente_datos_personales_basicos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2318 (class 0 OID 0)
-- Dependencies: 199
-- Name: paciente_datos_personales_basicos_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.paciente_datos_personales_basicos_id_seq OWNED BY covid19.paciente_datos_personales_basicos.id;


--
-- TOC entry 200 (class 1259 OID 90414)
-- Name: paciente_estado_salud; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.paciente_estado_salud (
    id bigint NOT NULL,
    id_paciente integer NOT NULL,
    id_historico_clinico integer NOT NULL,
    clasificacion_paciente character varying(255) NOT NULL,
    ultimo_reporte_fecha timestamp without time zone NOT NULL,
    ultimo_reporte_tipo character varying(255) NOT NULL,
    ultimo_registro_tipo character varying(255) NOT NULL,
    sintomas_fiebre boolean DEFAULT false NOT NULL,
    sintomas_fiebre_ultima_medicion character varying(255),
    sintomas_tos boolean DEFAULT false NOT NULL,
    sintomas_dificultad_respirar boolean DEFAULT false NOT NULL,
    sintomas_dif_respirar_dolor_garganta boolean DEFAULT false NOT NULL,
    sintomas_dif_respirar_cansancio_caminar boolean DEFAULT false,
    sintomas_dif_respirar_falta_aire boolean DEFAULT false,
    sintomas_dif_respirar_rinorrea boolean DEFAULT false NOT NULL,
    sintomas_dif_respirar_congestion_nasal boolean DEFAULT false NOT NULL,
    sintomas_diarrea boolean,
    sintomas_otros character varying(255)
);


--
-- TOC entry 201 (class 1259 OID 90428)
-- Name: paciente_estado_salud_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.paciente_estado_salud_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2319 (class 0 OID 0)
-- Dependencies: 201
-- Name: paciente_estado_salud_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.paciente_estado_salud_id_seq OWNED BY covid19.paciente_estado_salud.id;


--
-- TOC entry 202 (class 1259 OID 90430)
-- Name: paciente_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.paciente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2320 (class 0 OID 0)
-- Dependencies: 202
-- Name: paciente_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.paciente_id_seq OWNED BY covid19.paciente.id;


--
-- TOC entry 203 (class 1259 OID 90432)
-- Name: pais; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.pais (
    id character varying(64) NOT NULL,
    value character varying(64) NOT NULL
);


--
-- TOC entry 204 (class 1259 OID 90435)
-- Name: registro; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.registro (
    id integer NOT NULL,
    codigo_verificacion character varying(50) NOT NULL,
    estado character varying(255) NOT NULL,
    fecha_creacion timestamp(0) without time zone NOT NULL,
    fecha_ultima_modificacion timestamp(0) without time zone,
    responsable_registro character varying(255) NOT NULL,
    tipo_registro character varying(255) NOT NULL,
    id_usuario integer
);


--
-- TOC entry 205 (class 1259 OID 90441)
-- Name: registro_formulario; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.registro_formulario (
    id integer NOT NULL,
    id_registro integer NOT NULL,
    id_paciente integer,
    registro_formulario_acompanante boolean NOT NULL,
    nombre character varying(255) NOT NULL,
    estado character varying(255) NOT NULL,
    fecha_creacion timestamp(0) without time zone NOT NULL,
    fecha_ultima_modificacion timestamp(0) without time zone
);


--
-- TOC entry 206 (class 1259 OID 90447)
-- Name: registro_formulario_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.registro_formulario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2321 (class 0 OID 0)
-- Dependencies: 206
-- Name: registro_formulario_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.registro_formulario_id_seq OWNED BY covid19.registro_formulario.id;


--
-- TOC entry 207 (class 1259 OID 90450)
-- Name: registro_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.registro_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2322 (class 0 OID 0)
-- Dependencies: 207
-- Name: registro_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.registro_id_seq OWNED BY covid19.registro.id;


--
-- TOC entry 208 (class 1259 OID 90452)
-- Name: registro_ubicacion; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.registro_ubicacion (
    id bigint NOT NULL,
    id_paciente integer NOT NULL,
    lat_reportado double precision NOT NULL,
    long_reportado double precision NOT NULL,
    timestamp_creacion timestamp without time zone NOT NULL,
    lat_dispositivo double precision,
    long_dispositivo double precision,
    altitude_accuracy double precision,
    speed double precision,
    altitude double precision,
    accuracy double precision,
    tipo_registro_ubicacion character varying(50),
    ip_cliente character varying(50),
    user_agent character varying(100),
    actividad_identificada character varying(50),
    tipo_evento character varying(50)
);


--
-- TOC entry 209 (class 1259 OID 90455)
-- Name: registro_ubicacion_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.registro_ubicacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2323 (class 0 OID 0)
-- Dependencies: 209
-- Name: registro_ubicacion_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.registro_ubicacion_id_seq OWNED BY covid19.registro_ubicacion.id;


--
-- TOC entry 210 (class 1259 OID 90457)
-- Name: reporte_salud_paciente; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.reporte_salud_paciente (
    id bigint NOT NULL,
    timestamp_creacion timestamp without time zone NOT NULL,
    como_te_sentis character varying(255),
    signos_sintomas_descritos character varying(255),
    id_registro_formulario bigint NOT NULL,
    como_te_sentis_con_relacion_ayer character varying(255),
    congestion_nasal character varying(255),
    secrecion_nasal character varying(255),
    dolor_garganta character varying(255),
    tos character varying(255),
    percibe_olores character varying(255),
    percibe_sabores character varying(255),
    dificultad_respirar character varying(255),
    sentis_fiebre character varying(255),
    sintomas_empeoraron character varying(255),
    sintomas_mejoraron character varying(255),
    desde_cuando_olores character varying(255),
    desde_cuando_sabores character varying(255),
    desde_cuando_fiebre character varying(255),
    tomaste_temperatura character varying(255),
    fiebre_ayer character varying(255),
    sentis_triste_desanimado character varying(255),
    sentis_angustia character varying(255),
    temperatura numeric(3,1)
);


--
-- TOC entry 211 (class 1259 OID 90463)
-- Name: reporte_salud_paciente_id_seq; Type: SEQUENCE; Schema: covid19; Owner: -
--

CREATE SEQUENCE covid19.reporte_salud_paciente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2324 (class 0 OID 0)
-- Dependencies: 211
-- Name: reporte_salud_paciente_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19; Owner: -
--

ALTER SEQUENCE covid19.reporte_salud_paciente_id_seq OWNED BY covid19.reporte_salud_paciente.id;


--
-- TOC entry 212 (class 1259 OID 90465)
-- Name: tipo_paciente_diagnostico; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.tipo_paciente_diagnostico (
    id character varying(50) NOT NULL,
    descripcion character varying(512) NOT NULL,
    frecuencia_reporte_ubicacion_horas integer,
    debe_reportar_ubicacion boolean DEFAULT false NOT NULL,
    frecuencia_reporte_estado_salud_horas integer,
    debe_reportar_estado_salud boolean DEFAULT false NOT NULL
);


--
-- TOC entry 213 (class 1259 OID 90473)
-- Name: tipo_registro; Type: TABLE; Schema: covid19; Owner: -
--

CREATE TABLE covid19.tipo_registro (
    id character varying(255) NOT NULL,
    descripcion character varying(1024) NOT NULL
);


--
-- TOC entry 214 (class 1259 OID 90479)
-- Name: usuario; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.usuario (
    id bigint NOT NULL,
    activo boolean,
    username character varying(255) NOT NULL,
    nombre character varying(255) NOT NULL,
    apellido character varying(255) NOT NULL,
    cedula character varying(255) NOT NULL,
    email character varying(255),
    password character varying(255),
    telefono character varying(255),
    token_reset character varying(250),
    estado_contacto boolean,
    celular character varying(100),
    fcm_registration_token character varying(255),
    sistema_operativo character varying(255)
);


--
-- TOC entry 215 (class 1259 OID 90485)
-- Name: v_reporte_registro_paciente; Type: VIEW; Schema: covid19; Owner: -
--

CREATE VIEW covid19.v_reporte_registro_paciente AS
 SELECT rffsdb.fecha_creacion,
    fsdb.nombre,
    fsdb.apellido,
        CASE fsdb.tipo_documento
            WHEN '0'::text THEN 'Cédula de Identidad'::text
            WHEN '1'::text THEN 'Pasaporte'::text
            ELSE NULL::text
        END AS tipo_documento,
    fsdb.numero_documento,
    fsdb.numero_celular,
    fsdb.fecha_nacimiento,
    ''::text AS estado_civil,
    fsdb.direccion_domicilio,
    fsdb.ciudad_domicilio,
    fsdb.departamento_domicilio,
    r.tipo_registro AS tipo_registro_ingreso,
        CASE
            WHEN (p.resultado_ultimo_diagnostico IS NULL) THEN (
            CASE r.tipo_registro
                WHEN 'aislamiento_confirmado'::text THEN 'positivo'::text
                ELSE 'sospechoso'::text
            END)::character varying
            ELSE p.resultado_ultimo_diagnostico
        END AS resultado_ultimo_diagnostico,
    p.fecha_ultimo_diagnostico,
        CASE
            WHEN (u.password IS NOT NULL) THEN 'Si'::text
            ELSE 'No'::text
        END AS abrio_sms,
        CASE
            WHEN (fsdc.id IS NULL) THEN 'No'::text
            ELSE 'Si'::text
        END AS completo_factores_riesgo,
    p.clasificacion_paciente,
    p.inicio_seguimiento,
    p.fin_seguimiento,
    p.inicio_aislamiento,
    p.fin_aislamiento,
    p.fin_previsto_aislamiento,
    rsp.timestamp_creacion AS "fecha ultimo reporte salud",
    ru.timestamp_creacion AS "fecha ultima ubicacion",
    ru.lat_dispositivo,
    ru.long_dispositivo,
    ru.lat_reportado,
    ru.long_reportado
   FROM ((((((((covid19.form_seccion_datos_basicos fsdb
     JOIN covid19.registro_formulario rffsdb ON ((fsdb.id_registro_formulario = rffsdb.id)))
     JOIN covid19.registro r ON ((r.id = rffsdb.id_registro)))
     LEFT JOIN covid19.paciente_datos_personales_basicos pdpb ON (((pdpb.numero_documento)::text = (fsdb.numero_documento)::text)))
     LEFT JOIN covid19.paciente p ON ((p.id = pdpb.id_paciente)))
     LEFT JOIN covid19admin.usuario u ON ((p.id_usuario = u.id)))
     LEFT JOIN covid19.form_seccion_datos_clinicos fsdc ON ((fsdb.id_registro_formulario = fsdc.id_registro_formulario)))
     LEFT JOIN ( SELECT DISTINCT ON (rf.id_paciente) rf.id,
            rf.id_registro,
            rf.id_paciente,
            rf.registro_formulario_acompanante,
            rf.nombre,
            rf.estado,
            rf.fecha_creacion,
            rf.fecha_ultima_modificacion,
            rsp_1.id,
            rsp_1.timestamp_creacion,
            rsp_1.como_te_sentis,
            rsp_1.signos_sintomas_descritos,
            rsp_1.id_registro_formulario,
            rsp_1.como_te_sentis_con_relacion_ayer,
            rsp_1.congestion_nasal,
            rsp_1.secrecion_nasal,
            rsp_1.dolor_garganta,
            rsp_1.tos,
            rsp_1.percibe_olores,
            rsp_1.percibe_sabores,
            rsp_1.dificultad_respirar,
            rsp_1.sentis_fiebre,
            rsp_1.sintomas_empeoraron,
            rsp_1.sintomas_mejoraron,
            rsp_1.desde_cuando_olores,
            rsp_1.desde_cuando_sabores,
            rsp_1.desde_cuando_fiebre,
            rsp_1.tomaste_temperatura,
            rsp_1.fiebre_ayer,
            rsp_1.sentis_triste_desanimado,
            rsp_1.sentis_angustia,
            rsp_1.temperatura
           FROM (covid19.registro_formulario rf
             JOIN covid19.reporte_salud_paciente rsp_1 ON ((rsp_1.id_registro_formulario = rf.id)))
          ORDER BY rf.id_paciente, rf.id DESC) rsp(id, id_registro, id_paciente, registro_formulario_acompanante, nombre, estado, fecha_creacion, fecha_ultima_modificacion, id_1, timestamp_creacion, como_te_sentis, signos_sintomas_descritos, id_registro_formulario, como_te_sentis_con_relacion_ayer, congestion_nasal, secrecion_nasal, dolor_garganta, tos, percibe_olores, percibe_sabores, dificultad_respirar, sentis_fiebre, sintomas_empeoraron, sintomas_mejoraron, desde_cuando_olores, desde_cuando_sabores, desde_cuando_fiebre, tomaste_temperatura, fiebre_ayer, sentis_triste_desanimado, sentis_angustia, temperatura) ON ((p.id = rsp.id_paciente)))
     LEFT JOIN ( SELECT DISTINCT ON (ru_1.id_paciente) ru_1.id,
            ru_1.id_paciente,
            ru_1.lat_reportado,
            ru_1.long_reportado,
            ru_1.timestamp_creacion,
            ru_1.lat_dispositivo,
            ru_1.long_dispositivo,
            ru_1.altitude_accuracy,
            ru_1.speed,
            ru_1.altitude,
            ru_1.accuracy,
            ru_1.tipo_registro_ubicacion,
            ru_1.ip_cliente,
            ru_1.user_agent,
            ru_1.actividad_identificada,
            ru_1.tipo_evento
           FROM covid19.registro_ubicacion ru_1
          ORDER BY ru_1.id_paciente, ru_1.id DESC) ru ON ((ru.id_paciente = p.id)))
  ORDER BY fsdb.id;


--
-- TOC entry 216 (class 1259 OID 90490)
-- Name: v_reporte_ubicacion; Type: VIEW; Schema: covid19; Owner: -
--

CREATE VIEW covid19.v_reporte_ubicacion AS
 SELECT pdpb.id_paciente,
    pdpb.numero_documento,
    ru.timestamp_creacion,
    ru.lat_reportado,
    ru.long_reportado
   FROM (covid19.paciente_datos_personales_basicos pdpb
     JOIN covid19.registro_ubicacion ru ON ((pdpb.id_paciente = ru.id_paciente)))
  ORDER BY pdpb.id_paciente, ru.id DESC;


--
-- TOC entry 217 (class 1259 OID 90495)
-- Name: configuracion; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.configuracion (
    id_configuracion bigint NOT NULL,
    estado character varying(255),
    nombre_variable character varying(255) NOT NULL,
    valor_variable character varying(255) NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 90501)
-- Name: login_automatico; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.login_automatico (
    id bigint NOT NULL,
    id_usuario bigint NOT NULL,
    token character varying(255) NOT NULL,
    estado character varying(25) DEFAULT 'activo'::character varying NOT NULL,
    timestamp_creacion timestamp without time zone NOT NULL
);


--
-- TOC entry 219 (class 1259 OID 90505)
-- Name: login_automatico_id_seq; Type: SEQUENCE; Schema: covid19admin; Owner: -
--

CREATE SEQUENCE covid19admin.login_automatico_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2325 (class 0 OID 0)
-- Dependencies: 219
-- Name: login_automatico_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19admin; Owner: -
--

ALTER SEQUENCE covid19admin.login_automatico_id_seq OWNED BY covid19admin.login_automatico.id;


--
-- TOC entry 220 (class 1259 OID 90507)
-- Name: permiso; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.permiso (
    id bigint NOT NULL,
    descripcion character varying(255),
    nombre character varying(255) NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 90513)
-- Name: permiso_id_seq; Type: SEQUENCE; Schema: covid19admin; Owner: -
--

CREATE SEQUENCE covid19admin.permiso_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2326 (class 0 OID 0)
-- Dependencies: 221
-- Name: permiso_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19admin; Owner: -
--

ALTER SEQUENCE covid19admin.permiso_id_seq OWNED BY covid19admin.permiso.id;


--
-- TOC entry 222 (class 1259 OID 90515)
-- Name: rol; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.rol (
    id bigint NOT NULL,
    activo boolean,
    descripcion character varying(255),
    nombre character varying(255) NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 90521)
-- Name: rol_id_seq; Type: SEQUENCE; Schema: covid19admin; Owner: -
--

CREATE SEQUENCE covid19admin.rol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2327 (class 0 OID 0)
-- Dependencies: 223
-- Name: rol_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19admin; Owner: -
--

ALTER SEQUENCE covid19admin.rol_id_seq OWNED BY covid19admin.rol.id;


--
-- TOC entry 224 (class 1259 OID 90523)
-- Name: rol_permiso; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.rol_permiso (
    rol_id bigint NOT NULL,
    permiso_id bigint NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 90526)
-- Name: rol_usuario; Type: TABLE; Schema: covid19admin; Owner: -
--

CREATE TABLE covid19admin.rol_usuario (
    usuario_id bigint NOT NULL,
    rol_id bigint NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 90529)
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: covid19admin; Owner: -
--

CREATE SEQUENCE covid19admin.usuario_id_seq
    START WITH 100
    INCREMENT BY 1
    MINVALUE 100
    MAXVALUE 2147483647
    CACHE 1;


--
-- TOC entry 2328 (class 0 OID 0)
-- Dependencies: 226
-- Name: usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: covid19admin; Owner: -
--

ALTER SEQUENCE covid19admin.usuario_id_seq OWNED BY covid19admin.usuario.id;


--
-- TOC entry 227 (class 1259 OID 90556)
-- Name: v_reporte_registro_paciente; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.v_reporte_registro_paciente AS
 SELECT rffsdb.fecha_creacion,
    fsdb.nombre,
    fsdb.apellido,
        CASE fsdb.tipo_documento
            WHEN '0'::text THEN 'Cédula de Identidad'::text
            WHEN '1'::text THEN 'Pasaporte'::text
            ELSE NULL::text
        END AS "case",
    fsdb.numero_documento,
    fsdb.numero_celular,
    fsdb.direccion_domicilio,
    fsdb.ciudad_domicilio,
    fsdb.departamento_domicilio,
        CASE
            WHEN (fsdb.contrasenha IS NOT NULL) THEN 'Si'::text
            ELSE 'No'::text
        END AS "Abrio sms?",
        CASE
            WHEN (pdpb.id IS NULL) THEN 'No'::text
            ELSE 'Si'::text
        END AS "Completó Factores Riesgo?",
    p.clasificacion_paciente,
    p.inicio_seguimiento,
    p.fin_seguimiento,
    p.inicio_aislamiento,
    p.fin_aislamiento,
    p.fin_previsto_aislamiento,
    rsp.timestamp_creacion AS "fecha ultimo reporte salud",
    ru.timestamp_creacion AS "fecha ultima ubicacion",
    ru.lat_dispositivo,
    ru.long_dispositivo,
    ru.lat_reportado,
    ru.long_reportado
   FROM (((((covid19.form_seccion_datos_basicos fsdb
     JOIN covid19.registro_formulario rffsdb ON ((fsdb.id_registro_formulario = rffsdb.id)))
     LEFT JOIN covid19.paciente_datos_personales_basicos pdpb ON (((pdpb.numero_documento)::text = (fsdb.numero_documento)::text)))
     LEFT JOIN covid19.paciente p ON ((p.id = pdpb.id_paciente)))
     LEFT JOIN ( SELECT DISTINCT ON (rf.id_paciente) rf.id,
            rf.id_registro,
            rf.id_paciente,
            rf.registro_formulario_acompanante,
            rf.nombre,
            rf.estado,
            rf.fecha_creacion,
            rf.fecha_ultima_modificacion,
            rsp_1.id,
            rsp_1.timestamp_creacion,
            rsp_1.como_te_sentis,
            rsp_1.signos_sintomas_descritos,
            rsp_1.id_registro_formulario,
            rsp_1.como_te_sentis_con_relacion_ayer,
            rsp_1.congestion_nasal,
            rsp_1.secrecion_nasal,
            rsp_1.dolor_garganta,
            rsp_1.tos,
            rsp_1.percibe_olores,
            rsp_1.percibe_sabores,
            rsp_1.dificultad_respirar,
            rsp_1.sentis_fiebre,
            rsp_1.sintomas_empeoraron,
            rsp_1.sintomas_mejoraron,
            rsp_1.desde_cuando_olores,
            rsp_1.desde_cuando_sabores,
            rsp_1.desde_cuando_fiebre,
            rsp_1.tomaste_temperatura,
            rsp_1.fiebre_ayer,
            rsp_1.sentis_triste_desanimado,
            rsp_1.sentis_angustia,
            rsp_1.temperatura
           FROM (covid19.registro_formulario rf
             JOIN covid19.reporte_salud_paciente rsp_1 ON ((rsp_1.id_registro_formulario = rf.id)))
          ORDER BY rf.id_paciente, rf.id DESC) rsp(id, id_registro, id_paciente, registro_formulario_acompanante, nombre, estado, fecha_creacion, fecha_ultima_modificacion, id_1, timestamp_creacion, como_te_sentis, signos_sintomas_descritos, id_registro_formulario, como_te_sentis_con_relacion_ayer, congestion_nasal, secrecion_nasal, dolor_garganta, tos, percibe_olores, percibe_sabores, dificultad_respirar, sentis_fiebre, sintomas_empeoraron, sintomas_mejoraron, desde_cuando_olores, desde_cuando_sabores, desde_cuando_fiebre, tomaste_temperatura, fiebre_ayer, sentis_triste_desanimado, sentis_angustia, temperatura) ON ((p.id = rsp.id_paciente)))
     LEFT JOIN ( SELECT DISTINCT ON (ru_1.id_paciente) ru_1.id,
            ru_1.id_paciente,
            ru_1.lat_reportado,
            ru_1.long_reportado,
            ru_1.timestamp_creacion,
            ru_1.lat_dispositivo,
            ru_1.long_dispositivo
           FROM covid19.registro_ubicacion ru_1
          ORDER BY ru_1.id_paciente, ru_1.id DESC) ru ON ((ru.id_paciente = p.id)))
  ORDER BY fsdb.id;


--
-- TOC entry 2105 (class 2604 OID 90561)
-- Name: audit_log log_id; Type: DEFAULT; Schema: audit; Owner: -
--

ALTER TABLE ONLY audit.audit_log ALTER COLUMN log_id SET DEFAULT nextval('audit.audit_log_log_id_seq'::regclass);


--
-- TOC entry 2106 (class 2604 OID 90562)
-- Name: censo_contacto id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.censo_contacto ALTER COLUMN id SET DEFAULT nextval('covid19.censo_contacto_id_seq'::regclass);


--
-- TOC entry 2107 (class 2604 OID 90563)
-- Name: diagnostico_accion id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.diagnostico_accion ALTER COLUMN id SET DEFAULT nextval('covid19.diagnostico_accion_id_seq'::regclass);


--
-- TOC entry 2108 (class 2604 OID 90564)
-- Name: diagnostico_recomendacion id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.diagnostico_recomendacion ALTER COLUMN id SET DEFAULT nextval('covid19.diagnostico_recomendacion_id_seq'::regclass);


--
-- TOC entry 2109 (class 2604 OID 90565)
-- Name: examen_laboratorial id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.examen_laboratorial ALTER COLUMN id SET DEFAULT nextval('covid19.examen_laboratorial_id_seq'::regclass);


--
-- TOC entry 2110 (class 2604 OID 90566)
-- Name: form_seccion_datos_basicos id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.form_seccion_datos_basicos ALTER COLUMN id SET DEFAULT nextval('covid19.form_seccion_datos_basicos_id_seq'::regclass);


--
-- TOC entry 2112 (class 2604 OID 90567)
-- Name: form_seccion_datos_clinicos id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.form_seccion_datos_clinicos ALTER COLUMN id SET DEFAULT nextval('covid19.form_seccion_datos_clinicos_id_seq'::regclass);


--
-- TOC entry 2113 (class 2604 OID 90568)
-- Name: historico_clinico id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.historico_clinico ALTER COLUMN id SET DEFAULT nextval('covid19.historico_clinico_id_seq'::regclass);


--
-- TOC entry 2114 (class 2604 OID 90569)
-- Name: historico_clinico_datos id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.historico_clinico_datos ALTER COLUMN id SET DEFAULT nextval('covid19.historico_clinico_datos_id_seq'::regclass);


--
-- TOC entry 2115 (class 2604 OID 90570)
-- Name: historico_diagnostico id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.historico_diagnostico ALTER COLUMN id SET DEFAULT nextval('covid19.historico_diagnostico_id_seq'::regclass);


--
-- TOC entry 2116 (class 2604 OID 90571)
-- Name: notificacion id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.notificacion ALTER COLUMN id SET DEFAULT nextval('covid19.notificacion_id_seq'::regclass);


--
-- TOC entry 2118 (class 2604 OID 90572)
-- Name: paciente id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.paciente ALTER COLUMN id SET DEFAULT nextval('covid19.paciente_id_seq'::regclass);


--
-- TOC entry 2119 (class 2604 OID 90573)
-- Name: paciente_datos_personales_basicos id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.paciente_datos_personales_basicos ALTER COLUMN id SET DEFAULT nextval('covid19.paciente_datos_personales_basicos_id_seq'::regclass);


--
-- TOC entry 2128 (class 2604 OID 90574)
-- Name: paciente_estado_salud id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.paciente_estado_salud ALTER COLUMN id SET DEFAULT nextval('covid19.paciente_estado_salud_id_seq'::regclass);


--
-- TOC entry 2129 (class 2604 OID 90575)
-- Name: registro id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.registro ALTER COLUMN id SET DEFAULT nextval('covid19.registro_id_seq'::regclass);


--
-- TOC entry 2130 (class 2604 OID 90576)
-- Name: registro_formulario id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.registro_formulario ALTER COLUMN id SET DEFAULT nextval('covid19.registro_formulario_id_seq'::regclass);


--
-- TOC entry 2131 (class 2604 OID 90577)
-- Name: registro_ubicacion id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.registro_ubicacion ALTER COLUMN id SET DEFAULT nextval('covid19.registro_ubicacion_id_seq'::regclass);


--
-- TOC entry 2132 (class 2604 OID 90578)
-- Name: reporte_salud_paciente id; Type: DEFAULT; Schema: covid19; Owner: -
--

ALTER TABLE ONLY covid19.reporte_salud_paciente ALTER COLUMN id SET DEFAULT nextval('covid19.reporte_salud_paciente_id_seq'::regclass);


--
-- TOC entry 2137 (class 2604 OID 90579)
-- Name: login_automatico id; Type: DEFAULT; Schema: covid19admin; Owner: -
--

ALTER TABLE ONLY covid19admin.login_automatico ALTER COLUMN id SET DEFAULT nextval('covid19admin.login_automatico_id_seq'::regclass);


--
-- TOC entry 2138 (class 2604 OID 90580)
-- Name: permiso id; Type: DEFAULT; Schema: covid19admin; Owner: -
--

ALTER TABLE ONLY covid19admin.permiso ALTER COLUMN id SET DEFAULT nextval('covid19admin.permiso_id_seq'::regclass);


--
-- TOC entry 2139 (class 2604 OID 90581)
-- Name: rol id; Type: DEFAULT; Schema: covid19admin; Owner: -
--

ALTER TABLE ONLY covid19admin.rol ALTER COLUMN id SET DEFAULT nextval('covid19admin.rol_id_seq'::regclass);


--
-- TOC entry 2135 (class 2604 OID 90582)
-- Name: usuario id; Type: DEFAULT; Schema: covid19admin; Owner: -
--

ALTER TABLE ONLY covid19admin.usuario ALTER COLUMN id SET DEFAULT nextval('covid19admin.usuario_id_seq'::regclass);


--
-- TOC entry 2250 (class 0 OID 90319)
-- Dependencies: 175
-- Data for Name: audit_log; Type: TABLE DATA; Schema: audit; Owner: -
--



--
-- TOC entry 2252 (class 0 OID 90329)
-- Dependencies: 177
-- Data for Name: censo_contacto; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2254 (class 0 OID 90337)
-- Dependencies: 179
-- Data for Name: diagnostico_accion; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2256 (class 0 OID 90345)
-- Dependencies: 181
-- Data for Name: diagnostico_recomendacion; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2258 (class 0 OID 90353)
-- Dependencies: 183
-- Data for Name: examen_laboratorial; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2260 (class 0 OID 90358)
-- Dependencies: 185
-- Data for Name: form_seccion_datos_basicos; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2262 (class 0 OID 90366)
-- Dependencies: 187
-- Data for Name: form_seccion_datos_clinicos; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2264 (class 0 OID 90375)
-- Dependencies: 189
-- Data for Name: historico_clinico; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2265 (class 0 OID 90378)
-- Dependencies: 190
-- Data for Name: historico_clinico_datos; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2268 (class 0 OID 90389)
-- Dependencies: 193
-- Data for Name: historico_diagnostico; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2270 (class 0 OID 90394)
-- Dependencies: 195
-- Data for Name: notificacion; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2272 (class 0 OID 90402)
-- Dependencies: 197
-- Data for Name: paciente; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2273 (class 0 OID 90406)
-- Dependencies: 198
-- Data for Name: paciente_datos_personales_basicos; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2275 (class 0 OID 90414)
-- Dependencies: 200
-- Data for Name: paciente_estado_salud; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2278 (class 0 OID 90432)
-- Dependencies: 203
-- Data for Name: pais; Type: TABLE DATA; Schema: covid19; Owner: -
--

INSERT INTO covid19.pais VALUES ('AF', 'Afganistán');
INSERT INTO covid19.pais VALUES ('AL', 'Albania');
INSERT INTO covid19.pais VALUES ('DE', 'Alemania');
INSERT INTO covid19.pais VALUES ('AD', 'Andorra');
INSERT INTO covid19.pais VALUES ('AO', 'Angola');
INSERT INTO covid19.pais VALUES ('AI', 'Anguila');
INSERT INTO covid19.pais VALUES ('AQ', 'Antártida');
INSERT INTO covid19.pais VALUES ('AG', 'Antigua y Barbuda');
INSERT INTO covid19.pais VALUES ('SA', 'Arabia Saudí');
INSERT INTO covid19.pais VALUES ('DZ', 'Argelia');
INSERT INTO covid19.pais VALUES ('AR', 'Argentina');
INSERT INTO covid19.pais VALUES ('AM', 'Armenia');
INSERT INTO covid19.pais VALUES ('AW', 'Aruba');
INSERT INTO covid19.pais VALUES ('AU', 'Australia');
INSERT INTO covid19.pais VALUES ('AT', 'Austria');
INSERT INTO covid19.pais VALUES ('AZ', 'Azerbaiyán');
INSERT INTO covid19.pais VALUES ('BS', 'Bahamas');
INSERT INTO covid19.pais VALUES ('BD', 'Bangladés');
INSERT INTO covid19.pais VALUES ('BB', 'Barbados');
INSERT INTO covid19.pais VALUES ('BH', 'Baréin');
INSERT INTO covid19.pais VALUES ('BE', 'Bélgica');
INSERT INTO covid19.pais VALUES ('BZ', 'Belice');
INSERT INTO covid19.pais VALUES ('BJ', 'Benín');
INSERT INTO covid19.pais VALUES ('BM', 'Bermudas');
INSERT INTO covid19.pais VALUES ('BY', 'Bielorrusia');
INSERT INTO covid19.pais VALUES ('BO', 'Bolivia');
INSERT INTO covid19.pais VALUES ('BA', 'Bosnia y Herzegovina');
INSERT INTO covid19.pais VALUES ('BW', 'Botsuana');
INSERT INTO covid19.pais VALUES ('BR', 'Brasil');
INSERT INTO covid19.pais VALUES ('BN', 'Brunéi');
INSERT INTO covid19.pais VALUES ('BG', 'Bulgaria');
INSERT INTO covid19.pais VALUES ('BF', 'Burkina Faso');
INSERT INTO covid19.pais VALUES ('BI', 'Burundi');
INSERT INTO covid19.pais VALUES ('BT', 'Bután');
INSERT INTO covid19.pais VALUES ('CV', 'Cabo Verde');
INSERT INTO covid19.pais VALUES ('KH', 'Camboya');
INSERT INTO covid19.pais VALUES ('CM', 'Camerún');
INSERT INTO covid19.pais VALUES ('CA', 'Canadá');
INSERT INTO covid19.pais VALUES ('IC', 'Canarias');
INSERT INTO covid19.pais VALUES ('BQ', 'Caribe neerlandés');
INSERT INTO covid19.pais VALUES ('QA', 'Catar');
INSERT INTO covid19.pais VALUES ('EA', 'Ceuta y Melilla');
INSERT INTO covid19.pais VALUES ('TD', 'Chad');
INSERT INTO covid19.pais VALUES ('CZ', 'Chequia');
INSERT INTO covid19.pais VALUES ('CL', 'Chile');
INSERT INTO covid19.pais VALUES ('CN', 'China');
INSERT INTO covid19.pais VALUES ('CY', 'Chipre');
INSERT INTO covid19.pais VALUES ('VA', 'Ciudad del Vaticano');
INSERT INTO covid19.pais VALUES ('CO', 'Colombia');
INSERT INTO covid19.pais VALUES ('KM', 'Comoras');
INSERT INTO covid19.pais VALUES ('CG', 'Congo');
INSERT INTO covid19.pais VALUES ('KP', 'Corea del Norte');
INSERT INTO covid19.pais VALUES ('KR', 'Corea del Sur');
INSERT INTO covid19.pais VALUES ('CR', 'Costa Rica');
INSERT INTO covid19.pais VALUES ('CI', 'Côte d’Ivoire');
INSERT INTO covid19.pais VALUES ('HR', 'Croacia');
INSERT INTO covid19.pais VALUES ('CU', 'Cuba');
INSERT INTO covid19.pais VALUES ('CW', 'Curazao');
INSERT INTO covid19.pais VALUES ('DG', 'Diego García');
INSERT INTO covid19.pais VALUES ('DK', 'Dinamarca');
INSERT INTO covid19.pais VALUES ('DM', 'Dominica');
INSERT INTO covid19.pais VALUES ('EC', 'Ecuador');
INSERT INTO covid19.pais VALUES ('EG', 'Egipto');
INSERT INTO covid19.pais VALUES ('SV', 'El Salvador');
INSERT INTO covid19.pais VALUES ('AE', 'Emiratos Árabes Unidos');
INSERT INTO covid19.pais VALUES ('ER', 'Eritrea');
INSERT INTO covid19.pais VALUES ('SK', 'Eslovaquia');
INSERT INTO covid19.pais VALUES ('SI', 'Eslovenia');
INSERT INTO covid19.pais VALUES ('ES', 'España');
INSERT INTO covid19.pais VALUES ('US', 'Estados Unidos');
INSERT INTO covid19.pais VALUES ('EE', 'Estonia');
INSERT INTO covid19.pais VALUES ('SZ', 'Esuatini');
INSERT INTO covid19.pais VALUES ('ET', 'Etiopía');
INSERT INTO covid19.pais VALUES ('PH', 'Filipinas');
INSERT INTO covid19.pais VALUES ('FI', 'Finlandia');
INSERT INTO covid19.pais VALUES ('FJ', 'Fiyi');
INSERT INTO covid19.pais VALUES ('FR', 'Francia');
INSERT INTO covid19.pais VALUES ('GA', 'Gabón');
INSERT INTO covid19.pais VALUES ('GM', 'Gambia');
INSERT INTO covid19.pais VALUES ('GE', 'Georgia');
INSERT INTO covid19.pais VALUES ('GH', 'Ghana');
INSERT INTO covid19.pais VALUES ('GI', 'Gibraltar');
INSERT INTO covid19.pais VALUES ('GD', 'Granada');
INSERT INTO covid19.pais VALUES ('GR', 'Grecia');
INSERT INTO covid19.pais VALUES ('GL', 'Groenlandia');
INSERT INTO covid19.pais VALUES ('GP', 'Guadalupe');
INSERT INTO covid19.pais VALUES ('GU', 'Guam');
INSERT INTO covid19.pais VALUES ('GT', 'Guatemala');
INSERT INTO covid19.pais VALUES ('GF', 'Guayana Francesa');
INSERT INTO covid19.pais VALUES ('GG', 'Guernsey');
INSERT INTO covid19.pais VALUES ('GN', 'Guinea');
INSERT INTO covid19.pais VALUES ('GQ', 'Guinea Ecuatorial');
INSERT INTO covid19.pais VALUES ('GW', 'Guinea-Bisáu');
INSERT INTO covid19.pais VALUES ('GY', 'Guyana');
INSERT INTO covid19.pais VALUES ('HT', 'Haití');
INSERT INTO covid19.pais VALUES ('HN', 'Honduras');
INSERT INTO covid19.pais VALUES ('HU', 'Hungría');
INSERT INTO covid19.pais VALUES ('IN', 'India');
INSERT INTO covid19.pais VALUES ('ID', 'Indonesia');
INSERT INTO covid19.pais VALUES ('IQ', 'Irak');
INSERT INTO covid19.pais VALUES ('IR', 'Irán');
INSERT INTO covid19.pais VALUES ('IE', 'Irlanda');
INSERT INTO covid19.pais VALUES ('AC', 'Isla de la Ascensión');
INSERT INTO covid19.pais VALUES ('IM', 'Isla de Man');
INSERT INTO covid19.pais VALUES ('CX', 'Isla de Navidad');
INSERT INTO covid19.pais VALUES ('NF', 'Isla Norfolk');
INSERT INTO covid19.pais VALUES ('IS', 'Islandia');
INSERT INTO covid19.pais VALUES ('AX', 'Islas Åland');
INSERT INTO covid19.pais VALUES ('KY', 'Islas Caimán');
INSERT INTO covid19.pais VALUES ('CC', 'Islas Cocos');
INSERT INTO covid19.pais VALUES ('CK', 'Islas Cook');
INSERT INTO covid19.pais VALUES ('FO', 'Islas Feroe');
INSERT INTO covid19.pais VALUES ('GS', 'Islas Georgia del Sur y Sandwich del Sur');
INSERT INTO covid19.pais VALUES ('FK', 'Islas Malvinas');
INSERT INTO covid19.pais VALUES ('MP', 'Islas Marianas del Norte');
INSERT INTO covid19.pais VALUES ('MH', 'Islas Marshall');
INSERT INTO covid19.pais VALUES ('UM', 'Islas menores alejadas de EE. UU.');
INSERT INTO covid19.pais VALUES ('PN', 'Islas Pitcairn');
INSERT INTO covid19.pais VALUES ('SB', 'Islas Salomón');
INSERT INTO covid19.pais VALUES ('TC', 'Islas Turcas y Caicos');
INSERT INTO covid19.pais VALUES ('VG', 'Islas Vírgenes Británicas');
INSERT INTO covid19.pais VALUES ('VI', 'Islas Vírgenes de EE. UU.');
INSERT INTO covid19.pais VALUES ('IL', 'Israel');
INSERT INTO covid19.pais VALUES ('IT', 'Italia');
INSERT INTO covid19.pais VALUES ('JM', 'Jamaica');
INSERT INTO covid19.pais VALUES ('JP', 'Japón');
INSERT INTO covid19.pais VALUES ('JE', 'Jersey');
INSERT INTO covid19.pais VALUES ('JO', 'Jordania');
INSERT INTO covid19.pais VALUES ('KZ', 'Kazajistán');
INSERT INTO covid19.pais VALUES ('KE', 'Kenia');
INSERT INTO covid19.pais VALUES ('KG', 'Kirguistán');
INSERT INTO covid19.pais VALUES ('KI', 'Kiribati');
INSERT INTO covid19.pais VALUES ('XK', 'Kosovo');
INSERT INTO covid19.pais VALUES ('KW', 'Kuwait');
INSERT INTO covid19.pais VALUES ('LA', 'Laos');
INSERT INTO covid19.pais VALUES ('LS', 'Lesoto');
INSERT INTO covid19.pais VALUES ('LV', 'Letonia');
INSERT INTO covid19.pais VALUES ('LB', 'Líbano');
INSERT INTO covid19.pais VALUES ('LR', 'Liberia');
INSERT INTO covid19.pais VALUES ('LY', 'Libia');
INSERT INTO covid19.pais VALUES ('LI', 'Liechtenstein');
INSERT INTO covid19.pais VALUES ('LT', 'Lituania');
INSERT INTO covid19.pais VALUES ('LU', 'Luxemburgo');
INSERT INTO covid19.pais VALUES ('MK', 'Macedonia');
INSERT INTO covid19.pais VALUES ('MG', 'Madagascar');
INSERT INTO covid19.pais VALUES ('MY', 'Malasia');
INSERT INTO covid19.pais VALUES ('MW', 'Malaui');
INSERT INTO covid19.pais VALUES ('MV', 'Maldivas');
INSERT INTO covid19.pais VALUES ('ML', 'Mali');
INSERT INTO covid19.pais VALUES ('MT', 'Malta');
INSERT INTO covid19.pais VALUES ('MA', 'Marruecos');
INSERT INTO covid19.pais VALUES ('MQ', 'Martinica');
INSERT INTO covid19.pais VALUES ('MU', 'Mauricio');
INSERT INTO covid19.pais VALUES ('MR', 'Mauritania');
INSERT INTO covid19.pais VALUES ('YT', 'Mayotte');
INSERT INTO covid19.pais VALUES ('MX', 'México');
INSERT INTO covid19.pais VALUES ('FM', 'Micronesia');
INSERT INTO covid19.pais VALUES ('MD', 'Moldavia');
INSERT INTO covid19.pais VALUES ('MC', 'Mónaco');
INSERT INTO covid19.pais VALUES ('MN', 'Mongolia');
INSERT INTO covid19.pais VALUES ('ME', 'Montenegro');
INSERT INTO covid19.pais VALUES ('MS', 'Montserrat');
INSERT INTO covid19.pais VALUES ('MZ', 'Mozambique');
INSERT INTO covid19.pais VALUES ('MM', 'Myanmar (Birmania)');
INSERT INTO covid19.pais VALUES ('NA', 'Namibia');
INSERT INTO covid19.pais VALUES ('NR', 'Nauru');
INSERT INTO covid19.pais VALUES ('NP', 'Nepal');
INSERT INTO covid19.pais VALUES ('NI', 'Nicaragua');
INSERT INTO covid19.pais VALUES ('NE', 'Níger');
INSERT INTO covid19.pais VALUES ('NG', 'Nigeria');
INSERT INTO covid19.pais VALUES ('NU', 'Niue');
INSERT INTO covid19.pais VALUES ('NO', 'Noruega');
INSERT INTO covid19.pais VALUES ('NC', 'Nueva Caledonia');
INSERT INTO covid19.pais VALUES ('NZ', 'Nueva Zelanda');
INSERT INTO covid19.pais VALUES ('OM', 'Omán');
INSERT INTO covid19.pais VALUES ('NL', 'Países Bajos');
INSERT INTO covid19.pais VALUES ('PK', 'Pakistán');
INSERT INTO covid19.pais VALUES ('PW', 'Palaos');
INSERT INTO covid19.pais VALUES ('PA', 'Panamá');
INSERT INTO covid19.pais VALUES ('PG', 'Papúa Nueva Guinea');
INSERT INTO covid19.pais VALUES ('PY', 'Paraguay');
INSERT INTO covid19.pais VALUES ('PE', 'Perú');
INSERT INTO covid19.pais VALUES ('PF', 'Polinesia Francesa');
INSERT INTO covid19.pais VALUES ('PL', 'Polonia');
INSERT INTO covid19.pais VALUES ('PT', 'Portugal');
INSERT INTO covid19.pais VALUES ('XA', 'Pseudo-Accents');
INSERT INTO covid19.pais VALUES ('XB', 'Pseudo-Bidi');
INSERT INTO covid19.pais VALUES ('PR', 'Puerto Rico');
INSERT INTO covid19.pais VALUES ('HK', 'RAE de Hong Kong (China)');
INSERT INTO covid19.pais VALUES ('MO', 'RAE de Macao (China)');
INSERT INTO covid19.pais VALUES ('GB', 'Reino Unido');
INSERT INTO covid19.pais VALUES ('CF', 'República Centroafricana');
INSERT INTO covid19.pais VALUES ('CD', 'República Democrática del Congo');
INSERT INTO covid19.pais VALUES ('DO', 'República Dominicana');
INSERT INTO covid19.pais VALUES ('RE', 'Reunión');
INSERT INTO covid19.pais VALUES ('RW', 'Ruanda');
INSERT INTO covid19.pais VALUES ('RO', 'Rumanía');
INSERT INTO covid19.pais VALUES ('RU', 'Rusia');
INSERT INTO covid19.pais VALUES ('EH', 'Sáhara Occidental');
INSERT INTO covid19.pais VALUES ('WS', 'Samoa');
INSERT INTO covid19.pais VALUES ('AS', 'Samoa Americana');
INSERT INTO covid19.pais VALUES ('BL', 'San Bartolomé');
INSERT INTO covid19.pais VALUES ('KN', 'San Cristóbal y Nieves');
INSERT INTO covid19.pais VALUES ('SM', 'San Marino');
INSERT INTO covid19.pais VALUES ('MF', 'San Martín');
INSERT INTO covid19.pais VALUES ('PM', 'San Pedro y Miquelón');
INSERT INTO covid19.pais VALUES ('VC', 'San Vicente y las Granadinas');
INSERT INTO covid19.pais VALUES ('SH', 'Santa Elena');
INSERT INTO covid19.pais VALUES ('LC', 'Santa Lucía');
INSERT INTO covid19.pais VALUES ('ST', 'Santo Tomé y Príncipe');
INSERT INTO covid19.pais VALUES ('SN', 'Senegal');
INSERT INTO covid19.pais VALUES ('RS', 'Serbia');
INSERT INTO covid19.pais VALUES ('SC', 'Seychelles');
INSERT INTO covid19.pais VALUES ('SL', 'Sierra Leona');
INSERT INTO covid19.pais VALUES ('SG', 'Singapur');
INSERT INTO covid19.pais VALUES ('SX', 'Sint Maarten');
INSERT INTO covid19.pais VALUES ('SY', 'Siria');
INSERT INTO covid19.pais VALUES ('SO', 'Somalia');
INSERT INTO covid19.pais VALUES ('LK', 'Sri Lanka');
INSERT INTO covid19.pais VALUES ('ZA', 'Sudáfrica');
INSERT INTO covid19.pais VALUES ('SD', 'Sudán');
INSERT INTO covid19.pais VALUES ('SS', 'Sudán del Sur');
INSERT INTO covid19.pais VALUES ('SE', 'Suecia');
INSERT INTO covid19.pais VALUES ('CH', 'Suiza');
INSERT INTO covid19.pais VALUES ('SR', 'Surinam');
INSERT INTO covid19.pais VALUES ('SJ', 'Svalbard y Jan Mayen');
INSERT INTO covid19.pais VALUES ('TH', 'Tailandia');
INSERT INTO covid19.pais VALUES ('TW', 'Taiwán');
INSERT INTO covid19.pais VALUES ('TZ', 'Tanzania');
INSERT INTO covid19.pais VALUES ('TJ', 'Tayikistán');
INSERT INTO covid19.pais VALUES ('IO', 'Territorio Británico del Océano Índico');
INSERT INTO covid19.pais VALUES ('TF', 'Territorios Australes Franceses');
INSERT INTO covid19.pais VALUES ('PS', 'Territorios Palestinos');
INSERT INTO covid19.pais VALUES ('TL', 'Timor-Leste');
INSERT INTO covid19.pais VALUES ('TG', 'Togo');
INSERT INTO covid19.pais VALUES ('TK', 'Tokelau');
INSERT INTO covid19.pais VALUES ('TO', 'Tonga');
INSERT INTO covid19.pais VALUES ('TT', 'Trinidad y Tobago');
INSERT INTO covid19.pais VALUES ('TA', 'Tristán de Acuña');
INSERT INTO covid19.pais VALUES ('TN', 'Túnez');
INSERT INTO covid19.pais VALUES ('TM', 'Turkmenistán');
INSERT INTO covid19.pais VALUES ('TR', 'Turquía');
INSERT INTO covid19.pais VALUES ('TV', 'Tuvalu');
INSERT INTO covid19.pais VALUES ('UA', 'Ucrania');
INSERT INTO covid19.pais VALUES ('UG', 'Uganda');
INSERT INTO covid19.pais VALUES ('UY', 'Uruguay');
INSERT INTO covid19.pais VALUES ('UZ', 'Uzbekistán');
INSERT INTO covid19.pais VALUES ('VU', 'Vanuatu');
INSERT INTO covid19.pais VALUES ('VE', 'Venezuela');
INSERT INTO covid19.pais VALUES ('VN', 'Vietnam');
INSERT INTO covid19.pais VALUES ('WF', 'Wallis y Futuna');
INSERT INTO covid19.pais VALUES ('YE', 'Yemen');
INSERT INTO covid19.pais VALUES ('DJ', 'Yibuti');
INSERT INTO covid19.pais VALUES ('ZM', 'Zambia');
INSERT INTO covid19.pais VALUES ('ZW', 'Zimbabue');


--
-- TOC entry 2279 (class 0 OID 90435)
-- Dependencies: 204
-- Data for Name: registro; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2280 (class 0 OID 90441)
-- Dependencies: 205
-- Data for Name: registro_formulario; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2283 (class 0 OID 90452)
-- Dependencies: 208
-- Data for Name: registro_ubicacion; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2285 (class 0 OID 90457)
-- Dependencies: 210
-- Data for Name: reporte_salud_paciente; Type: TABLE DATA; Schema: covid19; Owner: -
--



--
-- TOC entry 2287 (class 0 OID 90465)
-- Dependencies: 212
-- Data for Name: tipo_paciente_diagnostico; Type: TABLE DATA; Schema: covid19; Owner: -
--

INSERT INTO covid19.tipo_paciente_diagnostico VALUES ('positivo', 'Caso Confirmado', 12, true, 12, true);
INSERT INTO covid19.tipo_paciente_diagnostico VALUES ('negativo', 'Examen Negativo', NULL, false, NULL, false);
INSERT INTO covid19.tipo_paciente_diagnostico VALUES ('sospechoso', 'Caso Sospechoso', 24, true, 24, true);
INSERT INTO covid19.tipo_paciente_diagnostico VALUES ('alta_confirmado', 'Alta de Caso Confirmado', NULL, false, NULL, false);
INSERT INTO covid19.tipo_paciente_diagnostico VALUES ('alta_aislamiento', 'Alta de Aislamiento', NULL, false, NULL, false);
INSERT INTO covid19.tipo_paciente_diagnostico VALUES ('fallecido', 'Fallecido', NULL, false, NULL, false);


--
-- TOC entry 2288 (class 0 OID 90473)
-- Dependencies: 213
-- Data for Name: tipo_registro; Type: TABLE DATA; Schema: covid19; Owner: -
--

INSERT INTO covid19.tipo_registro VALUES ('ingreso_pais', 'Viajeros que llegaron al País');
INSERT INTO covid19.tipo_registro VALUES ('aislamiento_confirmado', 'Casos confirmados de COVID-19');
INSERT INTO covid19.tipo_registro VALUES ('aislamiento_contacto', 'Contactos de casos confirmados de COVID-19');
INSERT INTO covid19.tipo_registro VALUES ('caso_sospechoso', 'Caso sospechoso de COVID-19');
INSERT INTO covid19.tipo_registro VALUES ('examen_laboratorio', 'Examen de Laboratorio de COVID-19');


--
-- TOC entry 2290 (class 0 OID 90495)
-- Dependencies: 217
-- Data for Name: configuracion; Type: TABLE DATA; Schema: covid19admin; Owner: -
--

INSERT INTO covid19admin.configuracion VALUES (5, 'ACTIVO', 'url.host', 'https://url/');
INSERT INTO covid19admin.configuracion VALUES (7, 'ACTIVO', 'adminportalpy.url', 'https://localhost:4200/#/');
INSERT INTO covid19admin.configuracion VALUES (9, 'ACTIVO', 'adminportalpy.redis.port', '1122');
INSERT INTO covid19admin.configuracion VALUES (11, 'ACTIVO', 'adminportalpy.redis.env', 'desarrollo');
INSERT INTO covid19admin.configuracion VALUES (6, 'INACTIVO', 'adminportalpyA.url', 'https://url/');
INSERT INTO covid19admin.configuracion VALUES (2, 'ACTIVO', 'mail.smtp.port', '465');
INSERT INTO covid19admin.configuracion VALUES (3, 'ACTIVO', 'mail.smtp.user', '');
INSERT INTO covid19admin.configuracion VALUES (1, 'ACTIVO', 'mail.smtp.host', '');
INSERT INTO covid19admin.configuracion VALUES (4, 'ACTIVO', 'mail.smtp.pass', '');
INSERT INTO covid19admin.configuracion VALUES (8, 'ACTIVO', 'adminportalpy.redis.host', 'localhost');
INSERT INTO covid19admin.configuracion VALUES (12, 'ACTIVO', 'covid19.onetimetoken.seconds_duration', '300');
INSERT INTO covid19admin.configuracion VALUES (10, 'ACTIVO', 'adminportalpy.redis.timetolive', '21600');
INSERT INTO covid19admin.configuracion VALUES (13, 'ACTIVO', 'covid19.dominioapp', 'https://devurl.gov.py/');
INSERT INTO covid19admin.configuracion VALUES (14, 'ACTIVO', 'covid19.nombre_app', 'Covid19 - Paraguay');
INSERT INTO covid19admin.configuracion VALUES (15, 'ACTIVO', 'covid19.path_cambiar_clave', 'login/cambiar-clave');
INSERT INTO covid19admin.configuracion VALUES (16, 'ACTIVO', 'covid19.agendados.lastId', '1');


--
-- TOC entry 2291 (class 0 OID 90501)
-- Dependencies: 218
-- Data for Name: login_automatico; Type: TABLE DATA; Schema: covid19admin; Owner: -
--



--
-- TOC entry 2293 (class 0 OID 90507)
-- Dependencies: 220
-- Data for Name: permiso; Type: TABLE DATA; Schema: covid19admin; Owner: -
--



--
-- TOC entry 2295 (class 0 OID 90515)
-- Dependencies: 222
-- Data for Name: rol; Type: TABLE DATA; Schema: covid19admin; Owner: -
--

INSERT INTO covid19admin.rol VALUES (1, true, 'Operador', 'Operador');
INSERT INTO covid19admin.rol VALUES (4, true, 'Tecnico Salud', 'Tecnico Salud');
INSERT INTO covid19admin.rol VALUES (3, true, 'Paciente', 'Paciente');
INSERT INTO covid19admin.rol VALUES (5, true, 'Registro Automatico Agendamiento', 'Registro Automatico Agendamiento');
INSERT INTO covid19admin.rol VALUES (2, true, 'Consulta', 'Consulta');


--
-- TOC entry 2297 (class 0 OID 90523)
-- Dependencies: 224
-- Data for Name: rol_permiso; Type: TABLE DATA; Schema: covid19admin; Owner: -
--



--
-- TOC entry 2298 (class 0 OID 90526)
-- Dependencies: 225
-- Data for Name: rol_usuario; Type: TABLE DATA; Schema: covid19admin; Owner: -
--

INSERT INTO covid19admin.rol_usuario VALUES (1, 1);
INSERT INTO covid19admin.rol_usuario VALUES (2, 5);


--
-- TOC entry 2289 (class 0 OID 90479)
-- Dependencies: 214
-- Data for Name: usuario; Type: TABLE DATA; Schema: covid19admin; Owner: -
--

INSERT INTO covid19admin.usuario VALUES (2, true, 'agendamiento', 'agendamiento', 'agendamiento', 'agendamiento', 'agendamiento@mspbs.gov.py', '$2a$10$O/3XYMVSFkwq7upXdyup.eVQtAfU0U.gYOkFEqv.4J9VyYfgXGfv2', '595981123456', NULL, true, '595981123456', NULL, NULL);
INSERT INTO covid19admin.usuario VALUES (1, true, 'admin', 'admin', 'sistema', '123456', 'admin@dominio.gov.py', '$2a$10$adTl19wPhEs9y2CKDIECKOc0CUQr9iZ/rAF7Uks.lInDkF/Uaab9m', '595991123456', NULL, true, '595991123456', NULL, NULL);


--
-- TOC entry 2329 (class 0 OID 0)
-- Dependencies: 176
-- Name: audit_log_log_id_seq; Type: SEQUENCE SET; Schema: audit; Owner: -
--

SELECT pg_catalog.setval('audit.audit_log_log_id_seq', 1, false);


--
-- TOC entry 2330 (class 0 OID 0)
-- Dependencies: 178
-- Name: censo_contacto_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.censo_contacto_id_seq', 1, false);


--
-- TOC entry 2331 (class 0 OID 0)
-- Dependencies: 180
-- Name: diagnostico_accion_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.diagnostico_accion_id_seq', 1, false);


--
-- TOC entry 2332 (class 0 OID 0)
-- Dependencies: 182
-- Name: diagnostico_recomendacion_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.diagnostico_recomendacion_id_seq', 1, false);


--
-- TOC entry 2333 (class 0 OID 0)
-- Dependencies: 184
-- Name: examen_laboratorial_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.examen_laboratorial_id_seq', 1, false);


--
-- TOC entry 2334 (class 0 OID 0)
-- Dependencies: 186
-- Name: form_seccion_datos_basicos_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.form_seccion_datos_basicos_id_seq', 1, false);


--
-- TOC entry 2335 (class 0 OID 0)
-- Dependencies: 188
-- Name: form_seccion_datos_clinicos_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.form_seccion_datos_clinicos_id_seq', 1, false);


--
-- TOC entry 2336 (class 0 OID 0)
-- Dependencies: 191
-- Name: historico_clinico_datos_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.historico_clinico_datos_id_seq', 1, false);


--
-- TOC entry 2337 (class 0 OID 0)
-- Dependencies: 192
-- Name: historico_clinico_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.historico_clinico_id_seq', 1, false);


--
-- TOC entry 2338 (class 0 OID 0)
-- Dependencies: 194
-- Name: historico_diagnostico_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.historico_diagnostico_id_seq', 1, false);


--
-- TOC entry 2339 (class 0 OID 0)
-- Dependencies: 196
-- Name: notificacion_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.notificacion_id_seq', 1, false);


--
-- TOC entry 2340 (class 0 OID 0)
-- Dependencies: 199
-- Name: paciente_datos_personales_basicos_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.paciente_datos_personales_basicos_id_seq', 1, false);


--
-- TOC entry 2341 (class 0 OID 0)
-- Dependencies: 201
-- Name: paciente_estado_salud_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.paciente_estado_salud_id_seq', 1, false);


--
-- TOC entry 2342 (class 0 OID 0)
-- Dependencies: 202
-- Name: paciente_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.paciente_id_seq', 1, false);


--
-- TOC entry 2343 (class 0 OID 0)
-- Dependencies: 206
-- Name: registro_formulario_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.registro_formulario_id_seq', 1, false);


--
-- TOC entry 2344 (class 0 OID 0)
-- Dependencies: 207
-- Name: registro_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.registro_id_seq', 1, false);


--
-- TOC entry 2345 (class 0 OID 0)
-- Dependencies: 209
-- Name: registro_ubicacion_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.registro_ubicacion_id_seq', 1, false);


--
-- TOC entry 2346 (class 0 OID 0)
-- Dependencies: 211
-- Name: reporte_salud_paciente_id_seq; Type: SEQUENCE SET; Schema: covid19; Owner: -
--

SELECT pg_catalog.setval('covid19.reporte_salud_paciente_id_seq', 1, false);


--
-- TOC entry 2347 (class 0 OID 0)
-- Dependencies: 219
-- Name: login_automatico_id_seq; Type: SEQUENCE SET; Schema: covid19admin; Owner: -
--

SELECT pg_catalog.setval('covid19admin.login_automatico_id_seq', 1, false);


--
-- TOC entry 2348 (class 0 OID 0)
-- Dependencies: 221
-- Name: permiso_id_seq; Type: SEQUENCE SET; Schema: covid19admin; Owner: -
--

SELECT pg_catalog.setval('covid19admin.permiso_id_seq', 1, false);


--
-- TOC entry 2349 (class 0 OID 0)
-- Dependencies: 223
-- Name: rol_id_seq; Type: SEQUENCE SET; Schema: covid19admin; Owner: -
--

SELECT pg_catalog.setval('covid19admin.rol_id_seq', 1, false);


--
-- TOC entry 2350 (class 0 OID 0)
-- Dependencies: 226
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: covid19admin; Owner: -
--

SELECT pg_catalog.setval('covid19admin.usuario_id_seq', 100, false);


-- Completed on 2020-08-13 11:01:51

--
-- PostgreSQL database dump complete
--

