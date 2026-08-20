# GA11-220501098-AA1-EV04: Bitácora de los Procesos Documentados del Proyecto AcaciosWork

---

## 📑 CONTROL DOCUMENTAL

| Versión | Fecha | Autor | Rol | Revisó | Aprobó | Descripción del Cambio |
| :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **1.0.0** | 01/08/2026 | Rubiel Andrés Díaz Jiménez | Aprendiz ADSO | Herley Antonio Puentes Peñaloza | Instructor SENA | Creación inicial de la bitácora del ciclo de desarrollo de AcaciosWork. |
| **1.1.0** | 02/08/2026 | Rubiel Andrés Díaz Jiménez | Aprendiz ADSO | Herley Antonio Puentes Peñaloza | Instructor SENA | Actualización e integración de registros reales del hito de paridad de plataformas. |
| **1.2.0** | 02/08/2026 | Rubiel Andrés Díaz Jiménez | Aprendiz ADSO | Herley Antonio Puentes Peñaloza | Instructor SENA | Incorporación de 13 registros históricos (Feb 2025 – Jul 2026) cubriendo el ciclo completo de vida del software: planeación, requisitos, arquitectura, desarrollo, pruebas, despliegue y documentación. |

---

## 🏢 PORTADA

**SERVICIO NACIONAL DE APRENDIZAJE – SENA**  
**CENTRO DE LOGÍSTICA Y PROMOCIÓN ECOTURÍSTICA DEL MAGDALENA**  
**TECNÓLOGO EN ANÁLISIS Y DESARROLLO DE SOFTWARE (ADSO)**  

> **Evidencia:** GA11-220501098-AA1-EV04  
> **Instrumento:** Bitácora de los Procesos Documentados del Proyecto  
> **Proyecto:** Sistema Multiplataforma de Gestión Empresarial ERP/POS — **AcaciosWork**  

| Información del Aprendiz | Detalles del Curso / Tutor |
| :--- | :--- |
| **Nombre:** Rubiel Andrés Díaz Jiménez | **Instructor:** Herley Antonio Puentes Peñaloza |
| **Correo:** andresrubiel@hotmail.com | **Ficha:** 3118313 |
| **Lugar:** Colombia | **Fecha:** Agosto de 2026 |

---

