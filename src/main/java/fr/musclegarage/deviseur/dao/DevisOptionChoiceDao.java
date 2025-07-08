package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.OptionChoice;

public interface DevisOptionChoiceDao {
    List<OptionChoice> findByDevisId(int devisId) throws SQLException;
}