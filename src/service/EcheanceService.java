package service;

import model.Echeance;
import repository.EcheanceRepository;

import java.util.List;

public class EcheanceService {
    private final EcheanceRepository echeanceRepository;

    public EcheanceService() {
        this.echeanceRepository = new EcheanceRepository();
    }

    public void addEcheance(Echeance echeance) {
        echeanceRepository.save(echeance);
    }

    public Echeance getEcheanceById(Long id) {
        return echeanceRepository.findById(id);
    }

    public List<Echeance> getAllEcheances() {
        return echeanceRepository.findAll();
    }

    public void updateEcheance(Echeance echeance) {
        echeanceRepository.update(echeance);
    }

    public void deleteEcheance(Long id) {
        echeanceRepository.delete(id);
    }
}
