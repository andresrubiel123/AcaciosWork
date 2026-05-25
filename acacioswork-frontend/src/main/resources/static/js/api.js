/** Cliente API robusto para manejar peticiones fetch, ApiResponse y JWT. @author RADJ */
const API_URL = "http://localhost:8081/api";

/** Petición fetch HTTP al backend centralizado con cabeceras de autorización JWT. @author RADJ */
async function apiRequest(endpoint, method = "GET", data = null) {
    /** Cabeceras HTTP estándar para intercambio JSON. @author RADJ */
    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    };

    /** Agregar token JWT si existe sesión activa en almacenamiento local. @author RADJ */
    const token = localStorage.getItem("jwt_token");
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    /** Configuración de la petición fetch. @author RADJ */
    const config = {
        method,
        headers
    };

    /** Serializar datos en formato JSON para el cuerpo del mensaje. @author RADJ */
    if (data) {
        config.body = JSON.stringify(data);
    }

    /** Realizar petición remota y validar respuesta del servidor. @author RADJ */
    try {
        const response = await fetch(`${API_URL}${endpoint}`, config);
        
        /** Manejar expiración de sesión y redirección a login. @author RADJ */
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("jwt_token");
            localStorage.removeItem("usuario");
            if (!window.location.pathname.endsWith("login.html") && !window.location.pathname.endsWith("login")) {
                window.location.href = "login";
            }
            throw new Error("Sesión expirada");
        }

        /** Manejar respuestas vacías con código 204. @author RADJ */
        if (response.status === 204) return true;

        /** Convertir respuesta a JSON. @author RADJ */
        const result = await response.json();

        /** Validar estado de la respuesta y lanzar excepción si hay error. @author RADJ */
        if (!response.ok || (result.success === false)) {
            throw new Error(result.message || `Error ${response.status}`);
        }

        /** Retornar solo la data para mantener compatibilidad con el resto del frontend. @author RADJ */
        return result.data;
    } catch (error) {
        /** Capturar y registrar errores de red o de la petición. @author RADJ */
        console.error("API Error:", error);
        throw error;
    }
}

/** Exportar cliente API request para uso global. @author RADJ */
window.apiRequest = apiRequest;
