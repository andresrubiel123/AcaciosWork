/*** cliente api robusto para manejar peticiones fetch, apiresponse y jwt. @author RADJ */
const API_URL = "http://localhost:8081/api";

/*** petición fetch http al backend centralizado con cabeceras de autorización jwt. @author RADJ */
async function apiRequest(endpoint, method = "GET", data = null) {
    /*** cabeceras http estándar para intercambio json. @author RADJ */
    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    };

    /*** agregar token jwt si existe sesión activa en almacenamiento local. @author RADJ */
    const token = localStorage.getItem("jwt_token");
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    /*** configuración de la petición fetch. @author RADJ */
    const config = {
        method,
        headers
    };

    /*** serializar datos en formato json para el cuerpo del mensaje. @author RADJ */
    if (data) {
        config.body = JSON.stringify(data);
    }

    /*** realizar petición remota y validar respuesta del servidor. @author RADJ */
    try {
        const response = await fetch(`${API_URL}${endpoint}`, config);
        
        /*** manejar expiración de sesión y redirección a login. @author RADJ */
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("jwt_token");
            localStorage.removeItem("usuario");
            if (!window.location.pathname.endsWith("login.html") && !window.location.pathname.endsWith("login")) {
                window.location.href = "login";
            }
            throw new Error("Sesión expirada");
        }

        /*** manejar respuestas vacías con código 204. @author RADJ */
        if (response.status === 204) return true;

        /*** convertir respuesta a json. @author RADJ */
        const result = await response.json();

        /*** validar estado de la respuesta y lanzar excepción si hay error. @author RADJ */
        if (!response.ok || (result.success === false)) {
            throw new Error(result.message || `Error ${response.status}`);
        }

        /*** retornar solo la data para mantener compatibilidad con el resto del frontend. @author RADJ */
        return result.data;
    } catch (error) {
        /*** capturar y registrar errores de red o de la petición. @author RADJ */
        console.error("API Error:", error);
        throw error;
    }
}

/*** exportar cliente api request para uso global. @author RADJ */
window.apiRequest = apiRequest;