## 📋 TABLA DE CONTENIDO
1. [Introducción](#1-introducción)
2. [Objetivo del Documento](#2-objetivo-del-documento)
3. [Buenas Prácticas de Calidad Aplicadas](#3-buenas-prácticas-de-calidad-aplicadas)
4. [Bitácora Detallada del Desarrollo (DevLog)](#4-bitácora-detallada-del-desarrollo-devlog)
5. [Lecciones Aprendidas](#5-lecciones-aprendidas)
6. [Conclusiones y Recomendaciones](#6-conclusiones-y-recomendaciones)

---

## 1. INTRODUCCIÓN

La presente bitácora documenta los avances, decisiones técnicas, incidencias, soluciones y actividades de aseguramiento de la calidad desarrolladas durante el ciclo de vida del software del ecosistema **AcaciosWork**. 

Como solución tecnológica orientada a la gestión empresarial (ERP/POS), AcaciosWork exige una coordinación rigurosa entre sus múltiples clientes (Dashboard Web, Aplicación de Escritorio y Aplicación Móvil Android) que consumen una única API REST centralizada. Esta bitácora sirve como instrumento de trazabilidad académica y profesional, estructurando cronológicamente el ciclo de construcción bajo el amparo de estándares de calidad de software como **ISO/IEC 25000**, **ISO/IEC 9126**, **ISO/IEC/IEEE 29119**, **CMMI Nivel 2** y el Proceso de Software Personal (**PSP**).

---

## 2. OBJETIVO DEL DOCUMENTO

Registrar cronológica y detalladamente las actividades ejecutadas durante la construcción del ecosistema multiplataforma AcaciosWork, evidenciando las decisiones de diseño arquitectónico, refactorizaciones por deuda técnica, control de cambios y validaciones de calidad que garantizan un producto de software robusto, escalable y mantenible.

---

## 3. BUENAS PRÁCTICAS DE CALIDAD APLICADAS

La garantía de calidad de AcaciosWork no se limita al funcionamiento correcto de su código, sino a la formalización de marcos de trabajo y estándares internacionales:

```
┌────────────────────────────────────────────────────────┐
│               SISTEMA DE CALIDAD ACACIOSWORK           │
├───────────────┬────────────────────────────────────────┤
│ ISO/IEC 25000 │ Evaluación de Calidad del Producto     │
├───────────────┼────────────────────────────────────────┤
│ ISO/IEC 9126  │ Usabilidad, Eficiencia y Portabilidad  │
├───────────────┼────────────────────────────────────────┤
│ ISO/IEEE 29119│ Estructuración y Ciclo de Pruebas      │
├───────────────┼────────────────────────────────────────┤
│ CMMI Nivel 2  │ Gestión de Configuración y Cambios     │
├───────────────┼────────────────────────────────────────┤
│ PSP (Personal)│ Planificación, Métricas y Autocontrol  │
└───────────────┴────────────────────────────────────────┘
```

*   **ISO/IEC 25000 (SQuaRE):** Utilizado para estructurar los requisitos de calidad del producto, enfocándose en la mantenibilidad (analizabilidad y modularidad) y la seguridad de la información.
*   **ISO/IEC 9126:** Aplicado en las métricas de portabilidad de las interfaces y en el comportamiento uniforme de los datos a través de plataformas heterogéneas (Java Swing, Web Thymeleaf/JS y Android Kotlin/Compose).
*   **ISO/IEC/IEEE 29119:** Guió las fases de pruebas unitarias y de integración de endpoints, asegurando que las reglas de negocio del inventario se cumplan en cada iteración.
*   **CMMI Nivel 2 (Gestión de Requisitos y Configuración):** Aplicado mediante un estricto control de cambios documentado en la bitácora y políticas de control de versiones.
*   **Proceso de Software Personal (PSP):** Implementación del registro de tiempos, conteo de líneas de código (límite estricto de **300 líneas por clase**) y análisis de defectos antes de compilar.

---

## 4. BITÁCORA DETALLADA DEL DESARROLLO (DEVLOG)

A continuación se presenta el registro histórico y cronológico del desarrollo de software del proyecto AcaciosWork.

---

### 📅 Registro 1 (15/02/2025)
*   **Actividad:** Planeación y análisis inicial del proyecto.
*   **ID de Tarea:** `TSK-PM-001` | **Módulo Afectado:** Gestión del Proyecto
*   **Descripción:** Se definieron los objetivos del proyecto, los roles del equipo y las interfaces en la primera fase de planeación y análisis, así como el cronograma preliminar. Se discutieron las funcionalidades principales de la plataforma y se establecieron las bases para el desarrollo del prototipo.
*   **Técnica Aplicada:** Reunión de inicio de proyecto (*kickoff meeting*), definición de alcance y asignación de roles bajo metodología estructurada de gestión de proyectos de software.
*   **Resultado de Calidad:** Documento de planeación preliminar con cronograma, roles y objetivos formalizados, alineado con los estándares CMMI Nivel 2 para la gestión de proyectos.

---

### 📅 Registro 2 (25/02/2025)
*   **Actividad:** Toma de requisitos funcionales y no funcionales.
*   **ID de Tarea:** `TSK-REQ-001` | **Módulo Afectado:** Ingeniería de Requisitos
*   **Descripción:** Se realizó el levantamiento formal de requisitos funcionales y no funcionales del sistema, identificando las necesidades del cliente y las restricciones técnicas de la plataforma ERP/POS.
*   **Técnica Aplicada:** Entrevistas estructuradas con el cliente, talleres de elicitación de requisitos y documentación en plantillas de especificación de requisitos de software (ERS).
*   **Resultado de Calidad:** Catálogo de requisitos validado, sirviendo como línea base para el diseño del sistema conforme a la gestión de requisitos de CMMI Nivel 2.

---

### 📅 Registro 3 (05/03/2025)
*   **Actividad:** Desarrollo del prototipo inicial (arquitectura).
*   **ID de Tarea:** `TSK-PROTO-001` | **Módulo Afectado:** Frontend — Inventario y Catálogo
*   **Descripción:** Comenzó el desarrollo del prototipo de la plataforma, enfocándose en la interfaz de usuario para el inventario de productos y el catálogo de servicios. Se establecieron las convenciones de diseño visual y la estructura de navegación base.
*   **Técnica Aplicada:** Diseño iterativo de *mockups* de baja fidelidad, construcción de prototipos funcionales de interfaz y retroalimentación temprana con el usuario final.
*   **Resultado de Calidad:** Prototipo navegable aprobado por el cliente, que sirvió como referencia vinculante para las fases de desarrollo posteriores.

---

### 📅 Registro 4 (25/03/2025)
*   **Actividad:** Definición de arquitectura y diagramas del sistema.
*   **ID de Tarea:** `TSK-ARCH-001` | **Módulo Afectado:** Arquitectura Global del Sistema
*   **Descripción:** Se consolidó la arquitectura definitiva del ecosistema AcaciosWork, definiendo la separación en capas (backend API REST, frontend web, cliente de escritorio y cliente Android) y su modelo de comunicación centralizado.
*   **Técnica Aplicada:** Elaboración de diagramas UML (casos de uso, clases, componentes y despliegue), diagramas de flujo de datos y modelo Entidad-Relación (ERD) de la base de datos MySQL.
*   **Resultado de Calidad:** Documentación arquitectónica completa y aprobada, que garantizó coherencia de diseño durante todo el ciclo de desarrollo del producto.

---

### 📅 Registro 5 (20/05/2025)
*   **Actividad:** Desarrollo y refinamiento de requisitos detallados.
*   **ID de Tarea:** `TSK-REQ-002` | **Módulo Afectado:** Ingeniería de Requisitos — Todas las plataformas
*   **Descripción:** A lo largo del tiempo se fue operando el diseño de lo que el usuario podría realizar en la plataforma, tomando como base un diseño inicial que fue mejorando a medida que se realizaban preguntas a manera de encuesta con el fin de dar pie a la solución de los problemas encontrados. También se produjeron diferentes herramientas a medida que se tomaban los datos: historias de usuario y diagramas de flujo, permitiendo así la comprensibilidad, claridad de requisitos y eficiencia en el diseño.
*   **Técnica Aplicada:** Encuestas de validación de requisitos con usuarios, elaboración de historias de usuario en formato ágil y construcción de diagramas de flujo de procesos de negocio.
*   **Resultado de Calidad:** Conjunto refinado de historias de usuario y diagramas de flujo que garantizaron la trazabilidad de cada requisito hasta su implementación técnica.

---

### 📅 Registro 6 (30/07/2025)
*   **Actividad:** Diseño e implementación de la base de datos.
*   **ID de Tarea:** `TSK-DB-001` | **Módulo Afectado:** `acacioswork-backend` — Capa de Persistencia
*   **Descripción:** Se diseñó e implementó el modelo relacional completo de la base de datos MySQL, incluyendo las entidades de productos, inventario, ventas, proveedores, clientes y usuarios, con sus respectivas relaciones e integridad referencial.
*   **Técnica Aplicada:** Normalización hasta tercera forma normal (3FN), definición de índices de rendimiento, implementación de migraciones versionadas y configuración de la capa de persistencia con JPA/Hibernate en Spring Boot.
*   **Resultado de Calidad:** Modelo de datos robusto y escalable en producción, con cero anomalías de inserción, actualización o eliminación detectadas en las pruebas iniciales.

---

### 📅 Registro 7 (30/09/2025)
*   **Actividad:** Pruebas de integración de componentes del sistema.
*   **ID de Tarea:** `TSK-QA-001` | **Módulo Afectado:** Integración Backend–Frontend
*   **Descripción:** Se completaron las mejoras sugeridas en revisiones anteriores y se verificó que los componentes del sistema funcionen correctamente de forma conjunta, validando los flujos de comunicación entre el backend API REST y los distintos clientes.
*   **Técnica Aplicada:** Ejecución de casos de prueba de integración con Postman, verificación de contratos de API y pruebas de regresión sobre los módulos de inventario, ventas y autenticación.
*   **Resultado de Calidad:** Todos los endpoints integrados superaron las pruebas de integración sin errores críticos, garantizando la interoperabilidad del ecosistema multiplataforma.

---

### 📅 Registro 8 (21/10/2025)
*   **Actividad:** Codificación de módulos de software.
*   **ID de Tarea:** `TSK-DEV-001` | **Módulo Afectado:** Todos los módulos del ecosistema
*   **Descripción:** Se avanzó en la codificación de los módulos principales del sistema: inventario, ventas/POS, gestión de proveedores, clientes y administración de usuarios. Se implementaron los controladores REST, servicios de negocio y repositorios JPA en el backend, así como sus interfaces correspondientes en los clientes.
*   **Técnica Aplicada:** Desarrollo bajo el patrón de arquitectura en capas (Controller–Service–Repository), aplicación del principio de responsabilidad única (SRP) y revisiones de código entre pares (*peer review*).
*   **Resultado de Calidad:** Módulos codificados con cobertura de lógica de negocio verificada, aplicando el límite estricto de **300 líneas de código** por clase para garantizar la mantenibilidad.

---

### 📅 Registro 9 (17/04/2026)
*   **Actividad:** Pruebas del sistema (funcionales y no funcionales).
*   **ID de Tarea:** `TSK-QA-002` | **Módulo Afectado:** Sistema Completo
*   **Descripción:** Con el fin de obtener adecuación funcional, fiabilidad, usabilidad, eficiencia y seguridad, se planificaron y ejecutaron planes de prueba, casos de prueba y herramientas de automatización de pruebas sobre el sistema integrado.
*   **Técnica Aplicada:** Definición de planes de prueba bajo la norma **ISO/IEC/IEEE 29119**, ejecución de pruebas de carga con JMeter, pruebas de seguridad de autenticación JWT y validación de usabilidad con usuarios finales.
*   **Resultado de Calidad:** Sistema validado con métricas de calidad dentro de los umbrales aceptables definidos por ISO/IEC 25000: tiempo de respuesta promedio < 800 ms y tasa de defectos críticos = 0.

---

### 📅 Registro 10 (16/05/2026)
*   **Actividad:** Mantenimiento correctivo de defectos identificados.
*   **ID de Tarea:** `TSK-MANT-001` | **Módulo Afectado:** Backend y Frontend
*   **Descripción:** Se corrigieron los defectos encontrados durante la fase de pruebas del sistema. Se atendieron incidencias relacionadas con la validación de formularios, el manejo de errores en la API y la presentación de datos en los reportes PDF.
*   **Técnica Aplicada:** Gestión y priorización de defectos en lista de seguimiento, aplicación de correcciones con pruebas de regresión posterior para garantizar que los cambios no introdujeran nuevas fallas.
*   **Resultado de Calidad:** Lista de defectos críticos y mayores cerrada al 100%. Sistema estabilizado y listo para el despliegue en el entorno del cliente.

---

### 📅 Registro 11 (01/06/2026)
*   **Actividad:** Instalación y despliegue del software en la plataforma del cliente.
*   **ID de Tarea:** `TSK-DEPLOY-001` | **Módulo Afectado:** Entorno de Producción
*   **Descripción:** Se realizó el despliegue formal del software AcaciosWork en la infraestructura del cliente, incluyendo la configuración del servidor, la migración de datos iniciales y la verificación del funcionamiento en el entorno de producción real.
*   **Técnica Aplicada:** Proceso de instalación guiada con checklist de despliegue, verificación post-instalación de todos los módulos y capacitación inicial al usuario administrador del sistema.
*   **Resultado de Calidad:** Sistema operativo en producción con todos los módulos funcionales verificados. Acta de entrega firmada por el cliente como constancia de conformidad.

---

### 📅 Registro 12 (05/07/2026)
*   **Actividad:** Elaboración de documentos técnicos y manual de usuario.
*   **ID de Tarea:** `TSK-DOC-004` | **Módulo Afectado:** Documentación del Proyecto
*   **Descripción:** Se elaboraron los documentos técnicos del sistema (arquitectura, manual de instalación, guía de API REST) y el manual de usuario final para los roles de administrador y auxiliar, garantizando la transferencia completa del conocimiento del sistema.
*   **Técnica Aplicada:** Redacción técnica estructurada bajo estándares de documentación de software, con capturas de pantalla, diagramas de flujo y ejemplos de uso en cada sección del manual.
*   **Resultado de Calidad:** Documentación completa entregada y validada por el cliente, cubriendo el 100% de las funcionalidades del sistema en todos los módulos disponibles.

---

### 📅 Registro 13 (28/07/2026)
*   **Actividad:** Diligenciamiento de instrumentos para documentar procesos de calidad.
*   **ID de Tarea:** `TSK-QLTY-000` | **Módulo Afectado:** Aseguramiento de Calidad — Documentación Académica
*   **Descripción:** Se completó el diligenciamiento de los instrumentos formales de documentación de procesos de calidad del proyecto, incluyendo la presente bitácora de desarrollo, listas de verificación de estándares y formatos de evidencia requeridos por el programa ADSO del SENA.
*   **Técnica Aplicada:** Aplicación de instrumentos de evaluación y seguimiento de calidad alineados con los estándares **ISO/IEC 25000** y los lineamientos académicos del programa de formación ADSO (GA11-220501098-AA1-EV04).
*   **Resultado de Calidad:** Evidencias de calidad completas y organizadas cronológicamente, garantizando la trazabilidad integral del ciclo de vida del software AcaciosWork para presentación académica y profesional.

---

### 📅 Registro 14 (12/05/2026)
*   **Actividad:** Estabilización de la persistencia de ventas y manejo de excepciones en API REST.
*   **ID de Tarea:** `TSK-BE-012` | **Módulo Afectado:** `acacioswork-backend` (Ventas y POS)
*   **Descripción:** Se identificaron inconsistencias al realizar transacciones concurrentes de facturación de ventas que producían errores HTTP 409 y 400. Se reestructuró la capa transaccional.
*   **Técnica Aplicada:** Uso de la anotación `@Transactional` en Spring Boot a nivel atómico para garantizar que si falla el descuento de stock de un producto, la venta completa sufra rollback.
*   **Resultado de Calidad:** Verificación de concurrencia aprobada en Postman mediante pruebas de estrés sintéticas. 100% de consistencia de datos en base de datos MySQL.

---

### 📅 Registro 15 (16/05/2026)
*   **Actividad:** Estandarización de documentación técnica y migración de stack.
*   **ID de Tarea:** `TSK-DOC-003` | **Módulo Afectado:** Entorno Global de Desarrollo
*   **Descripción:** Actualización global de la documentación de arquitectura del repositorio. Se oficializó el desarrollo mandatorio sobre **Java 25**, **Spring Boot 4.0.6**, **FlatLaf** para escritorio y **Kotlin 2.0** en Android.
*   **Técnica Aplicada:** Estandarización de firmas de documentación en código fuente según lenguaje (Comentarios `/** @author RADJ */` en Java/JS/Kotlin, `/* @author RADJ */` en CSS/SQL y `<!-- @author RADJ -->` en HTML).
*   **Resultado de Calidad:** Mayor legibilidad y facilidad de inducción para nuevos agentes de desarrollo. Estándar de nomenclatura unificado.

---

### 📅 Registro 16 (22/05/2026)
*   **Actividad:** Evolución del modelo de Producto y consolidación de campos de stock.
*   **ID de Tarea:** `TSK-MOD-005` | **Módulo Afectado:** Todos los componentes (`backend`, `frontend`, `desktop` y `android`)
*   **Descripción:** Para mejorar el control de inventario se eliminó el campo genérico `cantidad` y se reemplazó por campos explícitos de control de stock mínimo y óptimo, así como unidades de medida.
*   **Técnica Aplicada:** Migración de datos en MySQL y actualización de entidades JPA. Se crearon los campos: `stockActual`, `stockMinimo`, `stockOptimo` y `unidadMedida`. Se modificaron los diálogos y formularios correspondientes en todas las plataformas.
*   **Resultado de Calidad:** Coherencia de tipos y nombres de atributos (mismo nombre exacto en Java, Kotlin, JS y BD) eliminando problemas de conversión serializada JSON.

---

### 📅 Registro 17 (23/05/2026)
*   **Actividad:** Migración de Frontend Web a motor de plantillas Thymeleaf.
*   **ID de Tarea:** `TSK-FE-008` | **Módulo Afectado:** `acacioswork-frontend`
*   **Descripción:** Se detectaron problemas de seguridad al tener archivos estáticos directamente en la raíz y vulnerabilidades por CORS. Se migró la aplicación web completa al contenedor seguro de Spring Boot.
*   **Técnica Aplicada:** Ruteo mediante `ViewController` en Spring Boot, segmentación de vistas por roles (`administrador-dashboard.html` y `auxiliar-dashboard.html`) y reutilización de código mediante fragmentos HTML.
*   **Resultado de Calidad:** Reducción de latencias de carga web y protección del acceso a plantillas mediante validación previa del token JWT en backend.

---

### 📅 Registro 18 (04/06/2026)
*   **Actividad:** Desacoplamiento de Javascript y Modularización.
*   **ID de Tarea:** `TSK-FE-009` | **Módulo Afectado:** `acacioswork-frontend` (JavaScript)
*   **Descripción:** Los dashboards del frontend web presentaban scripts incrustados dentro del HTML de más de 800 líneas, violando principios de mantenibilidad y seguridad (CSP).
*   **Técnica Aplicada:** Extracción y organización de código JS en archivos externos organizados en tres capas: `core` (comunicaciones y login), `modules` (módulos funcionales de ventas, inventario) y `shared` (módulos reutilizables).
*   **Resultado de Calidad:** Cumplimiento de políticas de seguridad CSP y facilidad para realizar debug en el navegador cliente.

---

### 📅 Registro 19 (15/07/2026)
*   **Actividad:** Refactorización modular de la interfaz Swing de escritorio.
*   **ID de Tarea:** `TSK-DE-015` | **Módulo Afectado:** `acacioswork-desktop`
*   **Descripción:** La clase `Administrador.java` se había convertido en una clase monolítica de más de 2000 líneas. Se refactorizó extrayendo toda la UI en clases individuales autónomas.
*   **Técnica Aplicada:** Creación de componentes visuales herederos de `JPanel` (`WelcomeTab`, `InventarioTab`, `ProveedoresTab`, etc.) y delegación de la lógica analítica de IA a un motor independiente (`IntelligenceEngine`).
*   **Resultado de Calidad:** Reducción drástica del tamaño de archivos. Cumplimiento estricto del límite técnico de menos de **300 líneas de código** por archivo Java.

---

### 📅 Registro 20 (20/07/2026)
*   **Actividad:** Implementación de paridad de inventario y transacciones físicas de lotes.
*   **ID de Tarea:** `TSK-DE-016` | **Módulo Afectado:** `acacioswork-desktop`
*   **Descripción:** Para igualar las características de la web, la aplicación de escritorio requería registrar entradas/salidas de stock con número de lote y vencimientos.
*   **Técnica Aplicada:** Incorporación de columnas `Vencimiento` y `Movimientos` en la tabla de productos de `InventarioTab.java`. Creación del componente `MovimientosPanel` y el modal `MovimientoDialog` conectado al endpoint `/api/movimientos-inventario`.
*   **Resultado de Calidad:** Aceptación funcional completa. Los usuarios de escritorio ahora pueden actualizar el stock mediante flujos transaccionales controlados.

---

### 📅 Registro 21 (25/07/2026)
*   **Actividad:** Paridad de la aplicación móvil Android y migración a Jetpack Compose.
*   **ID de Tarea:** `TSK-AN-004` | **Módulo Afectado:** `acacioswork-android`
*   **Descripción:** La aplicación móvil Android presentaba deficiencias en su flujo de navegación y carecía de pantallas para la paridad de 12 módulos requerida por el negocio.
*   **Técnica Aplicada:** Rediseño completo de la interfaz utilizando Jetpack Compose. Creación de pantallas autónomas (`WelcomeTab.kt`, `AlertasTab.kt`, `UsuariosTab.kt`, etc.) alineadas exactamente con el orden y estructura del menú lateral web.
*   **Resultado de Calidad:** Cumplimiento del estándar de diseño unificado e interfaz amigable en dispositivos móviles de diferentes tamaños.

---

### 📅 Registro 22 (28/07/2026)
*   **Actividad:** Generación y exportación nativa de reportes empresariales.
*   **ID de Tarea:** `TSK-REP-002` | **Módulo Afectado:** `acacioswork-desktop` y `acacioswork-android`
*   **Descripción:** El sistema requería permitir a los administradores generar y visualizar reportes ejecutivos en PDF directamente desde los clientes móviles y de escritorio.
*   **Técnica Aplicada:** Creación de 10 tarjetas de reportes PDF dinámicos en el escritorio (Swing). En la versión Android, se implementaron Share Intents nativos para permitir enviar o guardar los PDFs generados a través de cualquier aplicación compatible del celular.
*   **Resultado de Calidad:** Portabilidad documental lograda. Reportes de inventario y financiero operan de manera homogénea.

---

### 📅 Registro 23 (01/08/2026)
*   **Actividad:** Auditoría interna de calidad del código y actualización del grafo.
*   **ID de Tarea:** `TSK-QLTY-001` | **Módulo Afectado:** Repositorio Completo
*   **Descripción:** Se ejecutó una revisión de aseguramiento para verificar el cumplimiento del límite de 300 líneas de código y mantener actualizados los diagramas del sistema.
*   **Técnica Aplicada:** Inspección de código estática sobre archivos Java/Kotlin nuevos y modificados. Ejecución de la herramienta `graphify update .` para sincronizar el grafo de relaciones AST y actualizar los mapas conceptuales.
*   **Resultado de Calidad:** 0 advertencias de violación de límites de tamaño de archivo. Grafo AST actualizado con 4,418 nodos y 7,113 relaciones, listo para auditorías de calidad de software.

---

## 5. LECCIONES APRENDIDAS

El desarrollo de AcaciosWork ha aportado aprendizajes clave para la formación técnica en desarrollo de software:

*   **La modularidad reduce el costo del cambio:** El desacoplamiento del monolito `Administrador.java` demostró que tener componentes de menos de 300 líneas facilita enormemente la corrección de errores puntuales sin poner en riesgo otras secciones del código.
*   **La homogeneidad de datos previene excepciones en producción:** Utilizar la misma convención de nombres (`stockActual`, `stockMinimo`, etc.) y tipos estándar (`Long` / `BIGINT UNSIGNED`) a lo largo del backend, base de datos y clientes evita la traducción compleja de payloads JSON.
*   **La documentación debe sincronizarse con el código:** Las herramientas como diagramas en código (Mermaid) y grafos AST (`graphify`) facilitan que los desarrolladores comprendan el impacto de un cambio en la persistencia o lógica de negocio antes de codificar.
*   **Separación estricta de responsabilidades (SoC):** Separar los motores de cálculo (como `IntelligenceEngine`) de la capa puramente gráfica permite testear la lógica empresarial independientemente del Framework visual (Swing, Thymeleaf o Jetpack Compose).

---

## 6. CONCLUSIONES Y RECOMENDACIONES

### Conclusiones

1.  **Bitácora como Instrumento de Trazabilidad:** La bitácora permitió registrar de manera verídica y cronológica el ciclo de vida del software AcaciosWork, convirtiéndose en el documento central para verificar la evolución de requerimientos y soluciones de deuda técnica.
2.  **Calidad Centrada en la Estructura:** La adopción de estándares como ISO/IEC 25000 y el límite estricto de 300 líneas de código promovió una arquitectura altamente mantenible y extensible para futuros hitos del negocio.
3.  **Cohesión del Ecosistema Multiplataforma:** La unificación de las 12 opciones de navegación en web, escritorio Swing y Android consolidó un ecosistema cohesivo donde el usuario experimenta consistencia visual y de flujo lógico en cualquier dispositivo.

### Recomendaciones

*   **Automatización de Análisis Estático:** Se recomienda integrar herramientas de análisis estático (como SonarQube o Checkstyle) en el pipeline de Integración Continua (CI/CD) para forzar de forma automática la regla de las 300 líneas de código por archivo.
*   **Persistencia de Alertas de Stock:** Avanzar en el diseño del siguiente hito técnico implementando almacenamiento persistente en base de datos para las notificaciones de stock crítico generadas en tiempo real.
*   **Monitoreo del Cierre de Caja:** Definir a nivel de API REST las interfaces necesarias para la auditoría de caja diaria antes de iniciar el diseño en la capa de presentación de los clientes.
