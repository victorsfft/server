package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iesfernandoaguilar.solsonafuentes.enums.EstadoTarea;
import com.iesfernandoaguilar.solsonafuentes.enums.Prioridad;
import com.iesfernandoaguilar.solsonafuentes.model.Incidencia;
import com.iesfernandoaguilar.solsonafuentes.repository.IncidenciaRepository;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    public List<Incidencia> obtenerIncidenciasPorGrupo(Long idGrupo) {
        return incidenciaRepository.obtenerIncidenciasPorGrupo(idGrupo);
    }

    public List<Incidencia> obtenerIncidenciasPorUsuario(Long idUsuario) {
        return incidenciaRepository.obtenerIncidenciasPorUsuario(idUsuario);
    }

    public Optional<Incidencia> obtenerIncidenciaPorId(Long idIncidencia) {
        return incidenciaRepository.findByIdIncidencia(idIncidencia);
    }

    @Transactional
    public Incidencia crearIncidencia(Incidencia incidencia) {
        if (incidencia.getFechaCreacion() == null) {
            incidencia.setFechaCreacion(LocalDateTime.now());
        }
        if (incidencia.getPrioridad() == null) {
            incidencia.setPrioridad(Prioridad.MEDIA);
        }
        if (incidencia.getEstado() == null) {
            incidencia.setEstado(EstadoTarea.PENDIENTE);
        }
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public void cambiarEstado(Long idIncidencia, EstadoTarea nuevoEstado) {
        Optional<Incidencia> incidenciaOpt = incidenciaRepository.findByIdIncidencia(idIncidencia);
        if (incidenciaOpt.isPresent()) {
            Incidencia incidencia = incidenciaOpt.get();
            incidencia.setEstado(nuevoEstado);
            incidenciaRepository.save(incidencia);
        }
    }

    @Transactional
    public void eliminarIncidencia(Long idIncidencia) {
        incidenciaRepository.deleteByIdIncidencia(idIncidencia);
    }

    @Transactional
    public List<Incidencia> obtenerConFiltros(com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosIncidencia filtros, com.iesfernandoaguilar.solsonafuentes.model.Usuario usuarioActual) {
        if (esAdmin(usuarioActual) && !filtros.tieneGrupo()) {
            System.out.println("⚠️ Admin sin grupo seleccionado, devolviendo lista vacía para Incidencias");
            return new java.util.ArrayList<>();
        }

        if (!esAdmin(usuarioActual)) {
            filtros.setIdGrupo(usuarioActual.getGrupo().getIdGrupo());
        }

        List<Incidencia> resultados = incidenciaRepository.obtenerIncidenciasPorGrupo(filtros.getIdGrupo());

        return aplicarFiltrosAIncidencias(resultados, filtros);
    }

    private boolean esAdmin(com.iesfernandoaguilar.solsonafuentes.model.Usuario usuario) {
        String rol = usuario.getRol().name();
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "SUPERADMIN".equalsIgnoreCase(rol);
    }

    private List<Incidencia> aplicarFiltrosAIncidencias(List<Incidencia> lista, com.iesfernandoaguilar.solsonafuentes.model.filtros.FiltrosIncidencia filtros) {
        return lista.stream()
            .filter(incidencia -> !filtros.tieneUsuario() || (incidencia.getUsuario() != null && incidencia.getUsuario().getIdUsuario().equals(filtros.getIdUsuario())))
            .filter(incidencia -> !filtros.tienePrioridad() || (incidencia.getPrioridad() != null && incidencia.getPrioridad().name().equalsIgnoreCase(filtros.getPrioridad())))
            .filter(incidencia -> !filtros.tieneEstado() || (incidencia.getEstado() != null && incidencia.getEstado().name().equalsIgnoreCase(filtros.getEstado())))
            .filter(incidencia -> {
                if (!filtros.tieneBusqueda()) return true;
                String busqueda = filtros.getTextoBusqueda().toLowerCase();
                return incidencia.getTitulo().toLowerCase().contains(busqueda) ||
                       incidencia.getDescripcion().toLowerCase().contains(busqueda);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
