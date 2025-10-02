package repository;

import model.Credit;
import model.enums.CreditType;
import resources.ConfigDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreditRepository {
    private Connection conn;

    public CreditRepository(){
        try {
            ConfigDB config = new ConfigDB();
            this.conn = DatabaseConnection.getInstance(config).getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }    }

    public void save(Credit c) {
        String sql = "INSERT INTO credit (personne_id, date_credit, montant_demande, montant_octroye, taux_interet, duree_en_mois, type_credit, decision) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, c.getPersonneId());
            stmt.setDate(2, Date.valueOf(c.getDateCredit()));
            stmt.setDouble(3, c.getMontantDemande());
            stmt.setDouble(4, c.getMontantOctroye());
            stmt.setDouble(5, c.getTauxInteret());
            stmt.setInt(6, c.getDureeEnMois());
            stmt.setString(7, c.getTypeCredit().name());
            stmt.setString(8, c.getDecision() != null ? c.getDecision().name() : null);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Credit findById(long id) {
        String sql = "SELECT * FROM credit WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Credit c = new Credit();
                c.setId(rs.getLong("id"));
                c.setPersonneId(rs.getLong("personne_id"));
                c.setDateCredit(rs.getDate("date_credit").toLocalDate());
                c.setMontantDemande(rs.getDouble("montant_demande"));
                c.setMontantOctroye(rs.getDouble("montant_octroye"));
                c.setTauxInteret(rs.getDouble("taux_interet"));
                c.setDureeEnMois(rs.getInt("duree_en_mois"));
                String type = rs.getString("type_credit");
                if (type != null) {
                    c.setTypeCredit(CreditType.valueOf(type));
                }
                String decision = rs.getString("decision");
                if (decision != null) {
                    c.setDecision(model.enums.DecisionType.valueOf(decision));
                }
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Credit> findAll() {

        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM credit";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Credit c = new Credit();
                c.setId(rs.getLong("id"));
                c.setPersonneId(rs.getLong("personne_id"));
                c.setDateCredit(rs.getDate("date_credit").toLocalDate());
                c.setMontantDemande(rs.getDouble("montant_demande"));
                c.setMontantOctroye(rs.getDouble("montant_octroye"));
                c.setTauxInteret(rs.getDouble("taux_interet"));
                c.setDureeEnMois(rs.getInt("duree_en_mois"));
                c.setTypeCredit(model.enums.CreditType.valueOf(rs.getString("type_credit")));
                String decision = rs.getString("decision");
                if (decision != null) {
                    c.setDecision(model.enums.DecisionType.valueOf(decision));
                }
                credits.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credits;
    }

    public void delete(long id) {
        String sql = "DELETE FROM credit WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Credit c) {
        String sql = "UPDATE credit SET montant_demande=?, montant_octroye=?, taux_interet=?, duree_en_mois=?, type_credit=?, decision=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, c.getMontantDemande());
            stmt.setDouble(2, c.getMontantOctroye());
            stmt.setDouble(3, c.getTauxInteret());
            stmt.setInt(4, c.getDureeEnMois());
            stmt.setString(5, c.getTypeCredit().name());
            stmt.setString(6, c.getDecision() != null ? c.getDecision().name() : null);
            stmt.setLong(7, c.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
