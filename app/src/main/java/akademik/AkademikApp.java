package akademik;

import akademik.database.DatabaseConnection;
import akademik.util.DatabaseTester;
import akademik.view.DosenView;
import akademik.view.MahasiswaView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main Application Class untuk Sistem Akademik
 * Tahap 3: Complete UI Implementation
 */
public class AkademikApp extends Application {

    private TabPane mainTabPane;
    private MahasiswaView mahasiswaView;
    private DosenView dosenView;

    @Override
    public void start(Stage primaryStage) {
        // Initialize database and sample data
        initializeDatabase();

        // Setup main window
        setupPrimaryStage(primaryStage);

        // Create main scene
        Scene scene = createMainScene();

        // Apply styling
        scene.getStylesheets().add(createStylesheet());

        // Show application
        primaryStage.setScene(scene);
        primaryStage.show();

        // Graceful shutdown
        primaryStage.setOnCloseRequest(e -> {
            DatabaseConnection.getInstance().closeConnection();
        });
    }

    /**
     * Initialize database connection and sample data
     */
    private void initializeDatabase() {
        System.out.println("🚀 Initializing application...");

        // Initialize database
        DatabaseConnection.getInstance();

        // Insert sample data
        DatabaseTester tester = new DatabaseTester();
        tester.insertSampleData();

        System.out.println("✅ Application initialized successfully!");
    }

    /**
     * Setup primary stage properties
     */
    private void setupPrimaryStage(Stage primaryStage) {
        primaryStage.setTitle("Sistem Informasi Akademik");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        // Center on screen
        primaryStage.centerOnScreen();
    }

    /**
     * Create main scene with complete layout
     */
    private Scene createMainScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        // Create header
        VBox header = createHeader();
        root.setTop(header);

        // Create main content with tabs
        mainTabPane = createMainContent();
        root.setCenter(mainTabPane);

        // Create footer
        HBox footer = createFooter();
        root.setBottom(footer);

        return new Scene(root);
    }

    /**
     * Create application header
     */
    private VBox createHeader() {
        VBox header = new VBox();
        header.getStyleClass().add("app-header");
        header.setPadding(new Insets(20));
        header.setSpacing(5);

        // Main title
        Label titleLabel = new Label("🎓 Sistem Informasi Akademik");
        titleLabel.getStyleClass().add("main-title");

        // Subtitle
        Label subtitleLabel = new Label("Manajemen Data Mahasiswa dan Dosen");
        subtitleLabel.getStyleClass().add("subtitle");

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    /**
     * Create main content area with tabs
     */
    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("main-tab-pane");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create views
        mahasiswaView = new MahasiswaView();
        dosenView = new DosenView();

        // Create tabs
        Tab mahasiswaTab = new Tab("👨‍🎓 Data Mahasiswa");
        mahasiswaTab.setContent(mahasiswaView);
        mahasiswaTab.getStyleClass().add("data-tab");

        Tab dosenTab = new Tab("👨‍🏫 Data Dosen");
        dosenTab.setContent(dosenView);
        dosenTab.getStyleClass().add("data-tab");

        tabPane.getTabs().addAll(mahasiswaTab, dosenTab);

        return tabPane;
    }

    /**
     * Create application footer
     */
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("app-footer");
        footer.setPadding(new Insets(10, 20, 10, 20));

        Label footerLabel = new Label("© 2024 - Sistem Akademik | Dibuat dengan ❤️ menggunakan JavaFX");
        footerLabel.getStyleClass().add("footer-text");

        footer.getChildren().add(footerLabel);
        return footer;
    }

    /**
 * Create CSS stylesheet URL
 */
private String createStylesheet() {
    // Return CSS file from resources
    try {
        return getClass().getResource("/css/application.css").toExternalForm();
    } catch (Exception e) {
        System.err.println("Warning: CSS file not found, using default styling");
        return null;
    }
}
}
