package com.iesfernandoaguilar.solsonafuentes.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
import com.iesfernandoaguilar.solsonafuentes.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public Optional<Usuario> findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findByIdUsuario(Long idUsuario){
        return usuarioRepository.findByIdUsuario(idUsuario);
    }
}