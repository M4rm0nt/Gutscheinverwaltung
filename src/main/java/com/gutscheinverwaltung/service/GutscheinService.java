package com.gutscheinverwaltung.service;

import com.gutscheinverwaltung.dao.GutscheinDao;
import com.gutscheinverwaltung.model.Gutschein;

import java.sql.SQLException;
import java.util.List;

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

    public void addGutschein(Gutschein gutschein) throws SQLException {
        try {
            gutscheinDao.addGutschein(gutschein);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found.", e);
        }
    }
}

