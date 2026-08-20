# GA11-220501098-AA2-EV02 Informe de Experiencias Aprendidas en el Proceso de Verificación del Software

Aprendiz Sena:
Rubiel Andrés Díaz Jiménez.

Tecnólogo en Análisis y Desarrollo de Software, Centro de Gestión y Desarrollo Sostenible Surcolombiano, Servicio Nacional de Aprendizaje.

Ficha: 3118313

Instructor: Herley Antonio Puentes Peñaloza
14 de agosto de 2026

## INTRODUCCIÓN

El presente informe recoge y sistematiza las experiencias y lecciones aprendidas durante el proceso de verificación y validación del software **AcaciosWork**, un ecosistema ERP/POS multiplataforma desarrollado en el marco del programa Tecnólogo en Análisis y Desarrollo de Software (ADSO) del SENA, durante el período comprendido entre febrero de 2025 y agosto de 2026.

El componente formativo "Aplicación de pruebas de software" propone que la verificación no es un evento puntual al final del ciclo de desarrollo, sino un proceso continuo que atraviesa todas las fases del proyecto: desde la elicitación de requisitos hasta el despliegue en producción. Esta visión integral fue adoptada plenamente en AcaciosWork, convirtiendo cada iteración de desarrollo en una oportunidad de aprendizaje tanto técnico como profesional.

El documento se estructura en torno a las habilidades técnicas adquiridas, las competencias blandas desarrolladas, las lecciones aprendidas organizadas por dominio, y las recomendaciones para futuros procesos de verificación. Se toma como referencia directa la bitácora de procesos documentada en la evidencia GA11-220501098-AA1-EV04 y los estándares de calidad ISO/IEC 25000, ISO/IEC/IEEE 29119 y CMMI Nivel 2.

## BUENAS PRÁCTICAS DE CALIDAD APLICADAS (MARCOS DE TRABAJO)

La garantía de calidad en AcaciosWork no se limitó al funcionamiento correcto del código, sino que se formalizó mediante marcos de trabajo reconocidos internacionalmente. Las prácticas seleccionadas y aplicadas fueron:

* **ISO/IEC 25000 (SQuaRE):** Sirvió como columna vertebral para definir los atributos de calidad del producto (adecuación funcional, fiabilidad, eficiencia de desempeño, usabilidad, mantenibilidad y seguridad). Permitió establecer métricas concretas: tiempo de respuesta promedio menor a 800 ms, tasa de defectos críticos igual a cero y cumplimiento del límite de 300 líneas por archivo de código fuente.
* **ISO/IEC 9126:** Aplicado en la validación de la portabilidad e interoperabilidad de las interfaces entre las plataformas Web (HTML/JS/Thymeleaf), Desktop (Java Swing/FlatLaf) y Android (Kotlin/Jetpack Compose). Garantizó que la experiencia de usuario fuera consistente sin importar el dispositivo de acceso.
* **ISO/IEC/IEEE 29119:** Orientó el diseño y la ejecución de los planes de prueba, asegurando una cobertura estructurada de los tipos de prueba: unitarias, de integración, de sistema, de regresión y de aceptación de usuario.
* **CMMI Nivel 2 (Gestión de Requisitos y Configuración):** Implementado mediante el control de cambios documentado en la bitácora (GA11-220501098-AA1-EV04), el uso de Git/GitHub para versionar el código y la política de que toda modificación significativa quedara registrada con identificador de tarea (ej: `TSK-QA-001`).
* **PSP (Personal Software Process):** Aplicado como disciplina individual: registro de tiempos de desarrollo, conteo sistemático de líneas de código y análisis de defectos antes de compilar. Esta práctica fue fundamental para mantener el límite de 300 líneas por clase y anticipar errores antes de que llegaran a las pruebas formales.

## HABILIDADES TÉCNICAS ADQUIRIDAS EN LA VERIFICACIÓN

### Diseño y Ejecución de Pruebas de API REST

