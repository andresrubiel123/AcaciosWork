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

### Notas Clave de la Arquitectura:
*   **Multi-Cliente:** El backend está diseñado para servir a una aplicación de escritorio (Swing), móvil (Android) y web simultáneamente.
*   **Modernización:** Uso de **JDK 25** y **Spring Boot 4** para aprovechar las últimas optimizaciones del lenguaje.
*   **Estandarización:** Identificadores `BIGINT UNSIGNED` en base de datos mapeados como `Long` en Java para consistencia y escalabilidad.
*   **Seguridad:** Implementación de seguridad centralizada para manejar CORS y autenticación para diferentes orígenes.
