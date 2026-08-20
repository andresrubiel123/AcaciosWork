# GA11-220501098-AA3-EV02 Verificaciones de Condiciones de Calidad del Producto de Software Ajustado

Aprendiz Sena:
Rubiel Andrés Díaz Jiménez.

Tecnólogo en Análisis y Desarrollo de Software, Centro de Gestión y Desarrollo Sostenible Surcolombiano, Servicio Nacional de Aprendizaje.

Ficha: 3118313

Instructor: Herley Antonio Puentes Peñaloza
14 de agosto de 2026

## INTRODUCCIÓN

El presente documento registra el proceso de verificación de las condiciones de calidad del producto de software **AcaciosWork** en su estado ajustado, es decir, tras la aplicación de los cambios técnicos y operativos documentados en la evidencia GA11-220501098-AA3-EV01. Esta verificación constituye el cierre del ciclo de mejora continua del software, confirmando que los ajustes realizados produjeron el impacto de calidad esperado y que el producto cumple con los atributos establecidos en los marcos de referencia seleccionados.

La metodología de evaluación adoptada se fundamenta en los lineamientos del componente formativo "Aplicación de pruebas de software" del SENA, la familia de normas ISO/IEC 25000 (SQuaRE) para la definición de características y métricas de calidad, y el estándar ISO/IEC/IEEE 29119 para la estructuración del proceso de pruebas. El documento describe las características y subcaracterísticas de calidad evaluadas, el conjunto de actividades de evaluación ejecutadas, las herramientas utilizadas para automatizar la medición y los resultados obtenidos.

El producto evaluado es el ecosistema multiplataforma AcaciosWork versión 1.0 (estado post-ajustes, agosto 2026), compuesto por cuatro componentes: **Backend API REST** (Spring Boot 4 / Java 25), **Cliente Web** (HTML/JS/Thymeleaf), **Cliente Desktop** (Java Swing / FlatLaf) y **Cliente Móvil** (Kotlin / Jetpack Compose).

## BITÁCORA DE PROCESOS DOCUMENTALES

### Control de Versiones del Proceso de Evaluación

| Versión | Fecha | Actividad | Responsable | Estado |
| :---: | :---: | :--- | :--- | :---: |
| **1.0** | 14/08/2026 | Definición del plan de evaluación y selección de características ISO/IEC 25000 | Rubiel Andrés Díaz Jiménez | Completado |
| **1.1** | 14/08/2026 | Ejecución de pruebas de verificación por módulo y plataforma | Rubiel Andrés Díaz Jiménez | Completado |
| **1.2** | 14/08/2026 | Medición de métricas de calidad y análisis comparativo (pre/post ajuste) | Rubiel Andrés Díaz Jiménez | Completado |
| **1.3** | 14/08/2026 | Elaboración del informe final de verificación con trazabilidad a ajustes aplicados | Rubiel Andrés Díaz Jiménez | Completado |

### Cronología de Actividades del Proceso Evaluativo

Las actividades de evaluación se ejecutaron en el siguiente orden, tomando como base los registros de la bitácora (GA11-220501098-AA1-EV04) y el historial de commits del repositorio Git:

1. **Actividad 1 (Revisión documental):** Lectura y análisis de los instrumentos de calidad previos (GA11-220501098-AA1-EV03) para identificar las incidencias abiertas y los cambios de control aplicados que debían verificarse.
2. **Actividad 2 (Configuración del entorno):** Inicialización del entorno de pruebas con la versión post-ajuste del software (commit `880fac7`), arrancando el backend Spring Boot en modo desarrollo y conectando la base de datos MySQL `tienda_acacios`.
3. **Actividad 3 (Ejecución de pruebas de verificación):** Ejecución sistemática de los casos de prueba por módulo funcional, usando Postman para la API REST y pruebas manuales para las interfaces de usuario.
4. **Actividad 4 (Medición de métricas):** Aplicación de métricas cuantitativas derivadas de ISO/IEC 25000 sobre el código fuente, los tiempos de respuesta y la cobertura funcional.
5. **Actividad 5 (Análisis comparativo):** Comparación de los resultados post-ajuste contra las calificaciones registradas en el instrumento de evaluación ISO/IEC 25000 del GA11-220501098-AA1-EV03 (madurez técnica del 70%).
6. **Actividad 6 (Documentación):** Registro de los resultados de verificación en el presente informe, con trazabilidad directa a los ajustes aplicados (ADJ-001 a ADJ-008).

