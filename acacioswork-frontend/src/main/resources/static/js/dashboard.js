/** Dashboard Administrador AcaciosWork - CRUD completo y Reportes PDF. @author RADJ */

/** Función de filtrado en tiempo real para las tablas. @author RADJ */
function filterTable(inputElement, tbodyId) {
    /** Obtener y limpiar el texto de búsqueda. @author RADJ */
    const filter = inputElement.value.toLowerCase().trim();
    /** Obtener la referencia del cuerpo de la tabla. @author RADJ */
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    /** Obtener todas las filas de la tabla. @author RADJ */
    const rows = tbody.getElementsByTagName('tr');
    /** Recorrer cada fila de la tabla para aplicar el filtro. @author RADJ */
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        /** Ignorar filas que representen mensajes de carga o error. @author RADJ */
        if (row.cells.length === 1 && row.cells[0].colSpan > 1) {
            continue;
        }
        /** Obtener el texto contenido en la fila actual. @author RADJ */
        const text = row.textContent || row.innerText;
        /** Mostrar u ocultar la fila según coincida con el filtro. @author RADJ */
        if (text.toLowerCase().includes(filter)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    }
}

/** Variables globales para el manejo de estado. @author RADJ */
let editId = null;
let currentModalType = '';
let cacheCategorias = [];
let cacheProveedores = [];
let cacheTiposDocumento = [];
let cacheRoles = [];

let cart = [];
let allProducts = [];
let allClientes = [];
let searchTimeout = null;

/** Verificación de autenticación al cargar la página. @author RADJ */
document.addEventListener('DOMContentLoaded', async () => {
    /** Validar existencia de sesión activa; redirigir si no existe. @author RADJ */
    if (!localStorage.getItem('jwt_token')) { window.location.href = 'login'; return; }
    /** Mostrar el nombre del usuario autenticado en la interfaz. @author RADJ */
    document.getElementById('userInfo').textContent = '👤 ' + (localStorage.getItem('user_name') || 'Admin');

    /** Cargar listas de referencia iniciales en memoria. @author RADJ */
    await loadReferences();
    /** Cargar y mostrar estadísticas iniciales en la ventana de inicio. @author RADJ */
    await loadStats();
});

/** Cerrar sesión del usuario actual y redirigir al login. @author RADJ */
function logout() {
    /** Limpiar todos los datos guardados en almacenamiento local. @author RADJ */
    localStorage.clear();
    /** Redireccionar a la pantalla de inicio de sesión. @author RADJ */
    window.location.href = 'login';
}

/** Carga las referencias de la base de datos necesarias para poblar selects. @author RADJ */
async function loadReferences() {
    try {
        /** Obtener categorías, proveedores, documentos y roles desde la API. @author RADJ */
        cacheCategorias = await apiRequest('/categorias') || [];
        cacheProveedores = await apiRequest('/proveedores') || [];
        cacheTiposDocumento = await apiRequest('/tipos-documentos') || [];
        cacheRoles = await apiRequest('/roles') || [];
    } catch (e) {
        /** Registrar en consola cualquier fallo de carga de datos referenciales. @author RADJ */
        console.error("Error al cargar referencias de base de datos:", e);
    }
}

/** Control de navegación entre secciones del dashboard. @author RADJ */
function showSection(name, btn) {
    /** Limpiar inputs de búsqueda al cambiar de sección. @author RADJ */
    const searchInputs = ['inv-search-input', 'prov-search-input', 'cli-search-input', 'usr-search-input', 'alertas-search-input', 'product-search'];
    searchInputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });

    /** Ocultar todas las secciones del panel. @author RADJ */
    document.querySelectorAll('.section').forEach(s => s.style.display = 'none');
    /** Remover la clase activa de todos los botones de la barra. @author RADJ */
    document.querySelectorAll('.toolbar-btn').forEach(b => b.classList.remove('active'));
    /** Mostrar la sección seleccionada y activar su botón correspondiente. @author RADJ */
    const targetSection = document.getElementById('sec-' + name);
    if (targetSection) targetSection.style.display = 'block';
    if (btn) btn.classList.add('active');

    /** Invocar carga de datos específica según la sección de destino. @author RADJ */
    if (name === 'welcome') loadStats();
    if (name === 'inventario') loadInventario();
    if (name === 'proveedores') loadProveedores();
    if (name === 'clientes') loadClientes();
    if (name === 'usuarios') loadUsuarios();
    if (name === 'alertas') loadAlertas();
    if (name === 'vender') loadVenderSection();
}

/** Actualiza la interfaz gráfica de las tarjetas de estadísticas con los productos proporcionados. @author RADJ */
function updateStatsUI(products) {
    /** Calcular indicadores financieros y métricas del inventario. @author RADJ */
    const total = products.length;
    const bajo = products.filter(p => p.stockActual <= (p.stockMinimo || 5)).length;
    const valor = products.reduce((a, p) => a + (p.stockActual * p.precioVenta), 0);
    const valorCosto = products.reduce((a, p) => a + (p.stockActual * (p.precioCompra || 0)), 0);
    const utilidad = valor - valorCosto;

    /** Actualizar indicadores numéricos en el dashboard. @author RADJ */
    const totalEl = document.getElementById('inv-total');
    if (totalEl) totalEl.textContent = total;

    const bajoEl = document.getElementById('inv-bajo');
    if (bajoEl) bajoEl.textContent = bajo;

    /** Habilitar dinámicamente animación de alerta en stock bajo. @author RADJ */
    const btnAlertas = document.getElementById('btn-alertas');
    if (btnAlertas) {
        if (bajo > 0) {
            btnAlertas.classList.add('pulsing');
        } else {
            btnAlertas.classList.remove('pulsing');
        }
    }

    /** Formatear y mostrar valores monetarios de costo e inventario. @author RADJ */
    const valorEl = document.getElementById('inv-valor');
    if (valorEl) valorEl.textContent = '$' + valor.toLocaleString();

    const costoEl = document.getElementById('inv-costo');
    if (costoEl) costoEl.textContent = '$' + valorCosto.toLocaleString();

    /** Actualizar color e indicador de utilidad estimada. @author RADJ */
    const utilidadEl = document.getElementById('inv-utilidad');
    if (utilidadEl) {
        utilidadEl.textContent = '$' + utilidad.toLocaleString();
        if (utilidad >= 0) {
            utilidadEl.style.color = '#10b981';
        } else {
            utilidadEl.style.color = '#ef4444';
        }
    }
}

/** Carga y visualización de estadísticas globales en la ventana de inicio. @author RADJ */
async function loadStats() {
    try {
        /** Obtener listado de productos desde la API. @author RADJ */
        const products = await apiRequest('/productos') || [];
        updateStatsUI(products);
    } catch (e) {
        console.error("Error al cargar estadísticas en inicio:", e);
    }
}

