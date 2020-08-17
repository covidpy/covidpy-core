CREATE TABLE covid19.tipo_registro (
    id varchar(255) primary key,
    descripcion varchar(1024) not null
);

INSERT INTO covid19.tipo_registro(id, descripcion) VALUES
    ('ingreso_pais', 'Viajeros que llegaron al País'),
    ('aislamiento_confirmado', 'Casos confirmados de COVID-19'),
    ('aislamiento_contacto', 'Contactos de casos confirmados de COVID-19'),
    ('caso_sospechoso', 'Caso sospechoso de COVID-19'),
    ('examen_laboratorio', 'Examen de Laboratorio de COVID-19');