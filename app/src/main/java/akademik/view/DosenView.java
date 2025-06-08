package akademik.view;

import akademik.model.Dosen;
import akademik.viewmodel.DosenViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * View untuk mengelola data Dosen dengan MVVM pattern
 * Menggunakan property binding untuk reactive UI
 */
public class DosenView extends BorderPane {

    // ViewModel instance
    private final DosenViewModel viewModel;

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

    // Status components
    private Label validationLabel;
    private ProgressIndicator loadingIndicator;

    public DosenView() {
        // Initialize ViewModel
        this.viewModel = new DosenViewModel();

        initializeComponents();
        setupLayout();
        setupPropertyBindings();
        setupEventHandlers();
        setupStyling();
    }

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

        deleteButton = new Button("🗑 Hapus");
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
        dosenTable = new TableView<>();
        dosenTable.getStyleClass().add("data-table");

        // Setup columns
        TableColumn<Dosen, String> nppColumn = new TableColumn<>("NPP");
        nppColumn.setCellValueFactory(new PropertyValueFactory<>("npp"));
        nppColumn.setPrefWidth(120);

        TableColumn<Dosen, String> namaColumn = new TableColumn<>("Nama Dosen");
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaColumn.setPrefWidth(250);

        TableColumn<Dosen, String> noHpColumn = new TableColumn<>("No. HP");
        noHpColumn.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        noHpColumn.setPrefWidth(150);

        dosenTable.getColumns().addAll(nppColumn, namaColumn, noHpColumn);
        dosenTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        formSection.setPrefWidth(350);

        setCenter(mainContent);
    }

    private VBox createFormSection() {
        VBox formSection = new VBox(15);
        formSection.getStyleClass().add("form-section");
        formSection.setPadding(new Insets(20));

        // Form title
        Label formTitle = new Label("📝 Form Data Dosen");
        formTitle.getStyleClass().add("section-title");

        // Form fields with labels
        VBox fieldsBox = new VBox(10);

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

        // Status section
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.getChildren().addAll(loadingIndicator, validationLabel);

        // Button section
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

        Label tableTitle = new Label("📋 Daftar Dosen");
        tableTitle.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        tableHeader.getChildren().addAll(tableTitle, spacer, refreshButton);

        // Table
        VBox.setVgrow(dosenTable, Priority.ALWAYS);

        tableSection.getChildren().addAll(tableHeader, dosenTable);

        return tableSection;
    }

    /**
     * Setup property bindings antara View dan ViewModel
     */
   private void setupPropertyBindings() {
    // Bind form fields to ViewModel properties (bidirectional)
    nppField.textProperty().bindBidirectional(viewModel.nppProperty());
    namaField.textProperty().bindBidirectional(viewModel.namaProperty());
    noHpField.textProperty().bindBidirectional(viewModel.noHpProperty());

    // Bind table data
    dosenTable.setItems(viewModel.getDosenList());

    // Bind button states to computed properties
    saveButton.disableProperty().bind(viewModel.canSaveProperty().not());
    updateButton.disableProperty().bind(viewModel.canUpdateProperty().not());
    deleteButton.disableProperty().bind(viewModel.canDeleteProperty().not());

    // Bind loading indicator
    loadingIndicator.visibleProperty().bind(viewModel.isLoadingProperty());

    // Bind validation message
    validationLabel.textProperty().bind(viewModel.statusMessageProperty());
    validationLabel.visibleProperty().bind(viewModel.statusMessageProperty().isNotEmpty());

    // Bind validation message style based on error state
    viewModel.hasErrorProperty().addListener((obs, oldVal, newVal) -> {
        validationLabel.getStyleClass().removeAll("validation-error", "validation-success");
        if (newVal) {
            validationLabel.getStyleClass().add("validation-error");
        } else {
            validationLabel.getStyleClass().add("validation-success");
        }
    });
}

private void setupEventHandlers() {
    saveButton.setOnAction(e -> viewModel.saveCommand());
    updateButton.setOnAction(e -> viewModel.updateCommand());
    deleteButton.setOnAction(e -> viewModel.deleteCommand());
    clearButton.setOnAction(e -> viewModel.clearForm());
    refreshButton.setOnAction(e -> viewModel.loadAllDosen());
    
    dosenTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        viewModel.selectDosen(newVal);
    });
}

private void setupStyling() {
        getStyleClass().add("dosen-view");
    }}