package akademik.view;

import akademik.model.Dosen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * View untuk mengelola data Dosen
 * Menggunakan JavaFX components tanpa FXML
 */
public class DosenView extends BorderPane {

    // Form components
    private TextField nppField;
    private TextField namaField;
    private TextField noHpField;

    // Table and data
    private TableView<Dosen> dosenTable;

    // Buttons
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button clearButton;
    private Button refreshButton;

    // Validation labels
    private Label validationLabel;

    public DosenView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupStyling();

        // Load initial data (temporary - will be moved to ViewModel later)
        loadSampleData();
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Form fields
        nppField = new TextField();
        nppField.setPromptText("Masukkan NPP Dosen (contoh: NPP001)");
        nppField.getStyleClass().add("form-field");

        namaField = new TextField();
        namaField.setPromptText("Masukkan Nama Lengkap Dosen");
        namaField.getStyleClass().add("form-field");

        noHpField = new TextField();
        noHpField.setPromptText("Masukkan No. HP (opsional)");
        noHpField.getStyleClass().add("form-field");

        // Buttons
        saveButton = new Button("💾 Simpan");
        saveButton.getStyleClass().addAll("btn", "btn-primary");

        updateButton = new Button("📝 Update");
        updateButton.getStyleClass().addAll("btn", "btn-secondary");
        updateButton.setDisable(true);

        deleteButton = new Button("🗑️ Hapus");
        deleteButton.getStyleClass().addAll("btn", "btn-danger");
        deleteButton.setDisable(true);

        clearButton = new Button("🔄 Bersihkan");
        clearButton.getStyleClass().addAll("btn", "btn-outline");

        refreshButton = new Button("↻ Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");

        // Validation label
        validationLabel = new Label();
        validationLabel.getStyleClass().add("validation-message");
        validationLabel.setVisible(false);

