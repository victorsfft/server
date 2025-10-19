package com.iesfernandoaguilar.solsonafuentes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.SolicitudGrupo;

@Repository
public interface SolicitudGrupoRepository extends JpaRepository<SolicitudGrupo, Long> {
    
}