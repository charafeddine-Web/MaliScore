package service;

import model.Personne;
import repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private ClientRepository clientRepository;

    public ClientService(){
        this.clientRepository = new ClientRepository();
    }

    public void addClient(Personne per){
        clientRepository.save(per);
    }
    public  Personne findClient(int id){
        return clientRepository.findById(id);
    }
    public List<Personne> findAll(){
        return clientRepository.findAll();
    }
    public void update(Personne pr){
         clientRepository.update(pr);
    }
    public void deleteClient(int id) {
        clientRepository.delete(id);
    }

    public double calculerScore(Personne p) {
        double score = 0;

        score += calculStabiliteProfessionnelle(p);
        score += calculCapaciteFinanciere(p);
        score += calculHistorique(p);
        score += calculRelationClient(p);
        score += calculPatrimoine(p);
        p.setScore(score);
        clientRepository.update(p);
        return score;
    }

    private double calculStabiliteProfessionnelle(Personne p){ return 0;};
    private double calculCapaciteFinanciere(Personne p){return 0;};
    private double calculHistorique(Personne p){return 0;};
    private double calculRelationClient(Personne p){return 0;};
    private double calculPatrimoine(Personne p){return 0;};



}