## BUENAS PRÁCTICAS DE CALIDAD SELECCIONADAS

| Marco de Trabajo | Práctica Seleccionada | Aplicación en la Evaluación |
| :--- | :--- | :--- |
| **ISO/IEC 25000 (SQuaRE)** | Modelo de calidad del producto con 8 características principales | Define qué se mide y con qué métricas en cada componente del ecosistema |
| **ISO/IEC 9126** | Factores de McCall — 11 criterios de calidad del producto | Complementa la evaluación de portabilidad, reusabilidad e interoperabilidad |
| **ISO/IEC/IEEE 29119** | Plan de Pruebas, Casos de Prueba, Reportes de Incidencias y Cierre | Estructura el proceso y los artefactos de la evaluación |
| **CMMI Nivel 2** | Gestión de Configuración y Trazabilidad de Cambios | Los resultados de verificación se vinculan a commits y ajustes documentados |
| **PSP** | Registro de Tiempos y Defectos Personales | Se registran los tiempos de ejecución de cada actividad de evaluación |

## CARACTERÍSTICAS, SUBCARACTERÍSTICAS Y MÉTRICAS DE CALIDAD

### Modelo de Evaluación: ISO/IEC 25010 Aplicado a AcaciosWork

A continuación se describen las características de calidad evaluadas conforme a ISO/IEC 25000, sus subcaracterísticas relevantes para AcaciosWork, las propiedades medibles y las métricas específicas aplicadas:

### Característica 1: Adecuación Funcional

Grado en que el software provee funciones que satisfacen las necesidades del negocio bajo condiciones específicas.

* **Subcaracterística 1.1 — Completitud Funcional:** Grado en que el conjunto de funciones cubre todas las tareas y objetivos del usuario.
  * **Propiedad:** Cobertura de los 12 módulos de negocio definidos (paridad Web/Desktop/Android).
  * **Métrica:** Porcentaje de módulos implementados y funcionales sobre el total planificado.
  * **Resultado:** 12/12 módulos implementados = **100% de completitud funcional**.

* **Subcaracterística 1.2 — Corrección Funcional:** Grado en que el software produce resultados correctos con el nivel de precisión necesario.
  * **Propiedad:** Corrección del cálculo de ganancias netas en el módulo de reportes (ADJ-003).
  * **Métrica:** Desviación porcentual entre el resultado calculado por el sistema y el resultado esperado calculado manualmente con datos de prueba conocidos.
  * **Resultado:** Desviación = **0%** tras la corrección de `ReporteService.reporteGanancias()` con `BigDecimal`.

* **Subcaracterística 1.3 — Pertinencia Funcional:** Grado en que las funciones facilitan el logro de tareas y objetivos específicos.
  * **Propiedad:** Disponibilidad de los flujos críticos de negocio (login → inventario → venta POS → reporte).
  * **Métrica:** Número de flujos críticos sin errores bloqueantes.
  * **Resultado:** **5/5** flujos críticos aprobados en prueba manual.

### Característica 2: Eficiencia de Desempeño

Desempeño relativo a la cantidad de recursos utilizados bajo condiciones establecidas.

* **Subcaracterística 2.1 — Comportamiento Temporal:** Tiempos de respuesta y velocidad de procesamiento.
  * **Propiedad:** Tiempo de respuesta promedio de los endpoints de la API REST.
  * **Métrica:** Tiempo promedio de respuesta HTTP medido con Postman (ms).
  * **Umbral de aprobación:** < 800 ms (definido en GA11-220501098-AA1-EV03).
  * **Resultados medidos:**

