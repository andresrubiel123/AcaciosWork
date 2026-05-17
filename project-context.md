# ARCHIVO DE CONTEXTO: ACACIOSWORK
**INSTRUCCIÓN PARA DESARROLLADORES Y AGENTES INTELIGENTES**

> **ESTADO**: DESARROLLO ACTIVO - FASE DE ESTABILIZACIÓN Y ESCALADO
> **AUTOR PRINCIPAL**: RADJ - andresrubiel@hotmail.com
> **ÚLTIMA REVISIÓN**: 17 de Mayo, 2026

---

## 🌍 VISIÓN GENERAL DEL PROYECTO
AcaciosWork es un ecosistema de gestión empresarial (ERP/POS) multiplataforma diseñado bajo una arquitectura de **Backend Centralizado**. El sistema permite la administración de inventarios, ventas, usuarios y clientes desde aplicaciones de escritorio, web y móviles, garantizando que toda la lógica de negocio y los datos residan en un único punto de verdad.

---

## 📊 DIAGRAMAS DEL SISTEMA

### 1. Arquitectura General
```mermaid
graph TD
    subgraph Clientes
        Web["🌐 Dashboard Web (HTML/JS)"]
        Desk["🖥️ Admin Desktop (Swing)"]
        Andr["📱 App Móvil (Kotlin)"]
    end

    subgraph Backend_Core ["Núcleo del Sistema"]
        API["🧠 Core API (Spring Boot 4)"]
        Auth["🔐 Seguridad JWT"]
        Logic["⚙️ Lógica de Negocio"]
    end

    subgraph Persistencia
        DB[("🗄️ MySQL (tienda_acacios)")]
    end

    Web <-->|HTTP/JSON + JWT| API
    Desk <-->|HTTP/JSON + JWT| API
    Andr <-->|HTTP/JSON + JWT| API

    API <--> Logic
    API <--> Auth
    Logic <--> DB
```

### 2. Mapa Estructural (Project Structure)
```mermaid
graph TD
    Root["AcaciosWork (Root)"]
    
    Root --> BE["acacioswork-backend (Core API)"]
    Root --> DE["acacioswork-desktop (Admin Swing)"]
    Root --> FE["acacioswork-frontend (Web Dash)"]
    Root --> AN["acacioswork-android (Mobile App)"]
    Root --> DB_S["database (SQL Scripts)"]
    Root --> CTX["proyect-context (AI/Docs)"]

    BE --> BE_CTRL["Controladores"]
    BE --> BE_SRV["Servicios (Business)"]
    BE --> BE_MOD["Modelos (JPA Entities)"]

    DE --> DE_UI["Interfaz (Swing JPanels)"]
    DE --> DE_MOD["Modelos Locales"]

    FE --> FE_HTML["Vistas (HTML/CSS)"]
    FE --> FE_JS["Lógica API (JS Fetch)"]
```

### 3. Flujo de Datos (Data Flow)
```mermaid
sequenceDiagram
    participant User as Usuario
    participant Client as Cliente (Web/Desk/And)
    participant API as Core API (Backend)
    participant DB as MySQL DB

    User->>Client: Realiza acción (Ej: Registrar Venta)
    Client->>API: Petición HTTP POST (JSON + JWT)
    API->>API: Valida Token y Reglas de Negocio
    API->>DB: Ejecuta Transacción SQL
    DB-->>API: Confirma Persistencia
    API-->>Client: Responde ApiResponse (Success/Data)
    Client->>User: Muestra Confirmación en UI
```

### 4. Diagrama de Módulos (Lógica de Negocio)
```mermaid
graph LR
    subgraph Gestion_Core ["Gestión de Identidad"]
        USR[Usuarios & Roles]
        PROV[Proveedores]
        CLI[Clientes]
    end

    subgraph Logistica ["Logística e Inventario"]
        INV[Inventario]
        PROD[Productos]
        CAT[Categorías]
        ALERT[Alertas de Stock]
    end

    subgraph Operaciones ["Operaciones de Venta"]
        VENTA[Punto de Venta - POS]
        DEV[Devoluciones]
        CAJA[Cierre de Caja]
    end

    subgraph Inteligencia ["Inteligencia de Negocio"]
        REP[Reportes & Estadísticas]
        HIST[Historial de Accesos]
    end

    Gestion_Core --> Logistica
    Logistica --> Operaciones
    Operaciones --> Inteligencia
```

### 5. Diagrama de Base de Datos (ERD Simplificado)
```mermaid
erDiagram
    ROL ||--o{ USUARIO : "asigna"
    USUARIO ||--o{ VENTA : "vende"
    CLIENTE ||--o{ VENTA : "compra"
    CATEGORIA ||--o{ PRODUCTO : "contiene"
    PRODUCTO ||--o{ DETALLE_VENTA : "se vende en"
    VENTA ||--|{ DETALLE_VENTA : "desglosa"
    PROVEEDOR ||--o{ PRODUCTO : "provee"
    PRODUCTO ||--|| INVENTARIO : "se almacena"
    PRODUCTO ||--o{ ALERTA_STOCK : "genera"
```

