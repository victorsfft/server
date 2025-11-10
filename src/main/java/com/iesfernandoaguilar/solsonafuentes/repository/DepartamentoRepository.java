package com.iesfernandoaguilar.solsonafuentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iesfernandoaguilar.solsonafuentes.model.Departamento;


@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    Optional<Departamento> findByIdDepartamento(Long idDepartamento);

    @Query("SELECT DISTINCT d FROM Departamento d WHERE d.subgrupo.idSubgrupo = :idSubgrupo")
    List<Departamento> obtenerDepartamentos(@Param("idSubgrupo") Long idSubgrupo);

    @Query("SELECT DISTINCT d FROM Departamento d WHERE d.subgrupo.grupo.idGrupo = :idGrupo")
    List<Departamento> obtenerTodosDepartamentos(@Param("idGrupo") Long idGrupo);

    @Query("SELECT d FROM Departamento d WHERE d.subgrupo.grupo.idGrupo = :idGrupo AND LOWER(d.nombre) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Departamento> buscarDepartamentosPorNombre(@Param("idGrupo") Long idGrupo, @Param("filtro") String filtro);

    @Modifying
    @Query("DELETE FROM Departamento d WHERE d.idDepartamento = :idDepartamento")
    void deleteByIdDepartamento(@Param("idDepartamento") Long idDepartamento);

}