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

}
