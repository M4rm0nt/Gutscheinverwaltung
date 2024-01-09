package com.gutscheinverwaltung.controller;

import com.gutscheinverwaltung.model.Gutschein;
import com.gutscheinverwaltung.service.GutscheinService;
import com.gutscheinverwaltung.ui.GutscheinUIBuilder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class GutscheinController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(GutscheinController.class);

    @Wire
    private Vbox gutscheinContainer;

    @Wire
    private Label gesamtpreisLabel;

    private final Map<Gutschein, Combobox> verfuegbarenGutscheine = new HashMap<>();
    private GutscheinService gutscheinService = GutscheinService.getInstance();

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);
        loadGutscheine();
    }

    private void loadGutscheine() {

        try {
            List<Gutschein> gutscheine = gutscheinService.getAllGutscheine();
            gutscheine.sort(Comparator.comparing(Gutschein::getGutscheinArt));

            for (Gutschein gutschein : gutscheine) {
                createGutscheinUI(gutschein);
            }
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Gutscheine", e);
        }
    }

    private void createGutscheinUI(Gutschein gutschein) {

        try {
            Groupbox groupBox = GutscheinUIBuilder.createGutscheinComponent(gutschein);
            gutscheinContainer.appendChild(groupBox);

            Combobox combobox = (Combobox) groupBox.getFellow("gutscheinCombobox_" + gutschein.getGutscheinArt());
            combobox.addEventListener(Events.ON_SELECT, event -> handleComboboxSelect(event, gutschein));
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen der Gutschein-Benutzeroberfläche", e);
        }
    }

    private void handleComboboxSelect(Event event, Gutschein gutschein) {
        SelectEvent selectEvent = (SelectEvent) event;
        Comboitem selectedItem = (Comboitem) selectEvent.getSelectedItems().iterator().next();
        int comboboxWert = Integer.parseInt(selectedItem.getLabel());
        updateGutscheinPreis(gutschein, comboboxWert);
        berechneGesamtsummeAllerGutscheine();
    }

    private void updateGutscheinPreis(Gutschein gutschein, int comboboxWert) {
        double infoBoxPreis = gutscheinService.berechneGesamtpreis(gutschein, comboboxWert);
        Label infoBoxPreisLable = (Label) gutscheinContainer.getFellow("infoBoxPreisLable" + gutschein.getGutscheinId());
        infoBoxPreisLable.setValue(String.format("Gesamtpreis: %.2f €", infoBoxPreis));
    }

    private void berechneGesamtsummeAllerGutscheine() {
        double gesamtsumme = 0.0;
        for (Map.Entry<Gutschein, Combobox> entry : verfuegbarenGutscheine.entrySet()) {
            Gutschein gutschein = entry.getKey();
            Combobox combobox = entry.getValue();
            if (combobox.getSelectedItem() != null) {
                int anzahl = Integer.parseInt(combobox.getSelectedItem().getLabel());
                gesamtsumme += gutscheinService.berechneGesamtpreis(gutschein, anzahl);
            }
        }
        gesamtpreisLabel.setValue(String.format("Gesamtsumme: %.2f €", gesamtsumme));
    }

}