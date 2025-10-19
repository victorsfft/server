package com.iesfernandoaguilar.solsonafuentes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.SolicitudGrupo;
import com.iesfernandoaguilar.solsonafuentes.repository.SolicitudGrupoRepository;

import jakarta.transaction.Transactional;


@Service
public class SolicitudGrupoService {

    @Autowired
    private SolicitudGrupoRepository solicitudGrupoRepository;

    @Transactional
    public SolicitudGrupo save(SolicitudGrupo solicitudGrupo) {
        return solicitudGrupoRepository.save(solicitudGrupo);
    }
    
}