        // Table
        setupTable();
    }

    /**
     * Setup table view with columns
     */
    private void setupTable() {
        dosenTable = new TableView<>();
        dosenTable.getStyleClass().add("data-table");

        // NPP Column
        TableColumn<Dosen, String> nppColumn = new TableColumn<>("NPP");
        nppColumn.setCellValueFactory(new PropertyValueFactory<>("npp"));
        nppColumn.setPrefWidth(120);
        nppColumn.getStyleClass().add("table-column");

        // Nama Column
        TableColumn<Dosen, String> namaColumn = new TableColumn<>("Nama Dosen");
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaColumn.setPrefWidth(250);
        namaColumn.getStyleClass().add("table-column");

        // No HP Column
        TableColumn<Dosen, String> noHpColumn = new TableColumn<>("No. HP");
        noHpColumn.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        noHpColumn.setPrefWidth(150);
        noHpColumn.getStyleClass().add("table-column");

        // Add columns to table
        dosenTable.getColumns().addAll(nppColumn, namaColumn, noHpColumn);

        // Table properties
        dosenTable.setRowFactory(tv -> {
            TableRow<Dosen> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    selectDosen(row.getItem());
                }
            });
            return row;
        });

        // Auto resize columns
        dosenTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Setup layout structure
     */
    private void setupLayout() {
        // Left side - Form
        VBox formSection = createFormSection();

        // Right side - Table
        VBox tableSection = createTableSection();

        // Split the content
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.getChildren().addAll(formSection, tableSection);

        // Set preferred widths
        HBox.setHgrow(formSection, Priority.NEVER);
        HBox.setHgrow(tableSection, Priority.ALWAYS);
        formSection.setPrefWidth(350);

        setCenter(mainContent);
    }

    /**
     * Create form section
     */
    private VBox createFormSection() {
        VBox formSection = new VBox(15);
        formSection.getStyleClass().add("form-section");
        formSection.setPadding(new Insets(20));

        // Form title
        Label formTitle = new Label("📝 Form Data Dosen");
        formTitle.getStyleClass().add("section-title");

        // Form fields
        VBox fieldsBox = new VBox(10);

        // NPP field with label
        Label nppLabel = new Label("NPP *");
        nppLabel.getStyleClass().add("field-label");

        Label namaLabel = new Label("Nama Dosen *");
        namaLabel.getStyleClass().add("field-label");

        Label noHpLabel = new Label("No. HP");
        noHpLabel.getStyleClass().add("field-label");

        fieldsBox.getChildren().addAll(
            nppLabel, nppField,
            namaLabel, namaField,
            noHpLabel, noHpField
        );

        // Validation message
        validationLabel.setWrapText(true);

        // Buttons
        HBox buttonBox1 = new HBox(10);
        buttonBox1.getChildren().addAll(saveButton, updateButton);

        HBox buttonBox2 = new HBox(10);
        buttonBox2.getChildren().addAll(deleteButton, clearButton);

        // Required fields note
        Label requiredNote = new Label("* Field wajib diisi");
        requiredNote.getStyleClass().add("required-note");

        formSection.getChildren().addAll(
            formTitle,
            fieldsBox,
            validationLabel,
            buttonBox1,
            buttonBox2,
            requiredNote
        );

        return formSection;
    }

    /**
     * Create table section
     */
    private VBox createTableSection() {
        VBox tableSection = new VBox(15);
        tableSection.getStyleClass().add("table-section");

        // Table header
        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        Label tableTitle = new Label("📋 Daftar Dosen");
        tableTitle.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        tableHeader.getChildren().addAll(tableTitle, spacer, refreshButton);

        // Table with scroll
        VBox.setVgrow(dosenTable, Priority.ALWAYS);

        tableSection.getChildren().addAll(tableHeader, dosenTable);

        return tableSection;
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Table selection
        dosenTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectDosen(newValue);
                }
            }
        );

        // Button actions (temporary - will be moved to ViewModel)
        saveButton.setOnAction(e -> handleSave());
        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClear());
        refreshButton.setOnAction(e -> handleRefresh());

        // Field validation (real-time)
        nppField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        namaField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    /**
     * Setup component styling
     */
    private void setupStyling() {
        getStyleClass().add("dosen-view");
    }

    /**
     * Select dosen and populate form
     */
    private void selectDosen(Dosen dosen) {
        nppField.setText(dosen.getNpp());
        namaField.setText(dosen.getNama());
        noHpField.setText(dosen.getNoHp() != null ? dosen.getNoHp() : "");

        // Enable/disable buttons
        saveButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);

        // Make NPP field read-only when updating
        nppField.setEditable(false);

        clearValidation();
    }

    /**
     * Validate form and show/hide validation messages
     */
    private void validateForm() {
        String npp = nppField.getText().trim();
        String nama = namaField.getText().trim();

        if (npp.isEmpty() || nama.isEmpty()) {
            showValidation("NPP dan Nama harus diisi!", "error");
            return;
        }

        if (npp.length() < 3) {
            showValidation("NPP minimal 3 karakter!", "error");
            return;
        }

        clearValidation();
    }

    /**
     * Show validation message
     */
    private void showValidation(String message, String type) {
        validationLabel.setText(message);
        validationLabel.getStyleClass().removeAll("validation-error", "validation-success");
        validationLabel.getStyleClass().add("validation-" + type);
        validationLabel.setVisible(true);
    }

    /**
     * Clear validation message
     */
    private void clearValidation() {
        validationLabel.setVisible(false);
    }

    // === TEMPORARY EVENT HANDLERS (Will be moved to ViewModel) ===

    private void handleSave() {
        if (validateInput()) {
            // Create new dosen object
            Dosen newDosen = new Dosen(
                nppField.getText().trim(),
                namaField.getText().trim(),
                noHpField.getText().trim().isEmpty() ? null : noHpField.getText().trim()
            );

            // Add to table (temporary)
            dosenTable.getItems().add(newDosen);
            showValidation("Data dosen berhasil disimpan!", "success");
            handleClear();
        }
    }

    private void handleUpdate() {
        Dosen selectedDosen = dosenTable.getSelectionModel().getSelectedItem();
        if (selectedDosen != null && validateInput()) {
            // Update selected dosen
            selectedDosen.setNama(namaField.getText().trim());
            selectedDosen.setNoHp(noHpField.getText().trim().isEmpty() ? null : noHpField.getText().trim());

            // Refresh table
            dosenTable.refresh();
            showValidation("Data dosen berhasil diupdate!", "success");
            handleClear();
        }
    }

    private void handleDelete() {
        Dosen selectedDosen = dosenTable.getSelectionModel().getSelectedItem();
        if (selectedDosen != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Konfirmasi Hapus");
            confirmDialog.setHeaderText("Hapus Data Dosen");
            confirmDialog.setContentText("Apakah Anda yakin ingin menghapus data dosen: " + selectedDosen.getNama() + "?");

            if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                dosenTable.getItems().remove(selectedDosen);
                showValidation("Data dosen berhasil dihapus!", "success");
                handleClear();
            }
        }
    }

    private void handleClear() {
        nppField.clear();
        namaField.clear();
        noHpField.clear();

        nppField.setEditable(true);
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        dosenTable.getSelectionModel().clearSelection();
        clearValidation();
    }

    private void handleRefresh() {
        // Reload data from database (temporary - load sample data)
        loadSampleData();
        showValidation("Data berhasil di-refresh!", "success");
    }

    private boolean validateInput() {
        String npp = nppField.getText().trim();
        String nama = namaField.getText().trim();

        if (npp.isEmpty()) {
            showValidation("NPP tidak boleh kosong!", "error");
            nppField.requestFocus();
            return false;
        }

        if (nama.isEmpty()) {
            showValidation("Nama tidak boleh kosong!", "error");
            namaField.requestFocus();
            return false;
        }

        if (npp.length() < 3) {
            showValidation("NPP minimal 3 karakter!", "error");
            nppField.requestFocus();
            return false;
        }

        // Check duplicate NPP (only for new records)
        if (saveButton.isDisabled() == false) {
            for (Dosen dosen : dosenTable.getItems()) {
                if (dosen.getNpp().equals(npp)) {
                    showValidation("NPP sudah ada! Gunakan NPP yang lain.", "error");
                    nppField.requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    private void loadSampleData() {
        dosenTable.getItems().clear();

        // Sample data
        dosenTable.getItems().addAll(
            new Dosen("NPP001", "Dr. Ahmad Fauzi, M.Kom", "08123456789"),
            new Dosen("NPP002", "Dr. Siti Rahayu, M.T", "08234567890"),
            new Dosen("NPP003", "Prof. Budi Santoso, Ph.D", "08345678901"),
            new Dosen("NPP004", "Dr. Rina Kartika, M.Sc", "08456789012")
        );
    }
}
