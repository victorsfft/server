package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Evento;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.EventoRepository;

import jakarta.transaction.Transactional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public Optional<Evento> findByIdEvento(Long idEvento) {
        return eventoRepository.findByIdEvento(idEvento);
    }

    public Optional<Evento> findByIdEventoWithUsuarios(Long idEvento) {
        return eventoRepository.findByIdEventoWithUsuarios(idEvento);
    }

    public Optional<Evento> findByIdEventoWithDepartamentos(Long idEvento) {
        return eventoRepository.findByIdEventoWithDepartamentos(idEvento);
    }

    @Transactional
    public List<Evento> obtenerEventosPorGrupo(Long idGrupo) {
        List<Evento> eventos = eventoRepository.obtenerEventosPorGrupo(idGrupo);
        if (!eventos.isEmpty()) {
            eventoRepository.fetchDepartamentosInvitados(eventos);
        }
        return eventos;
    }

    public List<Evento> obtenerEventosDeUsuario(Long idUsuario) {
        return eventoRepository.obtenerEventosDeUsuario(idUsuario);
    }

    public List<Evento> obtenerEventosPorDepartamento(Long idDepartamento) {
        return eventoRepository.obtenerEventosPorDepartamento(idDepartamento);
    }

    @Transactional
    public Evento crearEvento(Evento evento) {
        if (evento.getFechaCreacion() == null) {
            evento.setFechaCreacion(LocalDateTime.now());
        }
        if (evento.getSeRepite() == null) {
            evento.setSeRepite(false);
        }
        if (evento.getDiasRepeticion() == null) {
            evento.setDiasRepeticion(0);
        }
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento actualizarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento asignarUsuario(Long idEvento, Usuario usuario) {
        try {
            Optional<Evento> eventoOpt = eventoRepository.findByIdEventoWithUsuarios(idEvento);
            if (eventoOpt.isPresent()) {
                Evento evento = eventoOpt.get();
                if (!evento.getUsuariosAsistentes().contains(usuario)) {
                    evento.addUsuarioAsistente(usuario);
                    Evento resultado = eventoRepository.save(evento);
                    eventoRepository.flush();
                    return resultado;
                }
                return evento;
            } else {
                System.err.println("Error: Evento con ID " + idEvento + " no encontrado");
            }
        } catch (Exception e) {
            System.err.println("Error al asignar usuario a evento: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public Evento asignarDepartamento(Long idEvento, Departamento departamento) {
        try {
            Optional<Evento> eventoOpt = eventoRepository.findByIdEventoWithDepartamentos(idEvento);
            if (eventoOpt.isPresent()) {
                Evento evento = eventoOpt.get();
                if (!evento.getDepartamentosInvitados().contains(departamento)) {
                    evento.addDepartamentoInvitado(departamento);
                    Evento resultado = eventoRepository.save(evento);
                    eventoRepository.flush();
                    return resultado;
                }
                return evento;
            } else {
                System.err.println("Error: Evento con ID " + idEvento + " no encontrado");
            }
        } catch (Exception e) {
            System.err.println("Error al asignar departamento a evento: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public void eliminarEvento(Long idEvento) {
        eventoRepository.deleteByIdEvento(idEvento);
    }
}
