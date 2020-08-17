UPDATE covid19.reporte_salud_paciente r_update
SET fiebre_ayer = (
    CASE
        WHEN (
                'si' IN (
                (
                    SELECT rsp.sentis_fiebre
                    FROM covid19.reporte_salud_paciente rsp
                             LEFT JOIN covid19.registro_formulario rf ON rsp.id_registro_formulario = rf.id
                    WHERE rf.id_paciente = (
                        select id_paciente
                        from covid19.registro_formulario rf1
                        WHERE rf1.id = r_update.id_registro_formulario
                    )
                      AND rsp.timestamp_creacion >= (r_update.timestamp_creacion::date) - 1
                      AND rsp.timestamp_creacion < (r_update.timestamp_creacion::date)
                )
                UNION (
                    SELECT rsp.fiebre_ayer
                    FROM covid19.reporte_salud_paciente rsp
                             LEFT JOIN covid19.registro_formulario rf ON rsp.id_registro_formulario = rf.id
                    WHERE
                        rf.id_paciente = (
                            select id_paciente
                            from covid19.registro_formulario rf1
                            WHERE rf1.id = r_update.id_registro_formulario
                        ) AND
                          ( -- Primer reporte de salud fue hoy, se toma el valor de fiebre_ayer
                              select min(rsp2.timestamp_creacion)::date
                              FROM covid19.reporte_salud_paciente rsp2
                                       LEFT JOIN covid19.registro_formulario rf2 ON rsp2.id_registro_formulario = rf2.id
                              WHERE rf2.id_paciente =
                                    (select id_paciente
                                     from covid19.registro_formulario rf1
                                     WHERE rf1.id = r_update.id_registro_formulario)
                    ) = r_update.timestamp_creacion::date
                    ORDER BY rsp.timestamp_creacion
                    LIMIT 1
                )
            )
            ) THEN 'si'
        ELSE 'no'
        END
    )
WHERE fiebre_ayer is null;