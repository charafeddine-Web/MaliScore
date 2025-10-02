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



}