| Endpoint | Tiempo Promedio (ms) | Resultado |
| :--- | :---: | :---: |
| `POST /api/auth/login` | 312 ms | **Aprobado** |
| `GET /api/productos` | 489 ms | **Aprobado** |
| `POST /api/ventas` | 623 ms | **Aprobado** |
| `GET /api/reportes/ganancias` | 741 ms | **Aprobado** |
| `GET /api/alertas-stock` | 198 ms | **Aprobado** |

* **Subcaracterística 2.2 — Utilización de Recursos:** Uso eficiente de los recursos de hardware.
  * **Propiedad:** Consumo de memoria RAM del proceso Java del backend en reposo y bajo carga.
  * **Métrica:** Consumo RAM medido con el monitor del IDE (IntelliJ IDEA / Visual VM).
  * **Resultado:** Reposo ≈ 320 MB, bajo carga de 10 peticiones simultáneas ≈ 480 MB. Dentro de los límites aceptables para entorno de desarrollo.

### Característica 3: Seguridad

Grado en que el software protege la información y los datos de accesos no autorizados.

* **Subcaracterística 3.1 — Confidencialidad:** Acceso a datos solo para personas autorizadas.
  * **Propiedad:** Protección de endpoints de la API con autenticación JWT (ADJ-001).
  * **Métrica:** Número de endpoints protegidos que responden datos sin token JWT válido.
  * **Umbral:** 0 endpoints vulnerables.
  * **Resultado:** **0 endpoints expuestos**. Todos los endpoints `/api/**` (excepto `/api/auth/login`) responden HTTP 401 sin token.

* **Subcaracterística 3.2 — Autenticidad:** Verificación de identidad de sujetos o recursos.
  * **Propiedad:** Control de acceso basado en roles (RBAC) con anotaciones `@PreAuthorize`.
  * **Métrica:** Número de intentos de acceso a endpoints de administrador con token de rol AUXILIAR que son correctamente rechazados.
  * **Resultado:** **3/3** casos de prueba de acceso no autorizado rechazados correctamente con HTTP 403.

* **Subcaracterística 3.3 — Integridad:** Prevención de modificaciones no autorizadas de datos.
  * **Propiedad:** Precisión de datos monetarios con `BigDecimal` (ADJ-002).
  * **Métrica:** Error de redondeo acumulativo en 1000 operaciones de precio con dos decimales.
  * **Resultado:** Error = **$0.00** (cero) con `BigDecimal`. Error esperado con `double`: hasta $0.15 por 1000 operaciones.

### Característica 4: Mantenibilidad

Grado en que el software puede ser modificado efectivamente y eficientemente por los responsables.

* **Subcaracterística 4.1 — Modularidad:** Grado en que el sistema se compone de componentes discretos que minimizan el impacto de cambios.
  * **Propiedad:** Tamaño de los archivos de código fuente Java, Kotlin y JavaScript.
  * **Métrica:** Porcentaje de archivos que cumplen el límite de 300 líneas (ADJ-006, ADJ-007).
  * **Resultado:** Auditoría del repositorio post-ajuste:

| Componente | Total Archivos Auditados | Archivos ≤ 300 líneas | Cumplimiento |
| :--- | :---: | :---: | :---: |
| Backend Java (servicios y controladores) | 34 | 34 | **100%** |
| Desktop Java (tabs y diálogos) | 12 | 12 | **100%** |
| Frontend JS (módulos y shared) | 11 | 9 | **81.8%** |
| Android Kotlin (screens y components) | 18 | 18 | **100%** |

* **Subcaracterística 4.2 — Analizabilidad:** Facilidad para diagnosticar el impacto de cambios o defectos.
  * **Propiedad:** Uso de la convención de commits semánticos en el historial Git (ADJ-005).
  * **Métrica:** Porcentaje de commits con mensaje en formato semántico sobre el total del repositorio.
  * **Resultado:** 5 de los últimos 7 commits (71.4%) usan la convención semántica. Los 22 commits totales del repositorio son trazables a través de la bitácora.

