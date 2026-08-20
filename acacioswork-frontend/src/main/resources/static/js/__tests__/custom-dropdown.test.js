/** Pruebas unitarias para el selector de clientes personalizado custom-dropdown.js. @author RADJ */

const fs = require('fs');
const path = require('path');

// Usar fake timers para el blur setTimeout de 200ms
jest.useFakeTimers();

/** Cargar y evaluar el archivo custom-dropdown.js. @author RADJ */
const customDropdownCode = fs.readFileSync(
    path.resolve(__dirname, '../shared/custom-dropdown.js'),
    'utf8'
);

eval(customDropdownCode);

describe('custom-dropdown.js tests', () => {
    let selectEl;

    beforeEach(() => {
        document.body.innerHTML = `
            <div>
                <select id="client-select">
                    <option value="">— Venta sin cliente registrado —</option>
                    <option value="1">Juan Perez</option>
                    <option value="2">Maria Gomez</option>
                </select>
            </div>
        `;
        selectEl = document.getElementById('client-select');
        jest.clearAllTimers();
    });

    test('initCustomClientDropdown debe inicializar y envolver el select real', () => {
        window.initCustomClientDropdown();

        // El select real debe estar oculto
        expect(selectEl.style.display).toBe('none');

        // Contenedor principal debe haberse creado
        const wrapper = selectEl.parentNode;
        expect(wrapper.className).toBe('custom-client-dropdown');

        // Disparador del dropdown e input
        const trigger = wrapper.querySelector('#client-select-trigger');
        expect(trigger).not.toBeNull();

        const input = trigger.querySelector('#client-select-input');
        expect(input.value).toBe('— Venta sin cliente registrado —');
        expect(input.readOnly).toBe(true);

        // Lista de opciones visuales
        const list = wrapper.querySelector('#client-dropdown-list');
        expect(list).not.toBeNull();
        expect(list.children.length).toBe(3);
        expect(list.children[1].textContent).toBe('Juan Perez');
        expect(list.children[1].dataset.value).toBe('1');
    });

    test('debe alternar la visibilidad de la lista al hacer clic en el disparador', () => {
        window.initCustomClientDropdown();

        const wrapper = selectEl.parentNode;
        const trigger = wrapper.querySelector('#client-select-trigger');
        const list = wrapper.querySelector('#client-dropdown-list');

        // Estado inicial cerrado o vacío
        expect(list.style.display).not.toBe('block');

        // Primer clic abre el dropdown
        trigger.click();
        expect(list.style.display).toBe('block');
        expect(wrapper.classList.contains('open')).toBe(true);

        // Segundo clic lo cierra
        trigger.click();
        expect(list.style.display).toBe('none');
        expect(wrapper.classList.contains('open')).toBe(false);
    });

    test('doble clic en el disparador debe permitir la edición e input de búsqueda', () => {
        window.initCustomClientDropdown();

        const wrapper = selectEl.parentNode;
        const trigger = wrapper.querySelector('#client-select-trigger');
        const input = trigger.querySelector('#client-select-input');

        // Doble clic
        trigger.dispatchEvent(new MouseEvent('dblclick', { bubbles: true }));

        expect(input.readOnly).toBe(false);
        expect(input.value).toBe('');
    });

    test('debe filtrar opciones en la lista visual al escribir', () => {
        window.initCustomClientDropdown();

        const wrapper = selectEl.parentNode;
        const trigger = wrapper.querySelector('#client-select-trigger');
        const input = trigger.querySelector('#client-select-input');
        const list = wrapper.querySelector('#client-dropdown-list');

        // Poner en modo búsqueda
        trigger.dispatchEvent(new MouseEvent('dblclick', { bubbles: true }));

        // Escribir "Maria"
        input.value = 'Maria';
        input.dispatchEvent(new Event('input'));

        expect(list.children[0].style.display).toBe('none'); // Sin cliente
        expect(list.children[1].style.display).toBe('none'); // Juan Perez
        expect(list.children[2].style.display).toBe('block'); // Maria Gomez
    });

    test('cambiar select.value mediante JS debe actualizar la UI visual', () => {
        window.initCustomClientDropdown();

        const wrapper = selectEl.parentNode;
        const input = wrapper.querySelector('#client-select-input');
        const list = wrapper.querySelector('#client-dropdown-list');

        // Invocar el setter del descriptor de la instancia directamente para JSDOM
        const desc = Object.getOwnPropertyDescriptor(selectEl, 'value');
        if (desc && desc.set) {
            desc.set.call(selectEl, '2');
        } else {
            selectEl.value = '2';
        }

        expect(input.value).toBe('Maria Gomez');
        expect(list.children[0].classList.contains('selected')).toBe(false);
        expect(list.children[2].classList.contains('selected')).toBe(true);
    });
});
