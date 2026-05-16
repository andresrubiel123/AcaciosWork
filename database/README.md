# TIENDA_ACACIOS - Base de Datos

Sistema de gestión comercial e inventario para tienda física.

---

# Estructura de Archivos

| Archivo | Descripción |
|---|---|
| 01_create_database.sql | Creación de la base de datos |
| 02_tables.sql | Creación de tablas y relaciones |
| 03_indexes.sql | Índices para optimización |
| 04_initial_data.sql | Datos iniciales del sistema |
| 05_views.sql | Vistas SQL |
| 06_triggers.sql | Triggers y validaciones |

---

# Orden de Ejecución

Ejecutar los archivos en el siguiente orden:

1. 01_create_database.sql
2. 02_tables.sql
3. 03_indexes.sql
4. 04_initial_data.sql
5. 05_views.sql
6. 06_triggers.sql

---

# Tecnologías

- MySQL
- Laragon
- HeidiSQL
- Spring Boot
- JPA / Hibernate

---

# Características

- Arquitectura normalizada
- Control de inventario
- Gestión de compras
- Gestión de ventas
- Historial de movimientos
- Auditoría básica
- Escalable

---

# Recomendaciones

- No modificar las tablas directamente en producción.
- Utilizar scripts versionados.
- Mantener respaldos periódicos.
- Usar Git para control de cambios.

---

@author RADJ