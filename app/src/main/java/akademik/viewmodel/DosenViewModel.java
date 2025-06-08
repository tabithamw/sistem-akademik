package akademik.viewmodel;

import akademik.dao.DosenDAO;
import akademik.dao.DosenDAOImpl;
import akademik.model.Dosen;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel untuk mengelola data Dosen Mengimplementasikan pola MVVM dengan
 * JavaFX Properties
 */

public class DosenViewModel {

    // DAO untuk database operations
    private final DosenDAO dosenDAO;

    // Observable list untuk table binding
    private final ObservableList<Dosen> dosenList;

    // Properties untuk form binding
    private final StringProperty npp = new SimpleStringProperty("");
    private final StringProperty nama = new SimpleStringProperty("");
    private final StringProperty noHp = new SimpleStringProperty("");

    // Properties untuk UI state management
    private final ObjectProperty<Dosen> selectedDosen = new SimpleObjectProperty<>();
    private final BooleanProperty isEditing = new SimpleBooleanProperty(false);
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty hasError = new SimpleBooleanProperty(false);

    // Computed properties untuk button states
    private final BooleanProperty canSave = new SimpleBooleanProperty();
    private final BooleanProperty canUpdate = new SimpleBooleanProperty();
    private final BooleanProperty canDelete = new SimpleBooleanProperty();

    public DosenViewModel() {
        this.dosenDAO = new DosenDAOImpl();
        this.dosenList = FXCollections.observableArrayList();

        setupComputedProperties();
        setupPropertyListeners();
        loadAllDosen();
    }

    /**
     * Setup computed properties yang bergantung pada properties lain
     */
    private void setupComputedProperties() {
        // canSave: bisa save jika tidak sedang editing dan form valid
        canSave.bind(
            isEditing.not()
                .and(npp.isNotEmpty())
                .and(nama.isNotEmpty())
                .and(isLoading.not())
        );

        // canUpdate: bisa update jika sedang editing dan form valid
        canUpdate.bind(
            isEditing
                .and(selectedDosen.isNotNull())
                .and(nama.isNotEmpty())
                .and(isLoading.not())
        );

        // canDelete: bisa delete jika ada yang dipilih dan tidak sedang loading
        canDelete.bind(
            selectedDosen.isNotNull()
                .and(isLoading.not())
        );
    }

