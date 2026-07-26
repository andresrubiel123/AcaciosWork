/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** auth.js - lógica de control de sesión y autenticación. @author RADJ */

/*** cerrar sesión del usuario actual y redirigir al login. @author RADJ */
window.logout = function() {
    /*** limpiar todos los datos guardados en almacenamiento local. @author RADJ */
    localStorage.clear();
    /*** redireccionar a la pantalla de inicio de sesión. @author RADJ */
    window.location.href = 'login';
};

/*** verificación de autenticación al cargar la página. @author RADJ */
document.addEventListener('DOMContentLoaded', () => {
   
/*** si estamos en la página de login, no verificar sesión para evitar loops de redirección. @author RADJ */
    if (window.location.pathname.endsWith('login') || window.location.pathname.endsWith('login.html')) {
        return;
    }

    /*** validar existencia de sesión activa; redirigir si no existe. @author RADJ */
    if (!localStorage.getItem('jwt_token')) { 
        window.location.href = 'login'; 
        return; 
    }
});
