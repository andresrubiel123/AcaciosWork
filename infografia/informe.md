# GA11-220501098-AA2-EV01 creación de informe con los resultados del comportamiento del software

Aprendiz Sena:
Rubiel Andrés Díaz Jiménez.

Tecnólogo en Análisis y Desarrollo de Software, Centro de Gestión y Desarrollo Sostenible Surcolombiano, Servicio Nacional de Aprendizaje.

Ficha: 3118313

Instructor: Herley Antonio Puentes Peñaloza
13 de agosto de 2026

## INTRODUCCIÓN

El presente informe tiene como propósito documentar los resultados obtenidos durante la evaluación del comportamiento del software del proyecto AcaciosWork. Esta evaluación es parte fundamental del ciclo de vida del desarrollo de software, ya que permite identificar posibles fallos, asegurar la calidad del producto y garantizar que cumple con los requerimientos funcionales y no funcionales establecidos. A través de este documento, se expondrá el proceso metodológico llevado a cabo, las herramientas utilizadas, las métricas evaluadas y los resultados obtenidos tras la ejecución de las pruebas.

## BITÁCORA CON LOS PROCESOS DOCUMENTALES

A continuación, se detalla la bitácora con los procesos documentales seguidos durante la evaluación:

1. **Planificación de las Pruebas:** Se definieron los objetivos de la evaluación, los recursos necesarios y el cronograma de ejecución.
2. **Diseño de Casos de Prueba:** Se elaboraron los casos de prueba basados en los requerimientos funcionales y no funcionales del sistema, asegurando la cobertura de los módulos clave (Inventario, Ventas, Reportes, etc.).
3. **Configuración del Entorno:** Se preparó el entorno de pruebas, configurando la base de datos, los servicios y la infraestructura necesaria para replicar el entorno de producción.
4. **Ejecución de Pruebas:** Se llevaron a cabo las pruebas planificadas, registrando los resultados y el comportamiento del sistema ante diferentes escenarios (casos de éxito, borde y fallo).
5. **Registro de Defectos:** Se documentaron las incidencias y errores encontrados, detallando los pasos para reproducirlos y su nivel de severidad.
6. **Análisis de Resultados y Elaboración del Informe:** Se analizaron los datos recopados durante la ejecución de las pruebas y se estructuró el presente informe con los hallazgos y conclusiones.

## BUENAS PRÁCTICAS DE CALIDAD (MARCOS DE TRABAJO)

Para asegurar la calidad del software AcaciosWork, se seleccionaron y aplicaron buenas prácticas basadas en marcos de trabajo reconocidos en la industria, específicamente tomando como referencia ISO/IEC 25000 (SQuaRE) e integrando prácticas ágiles:

* **Adecuación Funcional:** Verificación de que el software provee funciones que satisfacen las necesidades explícitas e implícitas bajo condiciones específicas (precisión y pertinencia en los cálculos de inventario y ventas).
* **Eficiencia de Desempeño:** Evaluación del comportamiento temporal y la utilización de recursos, asegurando tiempos de respuesta óptimos en las consultas a la base de datos y la generación de reportes.
* **Usabilidad:** Comprobación de que la interfaz de usuario (UI) sea intuitiva, accesible y de fácil aprendizaje para los usuarios finales, manteniendo la paridad entre las plataformas (Web, Desktop y Android).
* **Fiabilidad:** Pruebas de tolerancia a fallos y capacidad de recuperación ante errores inesperados, como interrupciones de red o entradas de datos no válidas.
* **Mantenibilidad e Integridad:** Asegurar que el código base cumple con el estándar definido (ej. límite de 300 líneas por archivo) facilitando futuras modificaciones y escalabilidad.

## RESUMEN DE RECURSOS UTILIZADOS PARA LA EVALUACIÓN

A continuación se resumen los recursos y parámetros utilizados para llevar a cabo la evaluación del software:

* **Equipo evaluador:** El equipo responsable de la planificación, ejecución y análisis de las pruebas estuvo conformado por el desarrollador líder (Rubiel Andrés Díaz Jiménez) asumiendo los roles de Analista de QA, Tester Manual y Automatizador.
* **Métricas utilizadas:**
  * Densidad de defectos (número de defectos por módulo).
  * Porcentaje de casos de prueba ejecutados frente a los planificados.
  * Tasa de éxito de los casos de prueba (Pass/Fail rate).
  * Tiempo medio de respuesta (para pruebas de rendimiento).
* **Ponderación:** Se asignó mayor peso a los módulos críticos (Ventas e Inventario - 40%), seguido de Reportes y Gráficos (30%), Gestión de Usuarios (15%) y Configuración (15%).
* **Fidelidades de medición:** Alta fidelidad. El entorno de pruebas fue configurado para ser una réplica exacta del entorno de producción, utilizando los mismos conjuntos de datos de prueba y configuraciones de hardware/software.
* **Criterios de aprobación:**
  * 100% de los casos de prueba de severidad "Alta" y "Crítica" deben ser aprobados.
  * No deben existir errores bloqueantes que impidan el flujo principal (ej. registrar una venta).
  * El tiempo de respuesta de los endpoints clave debe ser menor a 2 segundos en el 95% de las peticiones.
* **Recursos de infraestructura:**
  * Servidor de Base de Datos relacional (entorno de desarrollo/pruebas).
  * Dispositivos de prueba Android (físicos y emuladores para validar la App móvil).
  * Entorno de escritorio (Windows) para la versión Desktop (Java Swing).
  * Navegadores web actualizados (Chrome, Firefox, Edge) para las pruebas de frontend.
* **Tipos de pruebas y pruebas realizadas:**
  * **Pruebas Funcionales:** Verificación de operaciones CRUD en inventario, ventas y gestión de usuarios.
  * **Pruebas de Interfaz de Usuario (UI):** Comprobación de la paridad visual de las 12 secciones entre Web, Desktop y Android.
  * **Pruebas de Integración:** Validación de la correcta comunicación entre el frontend, el motor analítico (`IntelligenceEngine`) y la base de datos.
  * **Pruebas de Regresión:** Ejecución de pruebas tras refactorizaciones masivas (ej. reorganización del módulo de reportes y límite de líneas) para asegurar que no se rompieran funcionalidades existentes.
  * **Pruebas de Usabilidad:** Navegación por las pantallas reconstruidas en Android para verificar que sean accesibles y paritarias a la web.

## CONCLUSIONES

A partir de los resultados obtenidos en la evaluación, se concluye lo siguiente:

1. El software AcaciosWork demuestra un comportamiento estable y coherente en todas sus plataformas (Web, Desktop y Android), logrando el objetivo de paridad de secciones.
2. La refactorización modular estricta ha impactado positivamente en la mantenibilidad del sistema, reduciendo la complejidad ciclomática de los componentes individuales.
3. Los módulos críticos como Inventario y Ventas cumplen con los criterios de aprobación establecidos, garantizando la fiabilidad de las operaciones principales del sistema ERP/POS.
4. Las pruebas confirmaron la robustez del nuevo Hub de Reportes y la correcta exportación en análisis gráficos.
5. Como recomendación futura, se sugiere automatizar un mayor porcentaje de las pruebas de regresión e incluir pruebas de carga para anticipar el comportamiento del sistema ante un alto volumen de transacciones concurrentes.
