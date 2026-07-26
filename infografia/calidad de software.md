# INVESTIGACIÓN Y DISEÑO DE INSTRUMENTOS DE CALIDAD DE SOFTWARE

**Programa de Formación:** Tecnólogo en Análisis y Desarrollo de Software  
**Ficha:** 3118313  
**Centro de Formación:** Centro de Gestión y Desarrollo Sostenible Surcolombiano  
**Regional:** Huila  
**SENA**  

---

**Presentado por:** Rubiel Andrés Díaz Jiménez  
**Presentado a:** Herley Antonio Puentes Peñaloza  
**Fecha de Presentación:** 25 de Julio de 2026  

---

## 1. INTRODUCCIÓN

En la industria moderna de la tecnología de la información, el aseguramiento de la calidad de software (*Software Quality Assurance* o SQA) ha dejado de ser una fase aislada al final del desarrollo para convertirse en una disciplina transversal e integrada en todo el ciclo de vida del producto. El desarrollo rápido y la alta competencia exigen que el software no solo sea funcional, sino también confiable, seguro, mantenible y eficiente. 

El presente documento recopila una investigación exhaustiva basada en el componente formativo **"Fundamentos de calidad de software"** del SENA. En él se analizan los pilares de la calidad del software divididos en tres áreas críticas: la estructuración y definición de las ideas de negocio, los estándares internacionales y modelos de madurez (como la familia ISO/IEC 25000, ISO/IEC 9126, CMMI y metodologías PSP/TSP y Scrum), y la documentación sistemática de los procesos de pruebas y gestión de incidencias (con base en la norma ISO/IEC/IEEE 29119-3).

Asimismo, como producto de esta investigación, se diseñan e incorporan cinco instrumentos prácticos de calidad de software, construidos bajo estándares profesionales y adaptados para su uso directo en proyectos reales de desarrollo de software.

---

## 2. DESARROLLO DE LA INVESTIGACIÓN

### TEMA 1: DESCRIPCIÓN DE LA IDEA DE NEGOCIO Y ESTRUCTURA IDEOLÓGICA

Cualquier proyecto de desarrollo de software nace a partir de una necesidad del mercado que se traduce en una idea de negocio. Para materializar y dar viabilidad a esta idea, se debe estructurar un plan de negocio sólido, el cual se divide tradicionalmente en ese cinco estructuras fundamentales:

1. **Estructura ideológica:** Contiene el alma de la organización. Incluye el nombre de la empresa, la misión, la visión, los valores corporativos, los objetivos generales y específicos, y la descripción detallada de las ventajas competitivas.
2. **Estructura del entorno:** Consiste en un análisis del mercado externo. Involucra el estudio de debilidades y fortalezas de la empresa (análisis interno), las amenazas y oportunidades (análisis externo), el comportamiento de la competencia, clientes potenciales y tendencias del sector.
3. **Estructura mecánica:** Define las estrategias operativas necesarias para llevar el producto al mercado. Incluye planes de distribución, ventas, mercadeo, publicidad y comunicación.
4. **Estructura financiera:** Evalúa la viabilidad económica del proyecto a través de proyecciones financieras, análisis de costos, flujo de caja, punto de equilibrio y rentabilidad estimada.
5. **Recursos humanos:** Determina los roles, perfiles de cargos, organigrama, derechos y obligaciones de cada colaborador de la organización.

#### Componentes de la Estructura Ideológica
* **La Empresa:** Definición clara del nombre y a qué se dedica. El nombre debe ser corto, fácil de recordar y pronunciar.
* **La Misión:** Representa la razón de ser de la empresa en el presente. Responde a las preguntas: *¿A qué se dedica el negocio?*, *¿Quién es el cliente objetivo?*, y *¿Qué nos diferencia de la competencia?*
* **La Visión:** Define la proyección a futuro de la organización. Debe ser motivadora, clara, alcanzable pero ambiciosa, y fácil de comunicar.
* **Los Objetivos:** Componente clave para centrar los esfuerzos de la organización. Para que sean efectivos, deben ser medibles (cuantitativos), alcanzables, comprensibles, exigir esfuerzo y ser coherentes entre sí.

---

