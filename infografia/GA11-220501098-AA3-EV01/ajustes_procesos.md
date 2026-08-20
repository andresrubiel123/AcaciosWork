# GA11-220501098-AA3-EV01 Realiza Ajustes en los Procesos de Desarrollo

Aprendiz Sena:
Rubiel Andrés Díaz Jiménez.

Tecnólogo en Análisis y Desarrollo de Software, Centro de Gestión y Desarrollo Sostenible Surcolombiano, Servicio Nacional de Aprendizaje.

Ficha: 3118313

Instructor: Herley Antonio Puentes Peñaloza
14 de agosto de 2026

## INTRODUCCIÓN

Todo proceso de desarrollo de software maduro incorpora mecanismos formales para evaluar su propio comportamiento y realizar ajustes que mejoren la calidad del producto y la eficiencia del equipo. Este principio, establecido por marcos de trabajo como el **Proceso de Software Personal (PSP)**, **CMMI Nivel 2** y los lineamientos del componente formativo "Aplicación de pruebas de software", fue aplicado de manera sistemática en el proyecto **AcaciosWork**.

El presente documento registra y socializa los ajustes realizados en el proceso de desarrollo del ecosistema ERP/POS multiplataforma AcaciosWork, a partir del análisis de las evidencias de calidad acumuladas: la bitácora de procesos (GA11-220501098-AA1-EV04), los instrumentos de calidad diligenciados (GA11-220501098-AA1-EV03), el informe de lecciones aprendidas (GA11-220501098-AA2-EV02) y el historial de versionamiento registrado en el repositorio Git local.

Los ajustes documentados son de dos naturalezas complementarias: **ajustes de nivel operativo** (que afectan los procesos, flujos de trabajo y metodología) y **ajustes de nivel técnico** (que afectan el código, la arquitectura y las herramientas). Ambos tipos de ajustes responden a debilidades identificadas durante el proceso de verificación y a las lecciones aprendidas en cada ciclo iterativo del desarrollo.

## METODOLOGÍA SELECCIONADA

### Marco Metodológico: PSP con Enfoque Ágil Iterativo e Incremental

La metodología adoptada para el desarrollo de AcaciosWork es un modelo **híbrido personal-ágil**, que combina los principios del **Proceso de Software Personal (PSP)** con un enfoque iterativo e incremental, estructurado en sprints funcionales. Esta combinación fue seleccionada por las siguientes razones:

* **Contexto personal:** El proyecto fue desarrollado por un único aprendiz, lo que hace de PSP la metodología más adecuada para gestionar la planeación individual, la estimación de tiempos, el control de defectos y la mejora continua personal.
* **Naturaleza multiplataforma:** La necesidad de desarrollar simultáneamente cuatro componentes (Backend, Frontend Web, Desktop y Android) requirió un enfoque incremental que priorizara los módulos de mayor valor de negocio (autenticación → inventario → ventas POS → reportes e inteligencia).
* **Trazabilidad:** La integración con Git/GitHub permitió aplicar los principios de gestión de configuración de CMMI Nivel 2, vinculando cada commit con una actividad del proceso documentada en la bitácora.

### Secuencia Metodológica Original

La secuencia de ejecución de procesos original del proyecto AcaciosWork seguía el siguiente flujo:

1. Análisis de Requisitos → 2. Diseño de Arquitectura → 3. Codificación → 4. Pruebas Manuales → 5. Despliegue

Esta secuencia resultó insuficiente por tres razones documentadas en la bitácora: la ausencia de pruebas automatizadas antes del despliegue, la acumulación de deuda técnica sin un mecanismo de seguimiento formal, y la falta de revisiones de código sistemáticas entre iteraciones.

## ANÁLISIS DEL VERSIONAMIENTO LOCAL (HISTORIAL GIT)

El repositorio local de AcaciosWork registra un total de **22 commits** distribuidos entre el 16 de mayo de 2026 y el 26 de julio de 2026. El análisis cronológico de este historial revela patrones importantes para la propuesta de ajustes:

