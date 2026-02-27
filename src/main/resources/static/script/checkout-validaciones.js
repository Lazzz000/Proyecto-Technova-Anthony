document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("step2Form");

    window.guardarStep2 = function () {

        if (!validarFormulario()) {
            return;
        }

        enviarDatos();
    };

});


function validarFormulario() {

    let valido = true;

    limpiarErrores();

    const nombre = getValue("nombreReceptor");
	const tipoDoc = getValue("idTipoDocumento");
    const numeroDoc = getValue("numeroDocumento");
    const direccion = getValue("direccionEnvio");
    const telefono = getValue("telefonoContacto");
    const metodo = getValue("metodoPago");

    // Nombre
    if (nombre.length < 3) {
        mostrarError("nombreReceptor", "Ingrese un nombre válido");
        valido = false;
    }

    // Tipo Documento
    if (!tipoDoc) {
        mostrarError("checkoutDTO.idTipoDocumento", "Seleccione tipo de documento");
        valido = false;
    }

    // Número Documento (DNI 8 dígitos ejemplo Perú)
    if (!/^\d{8,12}$/.test(numeroDoc)) {
        mostrarError("numeroDocumento", "Documento inválido");
        valido = false;
    }

    // Dirección
    if (direccion.length < 5) {
        mostrarError("direccionEnvio", "Ingrese dirección válida");
        valido = false;
    }

    // Teléfono Peruano
    if (!/^9\d{8}$/.test(telefono)) {
        mostrarError("telefonoContacto", "Teléfono inválido (9XXXXXXXX)");
        valido = false;
    }

    // Método de pago
    if (!metodo) {
        mostrarError("metodoPago", "Seleccione método de pago");
        valido = false;
    }

    return valido;
}


function enviarDatos() {

    const form = document.getElementById('step2Form');

    const data = {
        nombreReceptor: getValue("nombreReceptor"),
        idTipoDocumento: getValue("idTipoDocumento"),
        numeroDocumento: getValue("numeroDocumento"),
        nombreTitular: getValue("nombreTitular"),
        direccionEnvio: getValue("direccionEnvio"),
        ciudad: getValue("ciudad"),
        telefonoContacto: getValue("telefonoContacto"),
        metodoPago: getValue("metodoPago")
    };

    fetch('/checkout/step2', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            showStep(3);
        } else {
            alert("Error al guardar datos");
        }
    })
    .catch(() => alert("Error de comunicación"));
}


function getValue(name) {
    const input = document.querySelector(`[name="${name}"]`);
    return input ? input.value.trim() : "";
}


function mostrarError(name, mensaje) {
    const input = document.querySelector(`[name="${name}"]`);

    if (input) {
        input.classList.add("border", "border-red-500");

        const error = document.createElement("p");
        error.className = "text-red-400 text-sm mt-1 error-msg";
        error.innerText = mensaje;

        input.parentElement.appendChild(error);
    }
}


function limpiarErrores() {
    document.querySelectorAll(".error-msg").forEach(e => e.remove());
    document.querySelectorAll("input, select").forEach(i => {
        i.classList.remove("border-red-500");
    });
	
	
}