* **Subcaracterística 4.3 — Capacidad de Ser Probado:** Facilidad para ejecutar pruebas del software.
  * **Propiedad:** Separación de la lógica de negocio de la capa de presentación.
  * **Métrica:** Número de servicios de negocio que pueden ser probados de forma aislada (sin contexto de UI).
  * **Resultado:** Los 20 servicios del backend (`VentaService`, `ProductoService`, `ReporteService`, `IntelligenceEngine`, etc.) son clases POJO con inyección de dependencias, testeables de forma aislada con JUnit + Mockito.

### Característica 5: Fiabilidad

Grado en que el sistema realiza funciones especificadas bajo condiciones determinadas durante un período de tiempo.

* **Subcaracterística 5.1 — Tolerancia a Fallos:** Capacidad del sistema de operar correctamente ante fallos de componentes.
  * **Propiedad:** Atomicidad de las transacciones de venta con `@Transactional` (Registro 14 de la bitácora).
  * **Métrica:** Porcentaje de ventas con fallo en un ítem que ejecutan rollback completo sin dejar registros parciales.
  * **Resultado:** **100% de rollback** confirmado con prueba de stress: se intentaron 10 ventas con un producto de stock cero en el ítem 2. Ninguna generó registros parciales en `DETALLE_VENTA`.

* **Subcaracterística 5.2 — Recuperabilidad:** Capacidad de restaurar los datos afectados y restablecer el estado del sistema tras una interrupción.
  * **Propiedad:** Consistencia de la base de datos MySQL tras una interrupción del servicio.
  * **Métrica:** Tiempo de recuperación y consistencia de datos tras reinicio del servidor.
  * **Resultado:** Reinicio del servicio en ≈ 18 segundos con consistencia total de datos confirmada mediante consulta SQL directa.

### Característica 6: Compatibilidad

Grado en que el sistema puede intercambiar información con otros sistemas o componentes.

* **Subcaracterística 6.1 — Interoperabilidad:** Capacidad de dos o más sistemas para intercambiar y usar información.
  * **Propiedad:** Consumo de la API REST por los tres clientes (Web, Desktop, Android) sobre el mismo contrato.
  * **Métrica:** Número de módulos funcionales que operan correctamente en las tres plataformas sin modificar el backend.
  * **Resultado:** **12/12 módulos** verificados en paridad entre Web, Desktop y Android.

### Característica 7: Usabilidad

Grado en que el software puede ser utilizado por usuarios específicos para lograr sus objetivos con efectividad, eficiencia y satisfacción.

* **Subcaracterística 7.1 — Facilidad de Aprendizaje:** Grado en que el software permite a los usuarios aprender a usarlo.
  * **Propiedad:** Consistencia de navegación entre plataformas (mismo orden de 12 secciones en Web, Desktop y Android).
  * **Métrica:** Número de secciones que aparecen en el mismo orden en las tres plataformas.
  * **Resultado:** **12/12 secciones** en orden idéntico confirmado.

### Característica 8: Portabilidad

Grado en que el software puede ser transferido de un entorno a otro.

* **Subcaracterística 8.1 — Adaptabilidad:** Grado en que el software puede adaptarse a diferentes entornos.
  * **Propiedad:** Funcionamiento del cliente Android en diferentes tamaños de pantalla.
  * **Métrica:** Número de resoluciones de pantalla en que la app funciona sin errores de layout.
  * **Resultado:** Probado en emuladores de 5", 6.1" y 7" (tablet). **3/3 resoluciones** aprobadas.

## PROCESO DE EVALUACIÓN: ACTIVIDADES Y TAREAS

### Fase 1: Planificación

**Tarea 1.1 — Definición del alcance:** Se identificaron los 8 módulos de negocio críticos a evaluar (autenticación, inventario, ventas POS, reportes, clientes, proveedores, usuarios, alertas de stock) y los 4 componentes tecnológicos (Backend, Web, Desktop, Android).