/** Carga y visualización de productos en inventario. @author RADJ */
async function loadInventario() {
    /** Limpiar input de búsqueda de inventario. @author RADJ */
    const searchInput = document.getElementById('inv-search-input');
    if (searchInput) searchInput.value = '';
    /** Obtener el cuerpo de la tabla de inventario. @author RADJ */
    const tbody = document.getElementById('inv-tbody');
    try {
        /** Obtener listado de productos desde la API. @author RADJ */
        const products = await apiRequest('/productos') || [];
        /** Actualizar las tarjetas de estadísticas. @author RADJ */
        updateStatsUI(products);

        /** Generar el HTML de las filas de la tabla de productos. @author RADJ */
        tbody.innerHTML = products.length ? products.map(p => {
            const stockActual = p.stockActual !== undefined ? p.stockActual : 0;
            const stockMinimo = p.stockMinimo !== undefined ? p.stockMinimo : 5;
            const stockOptimo = p.stockOptimo ? p.stockOptimo : 200;

            /** Calcular el porcentaje de stock respecto al nivel óptimo. @author RADJ */
            const pct = stockOptimo > 0 ? Math.round((stockActual / stockOptimo) * 100) : 0;
            let colorClass = 'green';
            if (pct <= 30) {
                colorClass = 'red';
            } else if (pct <= 69) {
                colorClass = 'orange';
            }
            const barWidth = Math.min(pct, 100);

            /** Retornar la estructura HTML de la fila del producto. @author RADJ */
            return `
            <tr>
                <td class="col-codigo" style="font-family:monospace;font-size:0.8rem">${p.codigoBarras || 'N/A'}</td>
                <td class="col-nombre" style="font-weight:500" title="${p.nombre}">${p.nombre}</td>
                <td class="col-unidad">${p.unidadMedida || 'Unidad'}</td>
                <td class="col-stock">
                    <div class="stock-bar-wrapper">
                        <div class="stock-bar-info">
                            <span class="stock-bar-qty">${stockActual} / ${stockOptimo} uds</span>
                            <span class="stock-bar-pct ${colorClass}">${pct}%</span>
                        </div>
                        <div class="stock-bar-container">
                            <div class="stock-bar-fill ${colorClass}" style="width: ${barWidth}%"></div>
                        </div>
                    </div>
                </td>
                <td class="col-precio">$${p.precioCompra !== undefined ? Math.round(p.precioCompra) : '0'}</td>
                <td class="col-precio">$${p.precioVenta !== undefined ? Math.round(p.precioVenta) : '0'}</td>
                <td class="col-iva">${p.iva !== undefined ? Number(p.iva).toFixed(1) : '0.0'}%</td>
                <td class="col-estado"><span class="badge ${p.estado === 1 ? 'badge-success' : 'badge-danger'}">${p.estado === 1 ? 'Activo' : 'Inactivo'}</span></td>
                <td class="col-acciones" style="display:flex;gap:0.4rem">
                    <button class="btn-sm" onclick="editProducto(${p.id})">Editar</button>
                    <button class="btn-sm btn-del" onclick="deleteProducto(${p.id})">Borrar</button>
                </td>
            </tr>`;
        }).join('') : '<tr><td colspan="9" style="text-align:center;padding:2rem;color:var(--text-muted)">Sin productos registrados.</td></tr>';
    } catch (e) {
        /** Renderizar mensaje de error en la tabla si falla la petición. @author RADJ */
        tbody.innerHTML = `<tr><td colspan="9" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
    }
}

/** Abrir el modal de edición para un producto específico. @author RADJ */
function editProducto(id) {
    /** Invocar modal con el contexto de inventario y el ID seleccionado. @author RADJ */
    openModal('inventario', id);
}

/** Solicitar confirmación y eliminar un producto de la base de datos. @author RADJ */
async function deleteProducto(id) {
    /** Mostrar confirmación nativa al usuario antes de proceder. @author RADJ */
    if (!confirm('¿Eliminar este producto?')) return;
    try {
        /** Realizar petición DELETE al endpoint de productos. @author RADJ */
        await apiRequest(`/productos/${id}`, 'DELETE');
        /** Recargar la tabla de inventario tras la eliminación. @author RADJ */
        loadInventario();
        alert('Producto eliminado con éxito.');
    } catch (e) {
        /** Informar error al usuario si falla la eliminación. @author RADJ */
        alert('Error al eliminar producto: ' + e.message);
    }
}

/** Carga y visualización de proveedores. @author RADJ */
async function loadProveedores() {
    /** Limpiar campo de filtrado para proveedores. @author RADJ */
    const searchInput = document.getElementById('prov-search-input');
    if (searchInput) searchInput.value = '';
    /** Obtener la referencia de la tabla de proveedores. @author RADJ */
    const tbody = document.getElementById('prov-tbody');
    try {
        /** Solicitar listado de proveedores a la API. @author RADJ */
        const data = await apiRequest('/proveedores') || [];
        /** Generar las filas de la tabla para cada proveedor. @author RADJ */
        tbody.innerHTML = data.length ? data.map(p => `
            <tr>
                <td style="font-weight:500">${p.nombre}</td>
                <td>${p.telefono || '—'}</td>
                <td>${p.email || '—'}</td>
                <td>${p.direccion || '—'}</td>
                <td style="display:flex;gap:0.4rem">
                    <button class="btn-sm" onclick="openModal('proveedor', ${p.id})">Editar</button>
                    <button class="btn-sm btn-del" onclick="deleteProveedor(${p.id})">Borrar</button>
                </td>
            </tr>`).join('') : '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted)">Sin proveedores registrados.</td></tr>';
    } catch (e) {
        /** Mostrar fila de error si falla la consulta de proveedores. @author RADJ */
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
    }
}

/** Solicitar confirmación y eliminar un proveedor del sistema. @author RADJ */
async function deleteProveedor(id) {
    /** Solicitar confirmación al usuario antes de la baja. @author RADJ */
    if (!confirm('¿Eliminar este proveedor?')) return;
    try {
        /** Enviar petición HTTP DELETE al endpoint de proveedores. @author RADJ */
        await apiRequest(`/proveedores/${id}`, 'DELETE');
        /** Recargar la sección de proveedores. @author RADJ */
        loadProveedores();
        alert('Proveedor eliminado con éxito.');
    } catch (e) {
        /** Alertar al usuario sobre el fallo en la eliminación. @author RADJ */
        alert('Error al eliminar proveedor: ' + e.message);
    }
}

/** Carga y visualización de clientes del sistema. @author RADJ */
async function loadClientes() {
    /** Limpiar campo de búsqueda de clientes. @author RADJ */
    const searchInput = document.getElementById('cli-search-input');
    if (searchInput) searchInput.value = '';
    /** Obtener cuerpo de la tabla de clientes. @author RADJ */
    const tbody = document.getElementById('cli-tbody');
    try {
        /** Obtener la lista de clientes registrados en el sistema. @author RADJ */
        const data = await apiRequest('/clientes') || [];
        /** Actualizar contadores de total y activos en cabecera. @author RADJ */
        document.getElementById('cli-total').textContent = data.length;
        document.getElementById('cli-activos').textContent = data.filter(c => c.activo === 1).length;
        /** Dibujar filas de clientes en la tabla. @author RADJ */
        tbody.innerHTML = data.length ? data.map(c => `
            <tr>
                <td style="font-weight:500">${c.nombre}</td>
                <td style="font-family:monospace;font-size:0.82rem">${c.numeroDocumento || '—'}</td>
                <td>${c.telefono || '—'}</td>
                <td>${c.email || '—'}</td>
                <td style="display:flex;gap:0.4rem">
                    <button class="btn-sm" onclick="openModal('cliente', ${c.id})">Editar</button>
                    <button class="btn-sm btn-del" onclick="deleteCliente(${c.id})">Borrar</button>
                </td>
            </tr>`).join('') : '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted)">Sin clientes registrados.</td></tr>';
    } catch (e) {
        /** Mostrar mensaje de error si no se pueden cargar los clientes. @author RADJ */
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
    }
}

/** Solicitar confirmación y eliminar un cliente del sistema. @author RADJ */
async function deleteCliente(id) {
    /** Confirmación nativa ante el usuario. @author RADJ */
    if (!confirm('¿Eliminar este cliente?')) return;
    try {
        /** Realizar petición DELETE al endpoint de clientes. @author RADJ */
        await apiRequest(`/clientes/${id}`, 'DELETE');
        /** Actualizar visualización de la lista de clientes. @author RADJ */
        loadClientes();
        alert('Cliente eliminado con éxito.');
    } catch (e) {
        /** Alertar al usuario ante fallos de eliminación. @author RADJ */
        alert('Error al eliminar cliente: ' + e.message);
    }
}

/** Carga y visualización de usuarios. @author RADJ */
async function loadUsuarios() {
    /** Limpiar campo de búsqueda de usuarios. @author RADJ */
    const searchInput = document.getElementById('usr-search-input');
    if (searchInput) searchInput.value = '';
    /** Obtener la referencia de la tabla de usuarios. @author RADJ */
    const tbody = document.getElementById('usr-tbody');
    try {
        /** Realizar solicitud GET a usuarios. @author RADJ */
        const data = await apiRequest('/usuarios') || [];
        /** Generar el HTML de las filas de la tabla de usuarios. @author RADJ */
        tbody.innerHTML = data.length ? data.map(u => `
            <tr>
                <td style="font-weight:500">${u.nombre} ${u.apellido || ''}</td>
                <td>${u.usuario || '—'}</td>
                <td><span class="badge ${u.idRol === 1 ? 'badge-warn' : 'badge-success'}">${u.idRol === 1 ? 'Administrador' : 'Auxiliar'}</span></td>
                <td><span class="badge ${u.activo === 1 ? 'badge-success' : 'badge-danger'}">${u.activo === 1 ? 'Activo' : 'Inactivo'}</span></td>
                <td style="display:flex;gap:0.4rem">
                    <button class="btn-sm" onclick="openModal('usuario', '${u.numeroDocumento}')">Editar</button>
                    <button class="btn-sm btn-del" onclick="deleteUsuario('${u.numeroDocumento}')">Borrar</button>
                </td>
            </tr>`).join('') : '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted)">Sin usuarios registrados.</td></tr>';
    } catch (e) {
        /** Mostrar error en la tabla si falla la petición. @author RADJ */
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
    }
}

/** Solicitar confirmación y eliminar un usuario por su documento. @author RADJ */
async function deleteUsuario(numeroDocumento) {
    /** Solicitar confirmación para dar de baja al usuario. @author RADJ */
    if (!confirm('¿Eliminar este usuario del sistema?')) return;
    try {
        /** Realizar petición HTTP DELETE por documento de usuario. @author RADJ */
        await apiRequest(`/usuarios/${numeroDocumento}`, 'DELETE');
        /** Recargar la sección de listado de usuarios. @author RADJ */
        loadUsuarios();
        alert('Usuario eliminado con éxito.');
    } catch (e) {
        /** Notificar error en caso de que la eliminación falle. @author RADJ */
        alert('Error al eliminar usuario: ' + e.message);
    }
}

/** Carga y visualización de alertas críticas de stock. @author RADJ */
async function loadAlertas() {
    /** Limpiar campo de búsqueda en alertas. @author RADJ */
    const searchInput = document.getElementById('alertas-search-input');
    if (searchInput) searchInput.value = '';
    /** Obtener el contenedor principal de alertas de stock. @author RADJ */
    const container = document.getElementById('alertas-container');
    try {
        /** Cargar referencias de base de datos si no existen en memoria. @author RADJ */
        if (!cacheProveedores.length) {
            await loadReferences();
        }
        /** Obtener catálogo de productos para analizar stock. @author RADJ */
        const products = await apiRequest('/productos') || [];
        const bajos = products.filter(p => p.stockActual <= (p.stockMinimo || 5));

        /** Manejar interfaz en caso de que no existan alertas de stock bajo. @author RADJ */
        if (bajos.length === 0) {
            container.innerHTML = '<p style="color:#10b981; font-weight: 500; display: flex; align-items: center; gap: 0.5rem;">✓ Todos los productos tienen stock suficiente.</p>';
            const btnAlertas = document.getElementById('btn-alertas');
            if (btnAlertas) btnAlertas.classList.remove('pulsing');
            const searchCont = document.getElementById('alertas-search-container');
            if (searchCont) searchCont.style.display = 'none';
            return;
        }

        /** Activar efecto visual e inputs de búsqueda si hay alertas. @author RADJ */
        const btnAlertas = document.getElementById('btn-alertas');
        if (btnAlertas) btnAlertas.classList.add('pulsing');
        const searchCont = document.getElementById('alertas-search-container');
        if (searchCont) searchCont.style.display = 'block';

        /** Construir estructura de tabla para el reporte de stock bajo. @author RADJ */
        let tableHtml = `
            <div class="card">
                <table>
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th style="text-align:center">Stock Actual</th>
                            <th style="text-align:center">Mínimo</th>
                            <th>Proveedor</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody id="alertas-tbody">
        `;

        /** Poblar la tabla de alertas y asociar proveedores. @author RADJ */
        bajos.forEach(p => {
            const prov = cacheProveedores.find(pr => pr.id === p.idProveedor);
            const provNombre = prov ? prov.nombre : 'Sin asignar';
            const isAgotado = p.stockActual === 0;

            const actionHtml = p.idProveedor
                ? `<a href="javascript:void(0)" onclick="verProveedor(${p.idProveedor})" style="color: #a5b4fc; text-decoration: none; font-weight: 600; display: inline-flex; align-items: center; gap: 0.35rem; transition: all 0.2s;" onmouseover="this.style.color='#c7d2fe'" onmouseout="this.style.color='#a5b4fc'">🔍 Ver Proveedor</a>`
                : `<span style="color: var(--text-muted);">—</span>`;

            tableHtml += `
                        <tr>
                            <td style="font-weight:500">${p.nombre}</td>
                            <td style="text-align:center; font-weight:600; ${isAgotado ? 'color:#ef4444; background:rgba(239, 68, 68, 0.1);' : 'color:#f59e0b; background:rgba(245, 158, 11, 0.1);'}">${p.stockActual} uds</td>
                            <td style="text-align:center; color:var(--text-muted)">${p.stockMinimo || 5} uds</td>
                            <td>${provNombre}</td>
                            <td>${actionHtml}</td>
                        </tr>
            `;
        });

        tableHtml += `
                    </tbody>
                </table>
            </div>
        `;

        container.innerHTML = tableHtml;
    } catch (e) {
        /** Presentar error en el contenedor de alertas. @author RADJ */
        container.innerHTML = `<p style="color:#ef4444">Error al cargar alertas: ${e.message}</p>`;
        const searchCont = document.getElementById('alertas-search-container');
        if (searchCont) searchCont.style.display = 'none';
    }
}

/** Cargar y mostrar modal con detalles completos de un proveedor. @author RADJ */
async function verProveedor(id) {
    try {
        /** Asegurar carga de proveedores referenciales. @author RADJ */
        if (!cacheProveedores.length) {
            await loadReferences();
        }
        /** Obtener proveedor desde la caché local o API. @author RADJ */
        const prov = cacheProveedores.find(p => p.id === id) || await apiRequest(`/proveedores/${id}`);
        if (!prov) {
            alert("No se encontró la información del proveedor.");
            return;
        }

        /** Poblar la vista de detalles y mostrar el modal del proveedor. @author RADJ */
        const content = document.getElementById('prov-details-content');
        content.innerHTML = `
            <div><strong>Nombre / Razón Social:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.nombre}</span></div>
            <div><strong>NIT / Identificación:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.numeroDocumento || '—'}</span></div>
            <div><strong>Teléfono:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.telefono || '—'}</span></div>
            <div><strong>Email:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.email || '—'}</span></div>
            <div><strong>Dirección:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.direccion || '—'}</span></div>
            <div><strong>Cuenta Bancaria:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.cuentaBancaria || '—'}</span></div>
            <div><strong>Estado:</strong> <span class="badge ${prov.activo === 1 ? 'badge-success' : 'badge-danger'}" style="margin-left: 0.5rem;">${prov.activo === 1 ? 'Activo' : 'Inactivo'}</span></div>
        `;
        document.getElementById('proveedorModal').style.display = 'flex';
    } catch (e) {
        /** Mostrar fallo al intentar cargar detalles del proveedor. @author RADJ */
        console.error(e);
        alert("Error al cargar detalles del proveedor: " + e.message);
    }
}

/** Cerrar el modal de detalles del proveedor. @author RADJ */
function closeProveedorModal() {
    document.getElementById('proveedorModal').style.display = 'none';
}

/** Generación de reportes PDF optimizados para impresión física. @author RADJ */
async function generarReporte(tipo) {
    try {
        let titulo = '';
        let headers = [];
        let rows = [];
        let resumenHtml = '';
        const nowStr = new Date().toLocaleString('es-CO');

        /** Generar reporte específico según la sección seleccionada. @author RADJ */
        if (tipo === 'inventario') {
            titulo = 'Inventario General de Productos';
            headers = ['Código de Barras', 'Nombre del Producto', 'Stock', 'P. Compra', 'P. Venta', 'Estado'];
            const data = await apiRequest('/productos') || [];

            let totalStock = 0;
            let totalValor = 0;
            let totalCosto = 0;

            /** Procesar cada producto del inventario para totales y filas. @author RADJ */
            rows = data.map(p => {
                totalStock += p.stockActual || 0;
                totalValor += (p.stockActual || 0) * (p.precioVenta || 0);
                totalCosto += (p.stockActual || 0) * (p.precioCompra || 0);
                return [
                    p.codigoBarras || 'N/A',
                    p.nombre,
                    `${p.stockActual} uds`,
                    `$${p.precioCompra?.toLocaleString('es-CO')}`,
                    `$${p.precioVenta?.toLocaleString('es-CO')}`,
                    p.estado === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /** Crear caja de resumen financiero de inventario. @author RADJ */
            resumenHtml = `
        <div class="summary-box">
            <p><strong>Total Productos:</strong> ${data.length}</p>
            <p><strong>Stock Total en Almacén:</strong> ${totalStock} unidades</p>
            <p><strong>Valoración Comercial (a P. Venta):</strong> $${totalValor.toLocaleString('es-CO')}</p>
            <p><strong>Valor Costo Total:</strong> $${totalCosto.toLocaleString('es-CO')}</p>
            <p><strong>Utilidad Neta Estimada:</strong> $${(totalValor - totalCosto).toLocaleString('es-CO')}</p>
        </div>
    `;
        } else if (tipo === 'stock-bajo') {
            titulo = 'Reporte de Productos con Stock Bajo';
            headers = ['Código de Barras', 'Nombre', 'Stock Actual', 'Stock Mínimo', 'P. Venta', 'Proveedor'];
            const data = await apiRequest('/productos') || [];
            const stockBajoData = data.filter(p => p.stockActual <= (p.stockMinimo || 5));

            /** Generar filas para productos con niveles de stock críticos. @author RADJ */
            rows = stockBajoData.map(p => {
                const prov = cacheProveedores.find(pr => pr.id === p.idProveedor);
                return [
                    p.codigoBarras || 'N/A',
                    p.nombre,
                    `<span style="color:#ef4444; font-weight:bold">${p.stockActual} uds</span>`,
                    `${p.stockMinimo || 5} uds`,
                    `$${p.precioVenta?.toLocaleString('es-CO')}`,
                    prov ? prov.nombre : 'Sin asignar'
                ];
            });

            /** Crear caja de resumen de stock crítico. @author RADJ */
            resumenHtml = `
        <div class="summary-box">
            <p><strong>Total en Stock Crítico:</strong> ${stockBajoData.length} productos</p>
        </div>
    `;
        } else if (tipo === 'clientes') {
            titulo = 'Reporte General de Clientes';
            headers = ['Nombre Completo', 'Identificación', 'Teléfono', 'Email', 'Dirección', 'Frecuente', 'Estado'];
            const data = await apiRequest('/clientes') || [];

            /** Construir filas con información detallada de cada cliente. @author RADJ */
            rows = data.map(c => {
                return [
                    c.nombre,
                    c.numeroDocumento || '—',
                    c.telefono || '—',
                    c.email || '—',
                    c.direccion || '—',
                    c.frecuente ? 'Sí' : 'No',
                    c.activo === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /** Crear caja resumen del reporte de clientes. @author RADJ */
            resumenHtml = `
        <div class="summary-box">
            <p><strong>Total Clientes Registrados:</strong> ${data.length}</p>
        </div>
    `;
        } else if (tipo === 'proveedores') {
            titulo = 'Directorio General de Proveedores';
            headers = ['Nombre / Empresa', 'NIT / Identificación', 'Teléfono', 'Email', 'Dirección', 'Cuenta Bancaria', 'Estado'];
            const data = await apiRequest('/proveedores') || [];

            /** Construir filas con información de cada proveedor. @author RADJ */
            rows = data.map(p => {
                return [
                    p.nombre,
                    p.numeroDocumento || '—',
                    p.telefono || '—',
                    p.email || '—',
                    p.direccion || '—',
                    p.cuentaBancaria || '—',
                    p.activo === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /** Crear caja resumen del reporte de proveedores. @author RADJ */
            resumenHtml = `
        <div class="summary-box">
            <p><strong>Total Proveedores Registrados:</strong> ${data.length}</p>
        </div>
    `;
        } else if (tipo === 'usuarios') {
            titulo = 'Reporte de Usuarios del Sistema';
            headers = ['Nombre Completo', 'Identificación', 'Usuario', 'Email', 'Rol', 'Estado'];
            const data = await apiRequest('/usuarios') || [];

            /** Crear filas de usuarios y sus roles asignados. @author RADJ */
            rows = data.map(u => {
                return [
                    `${u.nombre} ${u.apellido || ''}`,
                    u.numeroDocumento || '—',
                    u.usuario || '—',
                    u.email || '—',
                    u.idRol === 1 ? 'Administrador' : 'Auxiliar',
                    u.activo === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /** Crear caja de resumen de usuarios. @author RADJ */
            resumenHtml = `
        <div class="summary-box">
            <p><strong>Total Usuarios Registrados:</strong> ${data.length}</p>
        </div>
    `;
        } else if (tipo === 'resumen') {
            titulo = 'Resumen Ejecutivo de la Empresa';
            headers = ['Indicador', 'Valor / Métrica', 'Estado / Detalle'];

            /** Obtener información de todos los módulos para el resumen de alto nivel. @author RADJ */
            const productos = await apiRequest('/productos') || [];
            const clientes = await apiRequest('/clientes') || [];
            const proveedores = await apiRequest('/proveedores') || [];
            const usuarios = await apiRequest('/usuarios') || [];

            let totalProd = productos.length;
            let totalStock = 0;
            let stockBajo = 0;
            let valorInventario = 0;

            productos.forEach(p => {
                let qty = p.stockActual || 0;
                let min = p.stockMinimo || 5;
                valorInventario += qty * (p.precioVenta || 0);
                totalStock += qty;
                if (qty <= min) stockBajo++;
            });

            /** Mapear métricas clave en la tabla resumen. @author RADJ */
            rows = [
                ['Total de Productos en Catálogo', `${totalProd}`, 'Productos registrados'],
                ['Unidades de Stock Físico', `${totalStock} uds`, 'Total unidades en inventario'],
                ['Productos con Stock Bajo', `<span style="color:#ef4444; font-weight:bold">${stockBajo}</span>`, 'Requieren reabastecimiento urgente'],
                ['Valoración de Inventario', `$${valorInventario.toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`, 'En base a precios de venta comerciales'],
                ['Clientes Registrados', `${clientes.length}`, 'Base de datos de clientes'],
                ['Proveedores Registrados', `${proveedores.length}`, 'Suministradores comerciales'],
                ['Usuarios en el Sistema', `${usuarios.length}`, 'Cuentas con acceso administrativo']
            ];

            /** Crear caja de resumen ejecutivo con marca temporal. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Fecha del Resumen:</strong> ${nowStr}</p>
                    <p><strong>Estado de Operación:</strong> Operando normalmente</p>
                </div>
            `;
        } else if (tipo === 'ventas') {
            titulo = 'Reporte Histórico de Ventas';
            headers = ['ID Venta', 'Fecha / Hora', 'Cliente', 'Procesado por', 'Productos', 'Total'];
            const dataVentas = await apiRequest('/ventas') || [];
            const dataClientes = await apiRequest('/clientes') || [];
            const dataUsuarios = await apiRequest('/usuarios') || [];

            // Crear mapas para cruzar IDs con nombres
            const clientesMap = {};
            dataClientes.forEach(c => {
                clientesMap[c.id] = c.nombre;
            });

            const usuariosMap = {};
            dataUsuarios.forEach(u => {
                usuariosMap[u.id] = `${u.nombre} ${u.apellido || ''}`;
            });

            let totalVentasMonto = 0;

            // Ordenar por fecha descendente
            dataVentas.sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora));

            rows = dataVentas.map(v => {
                totalVentasMonto += v.valorTotal || 0;
                const fecha = v.fechaHora ? new Date(v.fechaHora).toLocaleString('es-CO') : '—';
                const cliente = v.idCliente ? (clientesMap[v.idCliente] || `Cliente #${v.idCliente}`) : 'Sin cliente';
                const usuario = v.idUsuario ? (usuariosMap[v.idUsuario] || `Usuario #${v.idUsuario}`) : 'Sistema';
                const nProductos = v.detalles ? v.detalles.length : 0;
                
                return [
                    `#${v.id}`,
                    fecha,
                    cliente,
                    usuario,
                    `${nProductos} producto(s)`,
                    `$${v.valorTotal?.toLocaleString('es-CO')}`
                ];
            });

            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total de Ventas Realizadas:</strong> ${dataVentas.length}</p>
                    <p><strong>Monto Total Recaudado:</strong> $${totalVentasMonto.toLocaleString('es-CO')}</p>
                </div>
            `;
        } else if (tipo === 'ganancias') {
            titulo = 'Reporte de Ganancias y Rentabilidad';
            headers = ['ID Venta', 'Fecha / Hora', 'Ingreso (Venta)', 'Costo total', 'Ganancia Neta', 'Margen %'];
            const dataVentas = await apiRequest('/ventas') || [];
            const dataProductos = await apiRequest('/productos') || [];

            // Map productos por ID para obtener precios de compra
            const prodMap = {};
            dataProductos.forEach(p => {
                prodMap[p.id] = p;
            });

            let globalIngresos = 0;
            let globalCostos = 0;

            // Ordenar por fecha descendente
            dataVentas.sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora));

            rows = dataVentas.map(v => {
                const fecha = v.fechaHora ? new Date(v.fechaHora).toLocaleString('es-CO') : '—';
                const ingreso = v.valorTotal || 0;
                globalIngresos += ingreso;

                let costoVenta = 0;
                if (v.detalles) {
                    v.detalles.forEach(d => {
                        const prod = prodMap[d.idProducto];
                        const precioCompra = prod ? (prod.precioCompra || 0) : 0;
                        costoVenta += (d.cantidad || 0) * precioCompra;
                    });
                }
                globalCostos += costoVenta;

                const ganancia = ingreso - costoVenta;
                const margen = ingreso > 0 ? ((ganancia / ingreso) * 100).toFixed(1) + '%' : '0%';

                return [
                    `#${v.id}`,
                    fecha,
                    `$${ingreso.toLocaleString('es-CO')}`,
                    `$${costoVenta.toLocaleString('es-CO')}`,
                    `<span style="color:${ganancia >= 0 ? '#10b981' : '#ef4444'}; font-weight:bold">$${ganancia.toLocaleString('es-CO')}</span>`,
                    margen
                ];
            });

            const globalGanancia = globalIngresos - globalCostos;
            const globalMargen = globalIngresos > 0 ? ((globalGanancia / globalIngresos) * 100).toFixed(1) + '%' : '0%';

            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total Ingresos (Ventas):</strong> $${globalIngresos.toLocaleString('es-CO')}</p>
                    <p><strong>Total Costo de Ventas:</strong> $${globalCostos.toLocaleString('es-CO')}</p>
                    <p><strong>Ganancia Neta Total:</strong> <span style="color:${globalGanancia >= 0 ? '#10b981' : '#ef4444'}; font-weight:bold">$${globalGanancia.toLocaleString('es-CO')}</span></p>
                    <p><strong>Margen de Ganancia Promedio:</strong> ${globalMargen}</p>
                </div>
            `;
        }

        /** Crear y poblar ventana de impresión para el reporte. @author RADJ */
        const win = window.open('', '_blank');
        if (!win) {
            alert("Por favor habilite los popups en su navegador para generar reportes.");
            return;
        }

        /** Código HTML dinámico de la página de impresión. @author RADJ */
        const html = `
    <!DOCTYPE html>
    <html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Reporte - ${titulo}</title>
        <style>
            body {
                font-family: 'Segoe UI', system-ui, sans-serif;
                color: #1e293b;
                background: #ffffff;
                margin: 0;
                padding: 2rem;
            }
            .header {
                border-bottom: 2px solid #0f172a;
                padding-bottom: 1rem;
                margin-bottom: 2rem;
                display: flex;
                justify-content: space-between;
                align-items: flex-end;
            }
            .header h1 {
                margin: 0 0 0.5rem 0;
                font-size: 1.8rem;
                color: #0f172a;
            }
            .header p {
                margin: 0;
                color: #64748b;
                font-size: 0.9rem;
            }
            .meta-info {
                text-align: right;
                font-size: 0.85rem;
                color: #64748b;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 2rem;
            }
            th {
                background: #0f172a;
                color: #ffffff;
                text-align: left;
                padding: 0.75rem 1rem;
                font-size: 0.85rem;
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }
            td {
                padding: 0.75rem 1rem;
                border-bottom: 1px solid #e2e8f0;
                font-size: 0.9rem;
            }
            tr:nth-child(even) td {
                background: #f8fafc;
            }
            .summary-box {
                background: #f1f5f9;
                border: 1px solid #e2e8f0;
                border-radius: 0.5rem;
                padding: 1.25rem;
                margin-top: 2rem;
                display: inline-block;
                min-width: 320px;
            }
            .summary-box p {
                margin: 0.35rem 0;
                font-size: 0.95rem;
            }
            @media print {
                body { padding: 0; }
                .no-print { display: none; }
            }
        </style>
    </head>
    <body>
        <div class="no-print" style="margin-bottom: 1.5rem; display: flex; gap: 0.75rem;">
            <button onclick="window.print()" style="padding: 0.6rem 1.2rem; background: #0f172a; color: white; border: none; border-radius: 0.375rem; cursor: pointer; font-weight: 600;">Imprimir / Guardar PDF</button>
            <button onclick="window.close()" style="padding: 0.6rem 1.2rem; background: #e2e8f0; color: #1e293b; border: none; border-radius: 0.375rem; cursor: pointer; font-weight: 600;">Cerrar</button>
        </div>
        <div class="header">
            <div>
                <h1>${titulo}</h1>
                <p>AcaciosWork — Sistema de Control Administrativo</p>
            </div>
            <div class="meta-info">
                <p><strong>Fecha de Generación:</strong> ${nowStr}</p>
                <p><strong>Generado por:</strong> ${localStorage.getItem('user_name') || 'Administrador'}</p>
            </div>
        </div>
        
        <table>
            <thead>
                <tr>
                    ${headers.map(h => `<th>${h}</th>`).join('')}
                </tr>
            </thead>
            <tbody>
                ${rows.map(row => `
                    <tr>
                        ${row.map(cell => `<td>${cell}</td>`).join('')}
                    </tr>
                `).join('')}
            </tbody>
        </table>
        
        ${resumenHtml}
        
        <script>
            window.onload = function() {
                setTimeout(function() {
                    window.print();
                }, 500);
            };
        <\/script>
    </body>
    </html>
