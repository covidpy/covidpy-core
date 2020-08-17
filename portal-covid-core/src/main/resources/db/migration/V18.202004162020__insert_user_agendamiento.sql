INSERT INTO covid19admin.usuario (id,activo,username,nombre,apellido,cedula,email,password,telefono,token_reset,estado_contacto,celular) VALUES (92,true,'agendamiento','agendamiento','agendamiento','agendamiento','agendamiento@mspbs.gov.py','$2a$10$AFchkTDd3entFCBxWW1YH.2ML38UkfREpGy79sHX3CmYZOGKOCraS','595961123456',NULL,true,'595961123456');

INSERT INTO covid19admin.rol (id,activo,descripcion,nombre) VALUES (5,true,'Registro Automatico Agendamiento','Registro Automatico Agendamiento');

INSERT INTO covid19admin.rol_usuario (usuario_id,rol_id) VALUES (92,5);