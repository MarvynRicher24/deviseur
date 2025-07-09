package fr.musclegarage.deviseur.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.DevisDao;
import fr.musclegarage.deviseur.dao.DevisDaoJdbc;
import fr.musclegarage.deviseur.dao.DevisOptionChoiceLinkDao;
import fr.musclegarage.deviseur.dao.DevisOptionChoiceLinkDaoJdbc;
import fr.musclegarage.deviseur.dao.OptionDao;
import fr.musclegarage.deviseur.dao.OptionDaoJdbc;
import fr.musclegarage.deviseur.model.Devis;
import fr.musclegarage.deviseur.model.Option;
import fr.musclegarage.deviseur.model.OptionChoice;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.util.Database;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class UserPanelRecapController {
    @FXML
    private StackPane modelPane;
    @FXML
    private VBox detailsContainer;
    @FXML
    private VBox recapPane;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();

            // 1) Image modèle de base
            String baseImg = QuoteSession.getModel().getModelImage();
            ImageView ivBase = new ImageView(new Image(
                    new File("uploads/models/" + baseImg).toURI().toString(), true));
            ivBase.setFitWidth(400);
            ivBase.setFitHeight(300);
            ivBase.setPreserveRatio(true);
            modelPane.getChildren().add(ivBase);

            // 2) Superposition des overlays d’options
            for (OptionChoice oc : QuoteSession.getAllChoices().values()) {
                File f = new File("uploads/options/" + oc.getOptionChoiceImage());
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString(), true));
                    iv.fitWidthProperty().bind(modelPane.widthProperty());
                    iv.fitHeightProperty().bind(modelPane.heightProperty());
                    iv.setPreserveRatio(true);
                    modelPane.getChildren().add(iv);
                }
            }

            // 3) Préparer map optionId→nom d’option
            OptionDao optionDao = new OptionDaoJdbc(conn);
            Map<Integer, String> optionNames = optionDao.findAll().stream()
                    .collect(Collectors.toMap(Option::getId, Option::getOptionName));

            // 4) Construire la liste des détails
            detailsContainer.getChildren().clear();

            detailsContainer.getChildren().add(new Label(
                    "Client : " + QuoteSession.getClient().getClientName()
                            + " " + QuoteSession.getClient().getClientSurname()));

            detailsContainer.getChildren().add(new Label(
                    "Catégorie : " + QuoteSession.getCategory().getCategoryName()));

            detailsContainer.getChildren().add(new Label(
                    "Modèle : " + QuoteSession.getModel().getModelName()
                            + " – " + QuoteSession.getModel().getModelPrice() + "€"));

            if (QuoteSession.getMotor() != null) {
                detailsContainer.getChildren().add(new Label(
                        "Moteur : " + QuoteSession.getMotor().getMotorName()
                                + " – " + QuoteSession.getMotor().getMotorPrice() + "€"));
            } else {
                detailsContainer.getChildren().add(new Label(
                        "Moteur : [non défini]"));
            }

            // Options + choix
            detailsContainer.getChildren().add(
                    new Label(QuoteSession.getAllChoices().size() > 1
                            ? "Options sélectionnées :"
                            : "Option sélectionnée :"));

            for (OptionChoice oc : QuoteSession.getAllChoices().values()) {
                String optName = optionNames.getOrDefault(oc.getOptionId(), "[Option inconnue]");
                String line = optName + " – "
                        + oc.getOptionChoiceName() + " – "
                        + oc.getOptionChoicePrice() + "€";
                detailsContainer.getChildren().add(new Label(line));
            }

            // Puis TOTAL
            Label total = new Label("TOTAL : " + QuoteSession.getTotalPrice() + "€");
            total.getStyleClass().add("label-title");
            detailsContainer.getChildren().add(total);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onPrev() {
        App.userBaseController.goToOption();
    }

    @FXML
    private void onSave() {
        try {
            Connection conn = Database.getConnection();
            DevisDao dao = new DevisDaoJdbc(conn);
            DevisOptionChoiceLinkDao linkDao = new DevisOptionChoiceLinkDaoJdbc(conn);

            // Construire l'objet Devis à enregistrer
            Devis d = new Devis();
            Integer existingId = QuoteSession.getDevisId();
            if (existingId != null) {
                d.setId(existingId);
            }
            d.setUserId(QuoteSession.getUser().getId());
            d.setClientId(QuoteSession.getClient().getId());
            d.setModelId(QuoteSession.getModel().getId());
            d.setMotorId(QuoteSession.getMotor().getId());
            d.setDateCreated(LocalDateTime.now());
            d.setTotalPrice(QuoteSession.getTotalPrice());

            // Si on édite, on supprime d'abord les anciens liens
            if (existingId != null) {
                linkDao.deleteByDevisId(existingId);
                dao.update(d);
            } else {
                dao.insert(d);
                QuoteSession.setDevisId(d.getId());
            }

            // Puis on réinsère tous les choix
            for (OptionChoice oc : QuoteSession.getAllChoices().values()) {
                linkDao.insertLink(d.getId(), oc.getId());
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Devis enregistré (ID : " + d.getId() + ").").showAndWait();
            App.userBaseController.onGoMenu();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onExportPdf() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Enregistrer le récapitulatif en PDF");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File target = chooser.showSaveDialog(recapPane.getScene().getWindow());
            if (target == null)
                return;
            if (!target.getName().toLowerCase().endsWith(".pdf")) {
                target = new File(target.getAbsolutePath() + ".pdf");
            }

            // Snapshot uniquement de recapPane
            WritableImage fxImage = recapPane.snapshot(new SnapshotParameters(), null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(fxImage, null);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage(new PDRectangle(bImage.getWidth(), bImage.getHeight()));
                doc.addPage(page);

                PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                        doc, toByteArray(bImage), "recap");
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.drawImage(pdImage, 0, 0,
                            page.getMediaBox().getWidth(),
                            page.getMediaBox().getHeight());
                }
                doc.save(target);
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "PDF généré : " + target.getAbsolutePath()).showAndWait();

        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Erreur création PDF : " + ex.getMessage()).showAndWait();
        }
    }

    /** Convertit BufferedImage en tableau de bytes PNG */
    private byte[] toByteArray(BufferedImage img) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "PNG", baos);
            return baos.toByteArray();
        }
    }

}