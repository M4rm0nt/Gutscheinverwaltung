package com.gutscheinverwaltung.ui;

import com.gutscheinverwaltung.model.Gutschein;
import com.gutscheinverwaltung.controller.GutscheinController;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GutscheinUIBuilder {

    public static Groupbox createGutscheinComponent(Gutschein gutschein, GutscheinController controller) throws IOException {
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

        Vbox infoBox = getGutscheinInfoBox(gutschein, controller);
        hbox.appendChild(infoBox);

        return groupBox;
    }

    private static Vbox getImageBox(Gutschein gutschein) throws IOException {
        AImage aImage = new AImage("", gutschein.getBild());
        Image image = new Image();
        image.setContent(aImage);
        image.setWidth("533px");
        image.setHeight("300px");

        Vbox imageBox = new Vbox();
        imageBox.setWidth("533px");
        imageBox.setSclass("centered-content");
        imageBox.appendChild(image);

        Label preisLabel = new Label("VPE: " + String.format("%.2f", gutschein.getPreisProStueck()) + " € pro Stück");
        preisLabel.setSclass("centered-label");
        imageBox.appendChild(preisLabel);

        return imageBox;
    }

    private static Vbox getGutscheinInfoBox(Gutschein gutschein, GutscheinController controller) {
        Vbox infoBox = new Vbox();
        infoBox.setSpacing("10px");

        Label mengeLabel = new Label("Menge:");
        infoBox.appendChild(mengeLabel);

        Combobox combobox = getGutscheinCombobox(gutschein);
        infoBox.appendChild(combobox);

        combobox.addEventListener(Events.ON_SELECT, event -> {
            SelectEvent selectEvent = (SelectEvent) event;
            Comboitem selectedItem = (Comboitem) selectEvent.getSelectedItems().iterator().next();
            int ausgewaehlterWert;
            try {
                ausgewaehlterWert = Integer.parseInt(selectedItem.getLabel());
                controller.updateAusgewaehlteGutscheine(gutschein, ausgewaehlterWert);
            } catch (NumberFormatException nfe) {
                // Log
            }
        });

        return infoBox;
    }

    private static Combobox getGutscheinCombobox(Gutschein gutschein) {
        Combobox combobox = new Combobox();
        combobox.setAutodrop(true);

        List<Integer> sortierteWerte = new ArrayList<>(gutschein.getWerte());
        Collections.sort(sortierteWerte);

        for (Integer wert : sortierteWerte) {
            Comboitem item = new Comboitem(wert.toString());
            combobox.appendChild(item);
        }

        combobox.setSelectedItem(combobox.getItemAtIndex(0));
        return combobox;
    }
}
