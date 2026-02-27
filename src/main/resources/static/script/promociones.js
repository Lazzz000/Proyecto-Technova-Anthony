let nombre = null;
let id = null;
let editar = false;
const guardar = document.getElementById("guardar")
const cerrarModal = document.getElementById("cerrarModal")
const modal = document.getElementById('dlgProductos');
const nombrePromo = document.getElementById('nombrePromocion');
const tituloDialog = document.getElementById('titulo-dialog')
const mensajeError = document.getElementById('mensaje-error')

function editarPromocion(elemento) {
    nombre = elemento.getAttribute('data-promo-nombre');
    id = elemento.getAttribute('data-promo-id');
    editar = true

    nombrePromo.value = nombre;
    tituloDialog.textContent = "Actualizar Promoción"
    modal.showModal();
}

guardar.addEventListener('click', (e) => {

    if(editar){
        let editarRequest = {
            idPromocion: id,
            nombrePromo: nombrePromo.value
        }
        fetch("/mantenimientoPromociones/editar", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(editarRequest)
            })
            .then(res => {
                if(!res.ok) throw new Error("Error");
                window.location.href = "/mantenimientoPromociones/listaPromociones";
            })
            .catch(err => {
                console.error(err);
            });
    }else{
         let guardarRequest = {

            nombrePromo: nombrePromo.value
        }
        fetch("/mantenimientoPromociones/guardar", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(guardarRequest)
            })
            .then(res => {
                if (res.status === 409) {
                    console.log("nombre duplicado")
                    mensajeError.classList.replace("d-none","d-block")
                    mensajeError.textContent = "❌ Error ya existe la promoción "+nombrePromo.value

                }
                if(!res.ok) throw new Error("Error");
                window.location.href = "/mantenimientoPromociones/listaPromociones";
            })
            .catch(err => {
                console.error(err);
            });

    }

})

cerrarModal.addEventListener( 'click', (e) =>{
    editar = false;
    nombrePromo.value = "";
    tituloDialog.textContent = "Nueva Promoción"
    mensajeError.classList.replace("d-block","d-none")
    modal.close();
})

document.getElementById("nombrePromocion").addEventListener("keydown", function (e) {

  if (e.key === "Enter") {
    e.preventDefault();
    guardar.click();
  }

});
