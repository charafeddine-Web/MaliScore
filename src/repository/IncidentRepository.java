package repository;

import model.Incident;
import model.enums.TypeIncident;
import resources.ConfigDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncidentRepository {
    private Connection conn;

    public IncidentRepository() {
        try {
            ConfigDB config = new ConfigDB();
            this.conn = DatabaseConnection.getInstance(config).getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean save(Incident incident) {
        String sql = "INSERT INTO incident (echeance_id, date_incident, type_incident, score_impact) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, incident.getEcheance().getId());
            stmt.setDate(2, Date.valueOf(incident.getDateIncident()));
            stmt.setString(3, incident.getTypeIncident().name());
            stmt.setInt(4, incident.getScoreImpact());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    incident.setId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Incident findById(Long id) {
        String sql = "SELECT * FROM incident WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Incident incident = new Incident();
                incident.setId(rs.getLong("id"));
                incident.setDateIncident(rs.getDate("date_incident").toLocalDate());
                incident.setScoreImpact(rs.getInt("score_impact"));
                incident.setTypeIncident(TypeIncident.valueOf(rs.getString("type_incident")));
                
                
                return incident;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Incident> findAll() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incident";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Incident incident = new Incident();
                incident.setId(rs.getLong("id"));
                incident.setDateIncident(rs.getDate("date_incident").toLocalDate());
                incident.setScoreImpact(rs.getInt("score_impact"));
                incident.setTypeIncident(TypeIncident.valueOf(rs.getString("type_incident")));
                
                incidents.add(incident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidents;
    }

    public boolean update(Incident incident) {
        String sql = "UPDATE incident SET date_incident=?, type_incident=?, score_impact=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(incident.getDateIncident()));
            stmt.setString(2, incident.getTypeIncident().name());
            stmt.setInt(3, incident.getScoreImpact());
            stmt.setLong(4, incident.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM incident WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Incident> findByEcheanceId(Long echeanceId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incident WHERE echeance_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, echeanceId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Incident incident = new Incident();
                incident.setId(rs.getLong("id"));
                incident.setDateIncident(rs.getDate("date_incident").toLocalDate());
                incident.setScoreImpact(rs.getInt("score_impact"));
                incident.setTypeIncident(TypeIncident.valueOf(rs.getString("type_incident")));
                
                incidents.add(incident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidents;
    }

    public List<Incident> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incident WHERE date_incident BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Incident incident = new Incident();
                incident.setId(rs.getLong("id"));
                incident.setDateIncident(rs.getDate("date_incident").toLocalDate());
                incident.setScoreImpact(rs.getInt("score_impact"));
                incident.setTypeIncident(TypeIncident.valueOf(rs.getString("type_incident")));
                
                incidents.add(incident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidents;
    }
}