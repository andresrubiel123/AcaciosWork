# Arquitectura del Proyecto AcaciosWork

Este documento presenta la arquitectura actual del ecosistema **AcaciosWork**, detallando la interacción entre los diferentes clientes y la estructura interna del backend.

## 1. Diagrama de Arquitectura de Sistema (Alto Nivel)

Este diagrama muestra cómo interactúan los diferentes módulos del proyecto. El núcleo es el **Backend (Spring Boot)**, que centraliza la lógica de negocio y la persistencia de datos para múltiples plataformas.

```mermaid
graph TD
    subgraph "Clientes (Frontend)"
        A["Desktop App<br/>(Java Swing)"]
        B["Mobile App<br/>(Android)"]
        C["Web Frontend<br/>(Modern Web)"]
    end

    subgraph "Capa de API"
        D["REST API Gateway<br/>(Spring Boot 4 / JDK 25)"]
    end

    subgraph "Persistencia"
        E[("MySQL Database")]
    end

    A -->|"JSON/HTTP"| D
    B -->|"JSON/HTTP"| D
    C -->|"JSON/HTTP"| D
    D <-->|"JPA/Hibernate"| E

    style D fill:#f9f,stroke:#333,stroke-width:4px
    style E fill:#fff,stroke:#333,stroke-width:2px
```

---

## 2. Diagrama de Arquitectura Interna del Backend

Este diagrama detalla la arquitectura de capas utilizada dentro del módulo `acacioswork-backend`, siguiendo las mejores prácticas de Spring Boot.

```mermaid
graph LR
    subgraph "Capa de Entrada (Request)"
        Req["HTTP Request"]
    end

    subgraph "Backend Application (Spring Boot)"
        direction TB
        subgraph "Security Layer"
            Sec["Spring Security<br/>(CORS, Auth, JWT)"]
        end

        subgraph "Presentation Layer"
            Ctrl["REST Controllers<br/>(Categorias, Clientes, Usuarios...)"]
        end

        subgraph "Business Logic Layer"
            Serv["Services / Managers<br/>(UsuarioManager, ServiceImpl)"]
        end

        subgraph "Data Access Layer"
            Repo["JPA Repositories<br/>(Spring Data JPA)"]
        end

        subgraph "Domain Model"
            Ent["Entities / Models<br/>(POJOs con Lombok)"]
        end
    end

    subgraph "External"
        DB[("MySQL Database")]
    end

    Req --> Sec
    Sec --> Ctrl
    Ctrl --> Serv
    Serv --> Repo
    Repo <--> Ent
    Repo <--> DB

    style Sec fill:#ff9,stroke:#333
    style Ctrl fill:#bbf,stroke:#333
    style Serv fill:#bfb,stroke:#333
    style Repo fill:#fbb,stroke:#333
    style Ent fill:#eee,stroke:#333
```
---
### 3. Arquitectura General
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

### 4. Mapa Estructural (Project Structure)
```mermaid
graph TD
    Root["AcaciosWork (Root)"]
    
    Root --> BE["acacioswork-backend (Core API)"]
    Root --> DE["acacioswork-desktop (Admin Swing)"]
    Root --> FE["acacioswork-frontend (Web Dash)"]
    Root --> AN["acacioswork-android (Mobile App)"]
    Root --> DB_S["database (SQL Scripts)"]
    Root --> CTX["project-context.md (AI/Docs)"]

    BE --> BE_CTRL["Controladores"]
    BE --> BE_SRV["Servicios (Business)"]
    BE --> BE_MOD["Modelos (JPA Entities)"]

    DE --> DE_UI["Interfaz (Swing JPanels)"]
    DE --> DE_MOD["Modelos Locales"]

    FE --> FE_HTML["Vistas (HTML/CSS)"]
    FE --> FE_JS["Lógica API (JS Fetch)"]
```

### 5. Flujo de Datos (Data Flow)
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

### 6. Diagrama de Módulos (Lógica de Negocio)
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

### 7. Diagrama de Base de Datos (ERD Simplificado)
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

### 8. API Map (Integraciones)
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
### Notas Clave de la Arquitectura:
*   **Multi-Cliente:** El backend está diseñado para servir a una aplicación de escritorio (Swing), móvil (Android) y web simultáneamente.
*   **Modernización:** Uso de **JDK 25** y **Spring Boot 4** para aprovechar las últimas optimizaciones del lenguaje.
*   **Estandarización:** Identificadores `BIGINT UNSIGNED` en base de datos mapeados como `Long` en Java para consistencia y escalabilidad.
*   **Seguridad:** Implementación de seguridad centralizada para manejar CORS y autenticación para diferentes orígenes.
*   **Modelo de Datos Homogéneo:** Coherencia en la denominación de campos de inventario (ej: `stockActual`, `stockMinimo`, `stockOptimo` y `unidadMedida`) en todas las capas y lenguajes del ecosistema (Java, Kotlin, JavaScript, MySQL).
