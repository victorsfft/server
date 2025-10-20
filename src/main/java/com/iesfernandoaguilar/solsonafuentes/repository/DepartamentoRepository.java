package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;


@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    Optional<Departamento> findByIdDepartamento(Long idDepartamento);

    @Query("SELECT DISTINCT d FROM Departamento d WHERE d.subgrupo.idSubgrupo = :idSubgrupo")
    List<Departamento> obtenerDepartamentos(@Param("idSubgrupo") Long idSubgrupo);

    
    
}