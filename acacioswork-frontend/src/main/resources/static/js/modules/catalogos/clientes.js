/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** clientes.js - lógica de negocio y renderizado para el catálogo de clientes. @author RADJ */

/*** carga y visualización de clientes del sistema. @author RADJ */
window.loadClientes = async function() {
    /*** limpiar campo de búsqueda de clientes. @author RADJ */
    const searchInput = document.getElementById('cli-search-input');
    if (searchInput) searchInput.value = '';
    /*** obtener cuerpo de la tabla de clientes. @author RADJ */
    const tbody = document.getElementById('cli-tbody');
    try {
        /*** obtener la lista de clientes registrados en el sistema. @author RADJ */
        const data = await apiRequest('/clientes') || [];
        AppState.allClientes = data;
        
        /*** actualizar contadores de total y activos en cabecera. @author RADJ */
        const totalEl = document.getElementById('cli-total');
        if (totalEl) totalEl.textContent = data.length;

        const activosEl = document.getElementById('cli-activos');
        if (activosEl) activosEl.textContent = data.filter(c => c.activo === 1).length;

        /*** dibujar filas de clientes en la tabla. @author RADJ */
        if (tbody) {
            window.setupTablePagination({
                tbodyId: 'cli-tbody',
                allItems: data,
                renderRowFn: (c) => `
                <tr>
                    <td style="font-weight:500">${c.nombre}</td>
                    <td style="font-family:monospace;font-size:0.82rem">${c.numeroDocumento || '—'}</td>
                    <td>${c.telefono || '—'}</td>
                    <td>${c.email || '—'}</td>
                    <td style="display:flex;gap:0.4rem">
                        <button class="btn-sm" onclick="openModal('cliente', ${c.id})">Editar</button>
                        <button class="btn-sm btn-del" onclick="deleteCliente(${c.id})">Borrar</button>
                    </td>
                </tr>`
            });
        }
    } catch (e) {
        /*** mostrar mensaje de error si no se pueden cargar los clientes. @author RADJ */
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
        }
    }
};

/*** solicitar confirmación y eliminar un cliente del sistema. @author RADJ */
window.deleteCliente = async function(id) {
    /*** confirmación nativa ante el usuario. @author RADJ */
    if (!confirm('¿Eliminar este cliente?')) return;
    try {
        /*** realizar petición delete al endpoint de clientes. @author RADJ */
        await apiRequest(`/clientes/${id}`, 'DELETE');
        /*** actualizar visualización de la lista de clientes. @author RADJ */
        loadClientes();
        alert('Cliente eliminado con éxito.');
    } catch (e) {
        /*** alertar al usuario ante fallos de eliminación. @author RADJ */
        alert('Error al eliminar cliente: ' + e.message);
    }
};
