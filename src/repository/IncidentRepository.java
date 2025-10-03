package repository;

import model.Echeance;
import model.Incident;
import model.enums.TypeIncident;
import resources.ConfigDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncidentRepository {

    private  Connection conn;

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
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, incident.getEcheance().getId());
            ps.setDate(2, Date.valueOf(incident.getDateIncident()));
            ps.setString(3, incident.getTypeIncident().name());
            ps.setInt(4, incident.getScoreImpact());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        incident.setId(rs.getLong(1));
                    }
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
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapToIncident(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Incident> findAll() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incident";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                incidents.add(mapToIncident(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidents;
    }

    public boolean update(Incident incident) {
        String sql = "UPDATE incident SET echeance_id = ?, date_incident = ?, type_incident = ?, score_impact = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, incident.getEcheance().getId());
            ps.setDate(2, Date.valueOf(incident.getDateIncident()));
            ps.setString(3, incident.getTypeIncident().name());
            ps.setInt(4, incident.getScoreImpact());
            ps.setLong(5, incident.getId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM incident WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Incident mapToIncident(ResultSet rs) throws SQLException {
        Incident incident = new Incident();
        incident.setId(rs.getLong("id"));

        Echeance echeance = new Echeance();
        echeance.setId(rs.getLong("echeance_id"));
        incident.setEcheance(echeance);

        incident.setDateIncident(rs.getDate("date_incident").toLocalDate());
        incident.setTypeIncident(TypeIncident.valueOf(rs.getString("type_incident")));
        incident.setScoreImpact(rs.getInt("score_impact"));

        return incident;
    }
}
