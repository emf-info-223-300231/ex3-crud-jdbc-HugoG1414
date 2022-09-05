package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import java.util.List;

/**
 *
 * @author grandjeanh
 */
public class PersonneManager {

    private int index = 0;
    private List<Personne> listePersonnes;

    public Personne courantPersonne() throws MyDBException {
        if (listePersonnes == null || listePersonnes.isEmpty()) {
            return null;
        }
        if (index >= listePersonnes.size()) {
            index = listePersonnes.size() - 1;
        }
        return listePersonnes.get(index);
    }

    public Personne debutPersonne() throws MyDBException {
        index = 0;
        return listePersonnes.get(0);
    }

    public Personne finPersonne() throws MyDBException {
        index = listePersonnes.size() - 1;
        return listePersonnes.get(index);
    }

    public Personne precedentPersonne() throws MyDBException {
        if (index > 0) {
            index -= 1;
        } else {
            index = 0;
        }
        return listePersonnes.get(index);
    }

    public Personne setPersonnes(List<Personne> l) throws MyDBException {
        listePersonnes = l;
        return listePersonnes.get(0);
    }

    public Personne suivantPersonne() throws MyDBException {
        if (index < listePersonnes.size() - 1) {
            index += 1;
        } else {
            index = listePersonnes.size() - 1;
        }
        return listePersonnes.get(index);
    }
}
