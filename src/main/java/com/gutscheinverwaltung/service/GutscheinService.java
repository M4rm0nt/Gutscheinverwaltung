package com.gutscheinverwaltung.service;

import com.gutscheinverwaltung.dao.GutscheinDao;
import com.gutscheinverwaltung.model.Gutschein;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GutscheinService {

    private static class Holder {
        static final GutscheinService INSTANCE = new GutscheinService();
    }

    public static GutscheinService getInstance() {
        return Holder.INSTANCE;
    }

    private final GutscheinDao gutscheinDao;

    private GutscheinService() {
        this.gutscheinDao = GutscheinDao.getInstance();
    }

    public List<Gutschein> getAllGutscheine() throws SQLException {
        try {
            return gutscheinDao.getAllGutscheine();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found.", e);
        }
    }

    public double berechneGesamtpreis(Gutschein gutschein, int comboboxWert) {
        return gutschein.getPreisProStueck() * comboboxWert;
    }
}