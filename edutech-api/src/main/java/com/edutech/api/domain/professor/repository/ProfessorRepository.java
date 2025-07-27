package com.edutech.api.domain.professor.repository;

import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.professor.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    boolean existsByCpf(String cpf);

    @Query("""
            SELECT p FROM Professor p 
            WHERE LOWER(p.nome) 
            LIKE LOWER(concat( :nome,'%'))
            """)
    List<Professor> findByNome(@Param("nome") String nome);

    List<Professor> findByModalidade(Modalidade modalidade);
}
