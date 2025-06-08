package akademik.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import akademik.model.Dosen;
import akademik.model.Mahasiswa;
import akademik.viewmodel.MahasiswaViewModel;

/**
 * View untuk mengelola data Mahasiswa dengan MVVM pattern
 * Menggunakan property binding untuk reactive UI
 */
public class MahasiswaView extends BorderPane {

    // ViewModel instance
    private final MahasiswaViewModel viewModel;

    // Form components
    private TextField nimField;
    private TextField namaField;
    private ComboBox<String> genderComboBox;
    private TextField ipkField;
    private ComboBox<Dosen> dosenWaliComboBox;

    // Table and data
    private TableView<Mahasiswa> mahasiswaTable;

    // Buttons
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button clearButton;
    private Button refreshButton;

    // Status components
    private Label validationLabel;
    private ProgressIndicator loadingIndicator;

    public MahasiswaView() {
        this.viewModel = new MahasiswaViewModel();

        initializeComponents();
        setupLayout();
        setupPropertyBindings();
        setupEventHandlers();
        setupStyling();
    }

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
        dosenWaliComboBox.setPromptText("Pilih Dosen Wali");
        dosenWaliComboBox.getStyleClass().add("form-field");
        dosenWaliComboBox.setPrefWidth(Double.MAX_VALUE);

        // Set converter untuk dosen combo box
        dosenWaliComboBox.setConverter(new StringConverter<Dosen>() {
            @Override
            public String toString(Dosen dosen) {
                return dosen != null ? dosen.getNama() + " (" + dosen.getNpp() + ")" : "";
            }

            @Override
            public Dosen fromString(String string) {
                return dosenWaliComboBox.getItems().stream()
                    .filter(dosen -> toString(dosen).equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });

        // Buttons
        saveButton = new Button("💾 Simpan");
        saveButton.getStyleClass().addAll("btn", "btn-primary");

        updateButton = new Button("📝 Update");
        updateButton.getStyleClass().addAll("btn", "btn-secondary");

        deleteButton = new Button("🗑️ Hapus");
        deleteButton.getStyleClass().addAll("btn", "btn-danger");

        clearButton = new Button("🔄 Bersihkan");
        clearButton.getStyleClass().addAll("btn", "btn-outline");

        refreshButton = new Button("↻ Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");

        // Status components
        validationLabel = new Label();
        validationLabel.getStyleClass().add("validation-message");
        validationLabel.setWrapText(true);

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(24, 24);
        loadingIndicator.setVisible(false);

        // Table
        setupTable();
    }

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

        // IPK Column with custom formatting
        TableColumn<Mahasiswa, Double> ipkColumn = new TableColumn<>("IPK");
        ipkColumn.setCellValueFactory(new PropertyValueFactory<>("ipk"));
        ipkColumn.setPrefWidth(80);
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

        mahasiswaTable.getColumns().addAll(nimColumn, namaColumn, genderColumn, ipkColumn, dosenWaliColumn);
        mahasiswaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupLayout() {
        // Form section
        VBox formSection = createFormSection();

        // Table section
        VBox tableSection = createTableSection();

        // Main content
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.getChildren().addAll(formSection, tableSection);

        HBox.setHgrow(formSection, Priority.NEVER);
        HBox.setHgrow(tableSection, Priority.ALWAYS);
        formSection.setPrefWidth(380);

        setCenter(mainContent);
    }

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

        // Status section
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.getChildren().addAll(loadingIndicator, validationLabel);

        // Buttons
        HBox buttonBox1 = new HBox(10);
        buttonBox1.getChildren().addAll(saveButton, updateButton);

        HBox buttonBox2 = new HBox(10);
        buttonBox2.getChildren().addAll(deleteButton, clearButton);

        // Required note
        Label requiredNote = new Label("* Field wajib diisi");
        requiredNote.getStyleClass().add("required-note");

        formSection.getChildren().addAll(
            formTitle,
            fieldsBox,
            statusBox,
            buttonBox1,
            buttonBox2,
            requiredNote
        );

        return formSection;
    }

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

        tableHeader.getChildren().addAll(tableTitle, spacer, refreshButton);

