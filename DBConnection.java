import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = System.getenv().getOrDefault("MEDSEARCH_DB_URL", "jdbc:postgresql://localhost:5432/medsearch");
    private static final String USER = System.getenv().getOrDefault("MEDSEARCH_DB_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("MEDSEARCH_DB_PASSWORD", "postgres");

    private static Boolean isConnected = null;
    private static boolean schemaInitialized = false;

    public static synchronized boolean isAvailable() {
        if (isConnected != null) return isConnected;
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.setLoginTimeout(2);
            try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
                isConnected = true;
                initSchemaIfNecessary(con);
                DatasetStore.getInstance().syncToDatabase(con);
                return true;
            }
        } catch (Throwable e) {
            isConnected = false;
            return false;
        }
    }

    public static Connection getConnection() throws Exception {
        if (!isAvailable()) {
            throw new IllegalStateException("PostgreSQL is not available. Using In-Memory dataset store.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void initSchemaIfNecessary(Connection con) {
        if (schemaInitialized) return;
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "patient_id INT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT NOT NULL, " +
                    "gender VARCHAR(30), " +
                    "phone VARCHAR(30), " +
                    "password VARCHAR(100) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "doctor_id INT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "specialization VARCHAR(100), " +
                    "department VARCHAR(100), " +
                    "availability VARCHAR(100), " +
                    "password VARCHAR(100) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointment_id INT PRIMARY KEY, " +
                    "patient_id INT, " +
                    "doctor_id INT, " +
                    "appointment_date VARCHAR(50), " +
                    "status VARCHAR(30));");

            st.execute("CREATE TABLE IF NOT EXISTS medical_records (" +
                    "record_id INT PRIMARY KEY, " +
                    "patient_id INT, " +
                    "doctor_id INT, " +
                    "diagnosis VARCHAR(255), " +
                    "clinical_notes TEXT, " +
                    "record_date VARCHAR(50));");

            st.execute("CREATE TABLE IF NOT EXISTS lab_reports (" +
                    "report_id INT PRIMARY KEY, " +
                    "patient_id INT, " +
                    "test_name VARCHAR(100), " +
                    "result VARCHAR(255), " +
                    "report_date VARCHAR(50));");

            st.execute("CREATE TABLE IF NOT EXISTS treatments (" +
                    "treatment_id INT PRIMARY KEY, " +
                    "patient_id INT, " +
                    "doctor_id INT, " +
                    "treatment VARCHAR(255), " +
                    "notes TEXT);");

            st.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "admin_id INT PRIMARY KEY, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS managers (" +
                    "manager_id INT PRIMARY KEY, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL);");

            schemaInitialized = true;
        } catch (Exception e) {
            System.err.println("Note: Schema init warning: " + e.getMessage());
        }
    }
}
