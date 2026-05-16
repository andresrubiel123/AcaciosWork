# AcaciosWork - Backend (El Cerebro)

## Descripción
Este es el núcleo central (Core API) del ecosistema **AcaciosWork**, diseñado para gestionar de manera integral operaciones comerciales multiplataforma (Web, Desktop y Android). Desarrollado con el estándar más moderno de **Java 25** y **Spring Boot 4**, este backend centraliza la lógica de negocio, la persistencia de datos y la seguridad, garantizando que todos los clientes consuman información consistente y en tiempo real.

### Funcionalidades Clave:
- **Gestión POS**: Registro de ventas con cálculo automático de totales e impuestos.
- **Control de Inventario**: Seguimiento de stock, categorías y alertas de reabastecimiento crítico.
- **Seguridad Robusta**: Autenticación basada en JWT y control de acceso por roles (RBAC).
- **Soporte Multiplataforma**: API REST optimizada para clientes Swing, Web (Vanilla JS) y Android.
- **Generación de QR/Barcodes**: Capacidad integrada para manejo de códigos de barras y QR.

## Tecnologías
- **Java 25+** (Utilizando las últimas características del lenguaje).
- **Spring Boot 4.0.6** (Ecosistema moderno y reactivo).
- **Spring Data JPA** (Persistencia eficiente con MySQL).
- **Spring Security + JWT 0.13.0** (Seguridad sin estado).
- **MySQL 8.0+** (Base de datos relacional robusta).
- **SpringDoc / OpenAPI 3** (Documentación interactiva de la API).

## Estructura de Paquetes
- `controller`: Definición de endpoints REST y manejo de peticiones.
- `service`: Capa de servicios que contiene la lógica de negocio pura.
- `repository`: Abstracción de acceso a datos mediante Spring Data.
- `model`: Entidades JPA que mapean el esquema de la base de datos.
- `dto`: Objetos para transferencia de datos y desacoplamiento de la API.
- `config`: Configuraciones críticas (Seguridad, CORS, Swagger, JWT).

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Arquitectura Base**: Configuración de Spring Boot 4 y JDK 25.
- **Seguridad**: Sistema de Login, generación/validación de JWT y filtros de seguridad.
- **CRUDs Core**: Usuarios, Roles, Clientes, Proveedores, Categorías y Productos.
- **Ventas**: Implementación de `Venta` y `DetalleVenta` con persistencia atómica.
- **Documentación**: Swagger UI totalmente funcional para pruebas de endpoints.

### 🔄 En Desarrollo / Estabilización
- **Alertas de Stock**: Refinamiento del sistema de alertas para productos mínimos.
- **Cierre de Caja**: Lógica para el balance diario y reportes de facturación.
- **Módulo de Reportes**: Estructuración de consultas complejas para estadísticas de ventas.

---

# Sugerencias y Próximos Pasos (Lo que falta)

1.  **Pruebas Automatizadas**: Implementar cobertura de tests unitarios y de integración (JUnit 5 + Mockito).
2.  **Exportación de Datos**: Añadir soporte para generar reportes en formatos PDF y Excel (Apache POI / iText).
3.  **Sincronización Android**: Validar y optimizar todos los endpoints para el consumo desde la app móvil nativa.
4.  **Dockerización**: Crear `Dockerfile` y `docker-compose.yml` para facilitar el despliegue en entornos de producción.
5.  **Notificaciones**: Integrar un sistema de notificaciones (Push o Email) para alertas críticas de inventario.

---

# Documentación Técnica

## REGLAS TÉCNICAS OBLIGATORIAS (PROHIBICIÓN DE SALTOS)
*   **AISLAMIENTO DE DATOS**: Queda estrictamente PROHIBIDO que el Frontend, Desktop o Android realicen conexiones directas a la base de datos. Toda transacción DEBE pasar por el Backend.
*   **REGLA DE ORO (ID STANDARD)**: Todos los identificadores (ID) en MySQL deben ser `BIGINT UNSIGNED`. En el código Java, deben mapearse exclusivamente como tipo `Long`.
*   **ENTORNO**: Desarrollo mandatorio en **Java 25** y **Spring Boot 4.0.6**.
*   **REFERENCIA DE DATOS (LECTURA OBLIGATORIA)**: Antes de generar entidades JPA, Repositorios o DTOs, DEBES leer el esquema de base de datos actual en la carpeta `database/`.

---

## ESTÁNDAR DE DOCUMENTACIÓN Y COMENTARIOS
Todo bloque de código generado debe incluir la firma del autor y una descripción funcional breve:

| Lenguaje / Tecnología | Formato Requerido |
| :--- | :--- |
| **Java / Spring / JS / Kotlin** | `/** Descripción breve. @author RADJ */` |
| **CSS / MySQL** | `/* Descripción breve. @author RADJ */` |
| **HTML** | `<!-- Descripción breve. @author RADJ -->` |

---

## STACK TECNOLÓGICO DETALLADO
*   **Lenguajes**: Java 25, Kotlin, JavaScript, SQL, HTML5, CSS3.
*   **Frameworks**: Spring Boot 4.0.6 (Data JPA, Security, Validation).
*   **Base de Datos**: MySQL 8.0+.
*   **Librerías Extra**: Lombok 1.18.46, JJWT 0.13.0, ZXing 3.5.1, SpringDoc 2.8.5.