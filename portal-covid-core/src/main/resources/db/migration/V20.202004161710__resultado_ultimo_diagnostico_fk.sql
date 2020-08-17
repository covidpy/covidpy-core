ALTER TABLE covid19.paciente
    ADD CONSTRAINT paciente_tipo_paciente_diagnostico_fk
        FOREIGN KEY (resultado_ultimo_diagnostico)
        REFERENCES covid19.tipo_paciente_diagnostico(id);