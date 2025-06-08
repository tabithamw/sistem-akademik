package akademik;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main Application Class untuk Sistem Akademik
 * Hello World JavaFX untuk testing setup
 */
public class AkademikApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create UI components
        Label titleLabel = new Label("🎓 Sistem Akademik JavaFX");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Setup berhasil! JavaFX siap digunakan.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        Button testButton = new Button("Test Button");
        testButton.setOnAction(e -> {
            statusLabel.setText("Button clicked! 🎉");
        });

        // Create layout
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, statusLabel, testButton);
        root.setStyle("-fx-padding: 50px; -fx-background-color: #f0f0f0;");

        // Create scene and stage
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("Sistem Akademik - Hello World");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}