# AcaciosWork - Ecosistema de Gestión Empresarial (SaaS)

AcaciosWork es una solución integral multiplataforma diseñada para la gestión de tiendas y pequeñas empresas. El sistema se basa en una arquitectura **SaaS (Software as a Service)** con un núcleo API centralizado y múltiples interfaces de cliente optimizadas para diferentes entornos.

---

## 🏗️ Arquitectura del Sistema

### 🧠 [Core API (Backend)](file:///c:/AcaciosWork/acacioswork-backend)
El "cerebro" del sistema. Gestiona la persistencia de datos en MySQL, la lógica de negocio y la seguridad JWT.
- **Stack**: Java 25, Spring Boot 4.0.6, JPA, JWT, MySQL 8.0.

### 🖥️ [Administración Desktop](file:///c:/AcaciosWork/acacioswork-desktop)
Interfaz robusta para la gestión pesada de inventarios, usuarios y Punto de Venta (POS).
- **Stack**: Java 25, Swing, FlatLaf (UI Moderna), Jackson (JSON).

### 🌐 [Dashboard Web](file:///c:/AcaciosWork/acacioswork-frontend)
Acceso ligero y universal para supervisión y consultas rápidas desde cualquier navegador.
- **Stack**: HTML5, CSS3, JavaScript (Vanilla ES6+).

### 📱 [App Móvil](file:///c:/AcaciosWork/acacioswork-android)
Gestión en movimiento para control de stock y alertas mediante dispositivos móviles.
- **Stack**: Kotlin 2.x, Android SDK, MVVM, Retrofit.

---

## 📂 Mapa del Repositorio

```text
AcaciosWork/
├── acacioswork-backend/   # API REST (Spring Boot)
├── acacioswork-desktop/   # App Escritorio (Swing)
├── acacioswork-frontend/  # App Web (HTML/JS)
├── acacioswork-android/   # App Móvil (Kotlin)
├── database/              # Scripts SQL y esquemas
├── proyect-context/       # Contexto detallado para Agentes de IA
└── run_backend.bat        # Lanzador rápido de desarrollo
```

---

## 🔱 Reglas de Oro del Proyecto

1.  **Aislamiento Total**: Solo el Backend tiene permiso para tocar la base de datos. Los clientes son 100% dependientes de la API.
2.  **Identidad Única**: Todos los IDs deben ser `Long` (Java) y `BIGINT UNSIGNED` (MySQL).
3.  **Seguridad JWT**: Toda petición privada debe incluir el `Authorization: Bearer <token>`.
4.  **Estándar de Código**: Todo bloque de código debe incluir firma de autor y descripción funcional.

---

## 🚀 Inicio Rápido (Desarrollo)

1.  **Base de Datos**: Importa `database/tienda_acacios.sql` en tu servidor MySQL.
2.  **Configuración**: Ajusta las credenciales en `acacioswork-backend/src/main/resources/application.properties`.
3.  **Backend**: Ejecuta el comando `mvn spring-boot:run` dentro de la carpeta del backend.
4.  **Clientes**: 
    - **Web**: Abre `acacioswork-frontend/login.html`.
    - **Desktop**: Ejecuta la clase `App.java` en el módulo desktop.

---

## 🤖 Información para Agentes Inteligentes
Si eres una IA trabajando en este proyecto, por favor consulta **[proyect-context/project-context.md](file:///c:/AcaciosWork/proyect-context/project-context.md)** antes de realizar cualquier cambio estructural. Contiene un mapeo detallado de archivos y reglas específicas de implementación.

