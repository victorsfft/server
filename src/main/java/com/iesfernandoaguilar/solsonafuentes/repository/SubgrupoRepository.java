package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Subgrupo;

@Repository
public interface SubgrupoRepository extends JpaRepository<Subgrupo, Long> {

    @Query("SELECT DISTINCT s FROM Subgrupo s WHERE s.grupo.idGrupo = :idGrupo")
    List<Subgrupo> obtenerSubgrupos(@Param("idGrupo") Long idGrupo);


    @Query("SELECT s FROM Subgrupo s WHERE s.grupo.idGrupo = :idGrupo AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Subgrupo> buscarSubgruposPorNombre(@Param("idGrupo") Long idGrupo, @Param("filtro") String filtro);

    @Modifying
    @Query("DELETE FROM Subgrupo s WHERE s.idSubgrupo = :idSubgrupo")
    void deleteByIdSubgrupo(@Param("idSubgrupo") Long idSubgrupo);

}