### TEMA 2: MODELOS DE CALIDAD Y ESTÁNDARES INTERNACIONALES

La calidad del software se evalúa tanto a nivel de los procesos utilizados para construirlo como a nivel del producto final entregado. 

#### 2.1 Fases del Ciclo de Vida del Desarrollo de Software
* **Análisis:** Evaluación de viabilidad técnica y financiera, estimaciones preliminares y la definición de requisitos funcionales y no funcionales. Genera el concepto del producto.
* **Diseño:** Formulación de la arquitectura del sistema, interfaces, bases de datos y la planificación de las pruebas de componentes.
* **Implementación:** Traducción del diseño a código fuente en el lenguaje de programación elegido, acompañado de pruebas unitarias y depuración.
* **Prueba:** Integración de los componentes de software en ambientes controlados y ejecución de pruebas de aceptación para validar los requisitos con usuarios finales.
* **Servicio y Mantenimiento:** Despliegue en producción y soporte continuo. Se divide en tres tipos de mantenimiento:
  * *Mantenimiento Correctivo:* Reparación de fallos o defectos detectados en producción.
  * *Mantenimiento Adaptativo:* Modificaciones para adaptar el sistema a nuevos entornos (hardware, sistemas operativos).
  * *Mantenimiento Perfectivo:* Adición de nuevas funcionalidades solicitadas por el cliente.

#### 2.2 Familia de Normas ISO/IEC 25000 (SQuaRE)
La norma **SQuaRE** (*Software Product Quality Requirements and Evaluation*) organiza la evaluación de calidad en cinco divisiones estructuradas:
1. **ISO/IEC 2500n - Gestión de Calidad:** Define los términos comunes, modelos y definiciones que guían a las demás normas.
2. **ISO/IEC 2501n - Modelo de Calidad:** Presenta modelos detallados para calidad interna, externa (ISO 2510) y de datos (ISO 25012).
3. **ISO/IEC 2502n - Medición de Calidad:** Describe el modelo de referencia para la medición, con métricas específicas para el ciclo de vida del software, calidad en uso y calidad de datos.
4. **ISO/IEC 2503n - Requisitos de Calidad:** Guías y metodologías para la especificación y recopilación de requisitos de calidad del producto.
5. **ISO/IEC 2504n - Evaluación de Calidad:** Detalla el modelo de referencia, directrices y módulos necesarios para realizar la evaluación del producto.

* **Ventajas:** Alineación con estándares internacionales unificados, garantiza la satisfacción del cliente al medir la calidad en uso, y disminuye notablemente los costos de mantenimiento a largo plazo.
* **Desventajas:** Alta demanda de esfuerzo y documentación inicial, complejidad percibida en su aplicación por microempresas y equipos pequeños, y la necesidad de capacitación técnica especializada del personal.

#### 2.3 Estándar ISO/IEC 9126 - Factores de McCall
Este estándar pionero evalúa la calidad a través de **18 factores clave** clasificados en tres perspectivas del producto de software:
* **Operación del Producto:**
  1. *Corrección:* ¿El software cumple con los requisitos del cliente?
  2. *Fiabilidad:* ¿Funciona con precisión constante en el tiempo?
  3. *Eficiencia:* Optimización en el uso de los recursos de hardware (memoria, CPU).
  4. *Integridad:* Control de accesos y protección ante amenazas de seguridad.
  5. *Facilidad de uso:* Simplicidad de aprendizaje y operación por el usuario final.
* **Revisión del Producto:**
  6. *Facilidad de mantenimiento:* Esfuerzo requerido para localizar y corregir fallos.
  7. *Facilidad de prueba:* Esfuerzo necesario para validar el correcto funcionamiento del software.
  8. *Flexibilidad:* Facilidad para introducir modificaciones en el código ya operativo.
* **Transición del Producto:**
  9. *Portabilidad:* Facilidad para migrar el sistema a otras plataformas de hardware o software.
  10. *Reusabilidad:* Capacidad para reutilizar fragmentos de código en otros sistemas.
  11. *Interoperabilidad:* Capacidad del software para interactuar y acoplarse con otros sistemas.
* **Factores de Soporte e Integración Adicionales:** *Exactitud, Completitud, Seguridad, Consistencia, Facilidad de auditoría, Normalización de comunicaciones y Tolerancia a errores*.

