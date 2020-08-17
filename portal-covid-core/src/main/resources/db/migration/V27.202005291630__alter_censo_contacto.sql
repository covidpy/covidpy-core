ALTER TABLE covid19.censo_contacto ADD COLUMN creado_por bigint;
ALTER TABLE covid19.censo_contacto ADD COLUMN modificado_por bigint;
ALTER TABLE covid19.censo_contacto ADD COLUMN fecha_modificacion timestamp without time zone;

ALTER TABLE covid19.censo_contacto ADD CONSTRAINT contacto_usuario_crea_id_fk FOREIGN KEY (creado_por)
    REFERENCES covid19admin.usuario (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE covid19.censo_contacto ADD CONSTRAINT contacto_usuario_modifica_id_fk FOREIGN KEY (modificado_por)
    REFERENCES covid19admin.usuario (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;