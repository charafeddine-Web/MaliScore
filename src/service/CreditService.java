package service;

import model.Credit;
import repository.CreditRepository;

import java.util.List;

public class CreditService {
    private CreditRepository creditRepository;

    public CreditService(){
        this.creditRepository = new CreditRepository();
    }
    public void addCredit(Credit c){
        creditRepository.save(c);
    }

    public void updateCredit(Credit c){
        creditRepository.update(c);
    }

    public void deleteCredit(long id){
        creditRepository.delete(id);
    }

    public List<Credit> findAllCredit(){
        return creditRepository.findAll();
    }

    public Credit findCreditById(long id){
        return creditRepository.findById(id);
    }


}
