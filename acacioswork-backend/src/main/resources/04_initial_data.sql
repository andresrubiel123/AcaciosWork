/* =========================================================
   DATOS INICIALES Y RESPALDO - TIENDA ACACIOS
   Sincronizado con el nuevo esquema de numero_documento
   @author RADJ / Antigravity
   ========================================================= */

USE tienda_acacios;

-- Limpieza preventiva (opcional, comentar si no se desea borrar lo actual)
-- DELETE FROM usuarios;
-- DELETE FROM clientes;

-- 1. ROLES
INSERT INTO roles (id, nombre, descripcion) VALUES
(1, 'Administrador', 'Control total del sistema'),
(2, 'Auxiliar', 'Acceso operativo limitado')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), descripcion=VALUES(descripcion);

-- 2. TIPOS DE DOCUMENTO (Sincronizado con HeidiSQL)
INSERT INTO tipos_documentos (id, nombre, activo) VALUES
(1, 'Cedula de Cuidadania', 1),
(2, 'Cedula de Extangeria', 1),
(3, 'Nit', 1)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), activo=VALUES(activo);

-- 3. CATEGORÍAS
INSERT INTO categorias (id, nombre) VALUES
(1, 'Abarrotes'),
(2, 'Limpieza'),
(3, 'Bebidas')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- 4. USUARIOS DE RESPALDO (Ejemplo basado en tus datos reales)
-- Ajusta la clave encriptada si es necesario. 111 es '$2a$10$8.VAgREUoE.H8bM9P1.J2.3GfS/6X.Wv9Rz6/3bX6G9oY7W2b1W0G' aprox.
INSERT INTO usuarios (
    numero_documento, id_tipo_documento, nombre, apellido, 
    telefono, email, usuario, clave, id_rol, activo, fecha_creacion
) VALUES (
    '1102720831', 1, 'Rubiel Andres', 'Diaz', 
    '3144655271', 'andresrubiel@gmail.com', 'Andres', 
    '$2a$10$6H6fI.T2C9r6lG3hW8fWz.Zf1mU.R7Y1v8mU.R7Y1v8mU.R7Y1v8', 1, 1, NOW()
) ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), email=VALUES(email);

-- 5. CLIENTES DE RESPALDO (Ejemplo)
INSERT INTO clientes (
    numero_documento, id_tipo_documento, nombre, telefono, 
    email, direccion, frecuente, activo, fecha_creacion
) VALUES (
    '22222222', 1, 'Cliente General', '000000', 
    'cliente@gmail.com', 'Calle Falsa 123', 0, 1, NOW()
) ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);