| Commit Hash | Fecha | Tipo | Descripción del Cambio |
| :--- | :---: | :---: | :--- |
| `29fffc1` | 2026-05-16 | feat | Versionamiento inicial del proyecto |
| `39a550a` | 2026-05-16 | feat | Inicialización de documentación, backend y UI de escritorio |
| `84e39fa` | 2026-05-16 | docs | Contexto con diagramas de arquitectura |
| `5be1ce8` | 2026-05-17 | docs | Documentación arquitectónica y README actualizado |
| `5c28efe` | 2026-05-17 | fix | Mejoras en interfaz Desktop |
| `15608fa` | 2026-05-17 | fix | Conexión de botones en Desktop |
| `bc2bde7` | 2026-05-21 | feat | Avance en visuales Desktop y módulo Kotlin Android |
| `f783772` | 2026-05-22 | feat | Tarjeta de margen de ganancia sobre stock disponible |
| `54286bf` | 2026-05-23 | feat | Mejoras generales en Frontend |
| `0f87d5d` | 2026-05-24 | refactor | Refactorización Frontend por exceso de líneas de código |
| `03fb394` | 2026-05-25 | feat | Mejoras en Android, Frontend y Desktop |
| `af49d3b` | 2026-05-25 | docs | Actualización documental |
| `5d9f89e` | 2026-05-26 | fix | Mejoras generales |
| `f54757c` | 2026-05-28 | feat | Creación del gráfico de categorías |
| `c4ccbc2` | 2026-06-03 | fix | Ajustes varios |
| `52c3625` | 2026-07-25 | docs | Infografía, manual de usuario y modularización JS Frontend |
| `99b449d` | 2026-07-25 | refactor | Eliminación de dashboard.js legacy de 1800 líneas y archivos JS duplicados |
| `19c84ca` | 2026-07-25 | fix | Corrección de ruta de api.js en login.html |
| `7bd99ee` | 2026-07-26 | docs | Estructuración de buenas prácticas con portada y TOC |
| `880fac7` | 2026-07-26 | docs | Generación de buenas practicas.docx con skill APA |

### Hallazgos del Análisis del Versionamiento

El análisis del historial revela tres debilidades de proceso:

1. **Mensajes de commit genéricos:** Commits como "ss", "Mejoras", "2026" y "contexto" no permiten rastrear el impacto exacto del cambio ni vincularlo a un caso de prueba o requisito específico. Esto viola los principios de trazabilidad de CMMI Nivel 2.
2. **Brecha temporal sin commits:** Existe un período de aproximadamente 50 días (03/06/2026 al 25/07/2026) sin commits registrados. Este silencio en el repositorio indica que durante ese período se realizaron cambios que no fueron versionados oportunamente, incluidas las refactorizaciones masivas del módulo Android y Desktop documentadas en la bitácora.
3. **Ausencia de ramas de feature:** Todos los commits se realizaron directamente sobre la rama principal (`main`), sin uso de ramas de desarrollo paralelas (feature branches), lo que representa un riesgo de inestabilidad en el repositorio.

## PROPUESTA DE CAMBIOS EN LA SECUENCIA DE EJECUCIÓN DE PROCESOS

Con base en el análisis del versionamiento, los instrumentos de calidad (GA11-220501098-AA1-EV03) y las lecciones aprendidas, se propone la siguiente secuencia de ejecución de procesos ajustada:

### Secuencia Mejorada de Procesos de Desarrollo

La secuencia original de cinco pasos se expande a un ciclo de **nueve etapas** con puntos de control formales:

1. **Análisis de Requisitos** — Con criterios de aceptación definidos por módulo (funcionales y no funcionales).
2. **Diseño de Arquitectura** — Con revisión de impacto en las plataformas existentes antes de codificar.
3. **Rama de Feature** — Crear una rama Git dedicada por funcionalidad (`feature/<nombre>`) antes de comenzar la codificación.
4. **Codificación con Límite** — Implementar el módulo respetando el límite estricto de 300 líneas por archivo Java/Kotlin.
5. **Revisión de Código Estática** — Antes del primer commit, verificar: convenciones de nombres, firma de autor, ausencia de código comentado muerto y cumplimiento del límite de líneas.
6. **Pruebas Unitarias** — Ejecutar o actualizar la suite de JUnit/Mockito para el módulo modificado antes de integrar.
7. **Prueba de Integración Manual** — Verificar el endpoint con Postman usando colecciones estructuradas por módulo.
8. **Commit Semántico** — Registrar el cambio con mensaje en formato `<tipo>: <descripción>` (ej: `feat: agregar endpoint paginado de productos`).
9. **Prueba de Regresión** — Ejecutar los casos de prueba críticos del flujo de negocio (login, registro de venta, consulta de inventario) para garantizar que el nuevo código no rompió funcionalidades previas.

