create view covid19.V_REPORTE_UBICACION as
select
pdpb.id_paciente
, pdpb.numero_documento
, timestamp_creacion
, lat_reportado
, long_reportado
from
covid19.paciente_datos_personales_basicos pdpb
join covid19.registro_ubicacion ru on pdpb.id_paciente=ru.id_paciente
order by
pdpb.id_paciente,ru.id desc;