package akademik.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import akademik.database.DatabaseConnection;
import akademik.model.Mahasiswa;

/**
 * Implementasi DAO untuk entitas Mahasiswa
 * Menggunakan SQLite untuk persistensi data
 */
public class MahasiswaDAOImpl implements MahasiswaDAO {

    private final Connection connection;

    public MahasiswaDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Mahasiswa mahasiswa) {
        // Validasi input
        if (mahasiswa == null || !mahasiswa.isValid()) {
            throw new IllegalArgumentException("Data mahasiswa tidak valid");
        }

        // Cek apakah NIM sudah ada
        if (existsByNim(mahasiswa.getNim())) {
            throw new RuntimeException("NIM " + mahasiswa.getNim() + " sudah ada dalam database");
        }

        String sql = "INSERT INTO mahasiswa (nim, nama, gender, ipk, dosen_wali) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mahasiswa.getNim());
            stmt.setString(2, mahasiswa.getNama());
            stmt.setString(3, mahasiswa.getGender());
            stmt.setDouble(4, mahasiswa.getIpk());
            stmt.setString(5, mahasiswa.getDosenWali());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Gagal menyimpan data mahasiswa");
            }

            System.out.println("✅ Mahasiswa " + mahasiswa.getNama() + " berhasil disimpan");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving mahasiswa: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Mahasiswa mahasiswa) {
        // Validasi input
        if (mahasiswa == null || !mahasiswa.isValid()) {
            throw new IllegalArgumentException("Data mahasiswa tidak valid");
        }

        String sql = "UPDATE mahasiswa SET nama = ?, gender = ?, ipk = ?, dosen_wali = ? WHERE nim = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mahasiswa.getNama());
            stmt.setString(2, mahasiswa.getGender());
            stmt.setDouble(3, mahasiswa.getIpk());
            stmt.setString(4, mahasiswa.getDosenWali());
            stmt.setString(5, mahasiswa.getNim());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Data mahasiswa dengan NIM " + mahasiswa.getNim() + " tidak ditemukan");
            }

            System.out.println("✅ Data mahasiswa " + mahasiswa.getNama() + " berhasil diupdate");

        } catch (SQLException e) {
            throw new RuntimeException("Error updating mahasiswa: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String nim) {
        if (nim == null || nim.trim().isEmpty()) {
            throw new IllegalArgumentException("NIM tidak boleh kosong");
        }

        String sql = "DELETE FROM mahasiswa WHERE nim = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nim);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Data mahasiswa dengan NIM " + nim + " tidak ditemukan");
            }

            System.out.println("✅ Mahasiswa dengan NIM " + nim + " berhasil dihapus");

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting mahasiswa: " + e.getMessage(), e);
        }
    }

    @Override
    public Mahasiswa findByNim(String nim) {
        if (nim == null || nim.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM mahasiswa WHERE nim = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nim);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMahasiswa(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding mahasiswa by NIM: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Mahasiswa> findAll() {
        List<Mahasiswa> mahasiswaList = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY nama";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                mahasiswaList.add(mapResultSetToMahasiswa(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all mahasiswa: " + e.getMessage(), e);
        }

        return mahasiswaList;
    }

    @Override
    public List<Mahasiswa> findByDosenWali(String nppDosenWali) {
        List<Mahasiswa> mahasiswaList = new ArrayList<>();

        if (nppDosenWali == null || nppDosenWali.trim().isEmpty()) {
            return mahasiswaList;
        }

        String sql = "SELECT * FROM mahasiswa WHERE dosen_wali = ? ORDER BY nama";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nppDosenWali);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mahasiswaList.add(mapResultSetToMahasiswa(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding mahasiswa by dosen wali: " + e.getMessage(), e);
        }

        return mahasiswaList;
    }

    @Override
    public List<Mahasiswa> findByGender(String gender) {
        List<Mahasiswa> mahasiswaList = new ArrayList<>();

        if (gender == null || gender.trim().isEmpty()) {
            return mahasiswaList;
        }

        String sql = "SELECT * FROM mahasiswa WHERE gender = ? ORDER BY nama";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, gender);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mahasiswaList.add(mapResultSetToMahasiswa(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding mahasiswa by gender: " + e.getMessage(), e);
        }

        return mahasiswaList;
    }

    @Override
    public List<Mahasiswa> findByNama(String nama) {
        List<Mahasiswa> mahasiswaList = new ArrayList<>();

        if (nama == null || nama.trim().isEmpty()) {
            return mahasiswaList;
        }

        String sql = "SELECT * FROM mahasiswa WHERE nama LIKE ? ORDER BY nama";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nama + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mahasiswaList.add(mapResultSetToMahasiswa(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding mahasiswa by nama: " + e.getMessage(), e);
        }

        return mahasiswaList;
    }

    @Override
    public boolean existsByNim(String nim) {
        if (nim == null || nim.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nim);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking mahasiswa existence: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM mahasiswa";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error counting mahasiswa: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Mapping ResultSet ke object Mahasiswa
     * @param rs ResultSet dari query database
     * @return object Mahasiswa
     * @throws SQLException jika terjadi error saat mapping
     */
    private Mahasiswa mapResultSetToMahasiswa(ResultSet rs) throws SQLException {
        return new Mahasiswa(
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("gender"),
            rs.getDouble("ipk"),
            rs.getString("dosen_wali")
        );
    }
}
