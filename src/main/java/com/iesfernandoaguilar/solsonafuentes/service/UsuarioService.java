package com.iesfernandoaguilar.solsonafuentes.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.enums.Rol;
import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.model.Usuario;
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
    public void eliminarEmpleado(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }


    public Usuario actualizarEmpleado(Long idUsuario, String rol, Long idDepartamento) {
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