`;

        /** Escribir el documento HTML en la nueva ventana. @author RADJ */
        win.document.open();
        win.document.write(html);
        win.document.close();
    } catch (e) {
        /** Notificar fallos en la generación del PDF. @author RADJ */
        console.error("Error al generar reporte:", e);
        alert("Error al generar el reporte: " + e.message);
    }
}

/** Obtiene los campos del formulario dinámico según el módulo. @author RADJ */
function getModalFields(type) {
    /** Retornar campos HTML para el formulario de Inventario / Producto. @author RADJ */
    if (type === 'inventario') {
        const catOpts = cacheCategorias.map(c => `<option value="${c.id}">${c.nombre}</option>`).join('');
        const provOpts = cacheProveedores.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
        return `
    <label>Código de Barras</label>
    <input id="prod-codigoBarras" placeholder="Ej: 7701234" required>
    
    <label>Nombre del Producto</label>
    <input id="prod-nombre" placeholder="Nombre del producto" required>
    
    <label>Unidad de Medida</label>
    <input id="prod-unidadMedida" placeholder="Ej: Unidad, Kilo, Litro" required>
    
    <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:0.75rem">
        <div>
            <label>Stock / Actual</label>
            <input id="prod-stockActual" type="number" value="0" min="0" required>
        </div>
        <div>
            <label>Stock Mínimo</label>
            <input id="prod-stockMinimo" type="number" value="5" min="0" required>
        </div>
        <div>
            <label>Stock Óptimo</label>
            <input id="prod-stockOptimo" type="number" value="200" min="0" required>
        </div>
    </div>
    
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem">
        <div>
            <label>Precio Compra</label>
            <input id="prod-precioCompra" type="number" step="0.01" value="0" required>
        </div>
        <div>
            <label>Precio Venta</label>
            <input id="prod-precioVenta" type="number" step="0.01" value="0" required>
        </div>
    </div>
    
    <label>Categoría</label>
    <select id="prod-idCategoria" required>
        <option value="">Seleccione una categoría</option>
        ${catOpts}
    </select>
    
    <label>Proveedor</label>
    <select id="prod-idProveedor" required>
        <option value="">Seleccione un proveedor</option>
        ${provOpts}
    </select>
    
    <label>IVA (%)</label>
    <input id="prod-iva" type="number" step="0.01" value="19" required>
    
    <label>Estado</label>
    <select id="prod-estado">
        <option value="1">Activo</option>
        <option value="0">Inactivo</option>
    </select>
