/** Cliente API robusto para manejar peticiones fetch, ApiResponse y JWT. @author RADJ */
const API_URL = "http://localhost:8081/api";

/** 
 * Realiza una petición fetch al backend con el token de autorización si existe. 
 * @param {string} endpoint - El endpoint de la API (ej: /productos)
 * @param {string} method - El método HTTP (GET, POST, PUT, DELETE)
 * @param {object} data - Los datos a enviar en el cuerpo de la petición
 * @author RADJ 
 */
async function apiRequest(endpoint, method = "GET", data = null) {
    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    };

    const token = localStorage.getItem("jwt_token");
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const config = {
        method,
        headers
    };

    if (data) {
        config.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_URL}${endpoint}`, config);
        
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("jwt_token");
            localStorage.removeItem("usuario");
            if (!window.location.pathname.endsWith("login.html") && !window.location.pathname.endsWith("login")) {
                window.location.href = "login";
            }
            throw new Error("Sesión expirada");
        }

        if (response.status === 204) return true;

        const result = await response.json();

        if (!response.ok || (result.success === false)) {
            throw new Error(result.message || `Error ${response.status}`);
        }

        // Retornar solo la data para mantener compatibilidad con el resto del frontend
        return result.data;
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
}

// Exportar para uso en otros scripts
window.apiRequest = apiRequest;
