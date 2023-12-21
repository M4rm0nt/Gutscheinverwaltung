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

}