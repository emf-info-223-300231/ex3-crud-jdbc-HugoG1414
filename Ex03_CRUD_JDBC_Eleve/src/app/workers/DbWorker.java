package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.SystemLib;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;
    private List<Personne> listePersonnes;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
    }

    @Override
    public void connecterBdMySQL(String nomDB) throws MyDBException {
        final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://172.23.85.187:3306/" + nomDB;
        final String user = "223";
        final String password = "emf123";

        System.out.println("url:" + url_remote);
        try {
            dbConnexion = DriverManager.getConnection(url_remote, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public List<Personne> lirePersonnes() throws MyDBException {
        listePersonnes = new ArrayList<>();
        try {
            Statement st = dbConnexion.createStatement();
            ResultSet rs = st.executeQuery("select * from t_personne");

            while (rs.next()) {
                int pk = rs.getInt("PK_PERS");
                String nom = rs.getString("Nom");
                String prenom = rs.getString("Prenom");
                Date date = rs.getDate("Date_Naissance");
                int noRue = rs.getInt("No_rue");
                String rue = rs.getString("Rue");
                int npa = rs.getInt("NPA");
                String ville = rs.getString("Ville");
                boolean actif = rs.getBoolean("Actif");
                double salaire = rs.getInt("Salaire");
                Date dateModif = rs.getDate("date_modif");
                Personne p = new Personne(pk, nom, prenom, date, noRue, rue, npa, ville, actif, salaire, dateModif);
                listePersonnes.add(p);
            }
        } catch (SQLException s) {

        }
        return listePersonnes;
    }

    @Override
    public void creer(Personne p) throws MyDBException {
        if (p != null) {
            String prep = "INSERT INTO t_personne VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,DEFAULT);";
            try ( PreparedStatement ps = dbConnexion.prepareStatement(prep)) {
                ps.setString(1, p.getNom());
                ps.setString(2, p.getPrenom());
                ps.setDate(3, new java.sql.Date(p.getDateNaissance().getTime()));
                ps.setInt(4, p.getNoRue());
                ps.setString(5, p.getRue());
                ps.setInt(6, p.getNpa());
                ps.setString(7, p.getLocalite());
                ps.setBoolean(8, p.isActif());
                ps.setDouble(9, p.getSalaire());
                ps.setTimestamp(10, new Timestamp((new java.util.Date()).getTime()));
                ps.executeUpdate();
            } catch (SQLException ex) {

            }
        }
    }

    @Override
    public void effacer(Personne p) throws MyDBException {
        if (p != null) {
            String prep = "DELETE from t_personne where PK_PERS=?";
            try ( PreparedStatement ps = dbConnexion.prepareStatement(prep)) {
                ps.setInt(1, p.getPkPers());
                ps.executeUpdate();
            } catch (SQLException ex) {

            }
        }
    }

    @Override
    public Personne lire(int i) throws MyDBException {
        Statement stmt = null;
        Personne p = new Personne();
        try {
            stmt = dbConnexion.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM t_personne " + "WHERE PK_PERS = " + i + ";");
            p = new Personne(
                    res.getInt("PK_PERS"),
                    res.getString("Nom"),
                    res.getString("Prenom"),
                    new java.util.Date(res.getDate("Date_Naissance").getTime()),
                    res.getInt("No_rue"),
                    res.getString("Rue"),
                    res.getInt("NPA"),
                    res.getString("Ville"),
                    res.getByte("Actif") == 1,
                    res.getDouble("Salaire"),
                    new java.util.Date(res.getDate("date_modif").getTime())
            );
        } catch (SQLException e) {

        }
        return p;
    }

    @Override
    public void modifier(Personne p) throws MyDBException {
        if (p != null) {
            String prep = "UPDATE t_personne set PK_PERS = ?, Prenom = ?, Nom = ?, Date_naissance = ?, No_rue = ?, Rue = ?, NPA = ?, Ville = ?, Actif = ?, Salaire = ?, date_modif = ? where PK_PERS=?";
            try ( PreparedStatement ps = dbConnexion.prepareStatement(prep)) {
                ps.setInt(1, p.getPkPers());
                ps.setString(2, p.getPrenom());
                ps.setString(3, p.getNom());
                ps.setDate(4, new java.sql.Date(p.getDateNaissance().getTime()));
                ps.setInt(5, p.getNoRue());
                ps.setString(6, p.getRue());
                ps.setInt(7, p.getNpa());
                ps.setString(8, p.getLocalite());
                ps.setBoolean(9, p.isActif());
                ps.setDouble(10, p.getSalaire());
                ps.setTimestamp(11, new Timestamp((new java.util.Date()).getTime()));
                ps.setInt(12, p.getPkPers());
                int nb = ps.executeUpdate();
                if (nb != 1) {
                    throw new MyDBException(SystemLib.getFullMethodName(), "Aucune mise à jour à été effectuée");
                }
            } catch (SQLException ex) {
                throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
            }
        }
    }

}
