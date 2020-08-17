CREATE TABLE covid19.tipo_paciente_diagnostico (
    id varchar(50) PRIMARY KEY,
    descripcion varchar(512) NOT NULL,
    frecuencia_reporte_ubicacion_horas integer,
    debe_reportar_ubicacion boolean NOT NULL DEFAULT false
);
