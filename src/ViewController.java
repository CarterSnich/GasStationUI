
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class ViewController extends AnchorPane {

    private Stage primaryStage;

    private Prices prices = new Prices();
    private GasCanImg gasCansImg = new GasCanImg();

    private String currentFuel = "";
    private BigDecimal currentFuelPrice = new BigDecimal("0.0");
    private BigDecimal currentFuelLitres = new BigDecimal("0.0");
    private BigDecimal fuelAmount = new BigDecimal("0.0");

    private String previouslyHoveredFuelButtonText = "";
    final protected BigDecimal maxFuelAmount = new BigDecimal("10000.00");
    final protected BigDecimal minFuelAmount = new BigDecimal("30.00");

    // UI components

    @FXML
    private Label amountLabel;

    @FXML
    private Label litresLabel;

    @FXML
    private Button refillButton;

    @FXML
    private ImageView gascCanImageView;

    @FXML
    private ImageView refillButtonBackGround;

    @FXML
    private ImageView adminCardImageView;

    @FXML
    private GridPane fuelButtonsWrapper;

    @FXML
    private GridPane numPadButtonsWrapper;

    @FXML
    private Pane swipePanelPane;

    @FXML
    private void initialize() {
    }

    @FXML
    private void fuelButtonMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        String fuel = button.getText().toLowerCase();

        previouslyHoveredFuelButtonText = fuel;
        button.setText(String.format("P %1.2f", prices.getPrice(fuel).doubleValue()));

    }

    @FXML
    private void fuelButtonMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setText(previouslyHoveredFuelButtonText.toUpperCase());
      
    }

    @FXML
    private void setFuel(ActionEvent event) {

        Button thisButton = (Button) event.getSource();
        currentFuel = previouslyHoveredFuelButtonText;
        currentFuelPrice = prices.getPrice(previouslyHoveredFuelButtonText);

        for (Node button : fuelButtonsWrapper.getChildren())
            button.getStyleClass().removeAll("active-fuel-button");
        thisButton.getStyleClass().add("active-fuel-button");

        currentFuelLitres = fuelAmount.divide(currentFuelPrice, 2, RoundingMode.HALF_UP);
        amountLabel.setText(String.format("%1.2f", fuelAmount.doubleValue()));
        litresLabel.setText(String.format("%1.2f", currentFuelLitres.doubleValue()));

    }

    @FXML
    private void numPadButtonsAction(ActionEvent event) throws IOException {
        if (currentFuel.equals("")) {
            new CustomDialog(
                primaryStage,
                "Customer error",
                "Please choose a fuel first.",
                CustomDialog.WARNING).showAndWait();
        } else {
            if (amountLabel.getText().length() <= 7) {
                Button thisButton = (Button) event.getSource();

                String observableInput = thisButton.getText();
                String oldInput = amountLabel.getText().replaceAll("\\.00", "");
                String newInput = (oldInput.equals("0") ? observableInput : oldInput + observableInput) + ".00";

                fuelAmount = new BigDecimal(newInput);
                currentFuelLitres = fuelAmount.divide(currentFuelPrice, 2, RoundingMode.HALF_UP);

                amountLabel.setText(newInput);
                litresLabel.setText(currentFuelLitres.toString());
            }
        }
    }

    @FXML
    private void backspaceButtonActon(ActionEvent event) {
        String oldInput = amountLabel.getText().replaceAll("\\.00", "");
        String newInput = (oldInput.length() == 1 ? "0" : oldInput.substring(0, oldInput.length() - 1)) + ".00";

        if (!oldInput.equals("0")) {
            fuelAmount = new BigDecimal(newInput);
            currentFuelLitres = fuelAmount.divide(currentFuelPrice, 2, RoundingMode.HALF_UP);
    
            amountLabel.setText(newInput);
            litresLabel.setText(currentFuelLitres.toString());
        }

    }

    @FXML
    private void startFill(ActionEvent event) throws IOException {
        if (currentFuel.equals("")) {
            new CustomDialog(
                primaryStage,
                "Customer error",
                "Please choose a fuel first.",
                CustomDialog.WARNING).showAndWait();

            return;
        }
        if (!(fuelAmount.compareTo(maxFuelAmount) <= 0 && fuelAmount.compareTo(minFuelAmount) >= 0)) {
            new CustomDialog(
                primaryStage,
                "Customer error",
                "Please enter amount between P30-P10000.",
                CustomDialog.INFORMATION).showAndWait();

            return;
        }

        disableButtons(true);
        refillButtonBackGround.setImage(new Image("img/fuel_button_active.png"));

        Task<Void> task = new Task<Void>() {
            BigDecimal i;
            BigDecimal filledPercentage;
            BigDecimal oneHundred = new BigDecimal("100.0");

            @Override
            public Void call() throws Exception {
                
                for (i = new BigDecimal("0.00"); i.compareTo(currentFuelLitres) <= 0; i = i.add(new BigDecimal("0.01"))) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            filledPercentage = i.divide(currentFuelLitres, 2, RoundingMode.HALF_UP).multiply(oneHundred);

                            litresLabel.setText(String.format("%1.2f", i.doubleValue()));
                            gascCanImageView.setImage(gasCansImg.getImage(filledPercentage.intValue() + ""));

                        }
                    });
                    Thread.sleep(100);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {

            refillButtonBackGround.setImage(new Image("img/fuel_button_done.png"));

            try {
                new CustomDialog(
                    primaryStage,
                    "Refill status",
                    "Refilling done. Please swipe your card to pay.",
                    CustomDialog.INFORMATION).showAndWait();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            adminCardImageView.setOpacity(1.0);

            swipePanelPane.setOnMousePressed(pressedEvent -> {
                swipePanelPane.setOnMouseDragged(draggedEvent -> {

                    if (draggedEvent.getX() >= swipePanelPane.getWidth()) {

                        Alert alert = new Alert(AlertType.NONE);
                        alert.initOwner(primaryStage);
                        alert.setTitle("Purchase report");
                        alert.setContentText(
                            "Thank you for choosing our service!" +
                            "\n\n\tInvoice:\n" +
                            "\n\t\tFuel : \t\tP" + currentFuel.toUpperCase() +
                            "\n\t\tFuel price: \t" + currentFuelPrice +
                            "\n\t\tAmount: \t\t" + String.format("P%1.2f", fuelAmount.doubleValue()) +
                            "\n\t\tLitre(s): \t\t" + currentFuelLitres + " L");
                        alert.getButtonTypes().add(ButtonType.OK);
                        alert.showAndWait();

                        resetViews();

                        pressedEvent.consume();
                        draggedEvent.consume();

                        adminCardImageView.setTranslateX(0.0);
                    } else {
                        adminCardImageView.setTranslateX(draggedEvent.getX() - pressedEvent.getX());
                    }
    
                });
    
            });
            swipePanelPane.setOnMouseReleased(draggedReleaseEvent -> {
                adminCardImageView.setTranslateX(0.0);
            });

        });
        new Thread(task).start();

    }

    private void disableButtons (boolean state) {
        refillButton.setDisable(state);

        for (Node fuelButtonNode : fuelButtonsWrapper.getChildren())
            fuelButtonNode.setDisable(state);

        for (Node numPadButtonNode : numPadButtonsWrapper.getChildren())
            numPadButtonNode.setDisable(state);

    }

    private void resetViews () {
        disableButtons(false);

        amountLabel.setText("0.00");
        litresLabel.setText("0.00");

        for (Node button : fuelButtonsWrapper.getChildren())
            button.getStyleClass().removeAll("active-fuel-button");

        gascCanImageView.setImage(gasCansImg.getImage("0"));

        refillButtonBackGround.setImage(new Image("img/fuel_button_deactive.png"));

        adminCardImageView.setOpacity(0.0);

        swipePanelPane.setOnMousePressed(null);
        swipePanelPane.setOnMouseDragged(null);
        swipePanelPane.setOnMouseReleased(null);


        currentFuel = "";
        currentFuelPrice = new BigDecimal("0.0");
        currentFuelLitres = new BigDecimal("0.0");
        fuelAmount = new BigDecimal("0.0");
    
        previouslyHoveredFuelButtonText = "";


    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

}