/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** buscador.js - lógica común de búsqueda para los diferentes módulos de acacioswork. permite buscar productos, clientes, proveedores y usuarios. @author RADJ */
class Buscador {
    /*** @param {object} config configuración del buscador. @param {string} config.entidad tipo de entidad: 'productos', 'clientes', 'proveedores', 'usuarios' @param {string} config.inputid id del input de búsqueda @param {string} [config.tbodyid] id opcional del tbody para aplicar filtrado rápido por dom @param {string} [config.endpoint] endpoint opcional para consultar a la api (por ejemplo, '/productos') @param {function} [config.onsearchresult] callback invocado cuando se obtienen los resultados filtrados. recibe (datosfiltrados, query) @param {array<string>} [config.camposfiltro] lista de propiedades de la entidad en las que buscar @param {number} [config.debouncems] tiempo de retardo para la búsqueda al escribir (default 300ms). @author RADJ */
    constructor(config) {
        this.entidad = config.entidad;
        this.inputId = config.inputId;
        this.tbodyId = config.tbodyId;
        this.endpoint = config.endpoint || `/${config.entidad}`;
        this.onSearchResult = config.onSearchResult;
        this.camposFiltro = config.camposFiltro || [];
        this.debounceMs = config.debounceMs !== undefined ? config.debounceMs : 300;
        
        this.inputElement = null;
        this.tbodyElement = null;
        
        this.allData = [];
/*** datos cargados originalmente de la api o memoria. @author RADJ */
        this.filteredData = [];
/*** datos resultantes después de filtrar. @author RADJ */
        this.debounceTimeout = null;

       
/*** mapeo por defecto de campos a buscar según la entidad si no se definen. @author RADJ */
        if (this.camposFiltro.length === 0) {
            switch(this.entidad) {
                case 'productos':
                    this.camposFiltro = ['nombre', 'codigoBarras', 'unidadMedida'];
                    break;
                case 'clientes':
                    this.camposFiltro = ['nombre', 'numeroDocumento', 'telefono', 'email'];
                    break;
                case 'proveedores':
                    this.camposFiltro = ['nombre', 'numeroDocumento', 'telefono', 'email', 'direccion'];
                    break;
                case 'usuarios':
                    this.camposFiltro = ['nombre', 'apellido', 'usuario', 'numeroDocumento', 'email'];
                    break;
                default:
                    this.camposFiltro = [];
            }
        }

        this.init();
    }

    /*** inicializa los elementos del dom y vincula los listeners de eventos. @author RADJ */
    init() {
        this.inputElement = document.getElementById(this.inputId);
        this.tbodyElement = document.getElementById(this.tbodyId);

        if (!this.inputElement) {
           
/*** vincular en diferido si el elemento aún no se ha renderizado en el dom. @author RADJ */
            document.addEventListener('DOMContentLoaded', () => {
                this.inputElement = document.getElementById(this.inputId);
                this.tbodyElement = document.getElementById(this.tbodyId);
                this.setupInputListener();
            });
           
/*** o registrar un listener de carga general en el dom. @author RADJ */
            setTimeout(() => {
                if (!this.inputElement) {
                    this.inputElement = document.getElementById(this.inputId);
                    this.tbodyElement = document.getElementById(this.tbodyId);
                    this.setupInputListener();
                }
            }, 100);
        } else {
            this.setupInputListener();
        }
    }

    setupInputListener() {
        if (!this.inputElement) return;
        if (this.inputElement.dataset.buscadorInitialized) return;
        this.inputElement.dataset.buscadorInitialized = "true";

        this.inputElement.addEventListener('input', (e) => {
            clearTimeout(this.debounceTimeout);
            this.debounceTimeout = setTimeout(() => {
                this.realizarBusqueda(e.target.value);
            }, this.debounceMs);
        });
    }

    /*** realiza la petición a la api para obtener todos los registros de la entidad. de esta forma cada buscador hace su propia consulta al endpoint que le corresponde. @author RADJ */
    async consultarAPI() {
        try {
            if (typeof window.apiRequest === 'function') {
                this.allData = await window.apiRequest(this.endpoint) || [];
            } else {
               
/*** fallback directo por si apirequest no está disponible globalmente. @author RADJ */
                const token = localStorage.getItem("jwt_token");
                const headers = { "Content-Type": "application/json" };
                if (token) headers["Authorization"] = `Bearer ${token}`;
                
                const response = await fetch(`http://localhost:8081/api${this.endpoint}`, { headers });
                if (response.ok) {
                    const result = await response.json();
                    this.allData = result.data || result;
                } else {
                    console.error("Error al consultar la API:", response.statusText);
                }
            }
            return this.allData;
        } catch (e) {
            console.error(`Error al consultar entidad '${this.entidad}' en endpoint '${this.endpoint}':`, e);
            return [];
        }
    }

    /*** establece los datos locales para la búsqueda (útil si el módulo ya cargó los datos de la api). @param {array} data arreglo de elementos a filtrar localmente. @author RADJ */
    setDatos(data) {
        this.allData = data || [];
    }

    /*** ejecuta el filtro de búsqueda. @param {string} query texto de búsqueda. @author RADJ */
    async realizarBusqueda(query) {
        const cleanQuery = (query || '').toLowerCase().trim();
        
       
/*** si no tenemos datos pre-cargados localmente y no se pasaron al buscador,. @author RADJ */
       
/*** consultamos la api de forma dinámica para obtener el dataset. @author RADJ */
        if (this.allData.length === 0) {
            await this.consultarAPI();
        }

        if (!cleanQuery) {
            this.filteredData = this.allData;
        } else {
            this.filteredData = this.allData.filter(item => {
                if (this.camposFiltro.length > 0) {
                    return this.camposFiltro.some(campo => {
                        const val = item[campo];
                        return val !== undefined && val !== null && String(val).toLowerCase().includes(cleanQuery);
                    });
                }
               
/*** si no hay campos definidos, busca recursivamente en todo el objeto. @author RADJ */
                return Object.values(item).some(val => {
                    if (val === null || val === undefined) return false;
                    return String(val).toLowerCase().includes(cleanQuery);
                });
            });
        }

       
/*** si hay callback de visualización, le enviamos los datos filtrados. @author RADJ */
        if (typeof this.onSearchResult === 'function') {
            this.onSearchResult(this.filteredData, cleanQuery);
        } else {
           
/*** asegurar referencia al tbody y filtrar visualmente las filas del dom. @author RADJ */
            if (!this.tbodyElement && this.tbodyId) {
                this.tbodyElement = document.getElementById(this.tbodyId);
            }
            this.filtrarTablaHTML(cleanQuery);
        }
    }

    /*** filtro visual rápido para elementos tr del dom (comportamiento de fallback). @author RADJ */
    filtrarTablaHTML(query) {
        if (!this.tbodyElement) return;
        const rows = this.tbodyElement.getElementsByTagName('tr');
        for (let i = 0; i < rows.length; i++) {
            const row = rows[i];
           
/*** ignorar filas especiales (mensajes de sin registros o cargando). @author RADJ */
            if (row.cells.length === 1 && row.cells[0].colSpan > 1) {
                continue;
            }
            const text = row.textContent || row.innerText;
            if (text.toLowerCase().includes(query)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        }
    }
}

/*** exponer la clase globalmente. @author RADJ */
window.Buscador = Buscador;
