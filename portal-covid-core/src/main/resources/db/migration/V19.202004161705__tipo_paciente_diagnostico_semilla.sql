INSERT INTO covid19.tipo_paciente_diagnostico(id, descripcion, frecuencia_reporte_ubicacion_horas, debe_reportar_ubicacion)
VALUES
    ('positivo', 'Caso Confirmado', 12,  true),
    ('negativo', 'Examen Negativo', null,  false),
    ('sospechoso', 'Caso Sospechoso', 24,  true),
    ('alta_confirmado', 'Alta de Caso Confirmado', null,  false),
    ('alta_aislamiento', 'Alta de Aislamiento', null,  false),
    ('fallecido', 'Fallecido', null,  false);
