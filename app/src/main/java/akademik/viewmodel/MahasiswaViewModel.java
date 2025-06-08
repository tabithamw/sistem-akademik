package akademik.viewmodel;

import akademik.dao.MahasiswaDAO;
import akademik.dao.MahasiswaDAOImpl;
import akademik.dao.DosenDAO;
import akademik.dao.DosenDAOImpl;
import akademik.model.Mahasiswa;
import akademik.model.Dosen;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel untuk mengelola data Mahasiswa
 * Implementasi yang lebih kompleks dengan relasi ke Dosen
 */
public class MahasiswaViewModel {

    // DAO instances
    private final MahasiswaDAO mahasiswaDAO;
    private final DosenDAO dosenDAO;

    // Observable lists
    private final ObservableList<Mahasiswa> mahasiswaList;
    private final ObservableList<Dosen> dosenList;

    // Form properties
    private final StringProperty nim = new SimpleStringProperty("");
    private final StringProperty nama = new SimpleStringProperty("");
    private final StringProperty gender = new SimpleStringProperty("");
    private final DoubleProperty ipk = new SimpleDoubleProperty(0.0);
    private final StringProperty dosenWali = new SimpleStringProperty("");

    // UI state properties
    private final ObjectProperty<Mahasiswa> selectedMahasiswa = new SimpleObjectProperty<>();
    private final BooleanProperty isEditing = new SimpleBooleanProperty(false);
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty hasError = new SimpleBooleanProperty(false);

    // Computed properties
    private final BooleanProperty canSave = new SimpleBooleanProperty();
    private final BooleanProperty canUpdate = new SimpleBooleanProperty();
    private final BooleanProperty canDelete = new SimpleBooleanProperty();
    private final StringProperty ipkText = new SimpleStringProperty("");

    public MahasiswaViewModel() {
        this.mahasiswaDAO = new MahasiswaDAOImpl();
        this.dosenDAO = new DosenDAOImpl();
        this.mahasiswaList = FXCollections.observableArrayList();
        this.dosenList = FXCollections.observableArrayList();

        setupComputedProperties();
        setupPropertyListeners();
        loadAllData();
    }

    /**
     * Setup computed properties
     */
    private void setupComputedProperties() {
        // canSave: bisa save jika tidak editing dan form valid
        canSave.bind(
            isEditing.not()
                .and(nim.isNotEmpty())
                .and(nama.isNotEmpty())
                .and(gender.isNotEmpty())
                .and(ipk.greaterThan(0.0))
                .and(ipk.lessThanOrEqualTo(4.0))
                .and(isLoading.not())
        );

        // canUpdate: bisa update jika editing dan form valid
        canUpdate.bind(
            isEditing
                .and(selectedMahasiswa.isNotNull())
                .and(nama.isNotEmpty())
                .and(gender.isNotEmpty())
                .and(ipk.greaterThan(0.0))
                .and(ipk.lessThanOrEqualTo(4.0))
                .and(isLoading.not())
        );

        // canDelete: bisa delete jika ada yang dipilih
        canDelete.bind(
            selectedMahasiswa.isNotNull()
                .and(isLoading.not())
        );

        // Bind IPK text representation
        ipkText.bind(ipk.asString("%.2f"));
    }

