package com.demo.controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.demo.modelo.*;

import com.demo.modelo.enums.MetodoPago;
import com.demo.repositorio.DatosFacturacionRepositorio;
import com.demo.repositorio.PedidoDetalleRepositorio;
import com.demo.repositorio.PedidoRepositorio;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.CarritoService;
import com.demo.service.CheckoutSessionService;
import com.demo.service.ComprobanteService;
import com.demo.service.PedidoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
	
	@Autowired
	UsuarioRepositorio usuariorepositorio;
    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CheckoutSessionService checkoutSessionService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ComprobanteService comprobanteService;
    
    @Autowired
    private DatosFacturacionRepositorio datosFacturacionRepositorio;
    
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    
    @Autowired
    private PedidoDetalleRepositorio pedidoDetalleRepositorio;

    // STEP 2: Guardar datos de envío y método de pago
    @PostMapping("/step2")
    @ResponseBody
    public ResponseEntity<?> guardarStep2Ajax(@RequestBody Map<String, String> datos,
                                              HttpSession session) {

        CheckoutDTO checkoutSession = checkoutSessionService.obtenerCheckout(session);

        // Campos String directos
        checkoutSession.setNombreReceptor(datos.get("nombreReceptor"));
        checkoutSession.setNumeroDocumento(datos.get("numeroDocumento"));
        checkoutSession.setNombreTitular(datos.get("nombreTitular"));
        checkoutSession.setDireccionEnvio(datos.get("direccionEnvio"));
        checkoutSession.setCiudad(datos.get("ciudad"));
        checkoutSession.setTelefonoContacto(datos.get("telefonoContacto"));

        // Conversión de tipos
        String idTipoDocStr = datos.get("idTipoDocumento");
        if (idTipoDocStr != null && !idTipoDocStr.isEmpty()) {
            checkoutSession.setIdTipoDocumento(Integer.valueOf(idTipoDocStr));
        }

        String metodoPagoStr = datos.get("metodoPago");
        if (metodoPagoStr != null && !metodoPagoStr.isEmpty()) {
            checkoutSession.setMetodoPago(MetodoPago.valueOf(metodoPagoStr));
        }

        System.out.println("Step2 datos recibidos: " + datos);
        System.out.println("checkoutDTO antes de guardar: " + checkoutSession);
        checkoutSessionService.guardarCheckout(session, checkoutSession);

        return ResponseEntity.ok().build();
    }

    // STEP 3: Mostrar confirmación
    @GetMapping("/step3")
    public String mostrarConfirmacion(HttpSession session,
                                      Model model) {

        CheckoutDTO checkout = 
            checkoutSessionService.obtenerCheckout(session);

        if (checkout.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        model.addAttribute("checkoutDTO", checkout);
        return "PaginaWeb/checkout_confirmacion";
    }

    @PostMapping("/confirmar")
    @ResponseBody
    public ResponseEntity<?> confirmarCompra(HttpSession session, Authentication authentication) {

        // Validar login
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Login requerido");
        }

        // Obtener checkout
        CheckoutDTO checkout = checkoutSessionService.obtenerCheckout(session);

        if (checkout.getItems().isEmpty()) {
            checkout.setItems(new ArrayList<>(carritoService.obtenerItems()));
            checkoutSessionService.guardarCheckout(session, checkout);
        }

        if (checkout.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("No hay items en el carrito");
        }

        // Procesar pedido y guardar en BD
        Pedido pedido = pedidoService.procesarPedidoCompleto(checkout, authentication);

        // Limpiar carrito y checkout de sesión
        carritoService.limpiarCarrito();
        checkoutSessionService.limpiarCheckout(session);

        // Retornar la URL de éxito con ID del pedido
        String redirectUrl = "/checkout/pedido/exito/" + pedido.getIdPedido();
        return ResponseEntity.ok(redirectUrl);
    }
    
    @PostMapping("/guardar-tarjeta")
    @ResponseBody
    public ResponseEntity<?> guardarTarjeta(@RequestBody String ultimos4,
                                            HttpSession session) {

        CheckoutDTO checkout = checkoutSessionService.obtenerCheckout(session);

        checkout.setUltimos4Tarjeta(ultimos4);

        checkoutSessionService.guardarCheckout(session, checkout);

        return ResponseEntity.ok().build();
    } 
    
    @GetMapping("/pedido/exito/{idPedido}")
    public String exitoPedido(@PathVariable Integer idPedido, Model model, Authentication au) {

        // 1️ Obtener pedido
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
 
        // 2️ Obtener detalles del pedido
        List<PedidoDetalle> detalles = pedidoDetalleRepositorio.findByPedidoIdPedido(pedido.getIdPedido());

        // 3️ Obtener comprobante (si ya existe)
        Comprobante comprobante = comprobanteService.buscarPorPedido(idPedido);

        if (comprobante == null) {
            // Obtener datos de facturación asociados al pedido
            DatosFacturacion datos = datosFacturacionRepositorio
                    .findByNumeroDocumento(pedido.getDocumento().getNumeroDocumento())
                    .orElseThrow(() -> new RuntimeException("Datos facturación no encontrados"));

            // Generar y guardar comprobante
            comprobante = comprobanteService.generarComprobante(pedido, datos);
        }
        

        if (au != null && au.isAuthenticated()
                && !(au instanceof AnonymousAuthenticationToken)) {

            Usuario usu = usuariorepositorio.findByCorreo(au.getName());

            if (usu != null) {
                model.addAttribute("usuarioLogueado", usu);
                model.addAttribute("nombreCom", usu.getNombreUsuario());
            }
        }
        // 5️ Pasar todo al modelo para la vista
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", detalles);
        model.addAttribute("comprobante", comprobante);

        return "pedido/exito";
    }
}
