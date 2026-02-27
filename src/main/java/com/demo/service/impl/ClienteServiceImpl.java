package com.demo.service.impl;

import com.demo.modelo.Cliente;
import com.demo.modelo.Usuario;
import com.demo.repositorio.ClienteRepositorio;
import com.demo.repositorio.UsuarioRepositorio;
import com.demo.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepositorio clienteRepository;
    
    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Cliente> listarActivos() {
        return clienteRepository.findAll()
                .stream()
                .filter(c -> "Activo".equalsIgnoreCase(c.getEstado()))
                .toList(); 
    }

    public long contarClientes() {
        return clienteRepository.count();
    }
    
    public void registrarUsuarioCliente(Usuario usu, Cliente cli) {
        // Validaciones de Usuario
        if (usu.getCorreo() == null || usu.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo no puede estar vacío");
        }
        if (!usu.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }
        if (usu.getClave() == null || usu.getClave().isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (usu.getClave().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        // Validaciones de Cliente
        if (cli.getNombres() == null || cli.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres no pueden estar vacíos");
        }
        if (cli.getApellidos() == null || cli.getApellidos().isBlank()) {
            throw new IllegalArgumentException("Los apellidos no pueden estar vacíos");
        }
        if (cli.getGenero() == null || cli.getGenero().isBlank()) {
            throw new IllegalArgumentException("El género es obligatorio");
        }
        if (cli.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (cli.getTelefono() == null || cli.getTelefono().isBlank() || !cli.getTelefono().matches("^\\d{7,15}$")) {
            throw new IllegalArgumentException("El teléfono es obligatorio y debe tener solo números");
        }

        // Preparar datos antes de guardar
        usu.setNombreUsuario(cli.getNombres() + " " + cli.getApellidos());
        usu.setClave(passwordEncoder.encode(usu.getClave()));
        cli.setEstado("Activo");
        usu.setRol("CLIENTE");

        // Guardar en la base de datos
        Usuario nuevoUsu = usuarioRepository.save(usu);
        cli.setUsuario(nuevoUsu);
        clienteRepository.save(cli);
    }
    public boolean actualizarCliente(Cliente clienteActualizado) {
        return clienteRepository.findById(clienteActualizado.getIdCliente())
                .map(cli -> {
                	  cli.setNombres(clienteActualizado.getNombres());
                      cli.setApellidos(clienteActualizado.getApellidos());
                      cli.setTelefono(clienteActualizado.getTelefono());
                  
                    clienteRepository.save(cli);
                    return true;
                })
                .orElse(false);
    }
    public boolean eliminarCliente(Integer idCliente) {
        return clienteRepository.findById(idCliente)
                .map(cli -> {
                    cli.setEstado("Inactivo");
                    clienteRepository.save(cli);
                    return true;
                })
                .orElse(false);
    }

	@Override
	public List<Cliente> ultimosClientes() {
		return clienteRepository.findTop5ByOrderByIdClienteDesc();
	}

	@Override
	public long clientesActivos() {
		return clienteRepository.countByEstado("ACTIVO");
	}

	@Override
	public long clientesInactivos() {
		
		return clienteRepository.countByEstado("INACTIVO");
	}

	@Override
	public long clientesTodos() {
	 return clienteRepository.count();
	}


	@Override
	public List<Cliente> listarInactivos() {
		return clienteRepository.findByEstado("INACTIVO");
	}

	@Override
	public Cliente buscarPorUsuario(Usuario usuario) {
		return clienteRepository.findByUsuario(usuario);
	}
	
	@Override
	public void desactivarPorUsuario(Usuario usuario) {
		Cliente cliente = clienteRepository.findByUsuario(usuario);
        if (cliente != null) {
            cliente.setEstado("INACTIVO");
            clienteRepository.save(cliente);
        }
		
	}

	@Override
	public Cliente buscarPorId(Integer id) {
		return clienteRepository.findById(id).orElse(null);
	}

	@Override
	public void activarCliente(Integer id) {
		  Cliente cliente = buscarPorId(id);
	        if (cliente != null) {
	            cliente.setEstado("ACTIVO");
	            clienteRepository.save(cliente);
	        }
		
	}

	@Override
	public void desactivarCliente(Integer id) {
		 Cliente cliente = buscarPorId(id);
	        if (cliente != null) {
	            cliente.setEstado("INACTIVO");
	            clienteRepository.save(cliente);
	        }
		
	}

	@Override
	public List<Cliente> listarTodos() {
		
		return clienteRepository.findAll();
	}
	

}