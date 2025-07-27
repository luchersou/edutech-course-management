package com.edutech.api.domain.aluno.repository;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.aluno.enums.StatusAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query("""
            SELECT p FROM Aluno p 
            WHERE LOWER(p.nome) 
            LIKE LOWER(concat( :nome,'%'))
            """)
    List<Aluno> findByNome(@Param("nome") String nome);

    boolean existsByCpf(String cpf);

    Page<Aluno> findByStatus(StatusAluno status, Pageable pageable);
}