#### 2.4 Modelo CMMI (Capability Maturity Model Integration)
CMMI provee un marco para medir y mejorar la madurez de los procesos organizacionales en el desarrollo de software. Admite dos representaciones:
* **Representación por Niveles de Madurez (Organización):**
  * *Nivel 1 (Inicial):* Procesos caóticos y ad hoc. Los éxitos no son repetibles.
  * *Nivel 2 (Administrado):* Proyectos planificados y monitoreados. Procesos estables y repetibles.
  * *Nivel 3 (Definido):* Procesos estandarizados a nivel organizacional y adaptados para cada proyecto.
  * *Nivel 4 (Administrado Cuantitativamente):* Procesos medidos y controlados mediante técnicas estadísticas.
  * *Nivel 5 (Optimización):* Enfoque en la mejora continua y prevención de causas comunes de variación.
* **Representación por Niveles de Capacidad (Procesos Individuales):** Va del *Nivel 0 (Incompleto)*, pasando por el *1 (Realizado)*, *2 (Administrado)*, *3 (Definido)*, *4 (Administrado Cuantitativamente)*, hasta el *Nivel 5 (Optimización)*.

#### 2.5 PSP (Personal Software Process) y TSP (Team Software Process)
* **PSP:** Disciplina individual que entrena a los ingenieros de software a estimar tiempos de desarrollo, registrar y clasificar sus propios defectos, y planificar su trabajo personal mediante formatos específicos (*Registro de Tiempo*, *Registro de Defectos*, *Resumen del Plan del Proyecto*). Consta de tres niveles/fases de madurez de proceso personal (PSP0, PSP1, PSP2).
* **TSP:** Metodología que organiza a los profesionales formados en PSP en equipos autogestionados y de alto rendimiento. Estructura el proyecto en ciclos iterativos de **8 fases**: 
  1. Lanzamiento
  2. Estrategia
  3. Planeación
  4. Requerimientos
  5. Diseño
  6. Implementación
  7. Pruebas
  8. Postmortem

#### 2.6 Marco de Trabajo Scrum (Metodología Ágil)
Metodología de desarrollo incremental e iterativo fundamentada en el control empírico de procesos bajo **3 pilares**:
* **Transparencia:** Todo aspecto del proceso es visible y de conocimiento público.
* **Inspección:** Evaluación regular de los artefactos para detectar desviaciones.
* **Adaptación:** Ajuste inmediato de los procesos ante cualquier problema.

* **Roles:** Divididos en *Centrales* (cerdos - Product Owner, Scrum Master, Development Team) y *No Centrales* (gallinas - clientes, inversionistas, stakeholders).
* **Artefactos:**
  * *Product Backlog:* Lista priorizada de todos los requisitos y características del producto (historias de usuario).
  * *Sprint Backlog:* Conjunto de tareas seleccionadas del Product Backlog para ser construidas durante un Sprint específico.
  * *Burndown Chart:* Gráfico que muestra las horas de trabajo o puntos de historia pendientes versus el tiempo del Sprint.

---

### TEMA 3: DOCUMENTACIÓN DEL PROCESO DE CALIDAD

La documentación en el proceso de calidad actúa como el único soporte objetivo que certifica la efectividad de las pruebas y la madurez del software antes del lanzamiento. 

#### 3.1 Estándar ISO/IEC/IEEE 29119-3:2013
Es el estándar de referencia internacional que define las plantillas y el contenido mínimo para documentar el proceso de pruebas de software, clasificado en tres niveles jerárquicos:
* **Documentación Organizacional de Pruebas:** Política de Pruebas y Estrategia Organizacional de Pruebas.
* **Documentación de Gestión de Pruebas:** Plan de Pruebas (incluye la estrategia particular del proyecto), Reporte de Estado de Prueba y Reporte de Cierre de Pruebas.
* **Documentación de Pruebas Dinámicas:** Especificación de diseño de pruebas, especificación de casos de prueba, especificación de procedimientos (scripts) de prueba, requisitos de datos de prueba, requisitos de entorno de prueba, registro de ejecución de pruebas y reportes de incidencias (bugs).

