/** Pruebas unitarias para el control de autenticación y sesión del frontend. @author RADJ */

const fs = require('fs');
const path = require('path');

// Mockear window.location
delete window.location;
window.location = { href: '', pathname: '/' };

/** Cargar y evaluar el archivo auth.js. @author RADJ */
const authCode = fs.readFileSync(
    path.resolve(__dirname, '../core/auth.js'),
    'utf8'
);

// Sobrescribir addEventListener para interceptar la carga de la página
const eventListeners = {};
document.addEventListener = jest.fn((event, cb) => {
    eventListeners[event] = cb;
});

eval(authCode);

describe('auth.js tests', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        window.location.href = '';
        window.location.pathname = '/';
    });

    test('window.logout debe limpiar localStorage y redirigir a login', () => {
        localStorage.setItem('jwt_token', 'token-valido');
        localStorage.setItem('usuario', 'nombre-usuario');

        expect(localStorage.getItem('jwt_token')).toBe('token-valido');

        window.logout();

        expect(localStorage.getItem('jwt_token')).toBeNull();
        expect(localStorage.getItem('usuario')).toBeNull();
        expect(window.location.href).toBe('login');
    });

    test('DOMContentLoaded no redirige si estamos en login', () => {
        window.location.pathname = '/login';
        
        // Ejecutar el callback del listener
        if (eventListeners['DOMContentLoaded']) {
            eventListeners['DOMContentLoaded']();
        }

        expect(window.location.href).toBe('');
    });

    test('DOMContentLoaded redirige si no hay token jwt en almacenamiento local', () => {
        window.location.pathname = '/dashboard';
        
        // Ejecutar el callback del listener
        if (eventListeners['DOMContentLoaded']) {
            eventListeners['DOMContentLoaded']();
        }

        expect(window.location.href).toBe('login');
    });

    test('DOMContentLoaded no redirige si hay token jwt en almacenamiento local', () => {
        window.location.pathname = '/dashboard';
        localStorage.setItem('jwt_token', 'token-valido');

        // Ejecutar el callback del listener
        if (eventListeners['DOMContentLoaded']) {
            eventListeners['DOMContentLoaded']();
        }

        expect(window.location.href).toBe('');
    });
});
