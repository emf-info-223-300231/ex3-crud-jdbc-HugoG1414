package app.presentation;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.DateTimeLib;
import app.helpers.JfxPopup;
import app.workers.DbWorker;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.io.File;
import app.workers.DbWorkerItf;
import app.workers.PersonneManager;
import java.sql.Date;
import java.time.LocalDate;
import javafx.application.Platform;

/**
 *
 * @author PA/STT
 */
public class MainCtrl implements Initializable {

    // DBs à tester
    private enum TypesDB {
        MYSQL, HSQLDB, ACCESS
    };

    // DB par défaut
    final static private TypesDB DB_TYPE = TypesDB.MYSQL;

    private DbWorkerItf dbWrk;
    private PersonneManager manPers;
    private final DateTimeLib dateMod = new DateTimeLib();
    private boolean modeAjout;

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtPK;
    @FXML
    private TextField txtNo;
    @FXML
    private TextField txtRue;
    @FXML
    private TextField txtNPA;
    @FXML
    private TextField txtLocalite;
    @FXML
    private TextField txtSalaire;
    @FXML
    private CheckBox ckbActif;
    @FXML
    private Button btnDebut;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnEnd;
    @FXML
    private Button btnSauver;
    @FXML
    private Button btnAnnuler;
    @FXML
    private DatePicker dateNaissance;

    /*
   * METHODES NECESSAIRES A LA VUE
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbWrk = new DbWorker();
        manPers = new PersonneManager();
        ouvrirDB();
        rendreVisibleBoutonsDepl(true);
    }

    @FXML
    private void actionPrevious(ActionEvent event) {
        try {
            afficherPersonne(manPers.precedentPersonne());
        } catch (MyDBException e) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", e.getMessage());
        }
    }

    @FXML
    private void actionNext(ActionEvent event) {
        try {
            afficherPersonne(manPers.suivantPersonne());
        } catch (MyDBException e) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", e.getMessage());
        }
    }

    @FXML
    private void actionEnd(ActionEvent event) {
        try {
            afficherPersonne(manPers.finPersonne());
        } catch (MyDBException e) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", e.getMessage());
        }
    }

    @FXML
    private void debut(ActionEvent event) {
        try {
            afficherPersonne(manPers.debutPersonne());
        } catch (MyDBException e) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", e.getMessage());
        }
    }

    @FXML
    private void menuAjouter(ActionEvent event) {
        effacerContenuChamps();
        rendreVisibleBoutonsDepl(false);
        modeAjout = true;
    }

    @FXML
    private void menuModifier(ActionEvent event) {
        modeAjout = false;
        rendreVisibleBoutonsDepl(false);
    }

    @FXML
    private void menuEffacer(ActionEvent event) {
        try {
            dbWrk.effacer(manPers.courantPersonne());
            manPers.setPersonnes(dbWrk.lirePersonnes());
            afficherPersonne(manPers.courantPersonne());
        } catch (MyDBException e) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", e.getMessage());
        }
    }

    @FXML
    private void menuQuitter(ActionEvent event) {
        try {
            dbWrk.deconnecter();
        } catch (MyDBException ex) {
            System.out.println(ex.getMessage());
        }
        Platform.exit();
    }

    @FXML
    private void annulerPersonne(ActionEvent event) {
        rendreVisibleBoutonsDepl(true);
        try {
            afficherPersonne(manPers.courantPersonne());
        } catch (MyDBException e) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", e.getMessage());
        }
    }

    @FXML
    private void sauverPersonne(ActionEvent event) {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        Date date = Date.valueOf(dateNaissance.getValue());
        int no = Integer.parseInt(txtNo.getText());
        String rue = txtRue.getText();
        int npa = Integer.parseInt(txtNPA.getText());
        String localite = txtLocalite.getText();
        boolean actif = ckbActif.isSelected();
        double salaire = Double.parseDouble(txtSalaire.getText());
        LocalDate date2 = LocalDate.now();
        Personne p = new Personne(nom, prenom, date, no, rue, npa, localite, actif, salaire, dateMod.localDateToDate(date2));

        try {
            if (modeAjout) {
                dbWrk.creer(p);
                manPers.setPersonnes(dbWrk.lirePersonnes());
                afficherPersonne(manPers.finPersonne());
            } else {
                p.setPkPers(Integer.parseInt(txtPK.getText()));
                dbWrk.modifier(p);
                manPers.setPersonnes(dbWrk.lirePersonnes());
                afficherPersonne(manPers.courantPersonne());
            }
            rendreVisibleBoutonsDepl(true);
        } catch (MyDBException e) {

        }
    }

    public void quitter() {
        try {
            dbWrk.deconnecter(); // ne pas oublier !!!
        } catch (MyDBException ex) {
            System.out.println(ex.getMessage());
        }
        Platform.exit();
    }

    /*
   * METHODES PRIVEES 
     */
    private void afficherPersonne(Personne p) {
        if (p != null) {
            txtPrenom.setText(p.getPrenom());
            txtNom.setText(p.getNom());
            txtLocalite.setText(p.getLocalite());
            txtNPA.setText(p.getNpa() + "");
            txtNo.setText(p.getNoRue() + "");
            txtPK.setText(p.getPkPers() + "");
            txtRue.setText(p.getRue());
            txtSalaire.setText(p.getSalaire() + "");
            ckbActif.setSelected(p.isActif());
            dateNaissance.setValue(dateMod.dateToLocalDate(p.getDateNaissance()));
        }
    }

    private void ouvrirDB() {
        try {
            switch (DB_TYPE) {
                case MYSQL:
                    dbWrk.connecterBdMySQL("223_personne_1table");
                    break;
                case HSQLDB:
                    dbWrk.connecterBdHSQLDB("../data" + File.separator + "223_personne_1table");
                    break;
                case ACCESS:
                    dbWrk.connecterBdAccess("../data" + File.separator + "223_Personne_1table.accdb");
                    break;
                default:
                    System.out.println("Base de données pas définie");
            }
            System.out.println("------- DB OK ----------");
            afficherPersonne(manPers.setPersonnes(dbWrk.lirePersonnes()));
        } catch (MyDBException ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
            System.exit(1);
        }
    }

    private void rendreVisibleBoutonsDepl(boolean b) {
        btnDebut.setVisible(b);
        btnPrevious.setVisible(b);
        btnNext.setVisible(b);
        btnEnd.setVisible(b);
        btnAnnuler.setVisible(!b);
        btnSauver.setVisible(!b);
    }

    private void effacerContenuChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtPK.setText("");
        txtNo.setText("");
        txtRue.setText("");
        txtNPA.setText("");
        txtLocalite.setText("");
        txtSalaire.setText("");
        ckbActif.setSelected(false);
    }

}
