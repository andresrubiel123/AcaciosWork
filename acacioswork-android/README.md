# AcaciosWork - Android App (La Movilidad)

## Descripción
Este es el cliente móvil nativo del ecosistema **AcaciosWork**, desarrollado en **Kotlin**. Su propósito es brindar portabilidad total a los operarios y dueños de tienda, permitiendo el control de inventario y la consulta de ventas directamente desde un smartphone o tablet.

La aplicación está diseñada bajo el patrón de arquitectura **MVVM (Model-View-ViewModel)** para garantizar un código limpio, testeable y mantenible.

### Funcionalidades Planificadas:
- **Consulta de Stock**: Verificación rápida de existencias mediante escaneo de códigos.
- **Alertas en Tiempo Real**: Notificaciones push para productos con stock bajo o vencimientos.
- **Gestión de Ventas Móvil**: Registro de transacciones básicas desde cualquier lugar de la tienda.
- **Sincronización Inteligente**: Funcionamiento offline con sincronización automática al recuperar conexión.

## Tecnologías
- **Kotlin 2.x**: Lenguaje moderno y seguro para el desarrollo Android.
- **Android SDK (Min SDK 26)**: Para garantizar compatibilidad con dispositivos modernos.
- **Retrofit 2**: Cliente HTTP para el consumo eficiente de la API REST.
- **Jetpack Compose / XML**: Interfaces modernas y responsivas.
- **Coroutines & Flow**: Manejo de tareas asíncronas de manera estructurada.

## Estructura de Carpetas
- `ui/`: Pantallas (Activities/Fragments) y componentes visuales.
- `network/`: Configuración de Retrofit y definiciones de la API.
- `viewmodel/`: Lógica de estado y puente entre datos y UI.
- `model/`: Clases de datos (POJOs) sincronizadas con el Backend.
- `repository/`: Capa de abstracción para el manejo de fuentes de datos (API/Local).

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Estructura Base**: Configuración inicial del proyecto con soporte para Gradle y Kotlin.
- **Integración de Modelos**: Mapeo inicial de entidades `Producto` y `Usuario`.
- **Capa de Red**: Configuración base de Retrofit para conectar con el servidor.

### 🔄 En Desarrollo / Estabilización
- **Módulo de Login**: Implementación de la pantalla de acceso y persistencia de token.
- **Diseño UI**: Creación de los primeros prototipos de la interfaz de dashboard móvil.

---

# Sugerencias y Próximos Pasos (Lo que falta)

1.  **Escaneo de Códigos**: Integrar la librería **ZXing** o **ML Kit** para el uso de la cámara como escáner de productos.
2.  **Persistencia Local**: Implementar **Room Database** para permitir que la aplicación funcione sin internet de manera temporal.
3.  **Sistema de Notificaciones**: Configurar Firebase Cloud Messaging (FCM) para recibir alertas del sistema.
4.  **Seguridad Biométrica**: Añadir soporte para desbloqueo por huella o rostro para acceso rápido al dashboard.
5.  **Temas Dinámicos**: Implementar soporte para modo oscuro basado en la configuración del sistema Android.

---

# Guía de Ejecución
1. Clona el repositorio y ábrelo en la última versión de **Android Studio**.
2. Configura la `BASE_URL` en el archivo de constantes de red para que apunte a la IP de tu servidor backend.
3. Compila y ejecuta en un emulador o dispositivo físico conectado.

---

# Estándares de Código
- Uso estricto de **Kdoc** para documentar funciones: `/** Descripción. @author RADJ */`.
- Seguir las guías de estilo de Kotlin (Google Android Style Guide).
- Toda petición de red debe manejarse dentro de un bloque `try-catch` en el ViewModel.
