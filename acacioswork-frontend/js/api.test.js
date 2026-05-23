/** Pruebas unitarias para apiRequest. @author RADJ */

// Mock de window.location antes de requerir la API para evitar errores de navegación de JSDOM
const originalLocation = window.location;
delete window.location;
window.location = {
    pathname: "/dashboard.html",
    href: "http://localhost/dashboard.html",
    endsWith: String.prototype.endsWith
};

// Mock de fetch global
global.fetch = jest.fn();

// Carga del script api.js (Vanilla JS que expone a window.apiRequest)
require('./api.js');
const apiRequest = window.apiRequest;

describe('Pruebas de apiRequest', () => {

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        window.location.pathname = "/dashboard.html";
        window.location.href = "http://localhost/dashboard.html";
    });

    afterAll(() => {
        window.location = originalLocation;
    });

    test('debe realizar una petición GET exitosa y retornar datos', async () => {
        const mockData = { id: 1, nombre: "Producto A" };
        const mockResponse = {
            status: 200,
            ok: true,
            json: jest.fn().mockResolvedValue({ success: true, data: mockData })
        };
        global.fetch.mockResolvedValue(mockResponse);

        const result = await apiRequest("/productos");

        expect(global.fetch).toHaveBeenCalledWith(
            "http://localhost:8081/api/productos",
            expect.objectContaining({
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            })
        );
        expect(result).toEqual(mockData);
    });

    test('debe incluir el token JWT en las cabeceras si existe en localStorage', async () => {
        localStorage.setItem("jwt_token", "mi_token_jwt");
        const mockResponse = {
            status: 200,
            ok: true,
            json: jest.fn().mockResolvedValue({ success: true, data: [] })
        };
        global.fetch.mockResolvedValue(mockResponse);

        await apiRequest("/usuarios");

        expect(global.fetch).toHaveBeenCalledWith(
            expect.any(String),
            expect.objectContaining({
                headers: expect.objectContaining({
                    "Authorization": "Bearer mi_token_jwt"
                })
            })
        );
    });

    test('debe enviar cuerpo JSON en peticiones POST', async () => {
        const bodyData = { nombre: "Nuevo" };
        const mockResponse = {
            status: 200,
            ok: true,
            json: jest.fn().mockResolvedValue({ success: true, data: bodyData })
        };
        global.fetch.mockResolvedValue(mockResponse);

        await apiRequest("/productos", "POST", bodyData);

        expect(global.fetch).toHaveBeenCalledWith(
            expect.any(String),
            expect.objectContaining({
                method: "POST",
                body: JSON.stringify(bodyData)
            })
        );
    });

    test('debe retornar true si el backend responde con 204 No Content', async () => {
        const mockResponse = {
            status: 204,
            ok: true
        };
        global.fetch.mockResolvedValue(mockResponse);

        const result = await apiRequest("/productos/1", "DELETE");

        expect(result).toBe(true);
        expect(global.fetch).toHaveBeenCalled();
    });

    test('debe limpiar el token, localStorage y redirigir a login en respuesta 401/403', async () => {
        localStorage.setItem("jwt_token", "expirado");
        localStorage.setItem("usuario", "admin");

        const mockResponse = {
            status: 401,
            ok: false
        };
        global.fetch.mockResolvedValue(mockResponse);

        await expect(apiRequest("/dashboard")).rejects.toThrow("Sesión expirada");

        expect(localStorage.getItem("jwt_token")).toBeNull();
        expect(localStorage.getItem("usuario")).toBeNull();
        expect(window.location.href).toBe("login.html");
    });

    test('debe lanzar error si success es falso en el JSON de respuesta', async () => {
        const mockResponse = {
            status: 400,
            ok: false,
            json: jest.fn().mockResolvedValue({ success: false, message: "Error personalizado del backend" })
        };
        global.fetch.mockResolvedValue(mockResponse);

        await expect(apiRequest("/productos")).rejects.toThrow("Error personalizado del backend");
    });
});
