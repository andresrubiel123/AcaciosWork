/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** @jest-environment jsdom. @author RADJ */

const fs = require('fs');
const path = require('path');

/*** read and evaluate buscador.js in the jsdom environment. @author RADJ */
const buscadorCode = fs.readFileSync(
  path.resolve(__dirname, '../shared/buscador.js'),
  'utf8'
);

/*** mock window.apirequest. @author RADJ */
window.apiRequest = jest.fn();

/*** evaluate buscador.js to declare buscador on window. @author RADJ */
eval(buscadorCode);

describe('Buscador class tests', () => {
  let inputEl;
  let tbodyEl;

  beforeEach(() => {
   
/*** reset mocks and dom. @author RADJ */
    jest.clearAllMocks();
    document.body.innerHTML = `
      <input type="text" id="test-search-input" />
      <table>
        <tbody id="test-tbody">
          <tr><td>Fila 1: Producto A</td></tr>
          <tr><td>Fila 2: Cliente B</td></tr>
          <tr><td colspan="5">Cargando...</td></tr>
        </tbody>
      </table>
    `;
    inputEl = document.getElementById('test-search-input');
    tbodyEl = document.getElementById('test-tbody');
  });

  test('should initialize with default fields based on entity', () => {
    const buscador = new window.Buscador({
      entidad: 'clientes',
      inputId: 'test-search-input',
      tbodyId: 'test-tbody'
    });

    expect(buscador.entidad).toBe('clientes');
    expect(buscador.camposFiltro).toEqual(['nombre', 'numeroDocumento', 'telefono', 'email']);
  });

  test('should filter DOM rows locally', async () => {
    const buscador = new window.Buscador({
      entidad: 'productos',
      inputId: 'test-search-input',
      tbodyId: 'test-tbody',
      debounceMs: 0
    });

   
/*** manually trigger search and await its completion. @author RADJ */
    await buscador.realizarBusqueda('Producto A');

    const rows = tbodyEl.getElementsByTagName('tr');
    expect(rows[0].style.display).toBe('');
    expect(rows[1].style.display).toBe('none');
    expect(rows[2].style.display).toBe('');
/*** ignored colspan row. @author RADJ */
  });

  test('should filter data array locally and trigger onSearchResult callback', () => {
    const mockCallback = jest.fn();
    const buscador = new window.Buscador({
      entidad: 'usuarios',
      inputId: 'test-search-input',
      onSearchResult: mockCallback,
      debounceMs: 0
    });

    const mockData = [
      { nombre: 'Carlos', usuario: 'carlos123' },
      { nombre: 'Maria', usuario: 'maria456' }
    ];

    buscador.setDatos(mockData);
    buscador.realizarBusqueda('carlos');

    expect(mockCallback).toHaveBeenCalledWith(
      [{ nombre: 'Carlos', usuario: 'carlos123' }],
      'carlos'
    );
  });
});
