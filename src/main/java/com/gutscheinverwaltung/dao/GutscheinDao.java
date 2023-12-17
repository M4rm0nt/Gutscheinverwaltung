package com.gutscheinverwaltung.dao;

import com.gutscheinverwaltung.model.Gutschein;
import com.gutscheinverwaltung.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GutscheinDao {
    private static GutscheinDao instance;

    private GutscheinDao() {}

    public static GutscheinDao getInstance() {
        if (instance == null) {
            instance = new GutscheinDao();
        }
        return instance;
    }

    public List<Gutschein> getAllGutscheine() throws ClassNotFoundException, SQLException {
        List<Gutschein> gutscheine = new ArrayList<>();
        String gutscheinQuery = "SELECT g.*, b.bild FROM gutschein g "
                + "LEFT JOIN gutschein_bild b ON g.gutschein_id = b.gutschein_id WHERE g.status != 0";

        try (Connection connection = DatabaseConnection.initializeDatabase();
             PreparedStatement gutscheinStatement = connection.prepareStatement(gutscheinQuery);
             ResultSet gutscheinResultSet = gutscheinStatement.executeQuery()) {

            while (gutscheinResultSet.next()) {
                Gutschein gutschein = new Gutschein();
                gutschein.setGutscheinId(gutscheinResultSet.getInt("gutschein_id"));
                gutschein.setGutscheinArt(gutscheinResultSet.getString("gutschein_art"));
                gutschein.setPreisProStueck(gutscheinResultSet.getFloat("preis_pro_stueck"));
                gutschein.setBild(gutscheinResultSet.getBytes("bild"));

                List<Integer> werte = new ArrayList<>();
                String stueckelungQuery = "SELECT wert FROM gutschein_stueckelung WHERE gutschein_id = ? AND status != 0";
                try (PreparedStatement stueckelungStatement = connection.prepareStatement(stueckelungQuery)) {
                    stueckelungStatement.setInt(1, gutschein.getGutscheinId());
                    try (ResultSet stueckelungResultSet = stueckelungStatement.executeQuery()) {
                        while (stueckelungResultSet.next()) {
                            werte.add(stueckelungResultSet.getInt("wert"));
                        }
                    }
                }
                gutschein.setWerte(werte);
                gutscheine.add(gutschein);
            }
        }
        return gutscheine;
    }

    public void addGutschein(Gutschein gutschein) throws SQLException, ClassNotFoundException {
        String insertGutscheinSQL = "INSERT INTO gutschein(gutschein_art, preis_pro_stueck, status) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.initializeDatabase()) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertGutscheinStmt = connection.prepareStatement(insertGutscheinSQL, Statement.RETURN_GENERATED_KEYS)) {
                insertGutscheinStmt.setString(1, gutschein.getGutscheinArt());
                insertGutscheinStmt.setFloat(2, gutschein.getPreisProStueck());
                insertGutscheinStmt.setInt(3, 1);  // Status 1 für aktive Gutscheine
                insertGutscheinStmt.executeUpdate();

                int gutscheinId = handleGeneratedKeys(insertGutscheinStmt);

                insertStueckelungen(connection, gutscheinId, gutschein.getWerte());
                insertBild(connection, gutscheinId, gutschein.getBild());

                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    private int handleGeneratedKeys(PreparedStatement stmt) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Erstellen des Gutscheins fehlgeschlagen, keine ID erzeugt.");
            }
        }
    }

    private void insertStueckelungen(Connection connection, int gutscheinId, List<Integer> werte) throws SQLException {
        String insertStueckelungSQL = "INSERT INTO gutschein_stueckelung(gutschein_id, wert, status) VALUES (?, ?, ?)";
        for (Integer wert : werte) {
            try (PreparedStatement insertStueckelungStmt = connection.prepareStatement(insertStueckelungSQL)) {
                insertStueckelungStmt.setInt(1, gutscheinId);
                insertStueckelungStmt.setInt(2, wert);
                insertStueckelungStmt.setInt(3, 1); // Status 1 für aktive Stückelungen
                insertStueckelungStmt.executeUpdate();
            }
        }
    }

    private void insertBild(Connection connection, int gutscheinId, byte[] bild) throws SQLException {
        if (bild != null && bild.length > 0) {
            String insertBildSQL = "INSERT INTO gutschein_bild(gutschein_id, bild, status) VALUES (?, ?, ?)";
            try (PreparedStatement insertBildStmt = connection.prepareStatement(insertBildSQL)) {
                insertBildStmt.setInt(1, gutscheinId);
                insertBildStmt.setBytes(2, bild);
                insertBildStmt.setInt(3, 1); // Status 1 für aktive Bilder
                insertBildStmt.executeUpdate();
            }
        }
    }
}