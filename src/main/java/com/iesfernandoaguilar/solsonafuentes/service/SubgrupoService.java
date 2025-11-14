package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.DepartamentoRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.SubgrupoRepository;
import com.iesfernandoaguilar.solsonafuentes.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class SubgrupoService {

    @Autowired
    private SubgrupoRepository subgrupoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Subgrupo> obtenerSubgrupos(Long idGrupo) {
        return subgrupoRepository.obtenerSubgrupos(idGrupo);
    }

    public Subgrupo save(Subgrupo subgrupo) {
    return subgrupoRepository.save(subgrupo);
}

    public Optional<Subgrupo> findByIdSubgrupo(Long idSubgrupo) {
        return subgrupoRepository.findById(idSubgrupo);
    }

    public List<Subgrupo> buscarSubgruposPorNombre(Long idGrupo, String filtro) {
        return subgrupoRepository.buscarSubgruposPorNombre(idGrupo, filtro);
    }

    @Transactional
    public void eliminarSubgrupo(Long idSubgrupo) {
        // Obtener todos los departamentos del subgrupo
        List<Departamento> departamentos = departamentoRepository.obtenerDepartamentos(idSubgrupo);

        // Eliminar cada departamento en cascada
        for (Departamento depto : departamentos) {
            Long idDepartamento = depto.getIdDepartamento();

            // Desvincular todos los usuarios del departamento
            List<Usuario> usuarios = usuarioRepository.findByIdDepartamento(idDepartamento);
            for (Usuario usuario : usuarios) {
                usuario.setDepartamento(null);
                usuarioRepository.save(usuario);
            }
            entityManager.flush(); // Forzar persistencia de cambios en usuarios

            // Eliminar el departamento (las relaciones ManyToMany se limpiarán automáticamente)
            departamentoRepository.deleteByIdDepartamento(idDepartamento);
            entityManager.flush(); // Forzar eliminación del departamento
        }

        // Ahora podemos eliminar el subgrupo sin violaciones de clave foránea
        subgrupoRepository.deleteByIdSubgrupo(idSubgrupo);
        entityManager.flush(); // Forzar eliminación del subgrupo
    }

    public Subgrupo actualizarSubgrupo(Long idSubgrupo, String nuevoNombre) {
        Optional<Subgrupo> subgrupoOpt = subgrupoRepository.findById(idSubgrupo);
        if (subgrupoOpt.isPresent()) {
            Subgrupo subgrupo = subgrupoOpt.get();
            subgrupo.setNombre(nuevoNombre);
            return subgrupoRepository.save(subgrupo);
        }
        return null;
    }

    @Transactional
    public List<Subgrupo> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosSubgrupo filtros, Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Subgrupos");
            return new java.util.ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        List<Subgrupo> resultados = subgrupoRepository.obtenerSubgrupos(filtros.getIdGrupo());

        return aplicarFiltrosASubgrupos(resultados, filtros);
    }

    private boolean esAdmin(Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private List<Subgrupo> aplicarFiltrosASubgrupos(List<Subgrupo> lista, com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosSubgrupo filtros) {
        return lista.stream()
            .filter(subgrupo -> {
                if (!filtros.tieneBusqueda()) return true;
                String busqueda = filtros.getTextoBusqueda().toLowerCase();
                return subgrupo.getNombre().toLowerCase().contains(busqueda);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
