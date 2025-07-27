package com.edutech.api.domain.matricula.repository;

import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    long countByTurmaId(Long turmaId);

    long countByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    @Query("SELECT m FROM Matricula m WHERE m.aluno.nome = :nome")
    List<Matricula> findByAlunoNome(@Param("nome") String nome);
}
