/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** usuarios.js - lógica de negocio y renderizado para la administración de usuarios. @author RADJ */

/*** carga y visualización de usuarios. @author RADJ */
window.loadUsuarios = async function() {
    /*** limpiar campo de búsqueda de usuarios. @author RADJ */
    const searchInput = document.getElementById('usr-search-input');
    if (searchInput) searchInput.value = '';
    try {
        /*** realizar solicitud get a usuarios. @author RADJ */
        const data = await apiRequest('/usuarios') || [];
        AppState.cache.usuarios = data;

        window.renderUsuarios(data);
    } catch (e) {
        const tbody = document.getElementById('usr-tbody');
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
        }
    }
};

/*** renderiza la tabla de usuarios usando paginación infinita. @author RADJ */
window.renderUsuarios = function(data) {
    const tbody = document.getElementById('usr-tbody');
    if (tbody) {
        window.setupTablePagination({
            tbodyId: 'usr-tbody',
            allItems: data,
            renderRowFn: (u) => `
            <tr>
                <td style="font-weight:500">${u.nombre} ${u.apellido || ''}</td>
                <td>${u.usuario || '—'}</td>
                <td><span class="badge ${u.idRol === 1 ? 'badge-warn' : 'badge-success'}">${u.idRol === 1 ? 'Administrador' : 'Auxiliar'}</span></td>
                <td><span class="badge ${u.activo === 1 ? 'badge-success' : 'badge-danger'}">${u.activo === 1 ? 'Activo' : 'Inactivo'}</span></td>
                <td style="display:flex;gap:0.4rem">
                    <button class="btn-sm" onclick="openModal('usuario', '${u.numeroDocumento}')">Editar</button>
                    <button class="btn-sm btn-del" onclick="deleteUsuario('${u.numeroDocumento}')">Borrar</button>
                </td>
            </tr>`
        });
    }
};

/*** solicitar confirmación y eliminar un usuario por su documento. @author RADJ */
window.deleteUsuario = async function(numeroDocumento) {
    /*** solicitar confirmación para dar de baja al usuario. @author RADJ */
    if (!confirm('¿Eliminar este usuario del sistema?')) return;
    try {
        /*** realizar petición http delete por documento de usuario. @author RADJ */
        await apiRequest(`/usuarios/${numeroDocumento}`, 'DELETE');
        /*** recargar la sección de listado de usuarios. @author RADJ */
        loadUsuarios();
        alert('Usuario eliminado con éxito.');
    } catch (e) {
        /*** notificar error en caso de que la eliminación falle. @author RADJ */
        alert('Error al eliminar usuario: ' + e.message);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    window.enableTableSorting({
        tableSelector: '#sec-usuarios table',
        getDataFn: () => AppState.cache.usuarios || [],
        setDataFn: (sorted) => { AppState.cache.usuarios = sorted; },
        renderFn: (sorted) => {
            window.renderUsuarios(sorted);
            const searchInput = document.getElementById('usr-search-input');
            if (searchInput && searchInput.value) {
                window.filterTable(searchInput, 'usr-tbody');
            }
        }
    });
});
