package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    
    Optional<Grupo> findByNombre(String nombre);
    Optional<Grupo> findByCif(String cif);
    Optional<Grupo> findByIdGrupo(Long idGrupo);

}
