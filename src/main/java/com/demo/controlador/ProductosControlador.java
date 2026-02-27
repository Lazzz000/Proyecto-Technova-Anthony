package com.demo.controlador;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;


import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.demo.modelo.Categoria;
import com.demo.modelo.Productos;
import com.demo.repositorio.DatosFacturacionRepositorio;
import com.demo.service.CategoriaService;
import com.demo.service.ProductosService;

@Controller
@RequestMapping("/mantenimientoProductos")
public class ProductosControlador {

    private final DatosFacturacionRepositorio datosFacturacionRepositorio;
	@Autowired
	private ProductosService productoService;
	
	@Autowired
	private CategoriaService categoriaService;

    ProductosControlador(DatosFacturacionRepositorio datosFacturacionRepositorio) {
        this.datosFacturacionRepositorio = datosFacturacionRepositorio;
    }
	
	//Listar producto
    @GetMapping("/lista")
    public String listaProductos(
            Model model,
            @RequestParam(required = false) String estado,
            @AuthenticationPrincipal UserDetails userdetails) {

        
        if (estado != null && !estado.isEmpty()) {
            model.addAttribute("listaproductos",
                    productoService.listarPorEstado(estado));
        } else {
            model.addAttribute("listaproductos",
                    productoService.listarTodos());
        }

        // Contadores
        model.addAttribute("activos", productoService.contarActivos());
        model.addAttribute("inactivos", productoService.contarInactivos());
        model.addAttribute("total", productoService.contarTotal());

        model.addAttribute("adminNombre", userdetails.getUsername());
        model.addAttribute("estadoActual", estado);

        return "/mantenimientoProductos/lista-productos";
    }

	
	//Formulario para crear un producto
	@GetMapping("/nuevo")
	public String nuevoProducto(Model model,
            @AuthenticationPrincipal UserDetails userdetails) {
		Productos produ= new Productos();
		model.addAttribute("producto",produ);
		model.addAttribute("categorias", categoriaService.listarCategoriasPadre());
		 model.addAttribute("adminNombre", userdetails.getUsername());
		return "/mantenimientoProductos/form-producto";
	}
	
	//Guardar o actualizar Producto
	@PostMapping ("/guardar")
	public String guardarProducto(    @ModelAttribute ("producto") Productos producto, 
	        @RequestParam ("imagenArchivo") MultipartFile imagenArchivo,
	        @RequestParam(value = "especificacionesInput", required = false) String especificacionesInput,
	        RedirectAttributes redirectAttributes) {
		 try {

		        // CONVERTIR ESPECIFICACIONES A JSON

		        if (especificacionesInput != null && !especificacionesInput.isBlank()) {

		            Map<String, String> specsMap = new LinkedHashMap<>();

		            String[] lineas = especificacionesInput.split("\n");

		            for (String linea : lineas) {
		                if (linea.contains(":")) {
		                    String[] partes = linea.split(":", 2);
		                    specsMap.put(
		                            partes[0].trim(),
		                            partes[1].trim()
		                    );
		                }
		            }

		            ObjectMapper mapper = new ObjectMapper();
		            String jsonSpecs = mapper.writeValueAsString(specsMap);

		            producto.setEspecificacionesTecnicas(jsonSpecs);
		        }

		        if (!imagenArchivo.isEmpty()) {
		            String nombreArchivo = imagenArchivo.getOriginalFilename();
		            Path ruta = Paths.get("imagenesTecnologia/" + nombreArchivo);

		            BufferedImage img = ImageIO.read(imagenArchivo.getInputStream());

		            int anchoMax = 800;
		            int altoMax = 600;
		            int ancho = img.getWidth();
		            int alto = img.getHeight();
		            double escala = Math.min((double) anchoMax / ancho, (double) altoMax / alto);

		            if (escala < 1) {
		                int nuevoAncho = (int) (ancho * escala);
		                int nuevoAlto = (int) (alto * escala);

		                BufferedImage imgRedimensionada = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_RGB);
		                Graphics2D g = imgRedimensionada.createGraphics();
		                g.drawImage(img, 0, 0, nuevoAncho, nuevoAlto, null);
		                g.dispose();

		                try (OutputStream os = Files.newOutputStream(ruta)) {
		                    ImageIO.write(imgRedimensionada, "jpg", os);
		                }
		            } else {
		                Files.copy(imagenArchivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
		            }

		            producto.setImagen(nombreArchivo);

		        } else if (producto.getIdProducto()!=null) {
		            Productos productoExistente = productoService.buscarPorId(producto.getIdProducto());
		            producto.setImagen(productoExistente.getImagen());
		        }

		        if (producto.getCategoria() != null 
		                && producto.getCategoria().getIdCategoria() != null) {

		            Categoria categoria = categoriaService
		                    .buscarPorId(producto.getCategoria().getIdCategoria());

		            producto.setCategoria(categoria);
		        }

		        if (producto.getIdProducto() == null) {

		            producto.setEstado("ACTIVO");
		            producto.setFechaRegistro(LocalDateTime.now());
		            
		            if (producto.getCategoria() != null) {

		                List<Categoria> hijos = categoriaService
		                        .listarSubcategorias(producto.getCategoria().getIdCategoria());

		                // Si tiene hijos y no seleccionaron subcategoría → error
		                if (!hijos.isEmpty()) {
		                    redirectAttributes.addFlashAttribute("msg", "Debe seleccionar una subcategoría");
		                    return "redirect:/mantenimientoProductos/nuevo";
		                }
		            }   

		        } else {

		            Productos productoExistente = productoService.buscarPorId(producto.getIdProducto());

		            producto.setFechaRegistro(productoExistente.getFechaRegistro());
		        }

		        productoService.guardar(producto);

		        redirectAttributes.addFlashAttribute("msg","ok");
		        return "redirect:/mantenimientoProductos/nuevo";

		    } catch (Exception e) {
		        e.printStackTrace();
		        redirectAttributes.addFlashAttribute("msg", "exception");
		        return "redirect:/mantenimientoProductos/nuevo";
		    }
		}
	
