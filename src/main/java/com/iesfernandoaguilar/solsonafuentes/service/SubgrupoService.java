package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.repository.SubgrupoRepository;

@Service
public class SubgrupoService {

    @Autowired
    private SubgrupoRepository subgrupoRepository;

    public List<Subgrupo> obtenerSubgrupos(Long idGrupo) {
        return subgrupoRepository.obtenerSubgrupos(idGrupo);
    }
    
}