#### 3.2 Clasificación de Artefactos por Etapas SQA
1. **Planificación:** *Plan de Pruebas*, el cual detalla el alcance, recursos, cronograma, estrategias de riesgos y criterios de aceptación.
2. **Construcción:** *Casos de Prueba* (condiciones conceptuales) y *Scripts de Prueba* (conjunto de instrucciones detalladas paso a paso).
3. **Ejecución:** *Resultados de Ejecución* (Pass/Fail) e *Incidencias (Bugs)*.
4. **Análisis y Cierre:** *Informe de Resultados de Prueba*, que resume las métricas obtenidas durante el ciclo de pruebas.

#### 3.3 Ciclo de Vida de una Incidencia (Bug)
El flujo sistemático que experimenta un fallo detectado desde su descubrimiento hasta su resolución definitiva consta de los siguientes estados:
1. **Nuevo / Reportado:** El tester detecta el fallo y lo documenta en una plataforma de gestión.
2. **Asignado / En Investigación:** Se asigna al desarrollador responsable para que valide y determine la causa raíz.
3. **Corregido / Resuelto:** El programador modifica el código y realiza el despliegue del parche en el ambiente de pruebas.
4. **En Pruebas (Re-Test):** El tester ejecuta de nuevo los scripts de prueba asociados a la corrección para validar que el fallo ha desaparecido y no ha introducido regresiones.
5. **Cerrado / Rechazado:** Si la prueba es exitosa, se cierra el bug. De lo contrario, se reabre volviendo al estado asignado.

---

## 3. DISEÑO DE INSTRUMENTOS DE CALIDAD DE SOFTWARE

A continuación, se presenta el diseño formal de cinco instrumentos de calidad de software mediante plantillas estructuradas y utilizables basadas en las normas e investigaciones detalladas anteriormente.

### INSTRUMENTO 1: PLANTILLA DE PLAN DE PRUEBAS
*Basado en los lineamientos del estándar ISO/IEC/IEEE 29119-3.*

| Sección | Elemento / Campo | Descripción / Contenido Sugerido |
| :--- | :--- | :--- |
| **1. Introducción** | 1.1 Identificador del Documento | Código único de control (Ej. *PL-PR-001-V1.0*). |
| | 1.2 Nombre del Sistema | Nombre comercial o técnico de la aplicación a evaluar. |
| | 1.3 Historial de Versiones | Tabla con fecha, versión, autor del cambio y descripción. |
| **2. Alcance** | 2.1 Características a probar | Lista de requisitos funcionales y no funcionales dentro de la prueba. |
| | 2.2 Características NO a probar | Módulos fuera de alcance por restricciones de tiempo o recursos. |
| **3. Criterios de Prueba** | 3.1 Criterios de Aceptación (Entrada) | Condiciones mínimas del código y ambiente para iniciar SQA. |
| | 3.2 Criterios de Suspensión | Situaciones bajo las cuales se detiene la prueba (Ej. Bloqueos críticos). |
| | 3.3 Criterios de Salida | Métricas que definen el fin de las pruebas (Ej. 100% de scripts ejecutados). |
| **4. Enfoque / Estrategia**| 4.1 Tipos de prueba a realizar | Pruebas de caja negra, funcionales, rendimiento, UAT, etc. |
| | 4.2 Herramientas TIC | Software de automatización o gestión (Jira, Postman, Selenium). |
| **5. Recursos** | 5.1 Recursos del Sistema | Servidores, bases de datos y licencias necesarias. |
| | 5.2 Personal / Roles | Asignación de Project Manager, Lider de SQA, Testers y Desarrolladores. |
| **6. Calendario** | 6.1 Fechas Hito | Fecha estimada de inicio, fin y entregables por iteración. |
| **7. Riesgos** | 7.1 Riesgos y Mitigación | Tabla de riesgos de pruebas identificados y su plan de acción. |

---

### INSTRUMENTO 2: PLANTILLA DE CASO DE PRUEBA (CON EJEMPLO)
*Instrumento de etapa de construcción para conceptualizar escenarios de pruebas.*

