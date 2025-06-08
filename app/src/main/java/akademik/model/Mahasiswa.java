package akademik.model;

/**
 * Model class untuk entitas Mahasiswa
 * Merepresentasikan data mahasiswa dalam sistem akademik
 */
public class Mahasiswa {
    private String nim;         // Nomor Induk Mahasiswa
    private String nama;        // Nama lengkap mahasiswa
    private String gender;      // Jenis kelamin
    private double ipk;         // Indeks Prestasi Kumulatif
    private String dosenWali;   // NPP dosen wali

    // Konstanta untuk gender
    public static final String GENDER_LAKI = "Laki-laki";
    public static final String GENDER_PEREMPUAN = "Perempuan";

    // Default constructor
    public Mahasiswa() {
    }

    // Constructor lengkap
    public Mahasiswa(String nim, String nama, String gender, double ipk, String dosenWali) {
        this.nim = nim;
        this.nama = nama;
        this.gender = gender;
        this.ipk = ipk;
        this.dosenWali = dosenWali;
    }

    // Constructor tanpa dosen wali (karena optional)
    public Mahasiswa(String nim, String nama, String gender, double ipk) {
        this(nim, nama, gender, ipk, null);
    }

    // === GETTERS ===
    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    public String getGender() {
        return gender;
    }

    public double getIpk() {
        return ipk;
    }

    public String getDosenWali() {
        return dosenWali;
    }

    // === SETTERS ===
    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIpk(double ipk) {
        this.ipk = ipk;
    }

    public void setDosenWali(String dosenWali) {
        this.dosenWali = dosenWali;
    }

    // === BUSINESS METHODS ===

    /**
     * Validasi data mahasiswa
     * @return true jika data valid
     */
    public boolean isValid() {
        return nim != null && !nim.trim().isEmpty() &&
               nama != null && !nama.trim().isEmpty() &&
               isValidGender() && isValidIpk();
    }

    /**
     * Validasi gender
     * @return true jika gender valid
     */
    public boolean isValidGender() {
        return GENDER_LAKI.equals(gender) || GENDER_PEREMPUAN.equals(gender);
    }

    /**
     * Validasi IPK
     * @return true jika IPK dalam rentang 0.0 - 4.0
     */
    public boolean isValidIpk() {
        return ipk >= 0.0 && ipk <= 4.0;
    }

    /**
     * Mendapatkan kategori IPK
     * @return kategori prestasi berdasarkan IPK
     */
    public String getPrestasiKategori() {
        if (ipk >= 3.50) return "Cum Laude";
        if (ipk >= 3.00) return "Sangat Memuaskan";
        if (ipk >= 2.50) return "Memuaskan";
        if (ipk >= 2.00) return "Cukup";
        return "Kurang";
    }

    // === OBJECT METHODS ===

    @Override
    public String toString() {
        return String.format("[%s] %s", nim, nama);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Mahasiswa mahasiswa = (Mahasiswa) obj;
        return nim != null ? nim.equals(mahasiswa.nim) : mahasiswa.nim == null;
    }

    @Override
    public int hashCode() {
        return nim != null ? nim.hashCode() : 0;
    }
}
