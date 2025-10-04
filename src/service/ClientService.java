package service;

import model.Employe;
import model.Personne;
import model.Professionnel;
import model.enums.ContratType;
import repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private ClientRepository clientRepository;

    public ClientService(){
        try {
            this.clientRepository = new ClientRepository();
        } catch (Exception e) {
            this.clientRepository = null;
        }
    }

    public boolean addClient(Personne per){
        if (clientRepository != null) {
            try {
                clientRepository.save(per);
                return true;
            } catch (Exception e) {
                System.out.println("[ERROR] Erreur lors de la sauvegarde: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("[WARNING] Impossible de sauvegarder - Base de donnees non disponible");
            return false;
        }
    }
    public  Personne findClient(int id){
        if (clientRepository != null) {
            return clientRepository.findById(id);
        }
        return null;
    }

    public List<Personne> findAll(){
        if (clientRepository != null) {
            return clientRepository.findAll();
        }
        return new ArrayList<>();
    }

    public void update(Personne pr){
        if (clientRepository != null) {
            clientRepository.update(pr);
        }
    }

    public void deleteClient(int id) {
        if (clientRepository != null) {
            clientRepository.delete(id);
        }
    }



}
