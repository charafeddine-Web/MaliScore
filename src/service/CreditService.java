package service;

import model.*;
import model.enums.DecisionType;
import model.enums.StatutPaiement;
import repository.CreditRepository;
import repository.EcheanceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreditService {

    private CreditRepository creditRepository;
    private EcheanceRepository echeanceRepository ;

    public CreditService(){
        this.creditRepository = new CreditRepository();
        this.echeanceRepository = new EcheanceRepository();
    }

    public void addCredit(Credit c){

        ClientService clientService = new ClientService();
        Personne personne = clientService.findClient(Math.toIntExact(c.getPersonneId()));
        if (personne == null) {
            System.out.println("Client introuvable !");
            return;
        }
        double score = personne.getScore();


        c.setDecision(deciderCredit(score));

        boolean estNouveauClient = estNouveauClient(personne);
        double montantOctroye = calculMontantOctroye(personne, score, estNouveauClient);

        if (c.getDecision() == DecisionType.REFUS_AUTOMATIQUE) {
            System.out.println("Crédit refusé automatiquement en raison du score insuffisant (" + score + ").");
            return;
        }

        c.setMontantOctroye(montantOctroye);

        creditRepository.save(c);

        List<Echeance> echeances = generateEcheances(c);
        for (Echeance e : echeances) {
            echeanceRepository.save(e);
        }

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



    private List<Echeance> generateEcheances(Credit c) {
        List<Echeance> echeances = new ArrayList<>();
        double mensualite = (c.getMontantOctroye()*c.getTauxInteret()) / c.getDureeEnMois();
        LocalDate dateEcheance = c.getDateCredit().plusMonths(1);

        for (int i = 1; i <= c.getDureeEnMois(); i++) {
            Echeance e = new Echeance();
            e.setCreditId(c.getId());
            e.setDateEcheance(dateEcheance);
            e.setMensualite(mensualite);
            e.setStatutPaiement(StatutPaiement.PAYEATEMPS);
            echeances.add(e);

            dateEcheance = dateEcheance.plusMonths(1);
        }

        return echeances;
    }
    private DecisionType deciderCredit(double score) {
        if (score >= 80) {
            return DecisionType.ACCORD_IMMEDIAT;
        } else if (score >= 60) {
            return DecisionType.ETUDE_MANUELLE;
        } else {
            return DecisionType.REFUS_AUTOMATIQUE;
        }
    }


    private double calculMontantOctroye(Personne personne, double score, boolean estNouveauClient) {
        double baseMontant;

        if (personne instanceof Employe) {
            baseMontant = ((Employe) personne).getSalaire();
        } else if (personne instanceof Professionnel) {
            baseMontant = ((Professionnel) personne).getRevenu();
        } else {
            baseMontant = 0;
        }

        if (estNouveauClient) {
            return baseMontant * 4;
        } else {
            if (score > 80) {
                return baseMontant * 10;
            } else {
                return baseMontant * 7;
            }
        }
    }
    private boolean estNouveauClient(Personne personne) {
        List<Credit> credits = creditRepository.findAll().stream()
                .filter(c -> c.getPersonneId() == personne.getId())
                .collect(Collectors.toList());

        return credits.isEmpty();
    }

}
