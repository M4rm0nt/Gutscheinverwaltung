package com.gutscheinverwaltung.service;

import com.gutscheinverwaltung.dao.GutscheinDao;
import com.gutscheinverwaltung.model.Gutschein;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GutscheinService {
    private static GutscheinService instance;
    private final GutscheinDao gutscheinDao;

    private GutscheinService() {
        this.gutscheinDao = GutscheinDao.getInstance();
    }

    public static synchronized GutscheinService getInstance() {
        if (instance == null) {
            instance = new GutscheinService();
        }
        return instance;
    }

    public List<Gutschein> getAllGutscheine() throws SQLException {
        try {
            return gutscheinDao.getAllGutscheine();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found.", e);
        }
    }

    public double calculateGesamtpreis(Map<Gutschein, Combobox> ausgewaehlteGutscheine) {
        double gesamtpreis = 0.0;
        for (Gutschein gutschein : ausgewaehlteGutscheine.keySet()) {
            Combobox combobox = ausgewaehlteGutscheine.get(gutschein);
            Comboitem selectedItem = combobox.getSelectedItem();
            if (selectedItem != null) {
                String ausgewaehlterWertString = selectedItem.getLabel().replace(",", ".");
                float ausgewaehlterWert;
                try {
                    ausgewaehlterWert = Float.parseFloat(ausgewaehlterWertString);
                } catch (NumberFormatException nfe) {
                    // Log error and continue with next iteration
                    continue;
                }
                double preis = ausgewaehlterWert * gutschein.getPreisProStueck();
                gesamtpreis += preis;
            }
        }
        return gesamtpreis;
    }
}