| Campo | Especificación del Campo | Ejemplo Práctico Aplicado |
| :--- | :--- | :--- |
| **ID del Caso de Prueba** | Código Alfanumérico Único | `CP-ACC-001` |
| **Nombre del Caso** | Nombre descriptivo y conciso | Validación de inicio de sesión con credenciales correctas |
| **Descripción / Objetivo** | Qué se desea verificar en el sistema | Comprobar que un usuario previamente registrado puede acceder al panel principal del sistema ingresando su correo y contraseña válidos. |
| **Requerimiento Asociado** | Trazabilidad con la especificación | `REQ-FUN-005` (Módulo de Autenticación de Usuarios) |
| **Número de Orden** | Secuencia de ejecución | 1 |
| **Precondición** | Estado inicial requerido del sistema | El usuario debe estar registrado previamente en la base de datos y estar en la pantalla de Login del sistema. |
| **Postcondición** | Estado final tras una ejecución exitosa | El sistema redirige al usuario a la vista del Dashboard principal y se crea una sesión activa. |
| **Resultado Esperado** | Comportamiento deseado al finalizar | Acceso exitoso, visualización del Dashboard con el nombre del usuario logueado en la esquina superior derecha y código HTTP 200. |

---

### INSTRUMENTO 3: PLANTILLA DE SCRIPT DE PRUEBA (CON EJEMPLO)
*Guía paso a paso para la ejecución de pruebas manuales o automatizadas.*

**ID de Script Asociado:** `SCR-ACC-001`  
**Caso de Prueba Relacionado:** `CP-ACC-001`  
**Tester Responsable:** Rubiel Andrés Díaz Jiménez  

| Paso | Acción / Entrada | Resultado Esperado | Punto de Verificación | ¿Aprobado? (S/N) |
| :---: | :--- | :--- | :--- | :---: |
| **1** | Ingresar la URL del sistema en el navegador web. | Se despliega la pantalla de inicio de sesión con los campos: "Correo" y "Contraseña". | El elemento con ID `#login-form` debe ser visible en el DOM. | S |
| **2** | Escribir en el campo correo: `admin@sena.edu.co`. | El campo de entrada muestra el texto digitado correctamente. | El valor de `#email-input` es igual a `admin@sena.edu.co`. | S |
| **3** | Escribir en el campo contraseña: `Sena2026*`. | El campo contraseña debe enmascarar los caracteres digitados. | El atributo `type` de `#password-input` es igual a `password`. | S |
| **4** | Hacer clic en el botón "Iniciar Sesión". | El sistema procesa la solicitud, muestra una pantalla de carga breve y redirige. | La URL cambia a `/dashboard` y la cookie `session_id` es creada. | S |
| **5** | Visualizar el panel principal. | Se muestra la vista del panel principal y el mensaje "Bienvenido, Administrador". | El elemento con clase `.welcome-message` contiene el texto exacto. | S |

---

### INSTRUMENTO 4: PLANTILLA DE REPORTE DE INCIDENCIAS (BUG REPORT)
*Instrumento para registrar fallos durante la fase de ejecución de pruebas.*

| Campo de Registro | Tipo / Selección | Ejemplo Práctico de Incidencia Registrada |
| :--- | :--- | :--- |
| **ID del Bug** | Alfanumérico Único | `BUG-ACC-012` |
| **Título del Fallo** | Conciso e ilustrativo | Error de desbordamiento de texto en el menú lateral en resolución móvil |
| **Descripción del Fallo**| Detalle del comportamiento | Al cargar la infografía interactiva en pantallas de resolución menor a 768px, el texto del botón "Madurez de Procesos" en la barra lateral se desborda y se traslapa con el icono de la sección. |
| **Pasos de Reproducción**| Lista numerada de pasos | 1. Abrir la página principal de la infografía.<br>2. Redimensionar el navegador a 360x740px (Emulación móvil).<br>3. Desplegar el menú lateral.<br>4. Observar la sección "Madurez de Procesos (CMMI, PSP, TSP)". |
| **Severidad** | Crítica / Alta / Media / Baja | Media (No impide el funcionamiento básico pero afecta gravemente la interfaz). |
| **Prioridad** | Inmediata / Alta / Media / Baja | Media (Debe solucionarse antes del release final de UI). |
| **Ambiente de Pruebas** | Entorno / Navegador / OS | Chrome v114.0, OS Android 13 (Viewport 360x740). |
| **Caso de Prueba** | Trazabilidad con CP | `CP-UI-004` (Validación de diseño responsivo de la interfaz). |
| **Estado Actual** | Ciclo de vida del Bug | **Nuevo** / Asignado / Corregido / En Re-Test / Cerrado |

