# AcaciosWork - Sistema de Gestión Empresarial

Este repositorio ha sido reorganizado bajo una arquitectura moderna y escalable (SaaS) con un backend centralizado y múltiples clientes.

## Estructura del Proyecto

### [Backend](file:///c:/AcaciosWork/acacioswork-backend)
API REST desarrollada con Spring Boot, Spring Data JPA y Spring Security. Es el núcleo que gestiona la lógica de negocio y la persistencia en MySQL.

### [Desktop](file:///c:/AcaciosWork/acacioswork-desktop)
Cliente de escritorio desarrollado con Java Swing. Consume la API REST para ofrecer una experiencia fluida al administrador y auxiliar.

### [Frontend Web](file:///c:/AcaciosWork/acacioswork-frontend)
Cliente web ligero construido con HTML5, CSS3 y JavaScript. Consume la API REST para ofrecer una experiencia fluida al administrador y auxiliar.

### [Android App](file:///c:/AcaciosWork/acacioswork-android)
Aplicación móvil nativa en Kotlin para la gestión remota del inventario y ventas (en desarrollo).

---
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

## Reglas del Proyecto
1. **Seguridad**: Solo el Backend tiene acceso a la base de datos MySQL.
2. **Comunicación**: Los clientes se comunican exclusivamente vía HTTP/JSON con el Backend.
3. **Escalabilidad**: El sistema permite añadir nuevos clientes (escritorio, iOS, webhooks) sin modificar el núcleo.

## Instalación Rápida
1. Configura la BD en `acacioswork-backend/src/main/resources/application.properties`.
2. Inicia el backend: `cd acacioswork-backend && mvn spring-boot:run`.
3. Abre `acacioswork-frontend/login.html` en tu navegador.

