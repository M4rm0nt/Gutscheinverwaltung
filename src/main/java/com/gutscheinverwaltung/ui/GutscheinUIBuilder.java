package com.gutscheinverwaltung.ui;

import com.gutscheinverwaltung.model.Gutschein;
import org.zkoss.image.AImage;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GutscheinUIBuilder {

    private static final String IMAGE_WIDTH = "533px";
    private static final String IMAGE_HEIGHT = "300px";

    public static Groupbox createGutscheinComponent(Gutschein gutschein) throws IOException {

        // Wrapper-Box
        Groupbox groupBox = new Groupbox();
        groupBox.setTitle(gutschein.getGutscheinArt());
        groupBox.setMold("3d");
        groupBox.setWidth("100%");

        // Haupt-Container
        Hbox hbox = new Hbox();
        hbox.setAlign("center");
        hbox.setPack("start");
        hbox.setWidth("100%");
        groupBox.appendChild(hbox);

        // Hauptkomponente 1: Bild
        if (gutschein.getBild() != null) {
            Vbox imageBox = getImageBox(gutschein);
            hbox.appendChild(imageBox);
        }

        // Hauptkomponente 2: Info-Box
        Vbox infoBox = getGutscheinInfoBox(gutschein);
        hbox.appendChild(infoBox);

        return groupBox;
    }

    private static Vbox getImageBox(Gutschein gutschein) throws IOException {

        AImage aImage = new AImage("", gutschein.getBild());
        Image image = new Image();
        image.setContent(aImage);
        image.setWidth(IMAGE_WIDTH);
        image.setHeight(IMAGE_HEIGHT);

        Vbox imageBox = new Vbox();
        imageBox.setWidth(IMAGE_WIDTH);
        imageBox.setSclass("centered-content");
        imageBox.appendChild(image);

        Label preisLabel = new Label("VPE: " + String.format("%.2f", gutschein.getPreisProStueck()) + " € pro Stück");
        preisLabel.setSclass("centered-label");
        imageBox.appendChild(preisLabel);

        return imageBox;
    }

    private static Vbox getGutscheinInfoBox(Gutschein gutschein) {

        Vbox infoBox = new Vbox();
        infoBox.setSpacing("10px");

        Label mengeLabel = new Label("Menge:");
        infoBox.appendChild(mengeLabel);

        Combobox combobox = getGutscheinCombobox(gutschein);
        infoBox.appendChild(combobox);

        Label infoBoxPreisLable = new Label("Gesamtpreis: 0,00 €");
        infoBoxPreisLable.setId("infoBoxPreisLable_" + gutschein.getGutscheinId());
        infoBox.appendChild(infoBoxPreisLable);

        return infoBox;
    }

    private static Combobox getGutscheinCombobox(Gutschein gutschein) {

        Combobox combobox = new Combobox();
        combobox.setAutodrop(true);

        List<Integer> werte = gutschein.getWerte();
        Collections.sort(werte);

        for (Integer wert : werte) {
            Comboitem item = new Comboitem(wert.toString());
            combobox.appendChild(item);
        }

        if (!combobox.getItems().isEmpty()) {
            combobox.setSelectedIndex(0);
        }

        return combobox;
    }
}