/** Pruebas unitarias para las utilidades globales del frontend. @author RADJ */

const fs = require('fs');
const path = require('path');

/** Cargar y evaluar el archivo utils.js. @author RADJ */
const utilsCode = fs.readFileSync(
    path.resolve(__dirname, '../core/utils.js'),
    'utf8'
);

eval(utilsCode);

describe('utils.js tests', () => {
    beforeEach(() => {
        // Resetear AppState a su estado inicial antes de cada prueba
        window.AppState.globalConfig = null;
        document.body.innerHTML = '';
    });

    test('formatCurrency debe formatear a COP por defecto', () => {
        const result = window.formatCurrency(5000);
        expect(result).toContain('5.000');
        expect(result.includes('$') || result.includes('COP')).toBe(true);
    });

    test('formatCurrency debe usar la moneda configurada en AppState', () => {
        window.AppState.globalConfig = { moneda: 'USD' };
        const result = window.formatCurrency(1234);
        expect(result).toContain('1.234');
        expect(result.includes('$') || result.includes('USD')).toBe(true);
    });

    test('sortTableData debe ordenar cadenas correctamente en orden ascendente y descendente', () => {
        const data = [
            { nombre: 'Manzana' },
            { nombre: 'Banano' },
            { nombre: 'Pera' }
        ];

        const asc = window.sortTableData(data, 'nombre', 'asc');
        expect(asc[0].nombre).toBe('Banano');
        expect(asc[2].nombre).toBe('Pera');

        const desc = window.sortTableData(data, 'nombre', 'desc');
        expect(desc[0].nombre).toBe('Pera');
        expect(desc[2].nombre).toBe('Banano');
    });

    test('sortTableData debe ordenar números correctamente', () => {
        const data = [
            { id: 10 },
            { id: 2 },
            { id: 5 }
        ];

        const asc = window.sortTableData(data, 'id', 'asc');
        expect(asc[0].id).toBe(2);
        expect(asc[2].id).toBe(10);
    });

    test('setupTablePagination debe renderizar bloques iniciales de registros en el tbody', () => {
        document.body.innerHTML = `
            <table>
                <tbody id="test-pagination-tbody"></tbody>
            </table>
        `;

        const items = [
            { id: 1, name: 'Item 1' },
            { id: 2, name: 'Item 2' }
        ];

        window.setupTablePagination({
            tbodyId: 'test-pagination-tbody',
            allItems: items,
            itemsPerBlock: 1,
            renderRowFn: (item) => `<tr><td>${item.name}</td></tr>`
        });

        const tbody = document.getElementById('test-pagination-tbody');
        const rows = tbody.querySelectorAll('tr');
        expect(rows.length).toBe(1);
        expect(rows[0].textContent).toBe('Item 1');
    });
});
