package akademik;

import akademik.database.DatabaseConnection;
import akademik.util.DatabaseTester;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main Application Class untuk Sistem Akademik
 * Tahap 2: Testing Database dan DAO Operations
 */
public class AkademikApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize database connection
        DatabaseConnection.getInstance();

        // Run database tests
        DatabaseTester tester = new DatabaseTester();
        tester.runAllTests();
        tester.insertSampleData();

        // Create UI components
        Label titleLabel = new Label("🎓 Sistem Akademik - Database Ready");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Database dan DAO layer berhasil diinisialisasi!");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        Button testDbButton = new Button("Test Database Connection");
        testDbButton.setOnAction(e -> {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn.testConnection()) {
                statusLabel.setText("✅ Database connection is working perfectly!");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
            } else {
                statusLabel.setText("❌ Database connection failed!");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            }
        });

        Button runTestsButton = new Button("Run Database Tests");
        runTestsButton.setOnAction(e -> {
            try {
                new DatabaseTester().runAllTests();
                statusLabel.setText("✅ All database tests passed!");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
            } catch (Exception ex) {
                statusLabel.setText("❌ Database tests failed: " + ex.getMessage());
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            }
        });

        // Create layout
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, statusLabel, testDbButton, runTestsButton);
        root.setStyle("-fx-padding: 50px; -fx-background-color: #f0f0f0;");

        // Create scene and stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Sistem Akademik - Database Testing");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Graceful shutdown
        primaryStage.setOnCloseRequest(e -> {
            DatabaseConnection.getInstance().closeConnection();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
