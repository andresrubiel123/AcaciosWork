# ARCHIVO DE CONTEXTO: ACACIOSWORK
** INSTRUCCIÓN PARA LA IA**

> **ESTADO**: DESARROLLO ACTIVO
> **AUTOR PRINCIPAL**: @author RADJ
> **ÚLTIMA REVISIÓN**: 02 de Mayo, 2026

---

## ARQUITECTURA DEL SISTEMA
Sistema de gestión integral de tiendas bajo un modelo multiplataforma:

1. **BACKEND CENTRALIZADO**: Única capa con acceso directo a la base de datos MySQL (`acacioswork_bd_big`).
2. **INTERFAZ WEB**: HTML5, CSS3 y JavaScript consume exclusivamente la API.
3. **CLIENTE ESCRITORIO**: Módulo especializado `acacioswork-desktop`conectado por API.
4. **INTEGRACIÓN MÓVIL**: Aplicación Android nativa que consume exclusivamente la API REST.

## Diagrama de Árbol de Carpetas:

AcaciosWork/ (Root)
├── acacioswork-backend/ (Core API - Spring Boot)
│   ├── src/main/java/com/acacioswork/
│   │   ├── config/ (Seguridad, CORS, JWT)
│   │   ├── controller/ (Endpoints REST: Usuarios, Clientes, etc.)
│   │   ├── model/ (Entidades JPA: Categoria, Cliente, Usuario)
│   │   ├── repository/ (Interfaces Spring Data JPA)
│   │   ├── service/ (Lógica de negocio: UsuarioManager)
│   │   ├── util/ (Clases auxiliares)
│   │   └── AcaciosWorkApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── .env.example
│   └── pom.xml
├── acacioswork-desktop/ (Cliente Windows - Java Swing)
│   ├── src/main/java/com/acacioswork/
│   │   ├── interfaz_usuario/ (Paneles, Formularios, Login)
│   │   ├── model/ (Modelos de datos locales)
│   │   ├── util/ (Conectividad API)
│   │   └── App.java (Clase Principal)
│   └── pom.xml
├── acacioswork-android/ (Cliente Móvil)
│   ├── app/src/main/java/...
│   └── build.gradle
├── acacioswork-frontend/ (Cliente Web - HTML/JS/CSS)
│   ├── css/ (Estilos visuales)
│   ├── js/ (Lógica de peticiones Fetch/API)
│   ├── login.html
│   └── dashboard.html
├── database/ (Scripts SQL)
│   ├── acacioswork_bd_big.sql
│   └── inventario_bodega.sql
├── ai-context/ (Contexto para IA)
├── arquitectura_acacioswork.md (Diagramas generados)
├── pom.xml (Parent POM / Configuración Global)
├── lombok.config (Configuración de anotaciones)
└── run_backend.bat (Script de ejecución rápida)

---

## REGLAS TÉCNICAS OBLIGATORIAS (PROHIBICIÓN DE SALTOS)
*   **AISLAMIENTO DE DATOS**: Queda estrictamente PROHIBIDO que el Frontend, Desktop o Android realicen conexiones directas a la base de datos. Toda transacción DEBE pasar por el Backend.
*   **REGLA DE ORO (ID STANDARD)**: Todos los identificadores (ID) en MySQL deben ser `BIGINT UNSIGNED`. En el código Java, deben mapearse exclusivamente como tipo `Long`.
*   **ENTORNO**: Desarrollo mandatorio en **Java 25** y **Spring Boot 4.0.6**.

*   **REFERENCIA DE DATOS (LECTURA OBLIGATORIA)**: Antes de generar entidades JPA, Repositorios o DTOs, DEBES leer el esquema de base de datos actual en la siguiente ruta: `database\acacioswork_bd_big.sql`

---

## ESTÁNDAR DE DOCUMENTACIÓN Y COMENTARIOS
Todo bloque de código generado debe incluir la firma del autor y una descripción funcional breve:

| Lenguaje / Tecnología | Formato Requerido |
| :--- | :--- |
| **Java / Spring / JS / Kotlin** | `/** Descripción breve. @author RADJ */` |
| **CSS / MySQL** | `/* Descripción breve. @author RADJ */` |
| **HTML** | `<!-- Descripción breve. @author RADJ -->` |

---

## STACK TECNOLÓGICO
*   **Lenguajes**: Java 25, Kotlin, JavaScript, SQL, HTML5, CSS3.
*   **Frameworks**: Spring Boot 4.0.6 (Data JPA, Security).
*   **Base de Datos**: MySQL 8.0+.

---

## REGISTRO DE CAMBIOS
*Instrucción para la IA: Cada vez que realices un cambio estructural o técnico importante, debes añadir una entrada en esta lista.*

*   **2026-05-02**: Creación de este archivo de contexto inicial.
*   **2026-05-02**: Migración exitosa de base de datos a tipos `BIGINT`.
*   **2026-05-02**: Configuración de `pom.xml` optimizada para compatibilidad con JDK 25 y Lombok 1.18.46.
*   **2026-05-05**: Sincronización de API: Se completaron los métodos CRUD en `UsuarioController` y `UsuarioService` para habilitar la gestión de usuarios desde el cliente desktop.
*   **2026-05-05**: Optimización de conectividad: Cambio de `127.0.0.1` a `localhost` en `ApiClient` del módulo desktop para mejorar la resolución de red local.
