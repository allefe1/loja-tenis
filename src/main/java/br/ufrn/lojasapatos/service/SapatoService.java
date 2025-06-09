package br.ufrn.lojasapatos.service;

import br.ufrn.lojasapatos.model.Sapato;
import br.ufrn.lojasapatos.repository.SapatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SapatoService {

    @Autowired
    private SapatoRepository sapatoRepository;

    public List<Sapato> findAllNotDeleted() {
        return sapatoRepository.findAllNotDeleted();
    }

    public List<Sapato> findAll() {
        return sapatoRepository.findAll();
    }

    public Sapato findById(Long id) {
        Optional<Sapato> sapato = sapatoRepository.findById(id);
        return sapato.orElse(null);
    }

    public Sapato save(Sapato sapato) {
        return sapatoRepository.save(sapato);
    }

    public void softDelete(Long id) {
        Sapato sapato = findById(id);
        if (sapato != null) {
            sapato.setIsDeleted(new Date());
            sapatoRepository.save(sapato);
        }
    }

    public void restore(Long id) {
        Sapato sapato = findById(id);
        if (sapato != null) {
            sapato.setIsDeleted(null);
            sapatoRepository.save(sapato);
        }
    }
}
