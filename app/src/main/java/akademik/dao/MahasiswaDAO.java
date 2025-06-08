package akademik.dao;

import java.util.List;

import akademik.model.Mahasiswa;

/**
 * DAO interface untuk entitas Mahasiswa
 * Mendefinisikan kontrak operasi CRUD untuk data mahasiswa
 */
public interface MahasiswaDAO {

    /**
     * Simpan data mahasiswa baru
     * @param mahasiswa object mahasiswa yang akan disimpan
     * @throws RuntimeException jika terjadi error atau NIM sudah ada
     */
    void save(Mahasiswa mahasiswa);

    /**
     * Update data mahasiswa yang sudah ada
     * @param mahasiswa object mahasiswa dengan data yang sudah diupdate
     * @throws RuntimeException jika terjadi error atau mahasiswa tidak ditemukan
     */
    void update(Mahasiswa mahasiswa);

    /**
     * Hapus data mahasiswa berdasarkan NIM
     * @param nim NIM mahasiswa yang akan dihapus
     * @throws RuntimeException jika terjadi error
     */
    void delete(String nim);

    /**
     * Cari mahasiswa berdasarkan NIM
     * @param nim NIM mahasiswa yang dicari
     * @return object Mahasiswa jika ditemukan, null jika tidak ditemukan
     */
    Mahasiswa findByNim(String nim);

    /**
     * Ambil semua data mahasiswa
     * @return List berisi semua data mahasiswa, list kosong jika tidak ada data
     */
    List<Mahasiswa> findAll();

    /**
     * Cari mahasiswa berdasarkan dosen wali
     * @param nppDosenWali NPP dosen wali
     * @return List mahasiswa yang memiliki dosen wali tersebut
     */
    List<Mahasiswa> findByDosenWali(String nppDosenWali);

    /**
     * Cari mahasiswa berdasarkan gender
     * @param gender jenis kelamin
     * @return List mahasiswa dengan gender yang diminta
     */
    List<Mahasiswa> findByGender(String gender);

    /**
     * Cari mahasiswa berdasarkan nama (partial match)
     * @param nama nama atau sebagian nama mahasiswa
     * @return List mahasiswa yang namanya mengandung keyword
     */
    List<Mahasiswa> findByNama(String nama);

    /**
     * Cek apakah NIM sudah ada di database
     * @param nim NIM yang akan dicek
     * @return true jika NIM sudah ada
     */
    boolean existsByNim(String nim);

    /**
     * Hitung total jumlah mahasiswa
     * @return jumlah total mahasiswa
     */
    int count();
}
