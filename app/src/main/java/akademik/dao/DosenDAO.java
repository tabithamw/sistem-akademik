package akademik.dao;

import java.util.List;

import akademik.model.Dosen;

/**
 * DAO interface untuk entitas Dosen
 * Mendefinisikan kontrak operasi CRUD untuk data dosen
 */
public interface DosenDAO {

    /**
     * Simpan data dosen baru
     * @param dosen object dosen yang akan disimpan
     * @throws RuntimeException jika terjadi error atau NPP sudah ada
     */
    void save(Dosen dosen);

    /**
     * Update data dosen yang sudah ada
     * @param dosen object dosen dengan data yang sudah diupdate
     * @throws RuntimeException jika terjadi error atau dosen tidak ditemukan
     */
    void update(Dosen dosen);

    /**
     * Hapus data dosen berdasarkan NPP
     * @param npp NPP dosen yang akan dihapus
     * @throws RuntimeException jika terjadi error
     */
    void delete(String npp);

    /**
     * Cari dosen berdasarkan NPP
     * @param npp NPP dosen yang dicari
     * @return object Dosen jika ditemukan, null jika tidak ditemukan
     */
    Dosen findByNpp(String npp);

    /**
     * Ambil semua data dosen
     * @return List berisi semua data dosen, list kosong jika tidak ada data
     */
    List<Dosen> findAll();

    /**
     * Cari dosen berdasarkan nama (partial match)
     * @param nama nama atau sebagian nama dosen
     * @return List dosen yang namanya mengandung keyword
     */
    List<Dosen> findByNama(String nama);

    /**
     * Cek apakah NPP sudah ada di database
     * @param npp NPP yang akan dicek
     * @return true jika NPP sudah ada
     */
    boolean existsByNpp(String npp);

    /**
     * Hitung total jumlah dosen
     * @return jumlah total dosen
     */
    int count();
}
