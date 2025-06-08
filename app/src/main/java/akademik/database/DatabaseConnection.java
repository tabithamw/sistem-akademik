package akademik.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection utility class menggunakan Singleton pattern
 * Mengelola koneksi ke database SQLite dan inisialisasi tabel
 */
public class DatabaseConnection {

    // Database configuration
    private static final String DB_NAME = "akademik.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor untuk Singleton pattern
    private DatabaseConnection() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Create connection
            connection = DriverManager.getConnection(DB_URL);

            // Enable foreign key support
            enableForeignKeys();

            // Initialize tables
            initializeTables();

            System.out.println("✅ Database connection established successfully");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }

    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Get database connection
     * @return Connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Enable foreign key constraints in SQLite
     */
    private void enableForeignKeys() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    /**
     * Initialize database tables
     */
    private void initializeTables() {
        createDosenTable();
        createMahasiswaTable();
    }

    /**
     * Create dosen table
     */
    private void createDosenTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS dosen (
                npp TEXT PRIMARY KEY,
                nama TEXT NOT NULL,
                no_hp TEXT
            )
        """;

        executeSQL(sql, "dosen table");
    }

    /**
     * Create mahasiswa table with foreign key to dosen
     */
    private void createMahasiswaTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS mahasiswa (
                nim TEXT PRIMARY KEY,
                nama TEXT NOT NULL,
                gender TEXT NOT NULL CHECK (gender IN ('Laki-laki', 'Perempuan')),
                ipk REAL NOT NULL CHECK (ipk >= 0.0 AND ipk <= 4.0),
                dosen_wali TEXT,
                FOREIGN KEY (dosen_wali) REFERENCES dosen(npp) ON DELETE SET NULL
            )
        """;

        executeSQL(sql, "mahasiswa table");
    }

    /**
     * Execute SQL statement with error handling
     * @param sql SQL statement to execute
     * @param description Description for logging
     */
    private void executeSQL(String sql, String description) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ " + description + " created/verified successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating " + description, e);
        }
    }

    /**
     * Test database connection
     * @return true if connection is valid
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔒 Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Get database file name
     * @return database file name
     */
    public String getDatabaseName() {
        return DB_NAME;
    }
}
