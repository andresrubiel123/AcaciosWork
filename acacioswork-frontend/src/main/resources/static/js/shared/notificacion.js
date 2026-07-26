/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** notificacion.js - componente de alertas flotantes visuales (toast). @author RADJ */

/*** muestra un mensaje flotante en la interfaz por un tiempo limitado. @author RADJ */
window.showToast = function(msg, type = 'success') {
    let t = document.getElementById('toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'toast';
        t.className = 'toast';
        document.body.appendChild(t);
    }
    t.textContent = msg;
    t.className = `toast ${type} show`;
    setTimeout(() => t.classList.remove('show'), 2000);
};
