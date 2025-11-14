package com.iesfernandoaguilar.solsonafuentes.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.model.ConfiguracionJornada;
import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosEmpleado;
import com.iesfernandoaguilar.solsonafuentes.repository.ConfiguracionJornadaRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private ConfiguracionJornadaRepository configuracionJornadaRepository;

    @Autowired
    private ConfiguracionJornadaService configuracionJornadaService;

    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String nombreOEmailUsuario) {
        return usuarioRepository.login(nombreOEmailUsuario);
    }

    public Optional<Usuario> findByNombre(String nombre){
        return usuarioRepository.findByNombre(nombre);
    }

    public List<Usuario> findByIdDepartamento(Long idDepartamento) {
        return usuarioRepository.findByIdDepartamento(idDepartamento);
    }
    
    public Optional<Usuario> findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findByIdUsuario(Long idUsuario){
        return usuarioRepository.findByIdUsuario(idUsuario);
    }

    public List<Usuario> obtenerEmpleados(Long idGrupo) {
        return usuarioRepository.obtenerEmpleados(idGrupo);
    }

    public List<Usuario> buscarEmpleados(Long idGrupo, String filtro) {
    return usuarioRepository.buscarEmpleados(idGrupo, filtro);
    }

    @Transactional
    public List<Usuario> obtenerConFiltros(FiltrosEmpleado filtros, Usuario usuarioActual) {
        // Si es admin sin grupo seleccionado, devolver vacío
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía");
            return new java.util.ArrayList<>();
        }

        // Si no es admin, forzar su grupo
        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        // Obtener lista base
        List<Usuario> resultados;
        if (filtros.tieneGrupo()) {
            resultados = usuarioRepository.obtenerEmpleados(filtros.getIdGrupo());
        } else {
            // Fallback a todos los usuarios si no hay grupo, aunque la lógica anterior lo previene para admins.
            resultados = usuarioRepository.findAll();
        }

        // Aplicar filtros
        return aplicarFiltrosAUsuarios(resultados, filtros);
    }

    private boolean esAdmin(Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private List<Usuario> aplicarFiltrosAUsuarios(List<Usuario> lista, FiltrosEmpleado filtros) {
        return lista.stream()
            .filter(usuario -> !filtros.tieneUsuario() || usuario.getIdUsuario().equals(filtros.getIdUsuario()))
            .filter(usuario -> {
                if (!filtros.tieneDepartamento()) return true;
                return usuario.getDepartamento() != null &&
                       usuario.getDepartamento().getIdDepartamento().equals(filtros.getIdDepartamento());
            })
            .filter(usuario -> {
                if (!filtros.tieneSubgrupo()) return true;
                return usuario.getSubgrupo() != null &&
                       usuario.getSubgrupo().getIdSubgrupo().equals(filtros.getIdSubgrupo());
            })
            .filter(usuario -> !filtros.tieneRol() || usuario.getRol().name().equalsIgnoreCase(filtros.getRol()))
            .filter(usuario -> {
                if (!filtros.tieneBusqueda()) return true;
                String busqueda = filtros.getTextoBusqueda().toLowerCase();
                
                boolean nombreMatch = usuario.getNombre().toLowerCase().contains(busqueda);
                boolean emailMatch = usuario.getEmail().toLowerCase().contains(busqueda);
                boolean rolMatch = usuario.getRol().name().toLowerCase().contains(busqueda);
                boolean dptoMatch = usuario.getDepartamento() != null && usuario.getDepartamento().getNombre().toLowerCase().contains(busqueda);
                boolean subgrupoMatch = usuario.getSubgrupo() != null && usuario.getSubgrupo().getNombre().toLowerCase().contains(busqueda);

                return nombreMatch || emailMatch || rolMatch || dptoMatch || subgrupoMatch;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void eliminarEmpleado(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }


    public Usuario actualizarEmpleado(Long idUsuario, String rol, Long idDepartamento, Long idConfiguracionJornada) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByIdUsuario(idUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (rol != null) {
                usuario.setRol(Rol.valueOf(rol));
            }

            if (idDepartamento != null) {
                Optional<Departamento> departamentoOpt = departamentoService.findByIdDepartamento(idDepartamento);
                if (departamentoOpt.isPresent()) {
                    usuario.setDepartamento(departamentoOpt.get());
                }
            }

            // Gestionar asignación de configuración de jornada
            if (idConfiguracionJornada != null && idConfiguracionJornada > 0) {
                // Buscar la configuración
                ConfiguracionJornada configuracion = configuracionJornadaRepository
                        .findByIdConfig(idConfiguracionJornada)
                        .orElseThrow(() -> new RuntimeException("Configuración de jornada no encontrada"));

                // Asignar configuración al usuario
                usuario.setConfiguracionJornada(configuracion);

                // Guardar primero para que el usuario tenga la configuración asignada
                usuario = usuarioRepository.save(usuario);

                // Generar jornadas laborales automáticamente para los próximos 30 días
                try {
                    java.time.LocalDate hoy = java.time.LocalDate.now();
                    java.time.LocalDate finRango = hoy.plusDays(30);
                    configuracionJornadaService.generarJornadasParaUsuario(
                            idUsuario, idConfiguracionJornada, hoy, finRango);
                    System.out.println("✅ Jornadas generadas para usuario " + idUsuario + " (próximos 30 días)");
                } catch (Exception e) {
                    System.err.println("⚠️ Error generando jornadas: " + e.getMessage());
                    // No falla la actualización si hay error al generar jornadas
                }

                return usuario;
            } else {
                // Si se pasa null o 0, se desasigna la configuración
                usuario.setConfiguracionJornada(null);
            }

            return usuarioRepository.save(usuario);
        }
        return null;
    }

    @Transactional
    public Usuario asignarUsuarioAGrupo(Long idUsuario, Long idGrupo, Subgrupo subgrupo, Departamento departamento, Rol rol) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByIdUsuario(idUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            Optional<Grupo> grupoOpt = grupoService.findByIdGrupo(idGrupo);
            if (grupoOpt.isPresent()) {
                usuario.setGrupo(grupoOpt.get());
                usuario.setSubgrupo(subgrupo);
                usuario.setDepartamento(departamento);
                usuario.setRol(rol);
                
                return usuarioRepository.save(usuario);
            }
        }
        return null;
    }

}