	@GetMapping("/editar/{id}")
	public String editarProducto(@PathVariable("id") Integer id, Model model) {

	    Productos producto = productoService.buscarPorId(id);

	    // Convertir JSON a texto plano para el textarea
	    if (producto.getEspecificacionesTecnicas() != null &&
	        !producto.getEspecificacionesTecnicas().isBlank()) {

	        try {
	            ObjectMapper mapper = new ObjectMapper();

	            Map<String, String> specsMap = mapper.readValue(
	                    producto.getEspecificacionesTecnicas(),
	                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
	            );

	            StringBuilder textoPlano = new StringBuilder();

	            for (Map.Entry<String, String> entry : specsMap.entrySet()) {
	                textoPlano.append(entry.getKey())
	                          .append(": ")
	                          .append(entry.getValue())
	                          .append("\n");
	            }

	            model.addAttribute("especificacionesInput", textoPlano.toString());

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    model.addAttribute("producto", producto);
	    model.addAttribute("categorias", categoriaService.listarCategoriasPadre());

	    return "/mantenimientoProductos/form-producto";
	}
	//Cambiar estado de  producto
	@GetMapping("/estado/{id}")
	public String cambiarEstadoProducto(@PathVariable Integer id) {
	    productoService.cambiarEstado(id);
	    return "redirect:/mantenimientoProductos/lista";
	}
	//Ver detalles del producto
	
	@GetMapping("/ver/{id}")
	public String verProducto(@PathVariable Integer id,
	                          Model model,
	                          @AuthenticationPrincipal UserDetails userDetails) {

	    Productos producto = productoService.buscarPorId(id);

	    if (producto == null) {
	        return "redirect:/productos/listar";
	    }

	    Long vecesVendido = productoService.contarVecesVendido(id);

	    // Categoria segura
	    String categoriaNombre = Optional.ofNullable(producto.getCategoria())
	            .map(Categoria::getNombre_Categoria)
	            .orElse("No disponible");

	    // Convertir JSON a Map
	    Map<String, String> especificacionesMap = new LinkedHashMap<>();

	    if (producto.getEspecificacionesTecnicas() != null &&
	        !producto.getEspecificacionesTecnicas().isBlank()) {

	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            especificacionesMap = mapper.readValue(
	                    producto.getEspecificacionesTecnicas(),
	                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
	            );
	        } catch (Exception e) {
	            System.out.println("Error al convertir especificaciones JSON");
	        }
	    }

	    model.addAttribute("vecesVendido", vecesVendido);
	    model.addAttribute("nombreUsuario",
	            userDetails != null ? userDetails.getUsername() : "");
	    model.addAttribute("producto", producto);
	    model.addAttribute("categoriaNombre", categoriaNombre);
	    model.addAttribute("especificaciones", especificacionesMap);

	    return "mantenimientoProductos/ver-producto";
	}
	
	@GetMapping("/subcategorias/{id}")
	@ResponseBody
	public List<Categoria> obtenerSubcategorias(@PathVariable Integer id) {
	    return categoriaService.listarSubcategorias(id);
	}

	@GetMapping("/buscar")
    @ResponseBody
    public List<Productos> buscarNombreProductos(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre);
    }
}
