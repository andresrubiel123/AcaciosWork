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

/***
 * Ordena un array de objetos por una columna y dirección específica.
 * @author RADJ
 */
window.sortTableData = function(data, column, direction) {
    return [...data].sort((a, b) => {
        let valA = a[column];
        let valB = b[column];

        // Manejo de valores vacíos o nulos
        if (valA === undefined || valA === null) valA = '';
        if (valB === undefined || valB === null) valB = '';

        // Si son strings, comparar usando localeCompare
        if (typeof valA === 'string' && typeof valB === 'string') {
            return direction === 'asc' 
                ? valA.localeCompare(valB, 'es', { sensitivity: 'base', numeric: true })
                : valB.localeCompare(valA, 'es', { sensitivity: 'base', numeric: true });
        }

        // Si son números o booleanos
        if (valA < valB) return direction === 'asc' ? -1 : 1;
        if (valA > valB) return direction === 'asc' ? 1 : -1;
        return 0;
    });
};

/***
 * Habilita el ordenamiento dinámico para una tabla al hacer clic en sus cabeceras.
 * @author RADJ
 */
window.enableTableSorting = function(config) {
    const {
        tableSelector,
        getDataFn,
        setDataFn,
        renderFn
    } = config;

    const init = () => {
        const table = document.querySelector(tableSelector);
        if (!table) return;

        const headers = table.querySelectorAll('thead th.sortable');
        let currentSortColumn = null;
        let currentSortDirection = 'asc';

        headers.forEach(th => {
            th.style.cursor = 'pointer';
            th.style.userSelect = 'none';
            
            let iconSpan = th.querySelector('.sort-icon');
            if (!iconSpan) {
                iconSpan = document.createElement('span');
                iconSpan.className = 'sort-icon';
                iconSpan.style.marginLeft = '0.35rem';
                iconSpan.style.fontSize = '0.75rem';
                iconSpan.style.opacity = '0.5';
                iconSpan.innerHTML = '↕';
                th.appendChild(iconSpan);
            }

            th.addEventListener('click', () => {
                const column = th.getAttribute('data-sort');
                if (!column) return;

                if (currentSortColumn === column) {
                    currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
                } else {
                    currentSortColumn = column;
                    currentSortDirection = 'asc';
                }

                headers.forEach(h => {
                    const span = h.querySelector('.sort-icon');
                    if (span) {
                        if (h === th) {
                            span.innerHTML = currentSortDirection === 'asc' ? '▲' : '▼';
                            span.style.opacity = '1';
                            span.style.color = '#39ff14';
                        } else {
                            span.innerHTML = '↕';
                            span.style.opacity = '0.5';
                            span.style.color = '';
                        }
                    }
                });

                const sortedData = window.sortTableData(getDataFn(), currentSortColumn, currentSortDirection);
                setDataFn(sortedData);
                renderFn(sortedData);
            });
        });
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
};