    /**
     * Setup property listeners untuk validation dan side effects
     */
    private void setupPropertyListeners() {
        // Clear error status when user starts typing
        npp.addListener((obs, oldVal, newVal) -> clearError());
        nama.addListener((obs, oldVal, newVal) -> clearError());
        noHp.addListener((obs, oldVal, newVal) -> clearError());

        // Validate NPP format
        npp.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty() && newVal.length() < 3) {
                setError("NPP minimal 3 karakter");
            }
        });
    }

    // === COMMAND METHODS (Actions) ===

    /**
     * Command untuk menyimpan dosen baru
     */
    public void saveCommand() {
        if (!canSave.get()) {
            return;
        }

        setLoading(true);
        clearError();

        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Check for duplicate NPP
            if (dosenDAO.existsByNpp(npp.get())) {
                setError("NPP " + npp.get() + " sudah ada dalam database");
                return;
            }

            // Create and save new dosen
            Dosen newDosen = new Dosen(npp.get().trim(), nama.get().trim(),
                    noHp.get().trim().isEmpty() ? null : noHp.get().trim());

            dosenDAO.save(newDosen);

            // Reload data and clear form
            loadAllDosen();
            clearForm();
            setSuccess("Data dosen berhasil disimpan!");

        } catch (Exception e) {
            setError("Error menyimpan data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Command untuk mengupdate dosen yang sudah ada
     */
    public void updateCommand() {
        if (!canUpdate.get() || selectedDosen.get() == null) {
            return;
        }

        setLoading(true);
        clearError();

        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Update dosen data
            Dosen dosenToUpdate = selectedDosen.get();
            dosenToUpdate.setNama(nama.get().trim());
            dosenToUpdate.setNoHp(noHp.get().trim().isEmpty() ? null : noHp.get().trim());

            dosenDAO.update(dosenToUpdate);

            // Reload data and clear form
            loadAllDosen();
            clearForm();
            setSuccess("Data dosen berhasil diupdate!");

        } catch (Exception e) {
            setError("Error mengupdate data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Command untuk menghapus dosen
     */
    public void deleteCommand() {
        if (!canDelete.get() || selectedDosen.get() == null) {
            return;
        }

        setLoading(true);
        clearError();

        try {
            dosenDAO.delete(selectedDosen.get().getNpp());

            // Reload data and clear form
            loadAllDosen();
            clearForm();
            setSuccess("Data dosen berhasil dihapus!");

        } catch (Exception e) {
            setError("Error menghapus data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    // === PRIVATE HELPER METHODS ===

    /**
     * Load semua data dosen dari database
     */
    public void loadAllDosen() {
        setLoading(true);

        try {
            dosenList.clear();
            dosenList.addAll(dosenDAO.findAll());
        } catch (Exception e) {
            setError("Error loading data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Clear form fields dan reset state
     */
    public void clearForm() {
        npp.set("");
        nama.set("");
        noHp.set("");
        selectedDosen.set(null);
        isEditing.set(false);
    }

    /**
     * Validate form input
     */
    private boolean validateInput() {
        if (npp.get().trim().isEmpty()) {
            setError("NPP tidak boleh kosong");
            return false;
        }

        if (nama.get().trim().isEmpty()) {
            setError("Nama tidak boleh kosong");
            return false;
        }

        if (npp.get().trim().length() < 3) {
            setError("NPP minimal 3 karakter");
            return false;
        }

        return true;
    }

    /**
     * Set loading state
     */
    private void setLoading(boolean loading) {
        isLoading.set(loading);
        if (loading) {
            statusMessage.set("Loading...");
            hasError.set(false);
        }
    }

    /**
     * Set error message
     */
    private void setError(String message) {
        statusMessage.set(message);
        hasError.set(true);
        isLoading.set(false);
    }

    /**
     * Set success message
     */
    private void setSuccess(String message) {
        statusMessage.set(message);
        hasError.set(false);
        isLoading.set(false);
    }

    /**
     * Clear error/success message
     */
    private void clearError() {
        hasError.set(false);
        if (!isLoading.get()) {
            statusMessage.set("");
        }
    }

    public ObservableList<Dosen> getDosenList() {
        return dosenList;
    }

    public StringProperty nppProperty() {
        return npp;
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public StringProperty noHpProperty() {
        return noHp;
    }

    public ObjectProperty<Dosen> selectedDosenProperty() {
        return selectedDosen;
    }

    public BooleanProperty isEditingProperty() {
        return isEditing;
    }

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public BooleanProperty hasErrorProperty() {
        return hasError;
    }

    public BooleanProperty canSaveProperty() {
        return canSave;
    }

    public BooleanProperty canUpdateProperty() {
        return canUpdate;
    }

    public BooleanProperty canDeleteProperty() {
        return canDelete;
    }

    public String getNpp() {
        return npp.get();
    }

    public String getNama() {
        return nama.get();
    }

    public String getNoHp() {
        return noHp.get();
    }

    public Dosen getSelectedDosen() {
        return selectedDosen.get();
    }

    public boolean isEditing() {
        return isEditing.get();
    }

    public boolean isLoading() {
        return isLoading.get();
    }

    public String getStatusMessage() {
        return statusMessage.get();
    }

    public boolean hasError() {
        return hasError.get();
    }

    public boolean canSave() {
        return canSave.get();
    }

    public boolean canUpdate() {
        return canUpdate.get();
    }

    public boolean canDelete() {
        return canDelete.get();
    }

    /**
     * Select dosen untuk editing
     */
    public void selectDosen(Dosen dosen) {
        selectedDosen.set(dosen);

        if (dosen != null) {
            // Populate form dengan data dosen yang dipilih
            npp.set(dosen.getNpp());
            nama.set(dosen.getNama());
            noHp.set(dosen.getNoHp() != null ? dosen.getNoHp() : "");

            // Set editing mode
            isEditing.set(true);
        } else {
            clearForm();
        }

        clearError();
    }
}
