package repository;

import model.Echeance;
import resources.ConfigDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EcheanceRepository {

    private Connection conn;

    public EcheanceRepository(){
        try {
            ConfigDB config = new ConfigDB();
            this.conn = DatabaseConnection.getInstance(config).getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(Echeance e) {
        String sql = "INSERT INTO echeance (credit_id, date_echeance, mensualite, date_paiement, statut_paiement) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, e.getCreditId());
            stmt.setDate(2, Date.valueOf(e.getDateEcheance()));
            stmt.setDouble(3, e.getMensualite());
            if (e.getDatePaiement() != null) {
                stmt.setDate(4, Date.valueOf(e.getDatePaiement()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, e.getStatutPaiement().name());
            stmt.executeUpdate();
            
            // Récupérer l'ID généré et l'assigner à l'échéance
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                e.setId(rs.getLong(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(Echeance e) {
        String sql = "UPDATE echeance SET date_echeance = ?, mensualite = ?, date_paiement = ?, statut_paiement = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(e.getDateEcheance()));
            stmt.setDouble(2, e.getMensualite());
            if (e.getDatePaiement() != null) {
                stmt.setDate(3, Date.valueOf(e.getDatePaiement()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setString(4, e.getStatutPaiement().name());
            stmt.setLong(5, e.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM echeance WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Echeance supprimé avec succès !");
            } else {
                System.out.println("Aucun Echeance trouvé avec cet ID.");
            }           } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Echeance findById(long id) {
        String sql = "SELECT * FROM echeance WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Echeance e = new Echeance();
                e.setId(rs.getLong("id"));
                e.setCreditId(rs.getLong("credit_id"));
                e.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
                e.setMensualite(rs.getDouble("mensualite"));
                Date dp = rs.getDate("date_paiement");
                if (dp != null) e.setDatePaiement(dp.toLocalDate());
                e.setStatutPaiement(model.enums.StatutPaiement.valueOf(rs.getString("statut_paiement")));
                return e;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Echeance> findAll() {
        List<Echeance> list = new ArrayList<>();
        String sql = "SELECT * FROM echeance";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Echeance e = new Echeance();
                e.setId(rs.getLong("id"));
                e.setCreditId(rs.getLong("credit_id"));
                e.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
                e.setMensualite(rs.getDouble("mensualite"));
                Date dp = rs.getDate("date_paiement");
                if (dp != null) e.setDatePaiement(dp.toLocalDate());
                e.setStatutPaiement(model.enums.StatutPaiement.valueOf(rs.getString("statut_paiement")));
                list.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }


    public List<Echeance> findByCreditId(Long creditId) {
        List<Echeance> list = new ArrayList<>();
        String sql = "SELECT * FROM echeance WHERE credit_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, creditId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Echeance e = new Echeance();
                e.setId(rs.getLong("id"));
                e.setCreditId(rs.getLong("credit_id"));
                e.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
                e.setMensualite(rs.getDouble("mensualite"));

                Date dp = rs.getDate("date_paiement");
                if (dp != null) e.setDatePaiement(dp.toLocalDate());

                e.setStatutPaiement(model.enums.StatutPaiement.valueOf(rs.getString("statut_paiement")));
                list.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

}
