const nuevaPromo = document.getElementById('buttonNuevaPromo');
const cerrarPromo = document.getElementById('buttonCerrartPromo');
const bloquePromo = document.getElementById('bloquePromo');
const bloqueNuevaPromo = document.getElementById('bloqueNuevaPromo');

const aplicarDescuentos = document.getElementById('buttonAplicarDescuentos');
const mensajeError = document.getElementById('mensaje-error')

nuevaPromo.addEventListener('click', (e) => {
    bloquePromo.classList.replace("abierta","d-none")
    bloqueNuevaPromo.classList.replace("d-none","abierta")
})

cerrarPromo.addEventListener('click', (e) => {
   bloqueNuevaPromo.classList.replace("abierta","d-none")
   bloquePromo.classList.replace("d-none","abierta")
})

const seleccionados = new Map();

const listaResultados = document.querySelector('.resultados');
const listaSeleccionados = document.querySelector('.seleccionados');
traerPromociones();
/* ===== AGREGAR DESDE COINCIDENCIAS ===== */
listaResultados.addEventListener('click', (e) => {
    const item = e.target.closest('li');
    if (!item) return;

    const id = item.dataset.id;
    const nombre = item.textContent.trim();

    if (seleccionados.has(nombre)) return; // evita duplicados

    seleccionados.set(id, nombre);
    renderSeleccionados();
});

/* ===== QUITAR DE SELECCIONADOS ===== */
listaSeleccionados.addEventListener('click', (e) => {
    if (!e.target.classList.contains('remove')) return;

    const li = e.target.closest('li');
    const id = li.dataset.id;

    seleccionados.delete(id);
    renderSeleccionados();
});

/* ===== RENDER ===== */
function renderSeleccionados() {
    listaSeleccionados.innerHTML = '';

    seleccionados.forEach((nombre, id) => {
        const li = document.createElement('li');
        li.dataset.id = id;
        li.innerHTML = `
            ${nombre}
            <span class="remove">✕</span>
        `;
        listaSeleccionados.appendChild(li);
    });
}

/* ===== EJEMPLO DE CARGA DE COINCIDENCIAS ===== */
/* (esto normalmente vendrá del backend) */
function cargarCoincidencias(productos) {
    listaResultados.innerHTML = '';

    productos.forEach(p => {
        const li = document.createElement('li');
        li.dataset.id = p.idProducto;
        li.textContent = p.nombre;
        listaResultados.appendChild(li);
    });
}

function buscarProductos(nombre) {
    if (nombre.length < 2) {
        document.querySelector('.resultados').innerHTML = '';
        return;
    }

    fetch(`/mantenimientoProductos/buscar?nombre=${encodeURIComponent(nombre)}`)
        .then(r => r.json())
        .then(data => cargarCoincidencias(data));
}

function traerPromociones() {

    fetch(`/mantenimientoDescuentos/promociones`)
        .then(r => r.json())
        .then(data => { cargarPromociones(data);})
         .catch(err => console.error(err));
}
function cargarPromociones(lista) {
    const select = document.getElementById('selectPromociones');

    // Limpiar opciones (excepto la primera)
    select.innerHTML = '<option value="">-- Seleccione promoción --</option>';

    lista.forEach(promo => {
        const option = document.createElement('option');
        option.value = promo.nombrePromo;
        option.textContent = promo.nombrePromo;
        select.appendChild(option);
    });
}

aplicarDescuentos.addEventListener('click', (e) => {
    let fechaInicio = document.getElementById('fechaInicio').value
    let fechaFin = document.getElementById('fechaFin').value
    let descuento = document.getElementById('descuento').value

//    if(fechaInicio && fechaFin && fechaFin < fechaInicio){
//        alert("Fecha fin no puede ser menor a Fecha Inicio");
//        return
//    }

    let promo = ""

    if(bloqueNuevaPromo.classList.contains('abierta')){
        promo = document.getElementById('promoNueva').value
    }else{
        promo = document.getElementById("selectPromociones").value
    }
    let mensaje = "❌ Error, "
    console.log(descuento)
    mensaje += seleccionados.size <1 ? "seleccione productos":
                    promo == "-- Seleccione promoción --" ||promo == "" ? "elige una promoción":
                    fechaInicio == ""?"seleccione una fecha de inicio":
                    fechaFin == ""?"seleccione una fecha fin":
                    fechaInicio && fechaFin && fechaFin < fechaInicio? "Fecha fin no puede ser menor a Fecha Inicio":
                    (descuento === "") ? "ingrese un descuento" :
                    parseInt(descuento)>100 || parseInt(descuento)<0 ? "el descuento debe estar en el rango 0 a 100.":"";

    mensajeError.textContent =mensaje;

    if(mensaje!="❌ Error, "){
         mensajeError.classList.replace("d-none","d-block")
         return
    }else{
        mensajeError.classList.replace("d-block","d-none")
    }

    let descuentos = []
    seleccionados.forEach((valor, clave) => {
        let desc = {
            porcentajeDescuento: descuento/100,
            fechaInicio:fechaInicio,
            fechaFin: fechaFin,
            promocion: null,
            producto:{
                idProducto:parseInt(clave)
            }
        }
        descuentos.push(desc)

    });
    requestDescuento= {
        promocion: {
            nombrePromo:promo
        },
        descuentos:descuentos


    }
    console.log(requestDescuento)





    fetch("/mantenimientoDescuentos/guardar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestDescuento)
    })
    .then(res => {
        if(!res.ok) throw new Error("Error");
        window.location.href = "/mantenimientoDescuentos/listaDescuentos";
    })
    .catch(err => {
        console.error(err);
    });
})

cargarCoincidencias([

]);

function redirigirPromocion(select) {

    const promo = select.value;
    const url = new URL(window.location.href);

    if (promo && promo !== "") {
        url.searchParams.set("promocion", promo);
    } else {
        url.searchParams.delete("promocion");
    }

    window.location.href = url.toString();
}

const params = new URLSearchParams(window.location.search);
const promoParam = params.get("promocion");
constEstadoParam = params.get("estado");

if (promoParam) {
    const select = document.getElementById("comboPromos");

    for (let option of select.options) {
        if (option.text === promoParam) {
            option.selected = true;
            break;
        }
    }
}

if(constEstadoParam){
    const titulo = document.getElementById("titulo")
    let estado = constEstadoParam == "en-curso" ? "Vigentes":"No Vigentes"
    titulo.textContent += " "+ estado
}

const cerrarModal = document.getElementById('cerrar-modal')
cerrarModal.addEventListener('click', () => {
     mensajeError.classList.replace("d-block","d-none")
    document.getElementById('dlgProductos').close();

});

