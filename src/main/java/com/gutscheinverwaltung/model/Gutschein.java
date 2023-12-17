package com.gutscheinverwaltung.model;

import java.util.List;
import java.util.Date;

public class Gutschein {
    private int gutscheinId;
    private String gutscheinArt;
    private float preisProStueck;
    private List<Integer> werte;
    private byte[] bild;
    private Date createDate;
    private Date modifyDate;
    private int status;
    private int version;

    public Gutschein() {
    }

    public Gutschein(int gutscheinId, String gutscheinArt, float preisProStueck, List<Integer> werte, byte[] bild, Date createDate, Date modifyDate, int status, int version) {
        this.gutscheinId = gutscheinId;
        this.gutscheinArt = gutscheinArt;
        this.preisProStueck = preisProStueck;
        this.werte = werte;
        this.bild = bild;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.status = status;
        this.version = version;
    }

    public int getGutscheinId() {
        return gutscheinId;
    }

    public void setGutscheinId(int gutscheinId) {
        this.gutscheinId = gutscheinId;
    }

    public String getGutscheinArt() {
        return gutscheinArt;
    }

    public void setGutscheinArt(String gutscheinArt) {
        this.gutscheinArt = gutscheinArt;
    }

    public float getPreisProStueck() {
        return preisProStueck;
    }

    public void setPreisProStueck(float preisProStueck) {
        this.preisProStueck = preisProStueck;
    }

    public List<Integer> getWerte() {
        return werte;
    }

    public void setWerte(List<Integer> werte) {
        this.werte = werte;
    }

    public byte[] getBild() {
        return bild;
    }

    public void setBild(byte[] bild) {
        this.bild = bild;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