`;
    }

    /** Retornar campos HTML para el formulario de Proveedor. @author RADJ */
    if (type === 'proveedor') {
        const tdOpts = cacheTiposDocumento.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
        return `
    <label>Nombre / Razón Social</label>
    <input id="prov-nombre" placeholder="Nombre de la empresa" required>
    
    <div style="display:grid;grid-template-columns:1fr 2fr;gap:0.75rem">
        <div>
            <label>Tipo Doc.</label>
            <select id="prov-idTipoDocumento" required>
                ${tdOpts}
            </select>
        </div>
        <div>
            <label>Número de Documento</label>
            <input id="prov-numeroDocumento" placeholder="NIT / Cédula" required>
        </div>
    </div>
    
    <label>Teléfono</label>
    <input id="prov-telefono" placeholder="Teléfono de contacto">
    
    <label>Email</label>
    <input id="prov-email" type="email" placeholder="correo@empresa.com">
    
    <label>Dirección (Ciudad/Dirección)</label>
    <input id="prov-direccion" placeholder="Dirección física">
    
    <label>Cuenta Bancaria</label>
    <input id="prov-cuentaBancaria" placeholder="Ej: Ahorros Bancolombia No. 123...">
    
    <label>Estado</label>
    <select id="prov-activo">
        <option value="1">Activo</option>
        <option value="0">Inactivo</option>
    </select>
