package com.edutech.api.domain.turma.repository;

import com.edutech.api.domain.turma.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    Optional<Turma> findById(Long id);

    Optional<Turma> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long turmaId);
}
