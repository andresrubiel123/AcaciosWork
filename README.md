# AcaciosWork - Plataforma Inteligente de Gestión Empresarial

AcaciosWork es una **plataforma inteligente de gestión empresarial para negocios físicos**, diseñada como un ecosistema multiplataforma que va más allá del inventario tradicional. Combina gestión operativa (inventarios, ventas, clientes) con **inteligencia de negocio automatizada** que analiza datos en tiempo real para tomar decisiones estratégicas. Arquitectura **SaaS (Software as a Service)** con un núcleo API centralizado y múltiples interfaces de cliente.

---

## 🏗 Arquitectura del Sistema

### 🧠 [Core API (Backend)](file:///c:/AcaciosWork/acacioswork-backend)
El "cerebro" del sistema. Gestiona la persistencia de datos en MySQL, la lógica de negocio y la seguridad JWT.
- **Stack**: Java 25, Spring Boot 4.0.6, JPA, JWT, MySQL 8.0.

### 🖥 [Administración Desktop](file:///c:/AcaciosWork/acacioswork-desktop)
Interfaz robusta para la gestión pesada de inventarios, usuarios y Punto de Venta (POS).
- **Stack**: Java 25, Swing, FlatLaf (UI Moderna), Jackson (JSON).

### 🌐 [Dashboard Web](file:///c:/AcaciosWork/acacioswork-frontend)
Acceso ligero y universal para supervisión, consultas rápidas e **inteligencia de negocio** desde cualquier navegador.
- **Stack**: HTML5, CSS3, JavaScript (Vanilla ES6+).
- **Módulo IA**: Preguntas Inteligentes — análisis automático de rentabilidad, rotación, proveedores, clientes y tendencias.

### 📱 [App Móvil](file:///c:/AcaciosWork/acacioswork-android)
Gestión en movimiento para control de stock y alertas mediante dispositivos móviles.
- **Stack**: Kotlin 2.x, Android SDK, MVVM, Retrofit.

---

## 🤖 Inteligencia de Negocio

AcaciosWork incluye un módulo de **Preguntas Inteligentes** que permite al usuario hacer preguntas predefinidas sobre su negocio y obtener respuestas automáticas basadas en el análisis de los datos reales del sistema:

| Pregunta | Análisis |
| :--- | :--- |
| ¿Cuáles fueron los productos más rentables? | Margen × volumen vendido |
| ¿Qué productos tienen baja rotación? | Menor cantidad vendida |
| ¿Qué productos debo reabastecer? | Stock actual vs. stock mínimo |
| ¿Cuál proveedor vende más caro? | Promedio de costo por proveedor |
| ¿Qué clientes compran más? | Volumen total de compras |
| ¿Qué mes tuvo mayores ganancias? | Ganancia neta mensual |
| ¿Qué producto genera pérdidas? | Precio venta < precio costo |
| ¿Qué productos llevan sin venderse? | Sin presencia en ventas recientes |

---

## 📁 Mapa del Repositorio

```text
AcaciosWork/
├── acacioswork-backend/   # API REST (Spring Boot)
├── acacioswork-desktop/   # App Escritorio (Swing)
├── acacioswork-frontend/  # App Web (HTML/JS) + Inteligencia de Negocio
├── acacioswork-android/   # App Móvil (Kotlin)
├── database/              # Scripts SQL y esquemas
├── proyect-context/       # Contexto detallado para Agentes de IA
└── run_backend.bat        # Lanzador rápido de desarrollo
```

---

##  Reglas de Oro del Proyecto

1.  **Aislamiento Total**: Solo el Backend tiene permiso para tocar la base de datos. Los clientes son 100% dependientes de la API.
2.  **Identidad Única**: Todos los IDs deben ser `Long` (Java) y `BIGINT UNSIGNED` (MySQL).
3.  **Seguridad JWT**: Toda petición privada debe incluir el `Authorization: Bearer <token>`.
4.  **Estándar de Código**: Todo bloque de código debe incluir firma de autor y descripción funcional.

---

##  Inicio Rápido (Desarrollo)

1.  **Base de Datos**: Importa `database/tienda_acacios.sql` en tu servidor MySQL.
2.  **Configuración**: Ajusta las credenciales en `acacioswork-backend/src/main/resources/application.properties`.
3.  **Backend**: Ejecuta el comando `mvn spring-boot:run` dentro de la carpeta del backend.
4.  **Clientes**:
    - **Web**: Abre `acacioswork-frontend/login.html`.
    - **Desktop**: Ejecuta la clase `App.java` en el módulo desktop.

---

##  Información para Desarrolladores y Agentes Inteligentes
Si eres un Desarrollador o IA trabajando en este proyecto, por favor consulta **[project-context.md]** antes de realizar cualquier cambio estructural. Contiene un mapeo detallado de archivos y reglas específicas de implementación.

