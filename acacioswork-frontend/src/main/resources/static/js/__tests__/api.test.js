/** Pruebas unitarias para el cliente apiRequest del frontend. @author RADJ */

const fs = require('fs');
const path = require('path');

// Mockear window.location
delete window.location;
window.location = { href: '', pathname: '/dashboard' };

// Mockear fetch de manera global
global.fetch = jest.fn();

/** Cargar y evaluar el archivo api.js. @author RADJ */
const apiCode = fs.readFileSync(
    path.resolve(__dirname, '../core/api.js'),
    'utf8'
);

eval(apiCode);

describe('api.js tests', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        window.location.href = '';
        window.location.pathname = '/dashboard';
        console.error = jest.fn(); // Evitar ruido en consola de errores simulados
    });

    test('apiRequest debe realizar un GET exitoso y devolver los datos', async () => {
        const mockResponse = { success: true, data: { id: 1, nombre: 'Producto A' } };
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => mockResponse
        });

        const data = await window.apiRequest('/productos');

        expect(global.fetch).toHaveBeenCalledWith(
            'http://localhost:8081/api/productos',
            expect.objectContaining({
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            })
        );
        expect(data).toEqual(mockResponse.data);
    });

    test('apiRequest debe enviar JWT en las cabeceras si existe en localStorage', async () => {
        localStorage.setItem('jwt_token', 'mi-token-secreto');
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => ({ success: true, data: [] })
        });

        await window.apiRequest('/productos');

        expect(global.fetch).toHaveBeenCalledWith(
            'http://localhost:8081/api/productos',
            expect.objectContaining({
                headers: expect.objectContaining({
                    'Authorization': 'Bearer mi-token-secreto'
                })
            })
        );
    });

    test('apiRequest debe enviar cuerpo JSON para peticiones POST', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 201,
            json: async () => ({ success: true, data: { id: 2 } })
        });

        const bodyData = { nombre: 'Nuevo Producto' };
        await window.apiRequest('/productos', 'POST', bodyData);

        expect(global.fetch).toHaveBeenCalledWith(
            'http://localhost:8081/api/productos',
            expect.objectContaining({
                method: 'POST',
                body: JSON.stringify(bodyData)
            })
        );
    });

    test('apiRequest debe limpiar sesión y redirigir ante un error 401', async () => {
        localStorage.setItem('jwt_token', 'token-antiguo');
        global.fetch.mockResolvedValueOnce({
            ok: false,
            status: 401,
            json: async () => ({ success: false, message: 'No autorizado' })
        });

        await expect(window.apiRequest('/productos')).rejects.toThrow('Sesión expirada');
        expect(localStorage.getItem('jwt_token')).toBeNull();
        expect(window.location.href).toBe('login');
    });

    test('apiRequest debe retornar true para respuestas 204', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 204
        });

        const result = await window.apiRequest('/productos/1', 'DELETE');
        expect(result).toBe(true);
    });

    test('apiRequest debe lanzar error cuando success es false', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => ({ success: false, message: 'Error de negocio' })
        });

        await expect(window.apiRequest('/productos')).rejects.toThrow('Error de negocio');
    });
});
