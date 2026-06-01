document.addEventListener("DOMContentLoaded", function() {
    
    const inputFechaDesde = document.getElementById("fechaDesde");
    const inputFechaHasta = document.getElementById("fechaHasta");

    // Validar al cargar la página
    configurarMinimoFechaHasta();

    // Validar cada vez que el usuario cambie la 'Fecha Desde'
    inputFechaDesde.addEventListener("change", function() {
        configurarMinimoFechaHasta();
        
        // Si la fecha 'Hasta' actual quedó en el pasado respecto a la nueva 'Desde', la igualamos
        if (inputFechaHasta.value < inputFechaDesde.value) {
            inputFechaHasta.value = inputFechaDesde.value;
        }
    });

    function configurarMinimoFechaHasta() {
        if (inputFechaDesde.value) {
            // El atributo 'min' bloquea en el calendario las fechas anteriores a la seleccionada
            inputFechaHasta.setAttribute("min", inputFechaDesde.value);
        }
    }
});

function limpiarFiltros() {
    window.location.href = '/reportes/ventas';
}