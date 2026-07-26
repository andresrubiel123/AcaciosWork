/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** utils.js - variables de estado global (appstate) y formateadores. @author RADJ */

/*** inicializar el espacio de nombres de estado global de la aplicación. @author RADJ */
window.AppState = {
    editId: null,
    currentModalType: null,
    salesChartInstance: null,
    cart: [],
    allProducts: [],
    allClientes: [],
    allProductos: [],
    globalConfig: null,
    searchTimeout: null,
    cache: {
        categorias: [],
        proveedores: [],
        tiposDocumento: [],
        roles: []
    }
};

/*** formatea un número según la moneda configurada en la aplicación. @param {number} n número a formatear. @returns {string} texto formateado. @author RADJ */
window.formatCurrency = function(n) {
    let moneda = (window.AppState && window.AppState.globalConfig) ? (window.AppState.globalConfig.moneda || 'COP') : 'COP';
    try {
        return new Intl.NumberFormat('es-CO', { style: 'currency', currency: moneda, maximumFractionDigits: 0 }).format(n);
    } catch (e) {
        return moneda + ' ' + Number(n).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    }
};

/***
 * Configura el desplazamiento infinito (Infinite Scroll) para una tabla.
 * @author RADJ
 */
window.setupTablePagination = function(config) {
    const {
        tbodyId,
        allItems,
        renderRowFn,
        onRenderComplete,
        itemsPerBlock = 25
    } = config;

    let currentPage = 0;
    let filteredItems = allItems;

    function renderPage() {
        const tbody = document.getElementById(tbodyId);
        if (!tbody) return;

        if (!filteredItems.length) {
            const table = tbody.closest('table');
            const cols = table ? (table.querySelectorAll('thead th').length || 5) : 5;
            tbody.innerHTML = `<tr><td colspan="${cols}" style="text-align:center;padding:2rem;color:var(--text-muted)">Sin registros.</td></tr>`;
            if (onRenderComplete) onRenderComplete([]);
            return;
        }

        const start = currentPage * itemsPerBlock;
        const end = start + itemsPerBlock;
        const pageItems = filteredItems.slice(start, end);
        const html = pageItems.map(item => renderRowFn(item)).join('');

        if (currentPage === 0) {
            tbody.innerHTML = html;
        } else {
            tbody.insertAdjacentHTML('beforeend', html);
        }

        if (onRenderComplete) onRenderComplete(filteredItems);
    }

    // Resetear paginación
    currentPage = 0;
    filteredItems = allItems;
    renderPage();

    // Registrar manejador de scroll global si no existe
    if (!window._paginationScrollHandlers) {
        window._paginationScrollHandlers = {};
        window.addEventListener('scroll', () => {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 150) {
                const activeSection = document.querySelector('.section:not([style*="display:none"]):not([style*="display: none"])');
                if (!activeSection) return;

                const tbodies = activeSection.querySelectorAll('tbody[id]');
                tbodies.forEach(tb => {
                    const handler = window._paginationScrollHandlers[tb.id];
                    if (handler) handler.loadNextPage();
                });
            }
        });
    }

    window._paginationScrollHandlers[tbodyId] = {
        loadNextPage: () => {
            if ((currentPage + 1) * itemsPerBlock < filteredItems.length) {
                currentPage++;
                renderPage();
            }
        },
        updateSearch: (results) => {
            filteredItems = results;
            currentPage = 0;
            renderPage();
        }
    };
};