Uno de los primeros y más impactantes aprendizajes fue el dominio de **Postman** como herramienta de verificación de endpoints. Al inicio del proyecto, el backend de AcaciosWork era una caja negra: se escribía código en Spring Boot pero no existía una forma sistemática de comprobar su comportamiento. La implementación de colecciones de pruebas en Postman transformó esta situación radicalmente.

Se aprendió a:

* Construir colecciones organizadas por módulo funcional (Autenticación, Inventario, Ventas, Reportes).
* Usar variables de entorno para manejar el token JWT de forma dinámica entre peticiones.
* Escribir scripts de aserción en JavaScript dentro de Postman para verificar automáticamente códigos HTTP, estructura JSON y valores de negocio.
* Diseñar escenarios de prueba para casos de éxito, casos de error controlado (HTTP 400, 404, 409) y casos de borde (stock cero, campos nulos, ventas de montos extremos).

El aprendizaje más profundo surgió al detectar una inconsistencia crítica: las pruebas de Postman revelaron que al registrar ventas concurrentes, se producían errores HTTP 409 que evidenciaban condiciones de carrera en la actualización del stock. Este hallazgo llevó a la implementación de la anotación `@Transactional` en Spring Boot, garantizando la atomicidad de las operaciones de facturación (Registro 14 de la bitácora, TSK-BE-012). Sin las pruebas de API formalizadas, este bug habría llegado a producción.

### Verificación de Integridad Transaccional

La experiencia con AcaciosWork enseñó que un sistema ERP/POS tiene un requisito crítico no siempre evidente al principio: **todas las operaciones que modifican el estado del negocio deben ser atómicas**. Si una venta registra tres productos y el segundo falla por stock insuficiente, ninguna línea de la venta debe persistir en la base de datos.

Se aprendió a verificar este comportamiento mediante:

* Pruebas de rollback: insertar deliberadamente un producto con stock = 0 en el medio de una venta con múltiples ítems y comprobar que la base de datos no registra ningún `DETALLE_VENTA` parcial.
* Consultas directas a MySQL Workbench inmediatamente después de la prueba para confirmar la consistencia de los datos.
* Interpretación de los logs de Spring Boot para rastrear la transacción y confirmar el rollback.

### Pruebas de Regresión tras Refactorizaciones Masivas

El proyecto AcaciosWork vivió múltiples refactorizaciones de gran envergadura: la migración del monolito `Administrador.java` (más de 2000 líneas) a 12 pestañas autónomas de menos de 300 líneas, la modularización del `dashboard.js` del frontend web, y la reescritura completa de las 10 pantallas de la app Android en Jetpack Compose.

Cada una de estas refactorizaciones representó un riesgo real de regresión. Se aprendió que:

* Una refactorización sin pruebas de regresión posteriores no es una refactorización completa, es una apuesta.
* Las pruebas de regresión deben ejecutarse sobre los flujos críticos del negocio (login, registro de venta, actualización de stock) antes de declarar que el refactor fue exitoso.
* La verificación visual multiplataforma (comprobar que la pantalla de Inventario se ve y funciona igual en Web, Desktop y Android después del cambio) es una forma válida y necesaria de prueba de regresión cuando no se dispone de pruebas automatizadas de UI.

### Interpretación de Métricas de Calidad

Al aplicar ISO/IEC 25000, se adquirió la habilidad de traducir atributos de calidad abstractos en métricas concretas y verificables:

| Atributo ISO/IEC 25000 | Métrica Aplicada en AcaciosWork | Umbral de Aprobación |
| :--- | :--- | :--- |
| **Eficiencia de Desempeño** | Tiempo de respuesta promedio de la API | < 800 ms |
| **Mantenibilidad** | Líneas de código por archivo Java/Kotlin | ≤ 300 líneas |
| **Fiabilidad** | Tasa de defectos críticos en producción | 0 defectos |
| **Adecuación Funcional** | Casos de prueba pasados sobre planificados | ≥ 95% |
| **Seguridad** | Endpoints protegidos sin JWT que responden datos | 0 endpoints |

