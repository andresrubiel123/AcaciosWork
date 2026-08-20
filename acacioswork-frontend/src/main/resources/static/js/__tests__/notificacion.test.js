/** Pruebas unitarias para el componente de notificaciones flotantes (showToast). @author RADJ */

const fs = require('fs');
const path = require('path');

// Usar fake timers para controlar setTimeout de manera exacta
jest.useFakeTimers();

/** Cargar y evaluar el archivo notificacion.js. @author RADJ */
const notificacionCode = fs.readFileSync(
    path.resolve(__dirname, '../shared/notificacion.js'),
    'utf8'
);

eval(notificacionCode);

describe('notificacion.js tests', () => {
    beforeEach(() => {
        document.body.innerHTML = '';
        jest.clearAllTimers();
    });

    test('showToast debe crear el elemento toast en el DOM y mostrar el mensaje', () => {
        window.showToast('Operación exitosa', 'success');

        const toast = document.getElementById('toast');
        expect(toast).not.toBeNull();
        expect(toast.textContent).toBe('Operación exitosa');
        expect(toast.className).toBe('toast success show');
    });

    test('showToast debe reutilizar el elemento toast si ya existe', () => {
        window.showToast('Primer mensaje');
        const firstToast = document.getElementById('toast');

        window.showToast('Segundo mensaje', 'error');
        const secondToast = document.getElementById('toast');

        expect(firstToast).toBe(secondToast); // Mismo elemento en el DOM
        expect(secondToast.textContent).toBe('Segundo mensaje');
        expect(secondToast.className).toBe('toast error show');
    });

    test('showToast debe remover la clase show después de 2000ms', () => {
        window.showToast('Mensaje de prueba');

        const toast = document.getElementById('toast');
        expect(toast.className).toContain('show');

        // Avanzar el tiempo 2000ms
        jest.advanceTimersByTime(2000);

        expect(toast.className).not.toContain('show');
    });
});
