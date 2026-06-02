document.addEventListener("DOMContentLoaded", function () {

    const formFiltro = document.getElementById('formFiltro');
    const inputDesde = document.getElementById('fechaDesde');
    const inputHasta = document.getElementById('fechaHasta');
    const btnBuscar = document.getElementById('btnBuscar');

    // Validación antes de enviar
    formFiltro.addEventListener('submit', function (event) {
        const fechaDesde = new Date(inputDesde.value);
        const fechaHasta = new Date(inputHasta.value);

        if (fechaDesde > fechaHasta) {
            event.preventDefault();
            alert("Error: La 'Fecha Desde' no puede ser posterior a la 'Fecha Hasta'.");
            return;
        }

        // Feedback visual
        const originalText = btnBuscar.innerHTML;
        btnBuscar.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Procesando...';
        btnBuscar.classList.add('disabled');
        
        setTimeout(() => {
            btnBuscar.innerHTML = originalText;
            btnBuscar.classList.remove('disabled');
        }, 3000);
    });
});

// Función para el botón "Limpiar"
function limpiarFiltros() {
    // Obtenemos la fecha de hoy
    const hoy = new Date();
    
    // Obtenemos el primer día del mes actual (formato YYYY-MM-DD)
    const primerDia = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
    const primerDiaStr = primerDia.toISOString().split('T')[0];
    
    // Obtenemos el último día del mes actual (formato YYYY-MM-DD)
    const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0);
    const ultimoDiaStr = ultimoDia.toISOString().split('T')[0];

    // Seteamos los inputs
    document.getElementById('fechaDesde').value = primerDiaStr;
    document.getElementById('fechaHasta').value = ultimoDiaStr;
}