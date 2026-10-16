import java.sql.*;
import java.util.*;

public class UserManagement {

    public void registerPatient(User patient) {
        if (patient == null) return;
        // Check for duplicate in dataset
        if (getPatient(patient.getPatientId()) != null) {
            System.out.println("Registration failed: Patient ID " + patient.getPatientId() + " already exists!");
            return;
        }

        boolean dbSuccess = false;
        if (DBConnection.isAvailable()) {
            String sql = "INSERT INTO patients(patient_id,name,age,gender,phone,password) VALUES (?,?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, patient.getPatientId());
                ps.setString(2, patient.getName());
                ps.setInt(3, patient.getAge());
                ps.setString(4, patient.getGender());
                ps.setString(5, patient.getPhone());
                ps.setString(6, patient.getPassword());
                ps.executeUpdate();
                dbSuccess = true;
            } catch (Exception e) {
                System.out.println("Note: DB write skipped (" + e.getMessage() + "), saving to persistent dataset.");
            }
        }
        DatasetStore.getInstance().addPatient(patient);
        System.out.println("Patient Registered Successfully! (ID: " + patient.getPatientId() + ")");
    }

    public User loginPatient(int patientId, String password) {
        if (DBConnection.isAvailable()) {
            String sql = "SELECT * FROM patients WHERE patient_id=? AND password=?";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new User(rs.getInt("patient_id"), rs.getString("name"), rs.getInt("age"), rs.getString("gender"), rs.getString("phone"), rs.getString("password"));
                    }
                }
            } catch (Exception e) {
                // fallback to DatasetStore
            }
        }
        return DatasetStore.getInstance().loginPatient(patientId, password);
    }

    public List<User> getAllPatients() {
        if (DBConnection.isAvailable()) {
            List<User> list = new ArrayList<>();
            String sql = "SELECT * FROM patients ORDER BY patient_id";
            try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new User(rs.getInt("patient_id"), rs.getString("name"), rs.getInt("age"), rs.getString("gender"), rs.getString("phone"), rs.getString("password")));
                }
                if (!list.isEmpty()) return list;
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getPatients();
    }

    public void viewPatients() {
        List<User> list = getAllPatients();
        if (list.isEmpty()) {
            System.out.println("No Patients Found in dataset.");
            return;
        }
        System.out.println("\n------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-5s | %-8s | %-12s\n", "PATIENT ID", "NAME", "AGE", "GENDER", "PHONE");
        System.out.println("------------------------------------------------------------");
        for (User p : list) {
            System.out.printf("%-10d | %-20s | %-5d | %-8s | %-12s\n",
                    p.getPatientId(), p.getName(), p.getAge(), p.getGender(), p.getPhone());
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total Patients Registered: " + list.size());
    }

    public User getPatient(int patientId) {
        if (DBConnection.isAvailable()) {
            String sql = "SELECT * FROM patients WHERE patient_id=?";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new User(rs.getInt("patient_id"), rs.getString("name"), rs.getInt("age"), rs.getString("gender"), rs.getString("phone"), rs.getString("password"));
                    }
                }
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getPatient(patientId);
    }

    public void searchPatientsWithAlgorithm(String query, MedicalSearchEngine.SearchMethod method) {
        List<User> all = getAllPatients();
        long start = System.nanoTime();
        List<MedicalSearchEngine.SearchResult<User>> results = MedicalSearchEngine.searchPatients(all, query, method);
        long elapsedMicros = (System.nanoTime() - start) / 1000;

        System.out.println("\n=== PATIENT SEARCH RESULTS [Algorithm: " + method.getDisplayName() + "] ===");
        System.out.println("Query: \"" + query + "\" | Time: " + elapsedMicros + " µs | Matches Found: " + results.size());
        if (results.isEmpty()) {
            System.out.println("No patients matched your query.");
            return;
        }
        for (MedicalSearchEngine.SearchResult<User> res : results) {
            User p = res.item;
            System.out.println("-> Patient ID: " + p.getPatientId() + " | Name: " + p.getName() + " | Age: " + p.getAge() + " | Phone: " + p.getPhone());
            System.out.println("   Matched Field: " + res.fieldName + " (" + res.highlightedText + ")");
            if (method == MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN) {
                System.out.printf("   Similarity Score: %.1f%%\n", res.similarityScore);
            } else {
                System.out.println("   Match Char Positions: " + res.matchIndices);
            }
            System.out.println("   --------------------------------------------------------");
        }
    }
}