## EXPERIENCIAS VIVIDAS EN LA VERIFICACIÓN (CASOS REALES)

### Experiencia 1: El Defecto Silencioso en el Cálculo de Ganancias

Durante las pruebas funcionales del módulo de Reportes, se detectó que el informe de ganancias mostraba valores incorrectamente altos. El método `reporteGanancias()` calculaba el ingreso bruto (precio de venta × cantidad) sin restar el costo de adquisición de los productos. En términos de negocio, esto significaba que el comerciante habría tomado decisiones estratégicas basadas en datos financieros erróneos.

**Lección aprendida:** Las pruebas funcionales deben incluir verificación de lógica de negocio con datos reales y conocidos, no solo verificar que el sistema "responde algo". Se aprendió a preparar conjuntos de datos de prueba con valores precalculados manualmente para poder comparar el resultado del sistema contra el resultado esperado.

### Experiencia 2: La Inconsistencia de Nombres entre Plataformas

En el Registro 16 de la bitácora (TSK-MOD-005, mayo de 2026), se detectó un problema grave: el campo de stock del producto tenía nombres diferentes en cada capa del sistema (`cantidad` en la base de datos, `stock` en el frontend JavaScript y `stockActual` en el backend Java). Esto producía errores silenciosos: los datos llegaban del backend al frontend pero no se renderizaban en pantalla porque el nombre del atributo no coincidía.

**Lección aprendida:** La verificación de contratos de API debe incluir la validación del nombre exacto de cada atributo del JSON, no solo su tipo o valor. Se adoptó el convenio de usar exactamente el mismo nombre (`stockActual`, `stockMinimo`, `stockOptimo`) en la base de datos (snake_case), el backend Java (camelCase) y todos los clientes. La verificación cruzada con Postman y los DevTools del navegador fue clave para detectar y corregir esta inconsistencia.

### Experiencia 3: La Migración al Frontend Seguro con Thymeleaf

En el Registro 17 de la bitácora (TSK-FE-008, mayo de 2026), la verificación de seguridad del frontend reveló que los archivos HTML estáticos eran accesibles sin autenticación. Cualquier persona con la URL podía ver el dashboard sin haber iniciado sesión. Esta vulnerabilidad fue detectada a través de pruebas de seguridad manuales: abrir el navegador en modo incógnito e intentar acceder directamente a `/dashboard.html` sin token JWT.

**Lección aprendida:** Las pruebas de seguridad no requieren herramientas sofisticadas para comenzar. Simulaciones manuales de ataques básicos (acceso sin autenticación, manipulación de URLs, envío de tokens expirados o inválidos) revelan vulnerabilidades críticas con cero costo de herramientas.

### Experiencia 4: La Cobertura de Pruebas como Deuda Técnica

La evaluación del estado del proyecto (julio de 2026) asignó una calificación de **4/10** al módulo de pruebas automatizadas. Si bien existía la carpeta `__tests__`, la cobertura real era mínima. Esta deficiencia se convirtió en un riesgo real durante las refactorizaciones: cada vez que se modificaba un módulo, existía incertidumbre sobre si algo había dejado de funcionar en otro lugar del sistema.

**Lección aprendida:** Las pruebas automatizadas no son un lujo académico. Son una inversión que se amortiza desde la primera refactorización. La ausencia de pruebas unitarias en los servicios de negocio (`VentaService`, `ProductoService`, `IntelligenceEngine`) fue el principal generador de estrés técnico durante las fases de estabilización. En el próximo ciclo, se debe establecer una cobertura mínima del 80% en los servicios de negocio antes de avanzar a la fase de integración.

## LECCIONES APRENDIDAS (SOCIALIZACIÓN)

A continuación se sistematizan las lecciones aprendidas más significativas, organizadas para ser transferibles a futuros proyectos:

### Lección 1: La Modularidad es la Primera Línea de Defensa de la Calidad

