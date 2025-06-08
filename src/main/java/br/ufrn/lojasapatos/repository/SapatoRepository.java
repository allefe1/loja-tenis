package br.ufrn.lojasapatos.repository;

import br.ufrn.lojasapatos.model.Sapato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SapatoRepository extends JpaRepository<Sapato, Long> {

    // Para questão 3 - buscar apenas não deletados
    @Query("SELECT s FROM Sapato s WHERE s.isDeleted IS NULL")
    List<Sapato> findAllNotDeleted();

    // Para questão 4 - buscar todos (incluindo deletados)
    List<Sapato> findAll();
}

