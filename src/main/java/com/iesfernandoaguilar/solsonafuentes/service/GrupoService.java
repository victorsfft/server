package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Grupo;
import com.iesfernandoaguilar.solsonafuentes.repository.GrupoRepository;

import jakarta.transaction.Transactional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Transactional
    public Grupo save(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    public Optional<Grupo> findByNombre(String nombre){
        return grupoRepository.findByNombre(nombre);
    }

    public Optional<Grupo> findByVat(String cif){
        return grupoRepository.findByCif(cif);
    }

    public Optional<Grupo> findByIdGrupo(Long idGrupo){
        return grupoRepository.findByIdGrupo(idGrupo);
    }
    
}
