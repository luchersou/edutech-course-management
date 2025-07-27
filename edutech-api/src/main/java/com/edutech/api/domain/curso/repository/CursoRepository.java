package com.edutech.api.domain.curso.repository;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByNivel(NivelCurso nivel);

    Optional<Curso> findByNome(String nome);

    @Query("""
            SELECT c FROM Curso c WHERE 
            (:cargaHorariaMin IS NULL OR c.cargaHorariaTotal >= :cargaHorariaMin) AND 
            (:cargaHorariaMax IS NULL OR c.cargaHorariaTotal <= :cargaHorariaMax)
            """)
    List<Curso> findByCargaHorariaTotalBetween(@Param("cargaHorariaMin") Integer cargaHorariaMin,
                                               @Param("cargaHorariaMax") Integer cargaHorariaMax);

    List<Curso> findByProfessoresId(Long professorId);
}