    /**
     * Setup property listeners
     */
    private void setupPropertyListeners() {
        // Clear error when user types
        nim.addListener((obs, oldVal, newVal) -> clearError());
        nama.addListener((obs, oldVal, newVal) -> clearError());
        gender.addListener((obs, oldVal, newVal) -> clearError());
        ipk.addListener((obs, oldVal, newVal) -> clearError());

        // Validate NIM format
        nim.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty() && newVal.length() < 6) {
                setError("NIM minimal 6 karakter");
            }
        });

        // Validate IPK range
        ipk.addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 0.0 || newVal.doubleValue() > 4.0) {
                setError("IPK harus antara 0.0 - 4.0");
            }
        });
    }

    // === COMMAND METHODS ===

    /**
     * Command untuk menyimpan mahasiswa baru
     */
    public void saveCommand() {
        if (!canSave.get()) {
            return;
        }

        setLoading(true);
        clearError();

        try {
            if (!validateInput()) {
                return;
            }

            // Check duplicate NIM
            if (mahasiswaDAO.existsByNim(nim.get())) {
                setError("NIM " + nim.get() + " sudah ada dalam database");
                return;
            }

            // Create new mahasiswa
            Mahasiswa newMahasiswa = new Mahasiswa(
                nim.get().trim(),
                nama.get().trim(),
                gender.get(),
                ipk.get(),
                dosenWali.get().isEmpty() ? null : dosenWali.get()
            );

            mahasiswaDAO.save(newMahasiswa);

            loadAllMahasiswa();
            clearForm();
            setSuccess("Data mahasiswa berhasil disimpan!");

        } catch (Exception e) {
            setError("Error menyimpan data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Command untuk mengupdate mahasiswa
     */
    public void updateCommand() {
        if (!canUpdate.get() || selectedMahasiswa.get() == null) {
            return;
        }

        setLoading(true);
        clearError();

        try {
            if (!validateInput()) {
                return;
            }

            Mahasiswa mahasiswaToUpdate = selectedMahasiswa.get();
            mahasiswaToUpdate.setNama(nama.get().trim());
            mahasiswaToUpdate.setGender(gender.get());
            mahasiswaToUpdate.setIpk(ipk.get());
            mahasiswaToUpdate.setDosenWali(dosenWali.get().isEmpty() ? null : dosenWali.get());

            mahasiswaDAO.update(mahasiswaToUpdate);

            loadAllMahasiswa();
            clearForm();
            setSuccess("Data mahasiswa berhasil diupdate!");

        } catch (Exception e) {
            setError("Error mengupdate data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Command untuk menghapus mahasiswa
     */
    public void deleteCommand() {
        if (!canDelete.get() || selectedMahasiswa.get() == null) {
            return;
        }

        setLoading(true);
        clearError();

        try {
            mahasiswaDAO.delete(selectedMahasiswa.get().getNim());

            loadAllMahasiswa();
            clearForm();
            setSuccess("Data mahasiswa berhasil dihapus!");

        } catch (Exception e) {
            setError("Error menghapus data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    /**
     * Command untuk clear form
     */
    public void clearCommand() {
        clearForm();
        clearError();
    }

    /**
     * Command untuk refresh data
     */
    public void refreshCommand() {
        loadAllData();
        setSuccess("Data berhasil di-refresh!");
    }

    /**
     * Select mahasiswa untuk editing
     */
    public void selectMahasiswa(Mahasiswa mahasiswa) {
        selectedMahasiswa.set(mahasiswa);

        if (mahasiswa != null) {
            nim.set(mahasiswa.getNim());
            nama.set(mahasiswa.getNama());
            gender.set(mahasiswa.getGender());
            ipk.set(mahasiswa.getIpk());
            dosenWali.set(mahasiswa.getDosenWali() != null ? mahasiswa.getDosenWali() : "");

            isEditing.set(true);
        } else {
            clearForm();
        }

        clearError();
    }

    /**
     * Set IPK from string (untuk TextField binding)
     */
    public void setIpkFromString(String ipkString) {
        try {
            if (ipkString == null || ipkString.trim().isEmpty()) {
                ipk.set(0.0);
            } else {
                double value = Double.parseDouble(ipkString.trim());
                ipk.set(value);
            }
        } catch (NumberFormatException e) {
            setError("IPK harus berupa angka yang valid");
        }
    }

    // === PRIVATE HELPER METHODS ===

    private void loadAllData() {
        loadAllMahasiswa();
        loadAllDosen();
    }

    private void loadAllMahasiswa() {
        setLoading(true);

        try {
            mahasiswaList.clear();
            mahasiswaList.addAll(mahasiswaDAO.findAll());
        } catch (Exception e) {
            setError("Error loading mahasiswa data: " + e.getMessage());
        } finally {
            setLoading(false);
        }
    }

    private void loadAllDosen() {
        try {
            dosenList.clear();
            dosenList.addAll(dosenDAO.findAll());
        } catch (Exception e) {
            setError("Error loading dosen data: " + e.getMessage());
        }
    }

    private void clearForm() {
        nim.set("");
        nama.set("");
        gender.set("");
        ipk.set(0.0);
        dosenWali.set("");
        selectedMahasiswa.set(null);
        isEditing.set(false);
    }

    private boolean validateInput() {
        if (nim.get().trim().isEmpty()) {
            setError("NIM tidak boleh kosong");
            return false;
        }

        if (nama.get().trim().isEmpty()) {
            setError("Nama tidak boleh kosong");
            return false;
        }

        if (gender.get().trim().isEmpty()) {
            setError("Gender harus dipilih");
            return false;
        }

        if (ipk.get() <= 0.0 || ipk.get() > 4.0) {
            setError("IPK harus antara 0.0 - 4.0");
            return false;
        }

        if (nim.get().trim().length() < 6) {
            setError("NIM minimal 6 karakter");
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        isLoading.set(loading);
        if (loading) {
            statusMessage.set("Loading...");
            hasError.set(false);
        }
    }

    private void setError(String message) {
        statusMessage.set(message);
        hasError.set(true);
        isLoading.set(false);
    }

    private void setSuccess(String message) {
        statusMessage.set(message);
        hasError.set(false);
        isLoading.set(false);
    }

    private void clearError() {
        hasError.set(false);
        if (!isLoading.get()) {
            statusMessage.set("");
        }
    }

    // === PROPERTY GETTERS FOR BINDING ===

    public ObservableList<Mahasiswa> getMahasiswaList() {
        return mahasiswaList;
    }

    public ObservableList<Dosen> getDosenList() {
        return dosenList;
    }

    public StringProperty nimProperty() {
        return nim;
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public DoubleProperty ipkProperty() {
        return ipk;
    }

    public StringProperty dosenWaliProperty() {
        return dosenWali;
    }

    public StringProperty ipkTextProperty() {
        return ipkText;
    }

    public ObjectProperty<Mahasiswa> selectedMahasiswaProperty() {
        return selectedMahasiswa;
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

    // === CONVENIENCE GETTERS ===

    public String getNim() {
        return nim.get();
    }

    public String getNama() {
        return nama.get();
    }

    public String getGender() {
        return gender.get();
    }

    public double getIpk() {
        return ipk.get();
    }

    public String getDosenWali() {
        return dosenWali.get();
    }

    public String getIpkText() {
        return ipkText.get();
    }

    public Mahasiswa getSelectedMahasiswa() {
        return selectedMahasiswa.get();
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
}