---

### INSTRUMENTO 5: FORMATO DE REGISTRO DE TIEMPO Y DEFECTOS (PSP)
*Instrumento de calidad a nivel personal para desarrolladores (PSP0/PSP1).*

#### A. Registro del Tiempo de Tarea (PSP Time Log)
*Permite medir la productividad real, tiempos muertos y estimaciones de fase.*

| Fecha (DD/MM) | Hora Inicio | Hora Fin | Tiempo de Interrupción (min) | Tiempo Neto (min) | Fase del Proceso (PSP) | Comentarios / Causa de Interrupción |
| :---: | :---: | :---: | :---: | :---: | :--- | :--- |
| 25/07 | 08:00 AM | 09:30 AM | 10 | 80 | Requerimientos / Análisis | Análisis de los 3 links provistos de Zajuna. Interrupción por problemas de red. |
| 25/07 | 09:40 AM | 11:15 AM | 0 | 95 | Diseño / Estructura | Diseño conceptual de las tablas y los 5 instrumentos. |
| 25/07 | 11:30 AM | 01:20 PM | 15 | 95 | Implementación (Código) | Redacción del documento en formato Markdown. Interrupción por almuerzo. |
| 25/07 | 02:00 PM | 02:30 PM | 0 | 30 | Pruebas / Postmortem | Revisión ortográfica y validación del renderizado de tablas en el visor. |

#### B. Registro de Defectos Personales (PSP Defect Log)
*Permite medir la densidad de defectos, el tipo de error más común y su costo de corrección.*

| ID Defecto | Fecha | Fase Introducido | Fase Detectado | Tiempo de Corrección (min) | Tipo de Defecto (PSP Class) | Descripción del Defecto y Causa Raíz |
| :---: | :---: | :---: | :---: | :---: | :--- | :--- |
| **D-01** | 25/07 | Implementación | Pruebas | 5 | *10 - Sintaxis* | Enlace de hipervínculo roto en el índice Markdown debido a una barra diagonal extra en la URI. |
| **D-02** | 25/07 | Diseño | Pruebas | 12 | *20 - Funcional/Lógico* | Omisión en el diseño del Instrumento 4 de la columna "Caso de Prueba Relacionado", esencial para garantizar la trazabilidad de los bugs. |

---

## 4. CONCLUSIONES

La realización de esta investigación y el consecuente diseño de instrumentos de calidad de software permiten extraer las siguientes conclusiones fundamentales para la práctica profesional:

1. **La Calidad no es Reactiva, es Proactiva:** Como fundamenta la disciplina del SQA, el aseguramiento de la calidad consiste en definir procesos estructurados antes de escribir código. Tratar de corregir errores en producción es exponencialmente más costoso que detectarlos y resolverlos en fases de requisitos o diseño.
2. **Los Instrumentos son Herramientas TIC Indispensables:** Las plantillas de planes de pruebas, casos, scripts de pruebas y reportes de incidencias actúan como la infraestructura lógica de calidad. Proporcionan un lenguaje unificado para el equipo, aseguran la trazabilidad bidireccional entre requerimientos y código ejecutado, y eliminan las conjeturas del proceso de certificación de software.
3. **El Impacto Metodológico Combinado (PSP / TSP / Scrum):** Mientras Scrum optimiza la gestión del proyecto en tiempos e incrementos ágiles, la disciplina individual PSP capacita al desarrollador para generar menos código defectuoso y estimar mejor sus plazos. La integración de ambos mundos constituye la clave para que los equipos entreguen productos estables en cada Sprint.
4. **Normalización por Estándares (ISO 25000 y 29119):** Contar con marcos y clasificaciones internacionales permite a las empresas evaluar cuantitativamente la madurez de su software, eliminando los criterios de aprobación subjetivos y facilitando la exportación e interoperabilidad en mercados internacionales globales.
