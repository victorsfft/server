package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.repository.DepartamentoRepository;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    public Optional<Departamento> findByIdDepartamento(Long idDepartamento){
        return departamentoRepository.findByIdDepartamento(idDepartamento);
    }
    
    public List<Departamento> obtenerDepartamentos(Long idSubgrupo) {
        return departamentoRepository.obtenerDepartamentos(idSubgrupo);
    }
    

    public Departamento save(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }
}




    

