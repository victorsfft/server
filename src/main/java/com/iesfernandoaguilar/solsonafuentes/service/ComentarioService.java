package com.iesfernandoaguilar.solsonafuentes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Comentario;
import com.iesfernandoaguilar.solsonafuentes.repository.ComentarioRepository;

import jakarta.transaction.Transactional;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    public Optional<Comentario> findByIdComentario(Long idComentario) {
        return comentarioRepository.findByIdComentario(idComentario);
    }

    public List<Comentario> obtenerComentariosPorTarea(Long idTarea) {
        return comentarioRepository.obtenerComentariosPorTarea(idTarea);
    }

    public List<Comentario> obtenerComentariosPorIncidencia(Long idIncidencia) {
        return comentarioRepository.obtenerComentariosPorIncidencia(idIncidencia);
    }

    public List<Comentario> obtenerComentariosPorUsuario(Long idUsuario) {
        return comentarioRepository.obtenerComentariosPorUsuario(idUsuario);
    }

    @Transactional
    public Comentario crearComentario(Comentario comentario) {
        if (comentario.getFechaCreacion() == null) {
            comentario.setFechaCreacion(LocalDateTime.now());
        }
        return comentarioRepository.save(comentario);
    }

    @Transactional
    public Comentario actualizarComentario(Long idComentario, String nuevoTexto) {
        Optional<Comentario> comentarioOpt = comentarioRepository.findByIdComentario(idComentario);
        if (comentarioOpt.isPresent()) {
            Comentario comentario = comentarioOpt.get();
            comentario.setTexto(nuevoTexto);
            return comentarioRepository.save(comentario);
        }
        return null;
    }

    @Transactional
    public void eliminarComentario(Long idComentario) {
        comentarioRepository.deleteByIdComentario(idComentario);
    }
}
