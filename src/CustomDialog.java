
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Window;
import java.awt.Toolkit;

public final class CustomDialog extends Dialog<Boolean> {

    @FXML
    private Label content_label;

    @FXML
    private Button ok_button;

    @FXML
    private ImageView graphic_imageview;

    static public int INFORMATION = 1;
    static public int WARNING = 2;

    final private Runnable exclamation = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
    final private Runnable asterisk = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.asterisk");
    private String graphicPath;

    public CustomDialog(Window owner, String title, String content, int type) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dialog.fxml"));
        loader.setController(this);

        DialogPane dialogPane = loader.load();

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        setResizable(false);
        setWidth(600);
        setHeight(110);

        setTitle(title);
        updateContent(content);

        switch (type) {
            case 1:
                if (asterisk != null) asterisk.run();
                graphicPath = "img/custom_dialog_graphics/jolly.gif";
                break;
            
            case 2:
                if (exclamation != null) exclamation.run();
                graphicPath = "img/custom_dialog_graphics/impostor.gif";
                break;
        }

        try {
            updateGraphic(new Image(graphicPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDialogPane(dialogPane);
    }

    @FXML
    private void initialize() {
        getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {
            closeDialog();
        });
    }

    @FXML
    private void ok_button_action(ActionEvent event) {
        closeDialog();
    }

    public void updateTitle(String title) {
        setTitle(title);
    }

    public void updateContent(String content) {
        content_label.setText(content);
    }

    public void updateGraphic(Image image) {
        graphic_imageview.setImage(image);
    }

    public void updateType(Image image) {
        graphic_imageview.setImage(image);
    }

    private void closeDialog() {
        setResult(Boolean.TRUE);
        close();
    }

}
