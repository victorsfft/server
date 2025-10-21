package com.iesfernandoaguilar.solsonafuentes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;
import com.iesfernandoaguilar.solsonafuentes.repository.DepartamentoRepository;

import jakarta.transaction.Transactional;

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

    public List<Departamento> obtenerTodosDepartamentos(Long idGrupo) {
        return departamentoRepository.obtenerTodosDepartamentos(idGrupo);
    }
    
    public Departamento save(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    public List<Departamento> buscarDepartamentosPorNombre(Long idGrupo, String filtro) {
        return departamentoRepository.buscarDepartamentosPorNombre(idGrupo, filtro);
    }

    @Transactional
    public void eliminarDepartamento(Long idDepartamento) {
        departamentoRepository.deleteByIdDepartamento(idDepartamento);
    }

    public Departamento actualizarDepartamento(Long idDepartamento, String nuevoNombre) {
        Optional<Departamento> departamentoOpt = departamentoRepository.findByIdDepartamento(idDepartamento);
        if (departamentoOpt.isPresent()) {
            Departamento departamento = departamentoOpt.get();
            departamento.setNombre(nuevoNombre);
            return departamentoRepository.save(departamento);
        }
        return null;
    }
}




    

