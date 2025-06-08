package akademik.view;

import akademik.model.Mahasiswa;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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
 * View untuk mengelola data Mahasiswa
 * Menggunakan JavaFX components tanpa FXML dengan form yang lebih kompleks
 */
public class MahasiswaView extends BorderPane {

    // Form components
    private TextField nimField;
    private TextField namaField;
    private ComboBox<String> genderComboBox;
    private TextField ipkField;
    private ComboBox<String> dosenWaliComboBox;

    // Table and data
    private TableView<Mahasiswa> mahasiswaTable;

    // Buttons
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button clearButton;
    private Button refreshButton;

    // Validation labels
    private Label validationLabel;

    public MahasiswaView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupStyling();

        // Load initial data (temporary)
        loadSampleData();
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Form fields
        nimField = new TextField();
        nimField.setPromptText("Masukkan NIM Mahasiswa (contoh: 123210001)");
        nimField.getStyleClass().add("form-field");

        namaField = new TextField();
        namaField.setPromptText("Masukkan Nama Lengkap Mahasiswa");
        namaField.getStyleClass().add("form-field");

        // Gender ComboBox
        genderComboBox = new ComboBox<>();
        genderComboBox.setItems(FXCollections.observableArrayList(
            Mahasiswa.GENDER_LAKI, Mahasiswa.GENDER_PEREMPUAN
        ));
        genderComboBox.setPromptText("Pilih Jenis Kelamin");
        genderComboBox.getStyleClass().add("form-field");
        genderComboBox.setPrefWidth(Double.MAX_VALUE);

        ipkField = new TextField();
        ipkField.setPromptText("Masukkan IPK (0.0 - 4.0)");
        ipkField.getStyleClass().add("form-field");

        // Dosen Wali ComboBox
        dosenWaliComboBox = new ComboBox<>();
        dosenWaliComboBox.setItems(FXCollections.observableArrayList(
            "NPP001 - Dr. Ahmad Fauzi, M.Kom",
            "NPP002 - Dr. Siti Rahayu, M.T",
            "NPP003 - Prof. Budi Santoso, Ph.D",
            "NPP004 - Dr. Rina Kartika, M.Sc"
        ));
        dosenWaliComboBox.setPromptText("Pilih Dosen Wali");
        dosenWaliComboBox.getStyleClass().add("form-field");
        dosenWaliComboBox.setPrefWidth(Double.MAX_VALUE);

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
        mahasiswaTable = new TableView<>();
        mahasiswaTable.getStyleClass().add("data-table");

        // NIM Column
        TableColumn<Mahasiswa, String> nimColumn = new TableColumn<>("NIM");
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        nimColumn.setPrefWidth(120);

        // Nama Column
        TableColumn<Mahasiswa, String> namaColumn = new TableColumn<>("Nama Mahasiswa");
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaColumn.setPrefWidth(200);

        // Gender Column
        TableColumn<Mahasiswa, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderColumn.setPrefWidth(100);

        // IPK Column
        TableColumn<Mahasiswa, Double> ipkColumn = new TableColumn<>("IPK");
        ipkColumn.setCellValueFactory(new PropertyValueFactory<>("ipk"));
        ipkColumn.setPrefWidth(80);

