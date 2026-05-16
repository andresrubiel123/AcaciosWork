USE tienda_acacios;

/* =========================================================
DATOS INICIALES
@author RADJ
========================================================= */

INSERT INTO roles (
    nombre,
    descripcion
)
VALUES
('Administrador', 'Control total del sistema'),
('Auxiliar', 'Acceso operativo limitado');


INSERT INTO tipos_documentos (
    nombre
)
VALUES
('NIT'),
('Cédula de ciudadanía'),
('Cédula de extranjería');


INSERT INTO categorias (
    nombre
)
VALUES
('Abarrotes'),
('Limpieza'),
('Bebidas');