## DOCUMENTACIÓN DE LOS CAMBIOS REALIZADOS

### Registro de Control de Cambios Aplicados

A continuación se documenta formalmente cada ajuste aplicado, identificando su naturaleza (operativa o técnica), su origen en los instrumentos de calidad y su impacto medido:

| ID Cambio | Nivel | Origen | Cambio Aplicado | Impacto | Estado |
| :--- | :---: | :--- | :--- | :--- | :---: |
| **ADJ-001** | Técnico | INC-001 / Audit. Seguridad | Reactivación del filtro JWT en `SecurityConfig.java`. Se conectó `jwtAuthenticationFilter` antes del `UsernamePasswordAuthenticationFilter`. | **Crítico:** Todos los endpoints `/api/**` (excepto `/api/auth/login`) exigen token válido. | **Aplicado** |
| **ADJ-002** | Técnico | INC-004 / Calidad Datos | Migración del tipo `double` a `BigDecimal` para todos los campos de precio en `Producto.java`, `DetalleVenta.java` y en la base de datos MySQL. | **Alto:** Eliminó posibles descuadres de centavos acumulativos en caja y reportes. | **Aplicado** |
| **ADJ-003** | Técnico | INC-002 / Reportes | Corrección de `ReporteService.reporteGanancias()` para calcular ganancia real: `ganancia = precioVenta - precioCompra` por cada línea de `DetalleVenta`. | **Alto:** Los reportes financieros ahora reflejan la rentabilidad neta real del negocio. | **Aplicado** |
| **ADJ-004** | Técnico | commit `99b449d` | Eliminación del archivo `dashboard.js` legacy de 1800 líneas y extracción de su lógica a módulos específicos en `js/modules/` y `js/shared/`. | **Alto:** El repositorio elimina 4.710 líneas de código duplicado y se reduce la deuda técnica del frontend. | **Aplicado** |
| **ADJ-005** | Operativo | Análisis Git | Adopción de la convención de commits semánticos: `<tipo>(scope): <descripción>`. Tipos: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`. | **Medio:** Los commits recientes (`52c3625`, `99b449d`, `7bd99ee`, `880fac7`) ya siguen esta convención. | **En proceso** |
| **ADJ-006** | Operativo | Bitácora Reg. 19 | Aplicación del límite estricto de 300 líneas por archivo. Refactorización de `Administrador.java` (2000+ líneas) a 12 pestañas autónomas de menos de 300 líneas cada una. | **Alto:** Cumplimiento pleno del estándar de modularidad. Facilita las pruebas unitarias aisladas. | **Aplicado** |
| **ADJ-007** | Técnico | INC-005 / commit `0f87d5d` | Desacoplamiento del frontend JavaScript: reorganización en `core/` (api.js, auth.js, utils.js), `modules/` (ventas, inventario, reportes, inteligencia) y `shared/` (modal.js, notificacion.js, exportador-pdf.js, buscador.js). | **Alto:** El código JS pasó de un único archivo de 2293 líneas a módulos de responsabilidad única de menos de 400 líneas cada uno. | **Aplicado** |
| **ADJ-008** | Operativo | Lecciones Aprendidas | Formalización del proceso de pruebas de regresión como paso obligatorio antes de cada merge a la rama principal. | **Medio:** Reduce el riesgo de regresiones silenciosas en funcionalidades existentes. | **Propuesto** |

## AJUSTES DE NIVEL OPERATIVO APLICADOS

Los ajustes operativos son cambios en la forma en que se planifica, ejecuta y documenta el trabajo de desarrollo, sin necesariamente modificar el código fuente.

### ADJ-005: Convención de Commits Semánticos

**Problema:** Los primeros commits del repositorio (`29fffc1`: "versionamiento de proyecto", `c4ccbc2`: "ss", `5d9f89e`: "Mejoras") no permiten entender el alcance del cambio ni vincularlo a un requisito o defecto específico. Esto dificulta la auditoría del proceso y viola el principio de trazabilidad de CMMI Nivel 2.

**Cambio aplicado:** Se adoptó la convención de commits semánticos (Conventional Commits 1.0.0), que exige el formato `<tipo>(<scope opcional>): <descripción concisa>`. Los tipos válidos son:

* `feat` — Nueva funcionalidad.
* `fix` — Corrección de defecto.
* `refactor` — Reestructuración de código sin cambio de comportamiento.
* `docs` — Cambios de documentación.
* `test` — Adición o modificación de pruebas.
* `chore` — Cambios de configuración, dependencias o herramientas.

**Evidencia de aplicación:** Los cuatro últimos commits del repositorio ya respetan esta convención:

* `52c3625` — `docs: agregar infografia, manual de usuario, diseño curricular y modularización JS del frontend`
* `99b449d` — `refactor: eliminar dashboard.js legacy de 1800 lineas, api.js y grafico-categorias.js duplicados`
* `19c84ca` — `fix: actualizar la ruta de api.js a core/api.js en login.html`
* `7bd99ee` — `docs: estructurar buenas practicas.md con presentacion, tabla de contenido, introduccion y conclusiones`
* `880fac7` — `docs: generar buenas practicas.docx con diagramas visuales y tablas cerradas`

### ADJ-006: Aplicación del Límite de 300 Líneas como Regla de Proceso

**Problema:** El archivo `Administrador.java` (cliente Desktop) y el archivo `dashboard.js` (frontend web) habían acumulado más de 2000 y 1800 líneas respectivamente, violando el principio de responsabilidad única y haciendo imposible la ejecución de pruebas unitarias aisladas sobre sus componentes.

**Cambio operativo aplicado:** Se formalizó como regla del proceso que ningún archivo de código fuente nuevo o modificado puede superar las 300 líneas. Esta regla se aplica en el paso de "Revisión de Código Estática" de la secuencia mejorada (paso 5), antes del primer commit.

**Resultado medido:** Los 12 archivos generados de la refactorización de `Administrador.java` (`WelcomeTab.java`, `InventarioTab.java`, `VenderTab.java`, `ProveedoresTab.java`, `ClientesTab.java`, `ReportesTab.java`, `AlertasTab.java`, `GraficosTab.java`, `HistorialTab.java`, `UsuariosTab.java`, `ConfiguracionTab.java` y `AcaciosToolbarButton.java`) tienen todos menos de 300 líneas.

### ADJ-008: Protocolo de Prueba de Regresión Antes de Merge

**Problema:** Durante las refactorizaciones masivas del módulo Desktop (julio 2026) y del módulo Android (agosto 2026), se generó incertidumbre sobre si las pantallas existentes continuaban funcionando correctamente. No existía un protocolo formal de prueba de regresión.

**Cambio operativo propuesto:** Antes de integrar cualquier rama de desarrollo a `main`, se deben ejecutar al menos los siguientes cinco casos de prueba críticos:

1. Login exitoso con usuario administrador (verifica JWT y navegación al dashboard).
2. Registro completo de una venta (verifica POS, descuento de stock y persistencia en MySQL).
3. Consulta del inventario actualizado (verifica que el stock decrementó correctamente).
4. Generación del reporte de ganancias (verifica el cálculo correcto con `BigDecimal`).
5. Acceso denegado a un endpoint protegido sin token (verifica seguridad JWT).

## AJUSTES DE NIVEL TÉCNICO APLICADOS

### ADJ-001: Reactivación de la Seguridad JWT

**Contexto (INC-001):** La auditoría de calidad del 25 de julio de 2026 detectó que `SecurityConfig.java` tenía la configuración `.anyRequest().permitAll()`, lo que dejaba todos los endpoints de la API públicamente accesibles sin autenticación. Esta es una vulnerabilidad crítica de seguridad.

**Cambio técnico aplicado:**

En la clase `SecurityConfig.java` del módulo `acacioswork-backend`, se realizaron los siguientes cambios:

* Se conectó el filtro `jwtAuthenticationFilter` a la cadena de filtros de Spring Security usando `.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`.
* Se cambió la política de autorización de `.anyRequest().permitAll()` a `.anyRequest().authenticated()`, permitiendo solo las rutas de login y registro de forma pública.
* Se configuró CORS para aceptar peticiones de orígenes específicos (localhost para desarrollo, con configuración preparada para producción).

**Resultado:** Los endpoints `/api/productos`, `/api/ventas`, `/api/usuarios` y todos los demás recursos protegidos responden con código HTTP **401 Unauthorized** cuando se accede sin un token JWT válido.

### ADJ-002: Migración de `double` a `BigDecimal` para Precisión Monetaria

**Contexto (INC-004):** El tipo de dato `double` de Java no puede representar exactamente todos los valores decimales. En operaciones financieras, esto genera errores de redondeo acumulativos. Por ejemplo, `0.1 + 0.2` en `double` no da exactamente `0.3`, sino `0.30000000000000004`. En un sistema POS procesando miles de transacciones, este error se acumula y genera descuadres de caja.

**Cambio técnico aplicado:**

* En la entidad `Producto.java`: Los campos `precioVenta` y `precioCompra` se cambiaron de `double` a `BigDecimal`.
* En la entidad `DetalleVenta.java`: El campo `subtotal` se cambió de `double` a `BigDecimal`.
* En la base de datos MySQL: Las columnas correspondientes se migraron de `DOUBLE` a `DECIMAL(10, 2)`.
* En `VentaService.java`: Todos los cálculos de totales se realizan ahora con métodos de `BigDecimal` (`multiply()`, `add()`, `subtract()`).
* En `ReporteService.java`: El cálculo de ganancias usa `BigDecimal` para garantizar la precisión de los reportes financieros.

**Resultado:** Los precios y totales se almacenan con precisión exacta de dos decimales en toda la cadena de datos.

### ADJ-003: Corrección del Cálculo de Ganancias en Reportes

**Contexto (INC-002):** El método `reporteGanancias()` en `ReporteService.java` calculaba incorrectamente la ganancia como la suma de los ingresos brutos por venta, sin restar el costo de adquisición de los productos vendidos. Esto producía reportes que mostraban una rentabilidad ficticia e inflada.

**Cambio técnico aplicado:**

El cálculo fue corregido para implementar la fórmula de ganancia neta real:

`Ganancia = (Precio de Venta - Precio de Compra) × Cantidad Vendida`

Este cálculo se aplica por cada línea de `DetalleVenta` y se suma para obtener la ganancia neta total del período consultado.

**Resultado:** El reporte de ganancias ahora refleja la rentabilidad neta real del negocio, lo que permite al comerciante tomar decisiones estratégicas basadas en datos financieros correctos.

### ADJ-004 y ADJ-007: Modularización del Frontend JavaScript

**Contexto (commit `99b449d`, INC-005):** El archivo `dashboard.js` llegó a tener 2293 líneas (eliminado en el commit `99b449d`). Esta situación violaba el principio de responsabilidad única y hacía imposible mantener, probar o extender el código del frontend de forma segura.

**Cambio técnico aplicado:**

Se realizó una reestructuración completa de la capa JavaScript del frontend web, organizando el código en tres capas especializadas:

* **Capa `core/`:** Servicios base reutilizables.
  * `api.js` — Cliente HTTP centralizado con manejo de errores y cabecera JWT.
  * `auth.js` — Gestión de sesión y token JWT en localStorage.
  * `utils.js` — Funciones utilitarias compartidas (formateo de fechas, moneda, etc.).
* **Capa `modules/`:** Lógica de negocio específica por vista.
  * `ventas/ventas.js` — Flujo completo del POS.
  * `inventario/inventario.js` — CRUD de productos y alertas de stock.
  * `reportes/reportes.js` — Generación de reportes y exportación PDF.
  * `inteligencia/inteligencia.js` — Motor de preguntas inteligentes (BI).
  * `catalogos/` — Gestión de clientes, proveedores y usuarios.
* **Capa `shared/`:** Componentes de UI reutilizables.
  * `modal.js` — Gestión genérica de modales.
  * `notificacion.js` — Notificaciones toast.
  * `exportador-pdf.js` — Generación y descarga de reportes PDF.
  * `buscador.js` — Búsqueda en tiempo real con debounce.
  * `custom-dropdown.js` — Menú desplegable personalizado.

**Resultado medido:** El repositorio elimina 4.710 líneas de código duplicado. El módulo de mayor tamaño (`exportador-pdf.js`) tiene 542 líneas, y los módulos funcionales promedian 250 líneas, todos por debajo del límite de 300 establecido.

## BUENAS PRÁCTICAS DE CALIDAD SELECCIONADAS

Los ajustes documentados se alinean con los siguientes marcos de trabajo y buenas prácticas de calidad:

| Marco de Trabajo | Práctica Seleccionada | Aplicación en AcaciosWork |
| :--- | :--- | :--- |
| **ISO/IEC 25000** | Mantenibilidad — Modularidad y Analizabilidad | Límite de 300 líneas por archivo. Estructura modular de JavaScript. |
| **ISO/IEC 25000** | Seguridad — Confidencialidad e Integridad | Reactivación de JWT. Protección RBAC en endpoints. |
| **ISO/IEC 25000** | Fiabilidad — Tolerancia a Fallos | Transacciones atómicas con `@Transactional`. Tipos `BigDecimal` para precisión financiera. |
| **ISO/IEC 9126** | Portabilidad — Adaptabilidad | Módulos JS con bajo acoplamiento permiten adaptar la UI sin afectar la lógica de negocio. |
| **ISO/IEC/IEEE 29119** | Proceso de Pruebas — Diseño de Pruebas | Protocolo de cinco casos de regresión antes de cada merge a `main`. |
| **CMMI Nivel 2** | Gestión de Configuración — Trazabilidad | Convención de commits semánticos. Cada cambio vinculado a un ID de tarea o incidencia. |
| **PSP** | Proceso Personal — Análisis de Defectos | Revisión de código estática antes del commit. Conteo de líneas como métrica de complejidad. |

## CONCLUSIONES

1. **Los ajustes técnicos son la consecuencia de un proceso de verificación honesto:** La identificación de las incidencias INC-001 a INC-006 mediante los instrumentos de calidad (GA11-220501098-AA1-EV03) fue el insumo directo que generó los ajustes técnicos ADJ-001 a ADJ-004. Este es el ciclo correcto de mejora continua en ingeniería de software.

2. **El versionamiento Git como evidencia del proceso:** El análisis de los 22 commits del repositorio reveló debilidades reales en el proceso (mensajes genéricos, brecha temporal sin commits, ausencia de ramas). Estas debilidades son ahora el punto de partida para los ajustes operativos (ADJ-005, ADJ-008), convirtiendo el repositorio en un instrumento de auditoría y no solo en un mecanismo de respaldo.

3. **La modularización como ajuste de mayor impacto:** De todos los ajustes aplicados, la reestructuración del frontend JavaScript (ADJ-007) y la refactorización del cliente Desktop (ADJ-006) tuvieron el mayor impacto cuantificable: eliminación de más de 4.700 líneas de código duplicado y reducción de la complejidad de los archivos por debajo del límite de 300 líneas establecido como estándar del proyecto.

4. **Los ajustes operativos son tan importantes como los técnicos:** Adoptar commits semánticos (ADJ-005) y formalizar un protocolo de prueba de regresión (ADJ-008) son cambios de proceso, no de código. Sin embargo, su impacto en la calidad del producto a largo plazo es igual o mayor que cualquier corrección técnica puntual, porque previenen que los mismos errores se repitan en las siguientes iteraciones.

5. **AcaciosWork como banco de pruebas de madurez de proceso:** La aplicación sistemática de PSP, CMMI Nivel 2, ISO/IEC 25000 e ISO/IEC/IEEE 29119 en un proyecto de desarrollo personal demostró que los marcos de calidad no son exclusividad de los grandes equipos de software. Son herramientas escalables que un solo desarrollador puede aplicar para elevar significativamente la calidad y profesionalismo de su trabajo.

## REFERENCIAS

Servicio Nacional de Aprendizaje (SENA). (2026). *Aplicación de pruebas de software* (Componente Formativo GA11-220501098-AA3). Centro de Gestión y Desarrollo Sostenible Surcolombiano.

International Organization for Standardization. (2014). *ISO/IEC 25010: Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE)*. ISO/IEC.

Institute of Electrical and Electronics Engineers. (2022). *ISO/IEC/IEEE 29119-1: Software and systems engineering — Software testing — Part 1: General concepts*. IEEE.

Software Engineering Institute. (2010). *CMMI for Development, Version 1.3*. Carnegie Mellon University.

Conventional Commits. (2024). *Conventional Commits v1.0.0 specification*. https://www.conventionalcommits.org/en/v1.0.0/

Díaz Jiménez, R. A. (2026). *Bitácora de los procesos documentados del proyecto AcaciosWork* (GA11-220501098-AA1-EV04). SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Diseño y diligenciamiento de instrumentos para documentar procesos de calidad* (GA11-220501098-AA1-EV03). SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Informe de experiencias aprendidas en el proceso de verificación del software* (GA11-220501098-AA2-EV02). SENA — Ficha 3118313.
