package akademik.model;

/**
 * Model class untuk entitas Dosen
 * Merepresentasikan data dosen dalam sistem akademik
 */
public class Dosen {
    private String npp;        // Nomor Pokok Pegawai
    private String nama;       // Nama lengkap dosen
    private String noHp;       // Nomor HP (optional)

    // Default constructor - diperlukan untuk beberapa framework
    public Dosen() {
    }

    // Constructor dengan semua parameter
    public Dosen(String npp, String nama, String noHp) {
        this.npp = npp;
        this.nama = nama;
        this.noHp = noHp;
    }

    // Constructor tanpa noHp (karena optional)
    public Dosen(String npp, String nama) {
        this(npp, nama, null);
    }

    // === GETTERS ===
    public String getNpp() {
        return npp;
    }

    public String getNama() {
        return nama;
    }

    public String getNoHp() {
        return noHp;
    }

    // === SETTERS ===
    public void setNpp(String npp) {
        this.npp = npp;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    // === BUSINESS METHODS ===

    /**
     * Validasi data dosen
     * @return true jika data valid
     */
    public boolean isValid() {
        return npp != null && !npp.trim().isEmpty() &&
               nama != null && !nama.trim().isEmpty();
    }

    // === OBJECT METHODS ===

    @Override
    public String toString() {
        return "[" + npp + "] " + nama ;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Dosen dosen = (Dosen) obj;
        return npp != null ? npp.equals(dosen.npp) : dosen.npp == null;
    }

    @Override
    public int hashCode() {
        return npp != null ? npp.hashCode() : 0;
    }
}