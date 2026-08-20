/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** reportes.js - coordinación del módulo de reportes y enlace con gráficos y exportadores. @author RADJ */

/*** inicialización o refresco de la sección de reportes. @author RADJ */
/*** inicialización o refresco de la sección de reportes. @author RADJ */
window.initReportes = function() {
    console.log("Módulo de Reportes inicializado.");
    if (typeof window.showReportSub === 'function') {
        window.showReportSub('hub');
    }
};