`;
    }

    /** Retornar campos HTML para el formulario de Cliente. @author RADJ */
    if (type === 'cliente') {
        const tdOpts = cacheTiposDocumento.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
        return `
    <label>Nombre Completo</label>
    <input id="cli-nombre" placeholder="Nombre del cliente" required>
    
    <div style="display:grid;grid-template-columns:1fr 2fr;gap:0.75rem">
        <div>
            <label>Tipo Doc.</label>
            <select id="cli-idTipoDocumento" required>
                ${tdOpts}
            </select>
        </div>
        <div>
            <label>Número de Documento</label>
            <input id="cli-numeroDocumento" placeholder="Cédula / NIT" required>
        </div>
    </div>
    
    <label>Teléfono</label>
    <input id="cli-telefono" placeholder="Teléfono">
    
    <label>Email</label>
    <input id="cli-email" type="email" placeholder="correo@ejemplo.com">
    
    <label>Dirección</label>
    <input id="cli-direccion" placeholder="Dirección física">
    
    <label>Cliente Frecuente</label>
    <select id="cli-frecuente">
        <option value="false">No</option>
        <option value="true">Sí</option>
    </select>
    
    <label>Estado</label>
    <select id="cli-activo">
        <option value="1">Activo</option>
        <option value="0">Inactivo</option>
    </select>