### 6. API Map (Integraciones)
```mermaid
graph LR
    subgraph Endpoints_Backend ["Endpoints API (REST)"]
        AUTH["/api/auth/** (Seguridad)"]
        USR_API["/api/usuarios (Gestión)"]
        PROD_API["/api/productos (Catálogo)"]
        INV_API["/api/inventario (Stock)"]
        VENT_API["/api/ventas (POS)"]
        REP_API["/api/reportes (BI)"]
    end

    subgraph Desktop_App ["Desktop (Admin/POS)"]
        D_AUTH["Login/JWT"]
        D_CRUD["CRUD Completo"]
        D_POS["Ventas POS"]
    end

    subgraph Web_Dashboard ["Web (Supervisión)"]
        W_AUTH["Login/JWT"]
        W_REP["Visualización BI"]
        W_INV["Consulta Stock"]
    end

    Desktop_App --> AUTH
    Desktop_App --> USR_API
    Desktop_App --> PROD_API
    Desktop_App --> INV_API
    Desktop_App --> VENT_API

    Web_Dashboard --> AUTH
    Web_Dashboard --> REP_API
    Web_Dashboard --> INV_API
```

---

### 1. Núcleo API (`acacioswork-backend/`)
*   **Configuración (Seguridad/JWT/CORS)**: `src/main/java/com/acacioswork/config/`
*   **Endpoints (Controladores)**: `src/main/java/com/acacioswork/controller/`
*   **Lógica de Negocio (Servicios)**: `src/main/java/com/acacioswork/service/`
*   **Entidades de Datos (JPA)**: `src/main/java/com/acacioswork/model/`
*   **Acceso a Datos (Repositorios)**: `src/main/java/com/acacioswork/repository/`
*   **Recursos (Propiedades/Env)**: `src/main/resources/`

### 2. Cliente de Escritorio (`acacioswork-desktop/`)
*   **Pantallas y UI (Swing)**: `src/main/java/com/acacioswork/interfaz_usuario/`
*   **Conectividad API (Util)**: `src/main/java/com/acacioswork/util/`
*   **Modelos Locales**: `src/main/java/com/acacioswork/model/`

### 3. Cliente Web (`acacioswork-frontend/`)
*   **Vistas (HTML)**: Raíz del módulo (`login.html`, `dashboard.html`).
*   **Estilos (CSS)**: `css/styles.css`
*   **Lógica API (JS)**: `js/api.js`

### 4. Cliente Móvil (`acacioswork-android/`)
*   **UI/ViewModels/Network**: `app/src/main/java/com/acacioswork/`

### 5. Base de Datos (`database/`)
*   **Esquema Actualizado**: `database/tienda_acacios.sql` (Lectura obligatoria para cambios en modelos).

---
## ESTÁNDAR DE DOCUMENTACIÓN Y COMENTARIOS
Todo bloque de código generado debe incluir: una descripción funcional breve y la firma del autor:

* Lenguaje Tecnología Formato Requerido
* **Java - Spring - JS / Kotlin** `/** Descripción breve. @author RADJ */`
* **CSS / MySQL** `/* Descripción breve. @author RADJ */`
* **HTML** `<!-- Descripción breve. @author RADJ -->`
* **JavaScript** `// Descripción breve. @author RADJ`

---

##  REGLAS TÉCNICAS OBLIGATORIAS (PROHIBICIÓN DE SALTOS)
1.  **AISLAMIENTO DE DATOS**: PROHIBIDO que clientes (Web, Desktop, Android) conecten directo a MySQL. Todo debe pasar por el Backend.
2.  **REGLA ID STANDARD**: Identificadores en MySQL: `BIGINT UNSIGNED`. En Java: `Long`.
3.  **ENTORNO**: Desarrollo mandatorio en **Java 25** y **Spring Boot 4.0.6**.
4.  **COMUNICACIÓN**: Exclusivamente vía **JSON/REST** con tokens **JWT** para autorización.

---

## 📈 ESTADO DEL AVANCE GLOBAL

### ✅ Finalizado (Producción Ready)
- **Seguridad**: Autenticación JWT en Backend y Clientes.
- **Gestión Core**: CRUDs de Usuarios, Clientes, Proveedores y Categorías operativos en Backend y Desktop.
- **Inventario**: Control de existencias básico y visualización.
- **Ventas**: Módulo de `Venta` y `DetalleVenta` con persistencia atómica.

### 🔄 En Proceso (Próximos Hitos)
- **Reportes**: Implementación de reportes visuales y exportación a PDF/Excel.
- **Alertas**: Sistema de notificaciones en tiempo real para stock crítico.
- **Android**: Implementación de la UI móvil y sincronización con la API.
- **Cierre de Caja**: Lógica contable para balance diario.

---

## 📝 REGISTRO DE CAMBIOS (LOG)
*   **2026-05-02**: Creación inicial del archivo de contexto y migración a `BIGINT`.
*   **2026-05-05**: Sincronización de CRUD de Usuarios entre Backend y Desktop.
*   **2026-05-12**: Estabilización de persistencia de Ventas y manejo de errores 409/400.
*   **2026-05-16**: **Estandarización de Documentación**: Se actualizaron todos los `README.md` del ecosistema para reflejar el stack tecnológico actual (Java 25, Spring Boot 4, FlatLaf, Kotlin 2).
*   **2026-05-16**: **Actualización de Contexto**: Reestructuración de este archivo para facilitar el mapeo de archivos a desarrolladores y agentes inteligentes.

---

## MAPEO RÁPIDO PARA DESARROLLADORES Y AGENTES INTELIGENTES
Si necesitas trabajar en un módulo específico, estos son los archivos clave:

| Tarea | Archivo/Ruta Principal |
| :--- | :--- |
| **Añadir un campo a la BD** | `database/tienda_acacios.sql` -> `backend/.../model/` -> `desktop/.../model/` |
| **Modificar la Seguridad** | `backend/.../config/SecurityConfig.java` |
| **Cambiar el diseño Desktop** | `desktop/.../interfaz_usuario/Administrador.java` |
| **Arreglar peticiones API Web** | `frontend/js/api.js` |
| **Añadir lógica de negocio** | `backend/.../service/` (Siempre usar servicios, no lógica en controladores) |
