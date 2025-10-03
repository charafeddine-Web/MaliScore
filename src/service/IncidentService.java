package service;

import model.Incident;
import repository.IncidentRepository;

import java.sql.SQLException;
import java.util.List;

public class IncidentService {

    private IncidentRepository incidentRepository;

    public IncidentService() {
        this.incidentRepository = new IncidentRepository();
    }

    public boolean addIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public boolean updateIncident(Incident incident) {
        return incidentRepository.update(incident);
    }

    public boolean deleteIncident(Long id) {
        return incidentRepository.delete(id);
    }
}
