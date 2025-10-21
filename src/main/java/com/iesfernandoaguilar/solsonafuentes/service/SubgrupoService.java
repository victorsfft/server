package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;
import com.iesfernandoaguilar.solsonafuentes.repository.SubgrupoRepository;

import jakarta.transaction.Transactional;

@Service
public class SubgrupoService {

    @Autowired
    private SubgrupoRepository subgrupoRepository;

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
        subgrupoRepository.deleteByIdSubgrupo(idSubgrupo);
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
    
}
