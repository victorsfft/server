package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.DepartamentoRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Departamento> findByIdDepartamento(Long idDepartamento){
        return departamentoRepository.findByIdDepartamento(idDepartamento);
    }
    
    public List<Departamento> obtenerDepartamentos(Long idSubgrupo) {
        return departamentoRepository.obtenerDepartamentos(idSubgrupo);
    }

    public List<Departamento> obtenerTodosDepartamentos(Long idGrupo) {
        return departamentoRepository.obtenerTodosDepartamentos(idGrupo);
    }
    
    public Departamento save(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    public List<Departamento> buscarDepartamentosPorNombre(Long idGrupo, String filtro) {
        return departamentoRepository.buscarDepartamentosPorNombre(idGrupo, filtro);
    }

    @Transactional
    public void eliminarDepartamento(Long idDepartamento) {
        // Primero, desvincular todos los usuarios del departamento
        List<Usuario> usuarios = usuarioRepository.findByIdDepartamento(idDepartamento);
        for (Usuario usuario : usuarios) {
            usuario.setDepartamento(null);
            usuarioRepository.save(usuario);
        }
        entityManager.flush(); // Forzar persistencia de cambios en usuarios

        // Las relaciones ManyToMany con Tarea y Evento se manejan automáticamente
        // al eliminar el departamento, ya que están mapeadas con "mappedBy"

        // Ahora podemos eliminar el departamento sin violaciones de clave foránea
        departamentoRepository.deleteByIdDepartamento(idDepartamento);
        entityManager.flush(); // Forzar eliminación del departamento
    }

    public Departamento actualizarDepartamento(Long idDepartamento, String nuevoNombre) {
        Optional<Departamento> departamentoOpt = departamentoRepository.findByIdDepartamento(idDepartamento);
        if (departamentoOpt.isPresent()) {
            Departamento departamento = departamentoOpt.get();
            departamento.setNombre(nuevoNombre);
            return departamentoRepository.save(departamento);
        }
        return null;
    }

    @Transactional
    public List<Departamento> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosDepartamento filtros, Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Departamentos");
            return new java.util.ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        List<Departamento> resultados = departamentoRepository.obtenerTodosDepartamentos(filtros.getIdGrupo());

        return aplicarFiltrosADepartamentos(resultados, filtros);
    }

    private boolean esAdmin(Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private List<Departamento> aplicarFiltrosADepartamentos(List<Departamento> lista, com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosDepartamento filtros) {
        return lista.stream()
            .filter(depto -> !filtros.tieneSubgrupo() || (depto.getSubgrupo() != null && depto.getSubgrupo().getIdSubgrupo().equals(filtros.getIdSubgrupo())))
            .filter(depto -> {
                if (!filtros.tieneBusqueda()) return true;
                String busqueda = filtros.getTextoBusqueda().toLowerCase();
                return depto.getNombre().toLowerCase().contains(busqueda) ||
                       (depto.getSubgrupo() != null && depto.getSubgrupo().getNombre().toLowerCase().contains(busqueda));
            })
            .collect(java.util.stream.Collectors.toList());
    }
}




    