`;
    }

    /** Retornar campos HTML para el formulario de Usuario. @author RADJ */
    if (type === 'usuario') {
        const tdOpts = cacheTiposDocumento.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
        const rolOpts = cacheRoles.map(r => `<option value="${r.id}">${r.nombre}</option>`).join('');
        return `
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem">
        <div>
            <label>Nombre</label>
            <input id="usr-nombre" placeholder="Nombre" required>
        </div>
        <div>
            <label>Apellido</label>
            <input id="usr-apellido" placeholder="Apellido" required>
        </div>
    </div>
    
    <div style="display:grid;grid-template-columns:1fr 2fr;gap:0.75rem">
        <div>
            <label>Tipo Doc.</label>
            <select id="usr-idTipoDocumento" required>
                ${tdOpts}
            </select>
        </div>
        <div>
            <label>Número de Documento</label>
            <input id="usr-numeroDocumento" placeholder="Número de Documento" required>
        </div>
    </div>
    
    <label>Teléfono</label>
    <input id="usr-telefono" placeholder="Teléfono">
    
    <label>Email</label>
    <input id="usr-email" type="email" placeholder="correo@ejemplo.com" required>
    
    <label>Nombre de Usuario</label>
    <input id="usr-usuario" placeholder="Nombre de usuario" required>
    
    <label>Contraseña</label>
    <input id="usr-clave" type="password" placeholder="Contraseña" required>
    
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem">
        <div>
            <label>Rol</label>
            <select id="usr-idRol" required>
                ${rolOpts}
            </select>
        </div>
        <div>
            <label>Estado</label>
            <select id="usr-activo" required>
                <option value="1">Activo</option>
                <option value="0">Inactivo</option>
            </select>
        </div>
    </div>