**Tarea 1.2 — Selección de características:** Con base en ISO/IEC 25010, se priorizaron las 8 características de calidad según su impacto en el negocio de Tienda Los Acacios: Adecuación Funcional (alta prioridad), Seguridad (alta), Fiabilidad (alta), Mantenibilidad (media), Eficiencia de Desempeño (media), Compatibilidad (media), Usabilidad (media) y Portabilidad (baja).

**Tarea 1.3 — Definición de criterios de aceptación:**

| Criterio | Umbral de Aprobación |
| :--- | :--- |
| Defectos críticos en flujos de negocio | 0 defectos |
| Endpoints sin autenticación JWT | 0 endpoints |
| Tiempo de respuesta promedio de API | < 800 ms |
| Cumplimiento de límite de 300 líneas | ≥ 95% de archivos |
| Módulos funcionales en paridad multiplataforma | 12/12 módulos |

### Fase 2: Diseño de Casos de Prueba

Se diseñaron y ejecutaron los siguientes casos de prueba organizados por característica de calidad:

| ID Caso | Característica | Módulo | Descripción | Resultado |
| :--- | :---: | :--- | :--- | :---: |
| **CP-SEC-001** | Seguridad | Autenticación | GET /api/productos sin token JWT | **Aprobado** (HTTP 401) |
| **CP-SEC-002** | Seguridad | Control de Roles | Acceso a endpoint ADMIN con token AUXILIAR | **Aprobado** (HTTP 403) |
| **CP-SEC-003** | Seguridad | Token Expirado | Petición con token JWT expirado | **Aprobado** (HTTP 401) |
| **CP-FUN-001** | Adecuación Funcional | Login | Login con credenciales correctas | **Aprobado** (HTTP 200 + JWT) |
| **CP-FUN-002** | Adecuación Funcional | Inventario | CRUD completo de productos | **Aprobado** |
| **CP-FUN-003** | Adecuación Funcional | Venta POS | Registro venta con 3 productos, cliente seleccionado | **Aprobado** |
| **CP-FUN-004** | Adecuación Funcional | Reportes | Ganancia neta = (P.Venta - P.Compra) × Cantidad | **Aprobado** |
| **CP-FUN-005** | Adecuación Funcional | Alertas Stock | Alerta visible en productos con stock ≤ mínimo | **Aprobado** |
| **CP-FIA-001** | Fiabilidad | Ventas | Venta con producto de stock 0 hace rollback total | **Aprobado** |
| **CP-FIA-002** | Fiabilidad | Concurrencia | 2 peticiones simultáneas de venta del mismo producto | **Aprobado** |
| **CP-MAN-001** | Mantenibilidad | Backend | Todos los archivos Java ≤ 300 líneas | **Aprobado** |
| **CP-MAN-002** | Mantenibilidad | Desktop | Todos los archivos Java de tabs ≤ 300 líneas | **Aprobado** |
| **CP-DES-001** | Eficiencia | API | Tiempo respuesta GET /api/productos < 800 ms | **Aprobado** (489 ms) |
| **CP-DES-002** | Eficiencia | API | Tiempo respuesta POST /api/ventas < 800 ms | **Aprobado** (623 ms) |
| **CP-COMP-001** | Compatibilidad | Paridad | 12 módulos funcionales en Web, Desktop y Android | **Aprobado** |
| **CP-PORT-001** | Portabilidad | Android | App funcional en 3 resoluciones de pantalla distintas | **Aprobado** |

**Total: 16/16 casos de prueba aprobados (100%).**

### Fase 3: Ejecución

Las pruebas se ejecutaron durante el período del 14 de agosto de 2026, en el siguiente entorno tecnológico:

* **Servidor Backend:** Ejecución local en puerto 8080, JDK 25, Spring Boot 4.0.6.
* **Base de datos:** MySQL 8.0, esquema `tienda_acacios`.
* **Cliente Web:** Google Chrome (última versión estable) en resolución 1920×1080.
* **Cliente Desktop:** Aplicación Java Swing ejecutada localmente en Windows 11.
* **Cliente Android:** Emulador Pixel 6 (Android 13) y dispositivo físico Samsung Galaxy A54.
* **Herramienta de pruebas API:** Postman v11.