        // Custom cell factory for IPK formatting
        ipkColumn.setCellFactory(column -> new TableCell<Mahasiswa, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        // Dosen Wali Column
        TableColumn<Mahasiswa, String> dosenWaliColumn = new TableColumn<>("Dosen Wali");
        dosenWaliColumn.setCellValueFactory(new PropertyValueFactory<>("dosenWali"));
        dosenWaliColumn.setPrefWidth(150);

        // Add columns to table
        mahasiswaTable.getColumns().addAll(nimColumn, namaColumn, genderColumn, ipkColumn, dosenWaliColumn);

        // Table properties
        mahasiswaTable.setRowFactory(tv -> {
            TableRow<Mahasiswa> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    selectMahasiswa(row.getItem());
                }
            });
            return row;
        });

        // Auto resize columns
        mahasiswaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        formSection.setPrefWidth(380);

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
        Label formTitle = new Label("📝 Form Data Mahasiswa");
        formTitle.getStyleClass().add("section-title");

        // Form fields
        VBox fieldsBox = new VBox(10);

        // Field labels
        Label nimLabel = new Label("NIM *");
        nimLabel.getStyleClass().add("field-label");

        Label namaLabel = new Label("Nama Mahasiswa *");
        namaLabel.getStyleClass().add("field-label");

        Label genderLabel = new Label("Jenis Kelamin *");
        genderLabel.getStyleClass().add("field-label");

        Label ipkLabel = new Label("IPK *");
        ipkLabel.getStyleClass().add("field-label");

        Label dosenWaliLabel = new Label("Dosen Wali");
        dosenWaliLabel.getStyleClass().add("field-label");

        // IPK Helper
        Label ipkHelper = new Label("(Rentang: 0.0 - 4.0)");
        ipkHelper.getStyleClass().add("field-helper");

        fieldsBox.getChildren().addAll(
            nimLabel, nimField,
            namaLabel, namaField,
            genderLabel, genderComboBox,
            ipkLabel, ipkField, ipkHelper,
            dosenWaliLabel, dosenWaliComboBox
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

        Label tableTitle = new Label("📋 Daftar Mahasiswa");
        tableTitle.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Statistics label
        Label statsLabel = new Label("Total: 0 mahasiswa");
        statsLabel.getStyleClass().add("stats-label");

        tableHeader.getChildren().addAll(tableTitle, spacer, statsLabel, refreshButton);

        // Table with scroll
        VBox.setVgrow(mahasiswaTable, Priority.ALWAYS);

        tableSection.getChildren().addAll(tableHeader, mahasiswaTable);

        return tableSection;
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Table selection
        mahasiswaTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectMahasiswa(newValue);
                }
            }
        );

        // Button actions
        saveButton.setOnAction(e -> handleSave());
        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClear());
        refreshButton.setOnAction(e -> handleRefresh());

        // Real-time validation
        nimField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        namaField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        genderComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        ipkField.textProperty().addListener((obs, oldVal, newVal) -> validateIPK());
    }

    /**
     * Setup component styling
     */
    private void setupStyling() {
        getStyleClass().add("mahasiswa-view");
    }

    /**
     * Select mahasiswa and populate form
     */
    private void selectMahasiswa(Mahasiswa mahasiswa) {
        nimField.setText(mahasiswa.getNim());
        namaField.setText(mahasiswa.getNama());
        genderComboBox.setValue(mahasiswa.getGender());
        ipkField.setText(String.valueOf(mahasiswa.getIpk()));

        // Set dosen wali if exists
        if (mahasiswa.getDosenWali() != null) {
            String dosenDisplay = findDosenDisplay(mahasiswa.getDosenWali());
            dosenWaliComboBox.setValue(dosenDisplay);
        } else {
            dosenWaliComboBox.setValue(null);
        }

        // Enable/disable buttons
        saveButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);

        // Make NIM field read-only when updating
        nimField.setEditable(false);

        clearValidation();
    }

    /**
     * Find dosen display string by NPP
     */
    private String findDosenDisplay(String npp) {
        for (String item : dosenWaliComboBox.getItems()) {
            if (item.startsWith(npp)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Extract NPP from dosen display string
     */
    private String extractNPP(String dosenDisplay) {
        if (dosenDisplay != null && dosenDisplay.contains(" - ")) {
            return dosenDisplay.split(" - ")[0];
        }
        return null;
    }

    /**
     * Validate form inputs
     */
    private void validateForm() {
        String nim = nimField.getText().trim();
        String nama = namaField.getText().trim();
        String gender = genderComboBox.getValue();

        if (nim.isEmpty() || nama.isEmpty() || gender == null) {
            showValidation("NIM, Nama, dan Gender harus diisi!", "error");
            return;
        }

        if (nim.length() < 6) {
            showValidation("NIM minimal 6 karakter!", "error");
            return;
        }

        clearValidation();
    }

    /**
     * Validate IPK field
     */
    private void validateIPK() {
        String ipkText = ipkField.getText().trim();

        if (ipkText.isEmpty()) {
            return;
        }

        try {
            double ipk = Double.parseDouble(ipkText);
            if (ipk < 0.0 || ipk > 4.0) {
                showValidation("IPK harus antara 0.0 - 4.0!", "error");
                return;
            }
            clearValidation();
        } catch (NumberFormatException e) {
            showValidation("IPK harus berupa angka!", "error");
        }
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

    // === TEMPORARY EVENT HANDLERS ===

    private void handleSave() {
        if (validateInput()) {
            try {
                // Create new mahasiswa object
                double ipk = Double.parseDouble(ipkField.getText().trim());
                String dosenWali = extractNPP(dosenWaliComboBox.getValue());

                Mahasiswa newMahasiswa = new Mahasiswa(
                    nimField.getText().trim(),
                    namaField.getText().trim(),
                    genderComboBox.getValue(),
                    ipk,
                    dosenWali
                );

                // Add to table (temporary)
                mahasiswaTable.getItems().add(newMahasiswa);
                showValidation("Data mahasiswa berhasil disimpan!", "success");
                handleClear();

            } catch (NumberFormatException e) {
                showValidation("IPK harus berupa angka yang valid!", "error");
            }
        }
    }

    private void handleUpdate() {
        Mahasiswa selectedMahasiswa = mahasiswaTable.getSelectionModel().getSelectedItem();
        if (selectedMahasiswa != null && validateInput()) {
            try {
                // Update selected mahasiswa
                double ipk = Double.parseDouble(ipkField.getText().trim());
                String dosenWali = extractNPP(dosenWaliComboBox.getValue());

                selectedMahasiswa.setNama(namaField.getText().trim());
                selectedMahasiswa.setGender(genderComboBox.getValue());
                selectedMahasiswa.setIpk(ipk);
                selectedMahasiswa.setDosenWali(dosenWali);

                // Refresh table
                mahasiswaTable.refresh();
                showValidation("Data mahasiswa berhasil diupdate!", "success");
                handleClear();

            } catch (NumberFormatException e) {
                showValidation("IPK harus berupa angka yang valid!", "error");
            }
        }
    }

    private void handleDelete() {
        Mahasiswa selectedMahasiswa = mahasiswaTable.getSelectionModel().getSelectedItem();
        if (selectedMahasiswa != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Konfirmasi Hapus");
            confirmDialog.setHeaderText("Hapus Data Mahasiswa");
            confirmDialog.setContentText("Apakah Anda yakin ingin menghapus data mahasiswa: " + selectedMahasiswa.getNama() + "?");

            if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                mahasiswaTable.getItems().remove(selectedMahasiswa);
                showValidation("Data mahasiswa berhasil dihapus!", "success");
                handleClear();
            }
        }
    }

    private void handleClear() {
        nimField.clear();
        namaField.clear();
        genderComboBox.setValue(null);
        ipkField.clear();
        dosenWaliComboBox.setValue(null);

        nimField.setEditable(true);
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        mahasiswaTable.getSelectionModel().clearSelection();
        clearValidation();
    }

    private void handleRefresh() {
        loadSampleData();
        showValidation("Data berhasil di-refresh!", "success");
    }

    private boolean validateInput() {
        String nim = nimField.getText().trim();
        String nama = namaField.getText().trim();
        String gender = genderComboBox.getValue();
        String ipkText = ipkField.getText().trim();

        if (nim.isEmpty()) {
            showValidation("NIM tidak boleh kosong!", "error");
            nimField.requestFocus();
            return false;
        }

        if (nama.isEmpty()) {
            showValidation("Nama tidak boleh kosong!", "error");
            namaField.requestFocus();
            return false;
        }

        if (gender == null) {
            showValidation("Jenis kelamin harus dipilih!", "error");
            genderComboBox.requestFocus();
            return false;
        }

        if (ipkText.isEmpty()) {
            showValidation("IPK tidak boleh kosong!", "error");
            ipkField.requestFocus();
            return false;
        }

        try {
            double ipk = Double.parseDouble(ipkText);
            if (ipk < 0.0 || ipk > 4.0) {
                showValidation("IPK harus antara 0.0 - 4.0!", "error");
                ipkField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidation("IPK harus berupa angka yang valid!", "error");
            ipkField.requestFocus();
            return false;
        }

        if (nim.length() < 6) {
            showValidation("NIM minimal 6 karakter!", "error");
            nimField.requestFocus();
            return false;
        }

        // Check duplicate NIM (only for new records)
        if (saveButton.isDisabled() == false) {
            for (Mahasiswa mahasiswa : mahasiswaTable.getItems()) {
                if (mahasiswa.getNim().equals(nim)) {
                    showValidation("NIM sudah ada! Gunakan NIM yang lain.", "error");
                    nimField.requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    private void loadSampleData() {
        mahasiswaTable.getItems().clear();

        // Sample data
        mahasiswaTable.getItems().addAll(
            new Mahasiswa("123210001", "Andi Prasetyo", Mahasiswa.GENDER_LAKI, 3.75, "NPP001"),
            new Mahasiswa("123210002", "Dewi Sartika", Mahasiswa.GENDER_PEREMPUAN, 3.85, "NPP001"),
            new Mahasiswa("123210003", "Rizki Ramadhan", Mahasiswa.GENDER_LAKI, 3.20, "NPP002"),
            new Mahasiswa("123210004", "Maya Indira", Mahasiswa.GENDER_PEREMPUAN, 3.95, "NPP002"),
            new Mahasiswa("123210005", "Faisal Ibrahim", Mahasiswa.GENDER_LAKI, 3.40, "NPP003")
        );
    }
}