El mayor aprendizaje estructural del proyecto fue que **un archivo de más de 300 líneas es un archivo que ya tiene más de un problema**. La clase `Administrador.java` con más de 2000 líneas tardaba minutos en compilarse, era imposible de depurar en aislamiento y cualquier cambio en ella podía romper funcionalidades aparentemente no relacionadas.

La refactorización hacia componentes de menos de 300 líneas (`WelcomeTab.java`, `InventarioTab.java`, `GraficosTab.java`, etc.) demostró que los defectos son más fáciles de encontrar, de corregir y de verificar cuando el scope del código es pequeño y enfocado.

**Aplicable a:** Todo lenguaje y paradigma. En Kotlin/Compose, en JavaScript modular, en SQL y en cualquier codebase que crezca con el tiempo.

### Lección 2: El Contrato de la API es la Especificación de la Prueba

Se aprendió que antes de escribir una sola línea de código de integración entre el backend y un cliente (web, desktop o móvil), el contrato de la API (nombres de campos, tipos de datos, códigos HTTP, estructura del JSON de respuesta) debe estar definido y documentado. Este contrato es literalmente la especificación de los casos de prueba de integración.

AcaciosWork adoptó la convención de documentar los endpoints clave en el `project-context.md` (`GET /api/productos`, `POST /api/ventas`, etc.) y usarlos como referencia tanto para el desarrollo como para la verificación.

### Lección 3: Los Errores Silenciosos son los Más Peligrosos

Los defectos más costosos del proyecto no fueron los que producían excepciones visibles (NullPointerException, errores HTTP 500), sino los **errores silenciosos**: datos que se procesaban sin error pero producían resultados incorrectos. El bug de las ganancias mal calculadas y la inconsistencia de nombres de atributos son ejemplos perfectos.

La verificación efectiva debe incluir casos de prueba que validen la **corrección del resultado**, no solo la **ausencia de error**. "El sistema responde HTTP 200" no es suficiente. "El sistema responde HTTP 200 y el campo `gananciaTotal` tiene el valor correcto calculado manualmente" sí lo es.

### Lección 4: La Documentación del Proceso es tan Valiosa como el Código

La bitácora (GA11-220501098-AA1-EV04) con sus 23 registros cronológicos fue el instrumento que permitió entender, meses después, por qué se tomó cierta decisión arquitectónica o por qué se refactorizó un módulo específico. Sin ella, el contexto del proyecto habría vivido exclusivamente en la memoria del desarrollador, lo que es un riesgo enorme.

En el contexto de las pruebas, documentar los casos de prueba ejecutados, sus resultados y las acciones correctivas tomadas no es burocracia: es trazabilidad. Es la diferencia entre saber que el software "fue probado" y poder demostrar exactamente qué, cómo, cuándo y por quién fue probado.

### Lección 5: La Verificación Comienza en el Diseño, no en el Testing

El aprendizaje más maduro del proceso fue comprender que la calidad no se "añade" al final mediante pruebas. Se diseña desde el principio. La decisión de usar `@Transactional` en Spring Boot, de normalizar la base de datos hasta 3FN, de usar `BigDecimal` para los precios monetarios en lugar de `double`, de centralizar toda la lógica en el backend y de separar el motor de inteligencia (`IntelligenceEngine`) de la capa visual: todas estas son decisiones de diseño que reducen exponencialmente la cantidad de defectos que llegan a la fase de pruebas.

## PLAN DE MEJORA PARA FUTUROS PROCESOS DE VERIFICACIÓN

Tomando como base el componente formativo y las experiencias vividas, se propone el siguiente plan de mejora para los próximos ciclos de desarrollo de AcaciosWork:

