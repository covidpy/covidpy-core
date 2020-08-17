DROP VIEW covid19.v_reporte_registro_paciente;

create view covid19.v_reporte_registro_paciente as
SELECT 
rffsdb.fecha_creacion
,fsdb.nombre
,fsdb.apellido
, case fsdb.tipo_documento when '0' then 'Cédula de Identidad' when '1' then 'Pasaporte' end "tipo_documento"
, fsdb.numero_documento
, fsdb.numero_celular
, fsdb.fecha_nacimiento
, '' as "estado_civil"
, fsdb.direccion_domicilio
, fsdb.ciudad_domicilio
, fsdb.departamento_domicilio
, r.tipo_registro "tipo_registro_ingreso"
, case when p.resultado_ultimo_diagnostico is null then case r.tipo_registro when 'aislamiento_confirmado' then 'positivo' else 'sospechoso' end else p.resultado_ultimo_diagnostico end "resultado_ultimo_diagnostico"
, p.fecha_ultimo_diagnostico
, case when u.password is not null then 'Si' else 'No' end "abrio_sms"
, case when fsdc.id is null then 'No' else  'Si' end "completo_factores_riesgo"
, clasificacion_paciente
, inicio_seguimiento
, fin_seguimiento
, p.inicio_aislamiento
, fin_aislamiento
, fin_previsto_aislamiento
, rsp.timestamp_creacion "fecha ultimo reporte salud"
, ru.timestamp_creacion "fecha ultima ubicacion"
, ru.lat_dispositivo
, ru.long_dispositivo
, ru.lat_reportado
, ru.long_reportado
FROM covid19.form_seccion_datos_basicos fsdb
join covid19.registro_formulario rffsdb on fsdb.id_registro_formulario=rffsdb.id
join covid19.registro r on r.id=rffsdb.id_registro
left join covid19.paciente_datos_personales_basicos pdpb on pdpb.numero_documento=fsdb.numero_documento
left join covid19.paciente p on p.id=pdpb.id_paciente
left join covid19admin.usuario u on p.id_usuario=u.id
left join covid19.form_seccion_datos_clinicos fsdc on fsdb.id_registro_formulario=fsdc.id_registro_formulario
left join (
  select distinct on(rf.id_paciente)
  *
  from covid19.registro_formulario rf 
  join covid19.reporte_salud_paciente rsp on rsp.id_registro_formulario=rf.id
  order by rf.id_paciente, rf.id desc
) rsp on p.id=rsp.id_paciente 
left join (
  select distinct on(ru.id_paciente)
  *
  from covid19.registro_ubicacion ru
  order by ru.id_paciente, ru.id desc
) ru on ru.id_paciente=p.id
order by 
fsdb.id;