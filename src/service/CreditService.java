package service;

import model.*;
import model.enums.DecisionType;
import model.enums.StatutPaiement;
import repository.CreditRepository;
import repository.EcheanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        List<Credit> credits = creditRepository.findByPersonneId(personne.getId());
        boolean creditEnCours = credits.stream()
                .anyMatch(cr -> cr.getMontantOctroye() > 0 && !isCreditRembourse(cr));

        if (creditEnCours) {
            System.out.println("Le client a déjà un crédit en cours et ne peut pas en demander un nouveau.");
            return;
        }
        double score = personne.getScore();
        c.setDecision(deciderCredit(score));

        double montantOctroye = calculerMontantOctroye(personne);

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

    private boolean isCreditRembourse(Credit credit) {
        List<Echeance> echeances = echeanceRepository.findByCreditId(credit.getId());
        return echeances.stream().allMatch(e -> e.getDatePaiement() != null);
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

    private double calculerMontantOctroye(Personne client) {
        double base = 0.0;

        if (client instanceof Employe) {
            base = ((Employe) client).getSalaire();
        } else if (client instanceof Professionnel) {
            base = ((Professionnel) client).getRevenu();
        } else {
            return 0.0;
        }

        boolean estNouveau = estNouveauClient(client);

        if (estNouveau) {
            if (client.getScore() >= 70 && client.getCreatedAt() != null &&
                    client.getCreatedAt().isBefore(LocalDateTime.now().minusYears(2))) {
                return base * 4;
            } else if (client.getScore() >= 80) {
                return base * 10;
            } else if (client.getScore() >= 60) {
                return base * 7;
            } else {
                return 0.0;
            }
        } else {
            if (client.getScore() >= 80) {
                return base * 10;
            } else if (client.getScore() >= 60) {
                return base * 7;
            } else {
                return 0.0;
            }
        }
    }

    private boolean estNouveauClient(Personne client) {
        List<Credit> credits = creditRepository.findByPersonneId(client.getId());
        return credits.isEmpty();
    }



}