`;
    }
    return '';
}

/** Abre el modal de creación o edición con los datos correspondientes. @author RADJ */
async function openModal(type, id = null) {
    currentModalType = type;
    editId = id;

    /** Asegurar que las referencias estén cargadas antes de dibujar el modal. @author RADJ */
    if (!cacheCategorias.length || !cacheProveedores.length) {
        await loadReferences();
    }

    /** Definir el título dinámico del modal según acción. @author RADJ */
    document.getElementById('modal-title').textContent = (editId ? 'Editar ' : 'Nuevo ') + {
        inventario: 'Producto',
        proveedor: 'Proveedor',
        cliente: 'Cliente',
        usuario: 'Usuario'
    }[type];

    /** Inyectar campos HTML dinámicos en el modal. @author RADJ */
    const fieldsContainer = document.getElementById('modal-fields');
    fieldsContainer.innerHTML = getModalFields(type);

    /** Si estamos editando, cargar los datos actuales desde la API. @author RADJ */
    if (editId) {
        try {
            let data = null;
            /** Consultar datos al endpoint adecuado según tipo de formulario. @author RADJ */
            if (type === 'inventario') {
                data = await apiRequest(`/productos/${editId}`);
            } else if (type === 'proveedor') {
                data = await apiRequest(`/proveedores/${editId}`);
            } else if (type === 'cliente') {
                data = await apiRequest(`/clientes/${editId}`);
            } else if (type === 'usuario') {
                const usuarios = await apiRequest('/usuarios') || [];
                data = usuarios.find(u => u.numeroDocumento === editId);
            }

            /** Rellenar formulario si se obtuvieron los datos. @author RADJ */
            if (data) {
                populateForm(type, data);
            }
        } catch (e) {
            /** Controlar errores en carga para edición y cerrar modal. @author RADJ */
            console.error("Error al cargar datos en modal para edición:", e);
            alert("No se pudieron obtener los datos para editar.");
            closeModal();
            return;
        }
    }

    /** Mostrar modal aplicando estilo flex. @author RADJ */
    document.getElementById('mainModal').style.display = 'flex';
}

/** Cerrar el modal principal de formularios. @author RADJ */
function closeModal() {
    document.getElementById('mainModal').style.display = 'none';
}

/** Rellena los campos del modal al editar un elemento existente. @author RADJ */
function populateForm(type, data) {
    /** Poblar campos del formulario de Inventario. @author RADJ */
    if (type === 'inventario') {
        document.getElementById('prod-codigoBarras').value = data.codigoBarras || '';
        document.getElementById('prod-nombre').value = data.nombre || '';
        document.getElementById('prod-stockActual').value = data.stockActual !== undefined ? data.stockActual : 0;
        document.getElementById('prod-stockMinimo').value = data.stockMinimo !== undefined ? data.stockMinimo : 5;
        document.getElementById('prod-stockOptimo').value = data.stockOptimo !== undefined ? data.stockOptimo : 200;
        document.getElementById('prod-precioCompra').value = data.precioCompra || 0;
        document.getElementById('prod-precioVenta').value = data.precioVenta || 0;
        document.getElementById('prod-idCategoria').value = data.idCategoria || '';
        document.getElementById('prod-idProveedor').value = data.idProveedor || '';
        document.getElementById('prod-estado').value = data.estado !== undefined ? data.estado : 1;
        document.getElementById('prod-iva').value = data.iva !== undefined ? data.iva : 19;
        document.getElementById('prod-unidadMedida').value = data.unidadMedida || '';
    }
    /** Poblar campos del formulario de Proveedor. @author RADJ */
    else if (type === 'proveedor') {
        document.getElementById('prov-nombre').value = data.nombre || '';
        document.getElementById('prov-idTipoDocumento').value = data.idTipoDocumento || '';
        document.getElementById('prov-numeroDocumento').value = data.numeroDocumento || '';
        document.getElementById('prov-telefono').value = data.telefono || '';
        document.getElementById('prov-email').value = data.email || '';
        document.getElementById('prov-direccion').value = data.direccion || '';
        document.getElementById('prov-cuentaBancaria').value = data.cuentaBancaria || '';
        document.getElementById('prov-activo').value = data.activo !== undefined ? data.activo : 1;
    }
    /** Poblar campos del formulario de Cliente. @author RADJ */
    else if (type === 'cliente') {
        document.getElementById('cli-nombre').value = data.nombre || '';
        document.getElementById('cli-idTipoDocumento').value = data.idTipoDocumento || '';
        document.getElementById('cli-numeroDocumento').value = data.numeroDocumento || '';
        document.getElementById('cli-telefono').value = data.telefono || '';
        document.getElementById('cli-email').value = data.email || '';
        document.getElementById('cli-direccion').value = data.direccion || '';
        document.getElementById('cli-frecuente').value = data.frecuente ? 'true' : 'false';
        document.getElementById('cli-activo').value = data.activo !== undefined ? data.activo : 1;
    }
    /** Poblar campos del formulario de Usuario. @author RADJ */
    else if (type === 'usuario') {
        document.getElementById('usr-nombre').value = data.nombre || '';
        document.getElementById('usr-apellido').value = data.apellido || '';
        document.getElementById('usr-idTipoDocumento').value = data.idTipoDocumento || '';

        const docInput = document.getElementById('usr-numeroDocumento');
        docInput.value = data.numeroDocumento || '';
        docInput.disabled = true; /** Bloqueado en edición para mantener consistencia. @author RADJ */

        document.getElementById('usr-telefono').value = data.telefono || '';
        document.getElementById('usr-email').value = data.email || '';
        document.getElementById('usr-usuario').value = data.usuario || '';

        const claveInput = document.getElementById('usr-clave');
        claveInput.required = false;
        claveInput.placeholder = "Dejar en blanco para conservar actual";

        document.getElementById('usr-idRol').value = data.idRol || '';
        document.getElementById('usr-activo').value = data.activo !== undefined ? data.activo : 1;
    }
}

/** Construye un objeto JSON a partir de los datos ingresados en el formulario dinámico. @author RADJ */
function getFormData(type) {
    /** Retornar objeto de datos para un Producto. @author RADJ */
    if (type === 'inventario') {
        return {
            codigoBarras: document.getElementById('prod-codigoBarras').value,
            nombre: document.getElementById('prod-nombre').value,
            stockActual: parseInt(document.getElementById('prod-stockActual').value) || 0,
            stockMinimo: parseInt(document.getElementById('prod-stockMinimo').value) || 5,
            stockOptimo: parseInt(document.getElementById('prod-stockOptimo').value) || 200,
            precioCompra: parseFloat(document.getElementById('prod-precioCompra').value) || 0,
            precioVenta: parseFloat(document.getElementById('prod-precioVenta').value) || 0,
            idCategoria: parseInt(document.getElementById('prod-idCategoria').value) || null,
            idProveedor: parseInt(document.getElementById('prod-idProveedor').value) || null,
            estado: parseInt(document.getElementById('prod-estado').value),
            iva: parseFloat(document.getElementById('prod-iva').value) || 19,
            unidadMedida: document.getElementById('prod-unidadMedida').value || 'Unidad'
        };
    }

    /** Retornar objeto de datos para un Proveedor. @author RADJ */
    if (type === 'proveedor') {
        return {
            nombre: document.getElementById('prov-nombre').value,
            idTipoDocumento: parseInt(document.getElementById('prov-idTipoDocumento').value) || null,
            numeroDocumento: document.getElementById('prov-numeroDocumento').value,
            telefono: document.getElementById('prov-telefono').value,
            email: document.getElementById('prov-email').value,
            direccion: document.getElementById('prov-direccion').value,
            cuentaBancaria: document.getElementById('prov-cuentaBancaria').value,
            activo: parseInt(document.getElementById('prov-activo').value)
        };
    }

    /** Retornar objeto de datos para un Cliente. @author RADJ */
    if (type === 'cliente') {
        return {
            nombre: document.getElementById('cli-nombre').value,
            idTipoDocumento: parseInt(document.getElementById('cli-idTipoDocumento').value) || null,
            numeroDocumento: document.getElementById('cli-numeroDocumento').value,
            telefono: document.getElementById('cli-telefono').value,
            email: document.getElementById('cli-email').value,
            direccion: document.getElementById('cli-direccion').value,
            frecuente: document.getElementById('cli-frecuente').value === 'true',
            activo: parseInt(document.getElementById('cli-activo').value)
        };
    }

    /** Retornar objeto de datos para un Usuario. @author RADJ */
    if (type === 'usuario') {
        const u = {
            nombre: document.getElementById('usr-nombre').value,
            apellido: document.getElementById('usr-apellido').value,
            numeroDocumento: document.getElementById('usr-numeroDocumento').value,
            idTipoDocumento: parseInt(document.getElementById('usr-idTipoDocumento').value) || null,
            telefono: document.getElementById('usr-telefono').value,
            email: document.getElementById('usr-email').value,
            usuario: document.getElementById('usr-usuario').value,
            idRol: parseInt(document.getElementById('usr-idRol').value) || null,
            activo: parseInt(document.getElementById('usr-activo').value)
        };
        const clave = document.getElementById('usr-clave').value;
        if (clave) {
            u.clave = clave;
        }
        return u;
    }

    return null;
}

/** Controlador de evento submit para registrar nuevas inserciones o actualizaciones (POST / PUT). @author RADJ */
document.getElementById('modal-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const type = currentModalType;
    const data = getFormData(type);

    if (!data) return;

    try {
        let endpoint = '';
        let method = editId ? 'PUT' : 'POST';

        /** Definir endpoint en función del tipo de módulo. @author RADJ */
        if (type === 'inventario') endpoint = editId ? `/productos/${editId}` : '/productos';
        else if (type === 'proveedor') endpoint = editId ? `/proveedores/${editId}` : '/proveedores';
        else if (type === 'cliente') endpoint = editId ? `/clientes/${editId}` : '/clientes';
        else if (type === 'usuario') endpoint = editId ? `/usuarios/${editId}` : '/usuarios';

        /** Enviar petición fetch para guardar cambios. @author RADJ */
        await apiRequest(endpoint, method, data);
        closeModal();

        /** Recargar vistas correspondientes y re-mapear referencias de proveedor/categoria por si variaron. @author RADJ */
        await loadReferences();

        /** Recargar la sección activa específica. @author RADJ */
        if (type === 'inventario') loadInventario();
        else if (type === 'proveedor') loadProveedores();
        else if (type === 'cliente') loadClientes();
        else if (type === 'usuario') loadUsuarios();

        alert((editId ? 'Registro actualizado' : 'Registro creado') + ' exitosamente.');
    } catch (e) {
        /** Capturar y reportar fallos en el proceso de guardado. @author RADJ */
        console.error("Error al persistir registro:", e);
        alert("Error al guardar: " + e.message);
    }
});

/** ─── SECCIÓN POS / VENTAS DE ADMINISTRADOR ─── */

/** Carga e inicializa la sección de Ventas. @author RADJ */
async function loadVenderSection() {
    try {
        allProducts = await apiRequest('/productos') || [];
    } catch (e) {
        console.error('Error al precargar productos:', e);
    }

    try {
        allClientes = await apiRequest('/clientes') || [];
        loadClienteSelect();
    } catch (e) {
        console.error('Error al cargar clientes:', e);
    }

    limpiarCarrito();
}

/** Carga los clientes en el selector. @author RADJ */
function loadClienteSelect() {
    const select = document.getElementById('client-select');
    if (!select) return;
    select.innerHTML = '<option value="">— Venta sin cliente registrado —</option>';
    allClientes.forEach(c => {
        if (c.activo === 1) {
            select.innerHTML += `<option value="${c.id}">${c.nombre} (${c.numeroDocumento || 'Sin doc'})</option>`;
        }
    });
}

/** Búsqueda de productos en tiempo real. @author RADJ */
function searchProducts(query) {
    clearTimeout(searchTimeout);
    const dropdown = document.getElementById('product-dropdown');
    if (!dropdown) return;
    if (!query || query.length < 1) { dropdown.style.display = 'none'; return; }

    searchTimeout = setTimeout(() => {
        const q = query.toLowerCase();
        const results = allProducts.filter(p =>
            (p.nombre && p.nombre.toLowerCase().includes(q)) ||
            (p.codigoBarras && p.codigoBarras.toLowerCase().includes(q))
        ).slice(0, 12);

        if (!results.length) {
            dropdown.innerHTML = '<div class="product-dropdown-item" style="color:var(--text-muted); cursor:default;">Sin resultados</div>';
        } else {
            dropdown.innerHTML = results.map(p => {
                const sinStock = (p.stockActual || 0) <= 0;
                return `<div class="product-dropdown-item ${sinStock ? 'no-stock' : ''}"
                    onclick="${sinStock ? '' : `addToCart(${p.id})`}">
                    <div>
                        <div class="p-name">${p.nombre}</div>
                        <div class="p-meta">Stock: ${p.stockActual || 0} uds${sinStock ? ' — Sin stock' : ''}</div>
                    </div>
                    <div class="p-price">${formatCurrency(p.precioVenta)}</div>
                </div>`;
            }).join('');
        }
        dropdown.style.display = 'block';
    }, 180);
}

// Cerrar dropdown al hacer click fuera
document.addEventListener('click', e => {
    const searchWrapper = e.target.closest('.pos-search-wrapper');
    if (!searchWrapper) {
        const dropdown = document.getElementById('product-dropdown');
        if (dropdown) dropdown.style.display = 'none';
    }
});

/** Formatear moneda a pesos colombianos. @author RADJ */
function formatCurrency(n) {
    return '$' + Number(n).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

/** Mostrar Toast Notification. @author RADJ */
function showToast(msg, type = 'success') {
    const t = document.getElementById('toast');
    if (!t) return;
    t.textContent = msg;
    t.className = `toast ${type} show`;
    setTimeout(() => t.classList.remove('show'), 3500);
}

/** Agregar un producto al carrito. @author RADJ */
function addToCart(productId) {
    const producto = allProducts.find(p => p.id === productId);
    if (!producto) return;

    const searchInput = document.getElementById('product-search');
    if (searchInput) searchInput.value = '';
    const dropdown = document.getElementById('product-dropdown');
    if (dropdown) dropdown.style.display = 'none';

    const existing = cart.find(item => item.producto.id === productId);
    if (existing) {
        if (existing.cantidad >= (producto.stockActual || 0)) {
            showToast('No hay más stock disponible para este producto.', 'error');
            return;
        }
        existing.cantidad++;
    } else {
        cart.push({ producto, cantidad: 1 });
    }
    renderCart();
}

/** Quitar producto del carrito. @author RADJ */
function removeFromCart(productId) {
    cart = cart.filter(item => item.producto.id !== productId);
    renderCart();
}

/** Cambiar cantidad del producto en el carrito. @author RADJ */
function updateQuantity(productId, newQty) {
    const item = cart.find(i => i.producto.id === productId);
    if (!item) return;
    const qty = parseInt(newQty) || 1;
    const maxStock = item.producto.stockActual || 0;
    item.cantidad = Math.max(1, Math.min(qty, maxStock));
    renderCart();
}

/** Limpiar todos los elementos del carrito. @author RADJ */
function limpiarCarrito() {
    cart = [];
    renderCart();
}

/** Renderizar la tabla del carrito. @author RADJ */
function renderCart() {
    const tbody = document.getElementById('cart-tbody');
    if (!tbody) return;

    if (!cart.length) {
        tbody.innerHTML = `<tr><td colspan="5"><div class="cart-empty-msg">🛒 El carrito está vacío.<br><span style="font-size:0.78rem;">Busca y agrega productos arriba.</span></div></td></tr>`;
        updateSummary(0, 0);
        const btn = document.getElementById('btn-registrar');
        if (btn) btn.disabled = true;
        return;
    }

    let totalItems = 0;
    let totalCost = 0;
    tbody.innerHTML = cart.map(item => {
        const subtotal = item.cantidad * item.producto.precioVenta;
        totalItems += item.cantidad;
        totalCost += subtotal;
        return `<tr>
            <td style="font-weight:500; max-width:180px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;" title="${item.producto.nombre}">${item.producto.nombre}</td>
            <td style="text-align:center;">
                <input type="number" class="qty-input" value="${item.cantidad}" min="1" max="${item.producto.stockActual || 1}"
                    onchange="updateQuantity(${item.producto.id}, this.value)" oninput="updateQuantity(${item.producto.id}, this.value)">
            </td>
            <td style="text-align:right; color:var(--text-muted);">${formatCurrency(item.producto.precioVenta)}</td>
            <td style="text-align:right; font-weight:600; color:#10b981;">${formatCurrency(subtotal)}</td>
            <td style="text-align:center;">
                <button class="remove-item-btn" onclick="removeFromCart(${item.producto.id})" title="Quitar">✕</button>
            </td>
        </tr>`;
    }).join('');

    updateSummary(totalItems, totalCost);
    const btn = document.getElementById('btn-registrar');
    if (btn) btn.disabled = false;
}

/** Actualizar resumen financiero. @author RADJ */
function updateSummary(items, total) {
    const summaryItems = document.getElementById('summary-items');
    const summarySubtotal = document.getElementById('summary-subtotal');
    const summaryTotal = document.getElementById('summary-total');

    if (summaryItems) summaryItems.textContent = items;
    if (summarySubtotal) summarySubtotal.textContent = formatCurrency(total);
    if (summaryTotal) summaryTotal.textContent = formatCurrency(total);
}

/** Registrar la venta en la base de datos. @author RADJ */
async function registrarVenta() {
    if (!cart.length) return;
    const btn = document.getElementById('btn-registrar');
    if (!btn) return;
    btn.disabled = true;
    btn.textContent = '⏳ Registrando...';

    // Obtener id de usuario administrador logueado
    let idUsuario = null;
    const usuarioRaw = localStorage.getItem('usuario');
    if (usuarioRaw) {
        try {
            idUsuario = JSON.parse(usuarioRaw).id;
        } catch (e) {
            console.error('Error al parsear usuario de localStorage:', e);
        }
    }

    const clienteIdRaw = document.getElementById('client-select').value;
    const idCliente = clienteIdRaw ? parseInt(clienteIdRaw) : null;

    const detalles = cart.map(item => ({
        idProducto: item.producto.id,
        cantidad: item.cantidad,
        precioUnitario: item.producto.precioVenta
    }));

    try {
        await apiRequest('/ventas', 'POST', { idUsuario, idCliente, detalles });
        showToast('✅ Venta registrada con éxito', 'success');
        limpiarCarrito();
        // Recargar productos para reflejar los nuevos stocks
        allProducts = await apiRequest('/productos') || [];
        const clientSelect = document.getElementById('client-select');
        if (clientSelect) clientSelect.value = '';
    } catch (e) {
        showToast('❌ Error al registrar: ' + e.message, 'error');
    } finally {
        btn.textContent = '✅ Registrar Venta';
        btn.disabled = cart.length === 0;
    }
}