        // Table
        VBox.setVgrow(mahasiswaTable, Priority.ALWAYS);

        tableSection.getChildren().addAll(tableHeader, mahasiswaTable);

        return tableSection;
    }

    /**
     * Setup property bindings antara View dan ViewModel
     */
    private void setupPropertyBindings() {
        // Bind form fields to ViewModel properties (bidirectional)
        nimField.textProperty().bindBidirectional(viewModel.nimProperty());
        namaField.textProperty().bindBidirectional(viewModel.namaProperty());
        genderComboBox.valueProperty().bindBidirectional(viewModel.genderProperty());

        // IPK field needs special handling for string <-> double conversion
        ipkField.textProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.setIpkFromString(newVal);
        });

        viewModel.ipkProperty().addListener((obs, oldVal, newVal) -> {
            if (!ipkField.isFocused()) {
                if (newVal.doubleValue() == 0.0) {
                    ipkField.setText("");
                } else {
                    ipkField.setText(String.format("%.2f", newVal.doubleValue()));
                }
            }
        });

        // Dosen wali combo box binding
        dosenWaliComboBox.setItems(viewModel.getDosenList());

        dosenWaliComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.dosenWaliProperty().set(newVal.getNpp());
            } else {
                viewModel.dosenWaliProperty().set("");
            }
        });

        viewModel.dosenWaliProperty().addListener((obs, oldVal, newVal) -> {
            Dosen selectedDosen = viewModel.getDosenList().stream()
                .filter(dosen -> dosen.getNpp().equals(newVal))
                .findFirst()
                .orElse(null);
            dosenWaliComboBox.setValue(selectedDosen);
        });

        // Bind table data
        mahasiswaTable.setItems(viewModel.getMahasiswaList());

        // Bind button states
        saveButton.disableProperty().bind(viewModel.canSaveProperty().not());
        updateButton.disableProperty().bind(viewModel.canUpdateProperty().not());
        deleteButton.disableProperty().bind(viewModel.canDeleteProperty().not());

        // Bind loading indicator
        loadingIndicator.visibleProperty().bind(viewModel.isLoadingProperty());

        // Bind validation message
        validationLabel.textProperty().bind(viewModel.statusMessageProperty());
        validationLabel.visibleProperty().bind(viewModel.statusMessageProperty().isNotEmpty());

        // Bind validation message style
        viewModel.hasErrorProperty().addListener((obs, oldVal, newVal) -> {
            validationLabel.getStyleClass().removeAll("validation-error", "validation-success");
            if (newVal) {
                validationLabel.getStyleClass().add("validation-error");
            } else if (!viewModel.statusMessageProperty().get().isEmpty()) {
                validationLabel.getStyleClass().add("validation-success");
            }
        });

        // Bind NIM field editability
        nimField.editableProperty().bind(viewModel.isEditingProperty().not());
    }

    private void setupEventHandlers() {
        // Table selection
        mahasiswaTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                viewModel.selectMahasiswa(newValue);
            }
        );

        // Button commands
        saveButton.setOnAction(e -> viewModel.saveCommand());
        updateButton.setOnAction(e -> viewModel.updateCommand());
        deleteButton.setOnAction(e -> {
            if (showDeleteConfirmation()) {
                viewModel.deleteCommand();
            }
        });
        clearButton.setOnAction(e -> {
            viewModel.clearCommand();
            mahasiswaTable.getSelectionModel().clearSelection();
        });
        refreshButton.setOnAction(e -> viewModel.refreshCommand());
    }

    private void setupStyling() {
        getStyleClass().add("mahasiswa-view");
    }

    /**
     * Show delete confirmation dialog
     */
    private boolean showDeleteConfirmation() {
        Mahasiswa selectedMahasiswa = viewModel.getSelectedMahasiswa();
        if (selectedMahasiswa == null) {
            return false;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Konfirmasi Hapus");
        confirmDialog.setHeaderText("Hapus Data Mahasiswa");
        confirmDialog.setContentText("Apakah Anda yakin ingin menghapus data mahasiswa: " + selectedMahasiswa.getNama() + "?");

        return confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Get ViewModel instance
     */
    public MahasiswaViewModel getViewModel() {
        return viewModel;
    }
}