### Fase 4: Análisis Comparativo Pre/Post Ajuste

| Característica ISO/IEC 25010 | Nota Pre-Ajuste (EV03) | Nota Post-Ajuste | Variación |
| :--- | :---: | :---: | :---: |
| **Adecuación Funcional** | 9.0 | **9.5** | +0.5 |
| **Seguridad** | 9.0 | **10.0** | +1.0 |
| **Fiabilidad** | 7.0 | **9.0** | +2.0 |
| **Mantenibilidad** | 7.0 | **9.0** | +2.0 |
| **Eficiencia de Desempeño** | 7.0 | **7.5** | +0.5 |
| **Compatibilidad** | 9.0 | **10.0** | +1.0 |
| **Usabilidad** | 7.0 | **7.5** | +0.5 |
| **Portabilidad** | 8.0 | **8.5** | +0.5 |
| **Promedio Ponderado** | **7.87** | **8.88** | **+1.01** |

### Fase 5: Gestión de Incidencias Pendientes

Tras la evaluación post-ajuste, se identifican las siguientes incidencias que permanecen abiertas y se convierten en deuda técnica del siguiente ciclo de desarrollo:

| ID Incidencia | Módulo | Descripción | Severidad | Plan de Acción |
| :--- | :--- | :--- | :---: | :--- |
| **INC-003** | Inventario | Alertas de stock crítico almacenadas en `List` estático en memoria — se pierden al reiniciar el servidor | Alta | Migrar a tabla `alertas_stock` en MySQL con repositorio JPA |
| **INC-006** | API REST | Ausencia de validaciones `@Valid` en DTOs y falta de paginación en endpoints `findAll()` | Media | Añadir Bean Validation y `Pageable` de Spring Data |
| **INC-007** | Frontend JS | 2 módulos JS (`exportador-pdf.js` con 542 líneas y `modal.js` con 752 líneas) superan el límite | Media | Subdividir en submódulos especializados |
| **INC-008** | Backend | `Pago.java` es código muerto — modelo sin controlador, servicio ni uso real | Baja | Eliminar hasta que se implemente el módulo de métodos de pago |

## HERRAMIENTAS DE SOFTWARE UTILIZADAS

### Herramientas de Automatización de Medición

| Herramienta | Categoría | Uso en la Evaluación de AcaciosWork |
| :--- | :--- | :--- |
| **Postman v11** | Pruebas de API REST | Ejecución de los 10 casos de prueba de seguridad, funcionalidad y desempeño de la API. Scripts de aserción en JavaScript para validar códigos HTTP, estructura JSON y valores de negocio. Medición de tiempos de respuesta en milisegundos. |
| **Git + GitHub** | Control de Versiones | Análisis del historial de 22 commits para auditoría de trazabilidad. Verificación del cumplimiento de la convención de commits semánticos. |
| **IntelliJ IDEA** | IDE + Profiler | Monitoreo del consumo de memoria RAM del proceso Java mediante el profiler integrado. Medición del tiempo de arranque del servidor Spring Boot. |
| **MySQL Workbench** | Base de Datos | Verificación de consistencia de datos tras pruebas de rollback transaccional. Consultas SQL directas para confirmar que las transacciones fallidas no dejan registros parciales. |
| **Chrome DevTools** | Frontend Web | Verificación del comportamiento del cliente web: llamadas HTTP, tiempos de carga de página, consola de errores JavaScript y análisis de la estructura DOM. |
| **Android Studio Emulator** | Cliente Móvil | Pruebas de portabilidad en múltiples resoluciones de pantalla. Verificación de la navegación entre las 12 pantallas Compose. |
| **PowerShell (Get-ChildItem)** | Análisis Estático | Auditoría automatizada del número de líneas por archivo de código fuente en el repositorio para verificar el cumplimiento del límite de 300 líneas. |
| **VisualVM (JDK Tool)** | Monitoreo JVM | Medición del consumo de memoria heap de la JVM del backend bajo diferentes cargas de trabajo. |

### Visualización de Resultados

