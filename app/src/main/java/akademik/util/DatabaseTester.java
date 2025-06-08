package akademik.util;

import java.util.List;

import akademik.dao.DosenDAO;
import akademik.dao.DosenDAOImpl;
import akademik.dao.MahasiswaDAO;
import akademik.dao.MahasiswaDAOImpl;
import akademik.database.DatabaseConnection;
import akademik.model.Dosen;
import akademik.model.Mahasiswa;

/**
 * Utility class untuk testing database operations
 * Untuk memverifikasi bahwa database dan DAO berfungsi dengan benar
 */
public class DatabaseTester {

    private final DosenDAO dosenDAO;
    private final MahasiswaDAO mahasiswaDAO;

    public DatabaseTester() {
        this.dosenDAO = new DosenDAOImpl();
        this.mahasiswaDAO = new MahasiswaDAOImpl();
    }

    /**
     * Run semua test database operations
     */
    public void runAllTests() {
        System.out.println("🧪 Starting Database Tests...");

        try {
            // Test database connection
            testDatabaseConnection();

            // Test Dosen operations
            testDosenOperations();

            // Test Mahasiswa operations
            testMahasiswaOperations();

            System.out.println("✅ All database tests completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testDatabaseConnection() {
        System.out.println("1. Testing Database Connection...");

        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (dbConn.testConnection()) {
            System.out.println("   ✅ Database connection is working");
        } else {
            throw new RuntimeException("Database connection failed");
        }

        System.out.println("   📄 Database file: " + dbConn.getDatabaseName());
        System.out.println();
    }

    private void testDosenOperations() {
        System.out.println("2. Testing Dosen CRUD Operations...");

        // Create test data
        Dosen testDosen = new Dosen("TEST001", "Dr. Test Dosen", "081234567890");

        // Test Save
        System.out.println("   📝 Testing save operation...");
        dosenDAO.save(testDosen);
        System.out.println("   ✅ Dosen saved successfully");

        // Test Find by NPP
        System.out.println("   🔍 Testing find by NPP...");
        Dosen foundDosen = dosenDAO.findByNpp("TEST001");
        if (foundDosen != null && foundDosen.getNama().equals("Dr. Test Dosen")) {
            System.out.println("   ✅ Find by NPP successful");
        } else {
            throw new RuntimeException("Find by NPP failed");
        }

        // Test Update
        System.out.println("   📝 Testing update operation...");
        foundDosen.setNama("Dr. Test Dosen Updated");
        foundDosen.setNoHp("087654321098");
        dosenDAO.update(foundDosen);
        System.out.println("   ✅ Dosen updated successfully");

        // Test Find All
        System.out.println("   📋 Testing find all...");
        List<Dosen> allDosen = dosenDAO.findAll();
        System.out.println("   ✅ Found " + allDosen.size() + " dosen(s)");

        // Test Count
        int dosenCount = dosenDAO.count();
        System.out.println("   📊 Total dosen: " + dosenCount);

        // Test Delete
        System.out.println("   🗑️ Testing delete operation...");
        dosenDAO.delete("TEST001");
        System.out.println("   ✅ Dosen deleted successfully");

        System.out.println();
    }

    private void testMahasiswaOperations() {
        System.out.println("3. Testing Mahasiswa CRUD Operations...");

        // Create test dosen first (for foreign key)
        Dosen testDosen = new Dosen("TEST002", "Dr. Dosen Wali", "081111111111");
        dosenDAO.save(testDosen);

        // Create test mahasiswa
        Mahasiswa testMahasiswa = new Mahasiswa("TEST123", "Test Mahasiswa",
                                               Mahasiswa.GENDER_LAKI, 3.75, "TEST002");

        // Test Save
        System.out.println("   📝 Testing save operation...");
        mahasiswaDAO.save(testMahasiswa);
        System.out.println("   ✅ Mahasiswa saved successfully");

        // Test Find by NIM
        System.out.println("   🔍 Testing find by NIM...");
        Mahasiswa foundMahasiswa = mahasiswaDAO.findByNim("TEST123");
        if (foundMahasiswa != null && foundMahasiswa.getNama().equals("Test Mahasiswa")) {
            System.out.println("   ✅ Find by NIM successful");
        } else {
            throw new RuntimeException("Find by NIM failed");
        }

        // Test Update
        System.out.println("   📝 Testing update operation...");
        foundMahasiswa.setNama("Test Mahasiswa Updated");
        foundMahasiswa.setIpk(3.85);
        mahasiswaDAO.update(foundMahasiswa);
        System.out.println("   ✅ Mahasiswa updated successfully");

        // Test Find by Dosen Wali
        System.out.println("   👨‍🏫 Testing find by dosen wali...");
        List<Mahasiswa> mahasiswaByWali = mahasiswaDAO.findByDosenWali("TEST002");
        System.out.println("   ✅ Found " + mahasiswaByWali.size() + " mahasiswa(s) with dosen wali TEST002");

        // Test Find All
        System.out.println("   📋 Testing find all...");
        List<Mahasiswa> allMahasiswa = mahasiswaDAO.findAll();
        System.out.println("   ✅ Found " + allMahasiswa.size() + " mahasiswa(s)");

        // Test Count
        int mahasiswaCount = mahasiswaDAO.count();
        System.out.println("   📊 Total mahasiswa: " + mahasiswaCount);

        // Cleanup - Delete test data
        System.out.println("   🧹 Cleaning up test data...");
        mahasiswaDAO.delete("TEST123");
        dosenDAO.delete("TEST002");
        System.out.println("   ✅ Test data cleaned up");

        System.out.println();
    }

    /**
     * Insert sample data untuk testing UI nanti
     */
    public void insertSampleData() {
        System.out.println("📝 Inserting sample data...");

        try {
            // Sample dosen
            if (dosenDAO.count() == 0) {
                dosenDAO.save(new Dosen("NPP001", "Dr. Ahmad Fauzi, M.Kom", "08123456789"));
                dosenDAO.save(new Dosen("NPP002", "Dr. Siti Rahayu, M.T", "08234567890"));
                dosenDAO.save(new Dosen("NPP003", "Prof. Budi Santoso, Ph.D", "08345678901"));
                System.out.println("✅ Sample dosen data inserted");
            }

            // Sample mahasiswa
            if (mahasiswaDAO.count() == 0) {
                mahasiswaDAO.save(new Mahasiswa("123210001", "Andi Prasetyo",
                                               Mahasiswa.GENDER_LAKI, 3.75, "NPP001"));
                mahasiswaDAO.save(new Mahasiswa("123210002", "Dewi Sartika",
                                               Mahasiswa.GENDER_PEREMPUAN, 3.85, "NPP001"));
                mahasiswaDAO.save(new Mahasiswa("123210003", "Rizki Ramadhan",
                                               Mahasiswa.GENDER_LAKI, 3.20, "NPP002"));
                mahasiswaDAO.save(new Mahasiswa("123210004", "Maya Indira",
                                               Mahasiswa.GENDER_PEREMPUAN, 3.95, "NPP002"));
                System.out.println("✅ Sample mahasiswa data inserted");
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error inserting sample data: " + e.getMessage());
        }
    }
}
