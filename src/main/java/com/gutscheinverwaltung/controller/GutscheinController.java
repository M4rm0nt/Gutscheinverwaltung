package com.gutscheinverwaltung.controller;

import com.gutscheinverwaltung.model.Gutschein;
import com.gutscheinverwaltung.service.GutscheinService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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

    GutscheinService gutscheinService = GutscheinService.getInstance();

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

            Groupbox groupBox = new Groupbox();
            groupBox.setTitle(gutschein.getGutscheinArt());
            groupBox.setMold("3d");
            groupBox.setWidth("100%");

            Hbox hbox = new Hbox();
            hbox.setAlign("center");
            hbox.setPack("start");
            hbox.setWidth("100%");
            groupBox.appendChild(hbox);

            Hbox buttonComboboxHbox = new Hbox();
            buttonComboboxHbox.setSpacing("10px");

            /* Soll später wieder eingebaut werden:
            Button editButton = new Button("Bearbeiten");
            editButton.addEventListener(Events.ON_CLICK, event -> onEditGutschein(gutschein));
            buttonComboboxHbox.appendChild(editButton);
             */

            if (gutschein.getBild() != null) {
                Vbox imageBox = getImageBox(gutschein);
                hbox.appendChild(imageBox);
            }

            Vbox infoBox = getGutscheinInfoBox(gutschein);
            hbox.appendChild(infoBox);

            hbox.appendChild(buttonComboboxHbox);

            gutscheinContainer.appendChild(groupBox);

        } catch(IOException e){
            logger.error("Fehler beim Erstellen der Gutschein-Benutzeroberfläche", e);
        }
    }

    private Vbox getImageBox(Gutschein gutschein) throws IOException {
        AImage aImage = new AImage("", gutschein.getBild());
        Image image = new Image();
        image.setContent(aImage);
        image.setWidth("533px");
        image.setHeight("300px");

        Vbox imageBox = new Vbox();
        imageBox.setWidth("533px");
        imageBox.setSclass("centered-content");
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
            updateGesamtpreisLabel();
        });

        ausgewaehlteGutscheine.put(gutschein, combobox);

        return infoBox;
    }

    private Combobox getGutscheinCombobox(Gutschein gutschein) {
        Combobox combobox = new Combobox();
        combobox.setAutodrop(true);
        List<Integer> sortedValues = new ArrayList<>(gutschein.getWerte());
        Collections.sort(sortedValues);

        for (Integer wert : sortedValues) {
            Comboitem item = new Comboitem(wert.toString());
            combobox.appendChild(item);
        }

        combobox.setSelectedItem(combobox.getItemAtIndex(0));
        combobox.setAutodrop(true);

        return combobox;
    }

    private void updateGesamtpreisLabel() {
        double gesamtpreis = GutscheinService.getInstance().calculateGesamtpreis(ausgewaehlteGutscheine);
        gesamtpreisLabel.setValue(String.format("%.2f", gesamtpreis) + " €");
    }

    public void updateAusgewaehlteGutscheine(Gutschein gutschein, int ausgewaehlteAnzahl) {
        Combobox combobox = new Combobox();
        combobox.setValue(String.valueOf(ausgewaehlteAnzahl));
        ausgewaehlteGutscheine.put(gutschein, combobox);
        updateGesamtpreis();
    }


    public void updateGesamtpreis() {
        double gesamtpreis = ausgewaehlteGutscheine.entrySet().stream()
                .mapToDouble(entry -> {
                    String valueStr = entry.getValue().getValue();
                    int numericValue = Integer.parseInt(valueStr);
                    return entry.getKey().getPreisProStueck() * numericValue;
                })
                .sum();
        gesamtpreisLabel.setValue(String.format("Gesamtpreis: %.2f €", gesamtpreis));
    }

}
