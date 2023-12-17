package com.gutscheinverwaltung.controller;

import com.gutscheinverwaltung.model.Gutschein;
import com.gutscheinverwaltung.service.GutscheinService;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class AdminController extends SelectorComposer<Component> {

    @Wire
    private Textbox gutscheinArtTextbox;
    @Wire
    private Textbox mengenTextbox;
    @Wire
    private Doublebox preisDoublebox;
    @Wire
    private Image pics;
    @Wire
    private Progressmeter uploadProgress;

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
    }

    @Command
    public void uploadFile(@ContextParam(ContextType.TRIGGER_EVENT) UploadEvent event) {
        Media media = event.getMedia();
        if (media instanceof org.zkoss.image.Image) {
            pics.setContent((org.zkoss.image.Image) media);
            Clients.showNotification("Bild erfolgreich hochgeladen", "info", null, "middle_center", 2000);
        } else {
            Clients.showNotification("Nur Bilder sind erlaubt", "warn", null, "middle_center", 3000);
        }
    }

    @Command
    public void onHinzufuegenButtonClick() {
        String gutscheinArt = gutscheinArtTextbox.getValue();
        List<Integer> mengen = parseMengenText(mengenTextbox.getValue());
        byte[] image = getImageBytes(pics);
        float preisProGutscheinkarte = preisDoublebox.getValue().floatValue();

        Gutschein gutschein = new Gutschein();
        gutschein.setGutscheinArt(gutscheinArt);
        gutschein.setWerte(mengen);
        gutschein.setBild(image);
        gutschein.setPreisProStueck(preisProGutscheinkarte);

        try {
            GutscheinService.getInstance().addGutschein(gutschein);
            Clients.showNotification("Gutschein erfolgreich hinzugefügt", "info", null, "middle_center", 3000);
        } catch (Exception e) {
            Clients.showNotification("Fehler beim Hinzufügen des Gutscheins: " + e.getMessage(), "error", null, "middle_center", 3000);
        }

    }

    private List<Integer> parseMengenText(String text) {
        List<Integer> mengen = new ArrayList<>();
        String[] parts = text.split(",");
        for (String part : parts) {
            try {
                int mengenwert = Integer.parseInt(part.trim());
                mengen.add(mengenwert);
            } catch (NumberFormatException e) {
                Clients.showNotification("Ungültige Mengeneingabe: " + part, "error", null, "middle_center", 3000);
            }
        }
        return mengen;
    }

    private byte[] getImageBytes(Image image) {
        if (image.getContent() != null) {
            return image.getContent().getByteData();
        }
        return null;
    }

}