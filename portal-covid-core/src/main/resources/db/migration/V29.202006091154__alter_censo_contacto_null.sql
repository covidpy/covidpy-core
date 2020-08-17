UPDATE covid19.censo_contacto
    SET creado_por = (SELECT p.id_usuario FROM covid19.paciente p WHERE p.id = id_paciente )
    WHERE creado_por IS NULL;
ALTER TABLE covid19.censo_contacto ALTER COLUMN creado_por SET NOT NULL;
