package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;

public interface DevisOptionChoiceLinkDao {
    void insertLink(int devisId, int optionChoiceId) throws SQLException;
    void deleteByDevisId(int devisId) throws SQLException; // pour récréation en edition
}
