package repository;

import model.Employe;
import model.Personne;
import model.Professionnel;
import resources.ConfigDB;
import model.enums.SecteurType;
import model.enums.ContratType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private Connection conn;
    public ClientRepository(){
        try {
            ConfigDB config = new ConfigDB();
            this.conn = DatabaseConnection.getInstance(config).getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void save(Personne p){
        String sql = "INSERT INTO personne (type_personne, nom, prenom, date_naissance, ville, nombre_enfants, investissement, placement, situation_familiale, created_at, score) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1,p instanceof Employe ? "EMPLOYE" : "PROFESSIONNEL");
            stmt.setString(2, p.getNom());
            stmt.setString(3, p.getPrenom());
            stmt.setDate(4, Date.valueOf(p.getDateNaissance()));
            stmt.setString(5, p.getVille());
            stmt.setInt(6, p.getNombreEnfants());
            stmt.setBoolean(7, p.getInvestissement());
            stmt.setBoolean(8, p.getPlacement());
            stmt.setString(9, p.getSituationFamiliale());
            if (p.getCreatedAt() == null) {
                p.setCreatedAt(java.time.LocalDateTime.now());
            }
            stmt.setTimestamp(10, Timestamp.valueOf(p.getCreatedAt()));

            stmt.setDouble(11, p.getScore());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getLong(1));
            }

            if (p instanceof Employe) {
                Employe e = (Employe) p;
                String sqlEmp = "INSERT INTO employe (id, salaire, anciennete, poste, type_contrat, secteur) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement st = conn.prepareStatement(sqlEmp)) {
                    st.setLong(1, e.getId());
                    st.setDouble(2, e.getSalaire());
                    st.setInt(3, e.getAnciennete());
                    st.setString(4, e.getPoste());
                    st.setString(5, e.getTypeContrat().name());
                    st.setString(6, e.getSecteur().name());
                    st.executeUpdate();
                }
            } else if (p instanceof Professionnel) {
                Professionnel prof = (Professionnel) p;
                String sqlProf = "INSERT INTO professionnel (id, revenu, immatriculation_fiscale, secteur_activite, activite) VALUES (?,?,?,?,?)";
                try (PreparedStatement stm = conn.prepareStatement(sqlProf)) {
                    stm.setLong(1, prof.getId());
                    stm.setDouble(2, prof.getRevenu());
                    stm.setString(3, prof.getImmatriculationFiscale());
                    stm.setString(4, prof.getSecteurActivite());
                    stm.setString(5, prof.getActivite());
                    stm.executeUpdate();
                }
            }

        }

        catch (SQLException e){
            e.printStackTrace();
        }



    }

    public Personne findById(int id){
        String sql = "SELECT * FROM personne WHERE id = ?";
        try(PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type_personne");

                if ("EMPLOYE".equals(type)) {

                    Employe e = new Employe();
                    e.setId(rs.getLong("id"));
                    e.setNom(rs.getString("nom"));
                    e.setPrenom(rs.getString("prenom"));
                    e.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
                    e.setVille(rs.getString("ville"));
                    e.setNombreEnfants(rs.getInt("nombre_enfants"));
                    e.setInvestissement(rs.getBoolean("investissement"));
                    e.setPlacement(rs.getBoolean("placement"));
                    e.setSituationFamiliale(rs.getString("situation_familiale"));
                    e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    e.setScore(rs.getDouble("score"));

                    String sqlEmp = "SELECT * FROM employe WHERE id = ?";
                    try (PreparedStatement stmtEmp = conn.prepareStatement(sqlEmp)) {
                        stmtEmp.setLong(1, id);
                        ResultSet rsEmp = stmtEmp.executeQuery();
                        if (rsEmp.next()) {
                            e.setSalaire(rsEmp.getDouble("salaire"));
                            e.setAnciennete(rsEmp.getInt("anciennete"));
                            e.setPoste(rsEmp.getString("poste"));
                            e.setTypeContrat(Enum.valueOf(model.enums.ContratType.class, rsEmp.getString("type_contrat")));
                            e.setSecteur(Enum.valueOf(model.enums.SecteurType.class, rsEmp.getString("secteur")));
                        }
                    }
                    return e;

                } else if ("PROFESSIONNEL".equals(type)) {
                    Professionnel p = new Professionnel();
                    p.setId(rs.getLong("id"));
                    p.setNom(rs.getString("nom"));
                    p.setPrenom(rs.getString("prenom"));
                    p.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
                    p.setVille(rs.getString("ville"));
                    p.setNombreEnfants(rs.getInt("nombre_enfants"));
                    p.setInvestissement(rs.getBoolean("investissement"));
                    p.setPlacement(rs.getBoolean("placement"));
                    p.setSituationFamiliale(rs.getString("situation_familiale"));
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    p.setScore(rs.getDouble("score"));

                    String sqlProf = "SELECT * FROM professionnel WHERE id = ?";
                    try (PreparedStatement stmtProf = conn.prepareStatement(sqlProf)) {
                        stmtProf.setLong(1, id);
                        ResultSet rsProf = stmtProf.executeQuery();
                        if (rsProf.next()) {
                            p.setRevenu(rsProf.getDouble("revenu"));
                            p.setImmatriculationFiscale(rsProf.getString("immatriculation_fiscale"));
                            p.setSecteurActivite(rsProf.getString("secteur_activite"));
                            p.setActivite(rsProf.getString("activite"));
                        }
                    }
                    return p;
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Personne> findAll(){
        List<Personne> clients = new ArrayList<>();

        String sql= "Select * from personne";

        try(PreparedStatement stmt = conn.prepareStatement(sql) ; ResultSet res= stmt.executeQuery()){

            while (res.next()) {
                String type = res.getString("type_personne");
                if ("EMPLOYE".equals(type)) {
                    Employe e = new Employe();
                    e.setId(res.getLong("id"));
                    e.setNom(res.getString("nom"));
                    e.setPrenom(res.getString("prenom"));
                    e.setDateNaissance(res.getDate("date_naissance").toLocalDate());
                    e.setVille(res.getString("ville"));
                    e.setNombreEnfants(res.getInt("nombre_enfants"));
                    e.setInvestissement(res.getBoolean("investissement"));
                    e.setPlacement(res.getBoolean("placement"));
                    e.setSituationFamiliale(res.getString("situation_familiale"));
                    e.setScore(res.getDouble("score"));

                    String sqlEmp = "SELECT * FROM employe WHERE id = ?";
                    try (PreparedStatement stmtEmp = conn.prepareStatement(sqlEmp)) {
                        stmtEmp.setLong(1, e.getId());
                        ResultSet rsEmp = stmtEmp.executeQuery();
                        if (rsEmp.next()) {
                            e.setSalaire(rsEmp.getDouble("salaire"));
                            e.setAnciennete(rsEmp.getInt("anciennete"));
                            e.setPoste(rsEmp.getString("poste"));
                            e.setTypeContrat(Enum.valueOf(ContratType.class, rsEmp.getString("type_contrat")));
                            e.setSecteur(Enum.valueOf(SecteurType.class, rsEmp.getString("secteur")));
                        }
                    }
                    clients.add(e);

                } else if ("PROFESSIONNEL".equals(type)) {

                    Professionnel p = new Professionnel();
                    p.setId(res.getLong("id"));
                    p.setNom(res.getString("nom"));
                    p.setPrenom(res.getString("prenom"));
                    p.setDateNaissance(res.getDate("date_naissance").toLocalDate());
                    p.setVille(res.getString("ville"));
                    p.setNombreEnfants(res.getInt("nombre_enfants"));
                    p.setInvestissement(res.getBoolean("investissement"));
                    p.setPlacement(res.getBoolean("placement"));
                    p.setSituationFamiliale(res.getString("situation_familiale"));
                    p.setScore(res.getDouble("score"));

                    String sqlProf = "SELECT * FROM professionnel WHERE id = ?";
                    try (PreparedStatement stmtProf = conn.prepareStatement(sqlProf)) {
                        stmtProf.setLong(1, p.getId());
                        ResultSet rsProf = stmtProf.executeQuery();
                        if (rsProf.next()) {
                            p.setRevenu(rsProf.getDouble("revenu"));
                            p.setImmatriculationFiscale(rsProf.getString("immatriculation_fiscale"));
                            p.setSecteurActivite(rsProf.getString("secteur_activite"));
                            p.setActivite(rsProf.getString("activite"));
                        }
                    }
                    clients.add(p);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();

        }
        return clients;
    }

    public void update(Personne p){
        String sql = "UPDATE personne SET nom=?, prenom=?, date_naissance=?, ville=?, nombre_enfants=?, "
                + "investissement=?, placement=?, situation_familiale=?, score=? WHERE id=?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setDate(3, Date.valueOf(p.getDateNaissance()));
            stmt.setString(4, p.getVille());
            stmt.setInt(5, p.getNombreEnfants());
            stmt.setBoolean(6, p.getInvestissement());
            stmt.setBoolean(7, p.getPlacement());
            stmt.setString(8, p.getSituationFamiliale());
            stmt.setDouble(9, p.getScore());
            stmt.setLong(10, p.getId());

            stmt.executeUpdate();

            if(p instanceof Employe){
                Employe e = (Employe) p;
                String sqlEmp = "UPDATE employe SET salaire=?, anciennete=?, poste=?, type_contrat=?, secteur=? WHERE id=?";
                try (PreparedStatement stmtEmp = conn.prepareStatement(sqlEmp)) {
                    stmtEmp.setDouble(1, e.getSalaire());
                    stmtEmp.setInt(2, e.getAnciennete());
                    stmtEmp.setString(3, e.getPoste());
                    stmtEmp.setString(4, e.getTypeContrat().name());
                    stmtEmp.setString(5, e.getSecteur().name());
                    stmtEmp.setLong(6, e.getId());
                    stmtEmp.executeUpdate();
                }
            }else if(p instanceof  Professionnel){
                Professionnel pr = (Professionnel) p;
                String sqlProf = "UPDATE professionnel SET revenu=?, immatriculation_fiscale=?, secteur_activite=?, activite=? WHERE id=?";
                try (PreparedStatement stmtProf = conn.prepareStatement(sqlProf)) {
                    stmtProf.setDouble(1, pr.getRevenu());
                    stmtProf.setString(2, pr.getImmatriculationFiscale());
                    stmtProf.setString(3, pr.getSecteurActivite());
                    stmtProf.setString(4, pr.getActivite());
                    stmtProf.setLong(5, pr.getId());
                    stmtProf.executeUpdate();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void delete(int id){
        String sql = "DELETE FROM personne WHERE id=?";

        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1,id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Client supprimé avec succès !");
            } else {
                System.out.println("Aucun client trouvé avec cet ID.");
            }        }catch (SQLException e){
            e.printStackTrace();
        }
    }







}
