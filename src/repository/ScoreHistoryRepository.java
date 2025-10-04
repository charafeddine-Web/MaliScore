package repository;

import model.ScoreHistory;
import resources.ConfigDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreHistoryRepository {
    private Connection conn;

    public ScoreHistoryRepository() {
        try {
            ConfigDB config = new ConfigDB();
            this.conn = DatabaseConnection.getInstance(config).getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(ScoreHistory scoreHistory) {
        if (conn == null) {
            System.out.println("WARNING: Cannot save ScoreHistory - Database connection not available.");
            return;
        }
        
        String sql = "INSERT INTO score_history (personne_id, ancien_score, nouveau_score, raison, date_changement) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, scoreHistory.getPersonneId());
            stmt.setDouble(2, scoreHistory.getAncienScore());
            stmt.setDouble(3, scoreHistory.getNouveauScore());
            stmt.setString(4, scoreHistory.getRaison());
            stmt.setTimestamp(5, Timestamp.valueOf(scoreHistory.getDateChangement()));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                scoreHistory.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ScoreHistory> findByPersonneId(Long personneId) {
        List<ScoreHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM score_history WHERE personne_id = ? ORDER BY date_changement DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, personneId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ScoreHistory history = new ScoreHistory();
                history.setId(rs.getLong("id"));
                history.setPersonneId(rs.getLong("personne_id"));
                history.setAncienScore(rs.getDouble("ancien_score"));
                history.setNouveauScore(rs.getDouble("nouveau_score"));
                history.setRaison(rs.getString("raison"));
                history.setDateChangement(rs.getTimestamp("date_changement").toLocalDateTime());
                
                histories.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    public List<ScoreHistory> findAll() {
        List<ScoreHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM score_history ORDER BY date_changement DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ScoreHistory history = new ScoreHistory();
                history.setId(rs.getLong("id"));
                history.setPersonneId(rs.getLong("personne_id"));
                history.setAncienScore(rs.getDouble("ancien_score"));
                history.setNouveauScore(rs.getDouble("nouveau_score"));
                history.setRaison(rs.getString("raison"));
                history.setDateChangement(rs.getTimestamp("date_changement").toLocalDateTime());
                
                histories.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }
}
