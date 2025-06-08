package akademik.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import akademik.database.DatabaseConnection;
import akademik.model.Dosen;

/**
 * Implementasi DAO untuk entitas Dosen
 * Menggunakan SQLite untuk persistensi data
 */
public class DosenDAOImpl implements DosenDAO {

    private final Connection connection;

    public DosenDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Dosen dosen) {
        // Validasi input
        if (dosen == null || !dosen.isValid()) {
            throw new IllegalArgumentException("Data dosen tidak valid");
        }

        // Cek apakah NPP sudah ada
        if (existsByNpp(dosen.getNpp())) {
            throw new RuntimeException("NPP " + dosen.getNpp() + " sudah ada dalam database");
        }

        String sql = "INSERT INTO dosen (npp, nama, no_hp) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dosen.getNpp());
            stmt.setString(2, dosen.getNama());
            stmt.setString(3, dosen.getNoHp());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Gagal menyimpan data dosen");
            }

            System.out.println("✅ Dosen " + dosen.getNama() + " berhasil disimpan");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving dosen: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Dosen dosen) {
        // Validasi input
        if (dosen == null || !dosen.isValid()) {
            throw new IllegalArgumentException("Data dosen tidak valid");
        }

        String sql = "UPDATE dosen SET nama = ?, no_hp = ? WHERE npp = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dosen.getNama());
            stmt.setString(2, dosen.getNoHp());
            stmt.setString(3, dosen.getNpp());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Data dosen dengan NPP " + dosen.getNpp() + " tidak ditemukan");
            }

            System.out.println("✅ Data dosen " + dosen.getNama() + " berhasil diupdate");

        } catch (SQLException e) {
            throw new RuntimeException("Error updating dosen: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String npp) {
        if (npp == null || npp.trim().isEmpty()) {
            throw new IllegalArgumentException("NPP tidak boleh kosong");
        }

        String sql = "DELETE FROM dosen WHERE npp = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, npp);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Data dosen dengan NPP " + npp + " tidak ditemukan");
            }

            System.out.println("✅ Dosen dengan NPP " + npp + " berhasil dihapus");

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting dosen: " + e.getMessage(), e);
        }
    }

    @Override
    public Dosen findByNpp(String npp) {
        if (npp == null || npp.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM dosen WHERE npp = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, npp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDosen(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding dosen by NPP: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Dosen> findAll() {
        List<Dosen> dosenList = new ArrayList<>();
        String sql = "SELECT * FROM dosen ORDER BY nama";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dosenList.add(mapResultSetToDosen(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all dosen: " + e.getMessage(), e);
        }

        return dosenList;
    }

    @Override
    public List<Dosen> findByNama(String nama) {
        List<Dosen> dosenList = new ArrayList<>();

        if (nama == null || nama.trim().isEmpty()) {
            return dosenList;
        }

        String sql = "SELECT * FROM dosen WHERE nama LIKE ? ORDER BY nama";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nama + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dosenList.add(mapResultSetToDosen(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding dosen by nama: " + e.getMessage(), e);
        }

        return dosenList;
    }

    @Override
    public boolean existsByNpp(String npp) {
        if (npp == null || npp.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM dosen WHERE npp = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, npp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking dosen existence: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM dosen";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error counting dosen: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Mapping ResultSet ke object Dosen
     * @param rs ResultSet dari query database
     * @return object Dosen
     * @throws SQLException jika terjadi error saat mapping
     */
    private Dosen mapResultSetToDosen(ResultSet rs) throws SQLException {
        return new Dosen(
            rs.getString("npp"),
            rs.getString("nama"),
            rs.getString("no_hp")
        );
    }
}
