package com.gutscheinverwaltung.controller;

import com.gutscheinverwaltung.model.Gutschein;
import com.gutscheinverwaltung.service.GutscheinService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.*;

public class GutscheinController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(GutscheinController.class);

    @Wire
    private Vbox gutscheinContainer;

    @Wire
    private Label gesamtpreisLabel;

    private final Map<Gutschein, Combobox> ausgewaehlteGutscheine = new HashMap<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadGutscheine();
    }

    private void loadGutscheine() {

        try {
            List<Gutschein> gutscheine = GutscheinService.getInstance().getAllGutscheine();
            gutscheine.sort(Comparator.comparing(Gutschein::getGutscheinArt));
            for (Gutschein gutschein : gutscheine) {
                createGutscheinUI(gutschein);
            }
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Gutscheine", e);
        }
    }

    private void createGutscheinUI(Gutschein gutschein) throws IOException {

        try {

            Groupbox groupBox = new Groupbox();
            groupBox.setTitle(gutschein.getGutscheinArt());
            groupBox.setMold("3d");
            groupBox.setWidth("100%");

            Hbox hbox = new Hbox();
            hbox.setAlign("center");
            hbox.setPack("start");
            hbox.setWidth("100%");
            groupBox.appendChild(hbox);

            if (gutschein.getBild() != null) {
                Vbox imageBox = getImageBox(gutschein);
                hbox.appendChild(imageBox);
            }

            Vbox infoBox = getGutscheinInfoBox(gutschein);
            hbox.appendChild(infoBox);

            gutscheinContainer.appendChild(groupBox);

        } catch(IOException e){
            logger.error("Fehler beim Erstellen der Gutschein-Benutzeroberfläche", e);
        }
    }

    private Vbox getImageBox(Gutschein gutschein) throws IOException {
        AImage aImage = new AImage("", gutschein.getBild());
        Image image = new Image();
        image.setContent(aImage);
        image.setWidth("533px");  // Bildbreite
        image.setHeight("300px"); // Bildhöhe

        Vbox imageBox = new Vbox();
        imageBox.setWidth("533px"); // Breite des Containers angepasst an die Bildbreite
        imageBox.setSclass("centered-content");  // Anwendung der CSS-Klasse
        imageBox.appendChild(image);

        Label preisLabel = new Label("VPE: " + String.format("%.2f", gutschein.getPreisProStueck()) + " €" + " pro Stück");
        preisLabel.setSclass("centered-label");
        imageBox.appendChild(preisLabel);

        return imageBox;
    }

    private Vbox getGutscheinInfoBox(Gutschein gutschein) {
        Vbox infoBox = new Vbox();
        infoBox.setSpacing("10px");

        Label mengeLabel = new Label("Menge:");
        infoBox.appendChild(mengeLabel);

        Combobox combobox = getGutscheinCombobox(gutschein);
        infoBox.appendChild(combobox);

        Label gesamtpreisLabel = new Label("Gesamtpreis: 0,00 €");
        infoBox.appendChild(gesamtpreisLabel);

        combobox.addEventListener(Events.ON_SELECT, event -> {
            SelectEvent selectEvent = (SelectEvent) event;
            Comboitem selectedItem = (Comboitem) selectEvent.getSelectedItems().iterator().next();
            int ausgewaehlterWert;
            try {
                ausgewaehlterWert = Integer.parseInt(selectedItem.getLabel());
            } catch (NumberFormatException nfe) {
                logger.error("Ungültiger Wert: " + selectedItem.getLabel(), nfe);
                return;
            }
            double gesamtpreis = ausgewaehlterWert * gutschein.getPreisProStueck();
            gesamtpreisLabel.setValue(String.format("Gesamtpreis: %.2f €", gesamtpreis));
            updateGesamtpreisLabel(calculateGesamtpreis());
        });

        ausgewaehlteGutscheine.put(gutschein, combobox);

        return infoBox;
    }

    private Combobox getGutscheinCombobox(Gutschein gutschein) {
        Combobox combobox = new Combobox();
        combobox.setAutodrop(true);

        // Erstellt eine neue Liste aus den Werten und sortiert sie
        List<Integer> sortedValues = new ArrayList<>(gutschein.getWerte());
        Collections.sort(sortedValues);

        // Fügt die sortierten Werte zur Combobox hinzu
        for (Integer wert : sortedValues) {
            Comboitem item = new Comboitem(wert.toString());
            combobox.appendChild(item);
        }

        // Setzt den ersten Wert aus der sortierten Liste als ausgewählten Wert
        combobox.setSelectedItem(combobox.getItemAtIndex(0));

        combobox.setAutodrop(true);
        return combobox;
    }

    private double calculateGesamtpreis() {
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
                    logger.error("Ungültiger Wert: " + ausgewaehlterWertString, nfe);
                    continue;
                }
                double preis = ausgewaehlterWert * gutschein.getPreisProStueck();
                gesamtpreis += preis;
            }
        }
        return gesamtpreis;
    }

    private void updateGesamtpreisLabel(double total) {
        gesamtpreisLabel.setValue(String.format("%.2f", total) + " €");
    }

}