| Área de Mejora | Problema Identificado | Acción de Mejora | Plazo | Impacto Esperado |
| :--- | :--- | :--- | :--- | :--- |
| **Pruebas Automatizadas** | Cobertura de tests unitarios insuficiente (4/10). | Implementar suite de pruebas JUnit 5 + Mockito para `VentaService`, `ProductoService` e `IntelligenceEngine` con cobertura mínima del 80%. | Corto (1 mes) | Mucho |
| **Análisis Estático** | Verificación manual del límite de 300 líneas. | Integrar SonarQube o Checkstyle en el pipeline de Maven para rechazar commits que violen el límite. | Corto (2 semanas) | Bastante |
| **Persistencia de Alertas** | Las alertas de stock se pierden al reiniciar el servidor (lista estática en memoria). | Migrar `InventarioManager` a persistencia en MySQL usando el modelo `AlertaStockMinimo` con su propio `Repository`. | Medio (2 meses) | Mucho |
| **Paginación de Endpoints** | `findAll()` retorna todos los registros sin límite, lo que puede degradar el rendimiento con grandes volúmenes. | Implementar `Pageable` de Spring Data JPA en los endpoints de productos, ventas e historial. | Medio (1 mes) | Bastante |
| **Pruebas de Carga** | Sin pruebas de desempeño bajo concurrencia real. | Ejecutar escenarios de carga con Apache JMeter simulando 50 usuarios concurrentes en el módulo de ventas. | Largo (3 meses) | Mucho |

## CONCLUSIONES

1. **La verificación como proceso continuo:** AcaciosWork demostró que la calidad del software no es el resultado de una fase de pruebas al final del proyecto, sino el resultado acumulado de decisiones de diseño correctas, refactorizaciones documentadas y verificaciones sistemáticas en cada iteración. El componente formativo "Aplicación de pruebas de software" es exactamente correcto en esta visión.

2. **El valor insustituible de la bitácora:** La documentación cronológica de los 23 registros de la bitácora (GA11-220501098-AA1-EV04) fue el instrumento más valioso del proceso de verificación. Permitió trazabilidad completa de cada cambio, facilitó las pruebas de regresión y sirvió como evidencia académica y profesional del proceso.

3. **Habilidades transferibles adquiridas:** El proceso de verificación de AcaciosWork generó habilidades concretas y transferibles: diseño de casos de prueba con datos reales, verificación de contratos de API con Postman, detección de errores silenciosos mediante validación de resultados esperados, pruebas de seguridad manual, y gestión del ciclo de vida de defectos.

4. **La deuda técnica en pruebas tiene un costo real:** La baja cobertura de pruebas automatizadas (calificación 4/10) fue el mayor factor de incertidumbre durante las refactorizaciones. Cada modificación mayor requirió una verificación manual exhaustiva que habría sido automática con una suite de pruebas adecuada. Esta es la lección más costosa aprendida y la que define la prioridad del siguiente hito técnico del proyecto.

5. **Preparación para la industria:** Las prácticas aplicadas en AcaciosWork (estándares ISO/IEC 25000, IEEE 29119, CMMI Nivel 2, PSP) no son exclusividad del entorno académico. Son las mismas prácticas que las organizaciones de software de nivel empresarial aplican para garantizar productos confiables y mantenibles. La experiencia vivida constituye una preparación directa y práctica para el ejercicio profesional.

## REFERENCIAS

Servicio Nacional de Aprendizaje (SENA). (2026). *Aplicación de pruebas de software* (Componente Formativo GA11-220501098-AA2). Centro de Gestión y Desarrollo Sostenible Surcolombiano.

International Organization for Standardization. (2014). *ISO/IEC 25010: Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE)*. ISO/IEC.

Institute of Electrical and Electronics Engineers. (2022). *ISO/IEC/IEEE 29119-1: Software and systems engineering — Software testing — Part 1: General concepts*. IEEE.

Software Engineering Institute. (2010). *CMMI for Development, Version 1.3*. Carnegie Mellon University.

Díaz Jiménez, R. A. (2026). *Bitácora de los procesos documentados del proyecto AcaciosWork* (GA11-220501098-AA1-EV04). SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Buenas prácticas de desarrollo de software — Ecosistema AcaciosWork*. SENA — Ficha 3118313.

Díaz Jiménez, R. A. (2026). *Estado actual del proyecto AcaciosWork: Evaluación técnica y hoja de ruta*. SENA — Ficha 3118313.