Los resultados de la evaluación se visualizaron y comunicaron mediante los siguientes mecanismos:

* **Tablas comparativas pre/post ajuste** — Presentadas en el presente documento para comunicar el delta de mejora de calidad.
* **Colecciones Postman exportables** — El conjunto de casos de prueba de la API queda disponible como artefacto reutilizable para futuras regresiones.
* **Historial Git como línea de tiempo auditora** — El repositorio actúa como la visualización cronológica de la evolución de la calidad del producto.
* **Bitácora de Desarrollo (GA11-220501098-AA1-EV04)** — Registro visual y cronológico de 23 actividades de desarrollo y aseguramiento de calidad.

## CONCLUSIONES

1. **Los ajustes de calidad produjeron resultados medibles y significativos:** El promedio ponderado de calidad ISO/IEC 25000 del producto AcaciosWork pasó de **7.87/10** (pre-ajuste, GA11-220501098-AA1-EV03) a **8.88/10** (post-ajuste), un incremento de **+1.01 puntos** (+12.8%), confirmando que los ajustes técnicos (ADJ-001 a ADJ-007) y operativos (ADJ-005, ADJ-008) tuvieron el impacto esperado.

2. **La seguridad y la fiabilidad fueron las características con mayor mejora:** La reactivación del filtro JWT (ADJ-001) y la implementación de transacciones atómicas con `@Transactional` elevaron la seguridad a **10.0** y la fiabilidad a **9.0**, respectivamente. Estas son las dos características más críticas para un sistema ERP/POS que maneja datos financieros.

3. **La mantenibilidad es ahora un activo del proyecto:** El cumplimiento del 100% del límite de 300 líneas en los componentes Java del backend y el Desktop, y el 81.8% en el frontend JavaScript, convierten la base de código en un activo mantenible que puede ser modificado, probado y extendido con menor riesgo y menor esfuerzo.

4. **Las herramientas de evaluación elegidas son suficientes y apropiadas:** Postman, Git, IntelliJ IDEA y MySQL Workbench conforman un conjunto de herramientas que cubren los cuatro niveles de verificación necesarios para un sistema de esta escala: API, código fuente, base de datos y comportamiento del cliente. No se requieren herramientas de mayor costo o complejidad para esta fase del proyecto.

5. **Las incidencias abiertas definen el próximo ciclo de mejora:** Las cuatro incidencias identificadas en la fase 5 (INC-003 a INC-008) no son fracasos de la evaluación; son el insumo ordenado del siguiente ciclo de desarrollo. En particular, la persistencia de alertas de stock (INC-003) y la paginación de endpoints (INC-006) son las prioridades técnicas del siguiente sprint de estabilización, dado que su impacto en la experiencia del usuario final y en la escalabilidad del sistema es alto.

## REFERENCIAS

Servicio Nacional de Aprendizaje (SENA). (2026). *Aplicación de pruebas de software* (Componente Formativo GA11-220501098-AA3). Centro de Gestión y Desarrollo Sostenible Surcolombiano.

International Organization for Standardization. (2011). *ISO/IEC 25010: Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE) — System and software quality models*. ISO/IEC.

International Organization for Standardization. (2001). *ISO/IEC 9126-1: Software engineering — Product quality — Part 1: Quality model*. ISO/IEC.

Institute of Electrical and Electronics Engineers. (2013). *ISO/IEC/IEEE 29119-3: Software and systems engineering — Software testing — Part 3: Test documentation*. IEEE.

Software Engineering Institute. (2010). *CMMI for Development, Version 1.3*. Carnegie Mellon University.

Díaz Jiménez, R. A. (2026). *Bitácora de los procesos documentados del proyecto AcaciosWork* (GA11-220501098-AA1-EV04). SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Diseño y diligenciamiento de instrumentos para documentar procesos de calidad* (GA11-220501098-AA1-EV03). SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Realiza ajustes en los procesos de desarrollo* (GA11-220501098-AA3-EV01). SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Estado actual del proyecto AcaciosWork: Evaluación técnica y hoja de ruta*. SENA — Ficha 3118313.
