/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** proveedores.js - lógica de negocio y renderizado para el catálogo de proveedores. @author RADJ */

/*** carga y visualización de proveedores. @author RADJ */
window.loadProveedores = async function() {
    /*** limpiar campo de filtrado para proveedores. @author RADJ */
    const searchInput = document.getElementById('prov-search-input');
    if (searchInput) searchInput.value = '';
    /*** obtener la referencia de la tabla de proveedores. @author RADJ */
    const tbody = document.getElementById('prov-tbody');
    try {
        /*** solicitar listado de proveedores a la api. @author RADJ */
        const data = await apiRequest('/proveedores') || [];
        
        /*** actualizar caché local. @author RADJ */
        AppState.cache.proveedores = data;

        /*** generar las filas de la tabla para cada proveedor. @author RADJ */
        if (tbody) {
            window.setupTablePagination({
                tbodyId: 'prov-tbody',
                allItems: data,
                renderRowFn: (p) => `
                <tr>
                    <td style="font-weight:500">${p.nombre}</td>
                    <td>${p.telefono || '—'}</td>
                    <td>${p.email || '—'}</td>
                    <td>${p.direccion || '—'}</td>
                    <td style="display:flex;gap:0.4rem">
                        <button class="btn-sm" onclick="openModal('proveedor', ${p.id})">Editar</button>
                        <button class="btn-sm btn-del" onclick="deleteProveedor(${p.id})">Borrar</button>
                    </td>
                </tr>`
            });
        }
    } catch (e) {
        /*** mostrar fila de error si falla la consulta de proveedores. @author RADJ */
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
        }
    }
};

/*** solicitar confirmación y eliminar un proveedor del sistema. @author RADJ */
window.deleteProveedor = async function(id) {
    /*** solicitar confirmación al usuario antes de la baja. @author RADJ */
    if (!confirm('¿Eliminar este proveedor?')) return;
    try {
        /*** enviar petición http delete al endpoint de proveedores. @author RADJ */
        await apiRequest(`/proveedores/${id}`, 'DELETE');
        /*** recargar la sección de proveedores. @author RADJ */
        loadProveedores();
        alert('Proveedor eliminado con éxito.');
    } catch (e) {
        /*** alertar al usuario sobre el fallo en la eliminación. @author RADJ */
        alert('Error al eliminar proveedor: ' + e.message);
    }
};
