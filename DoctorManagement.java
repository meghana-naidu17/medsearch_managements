import java.sql.*;
import java.util.*;

public class DoctorManagement {

    public void addDoctor(Doctor d) {
        if (d == null) return;
        if (getDoctor(d.getDoctorId()) != null) {
            System.out.println("Error: Doctor ID " + d.getDoctorId() + " already exists!");
            return;
        }

        if (DBConnection.isAvailable()) {
            String sql = "INSERT INTO doctors(doctor_id,name,specialization,department,availability,password) VALUES(?,?,?,?,?,?)";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, d.getDoctorId());
                p.setString(2, d.getName());
                p.setString(3, d.getSpecialization());
                p.setString(4, d.getDepartment());
                p.setString(5, d.getAvailability());
                p.setString(6, d.getPassword());
                p.executeUpdate();
            } catch (Exception e) {
                System.out.println("Note: DB write skipped (" + e.getMessage() + "), saving to persistent dataset.");
            }
        }
        DatasetStore.getInstance().addDoctor(d);
        System.out.println("Doctor Added Successfully! (ID: " + d.getDoctorId() + ")");
    }

    public List<Doctor> getAllDoctors() {
        if (DBConnection.isAvailable()) {
            List<Doctor> list = new ArrayList<>();
            String sql = "SELECT * FROM doctors ORDER BY doctor_id";
            try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
                while (r.next()) {
                    list.add(new Doctor(r.getInt("doctor_id"), r.getString("name"), r.getString("specialization"),
                            r.getString("department"), r.getString("availability"), r.getString("password")));
                }
                if (!list.isEmpty()) return list;
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getDoctors();
    }

    public void viewDoctors() {
        List<Doctor> list = getAllDoctors();
        if (list.isEmpty()) {
            System.out.println("No Doctors Found in dataset.");
            return;
        }
        System.out.println("\n---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-18s | %-28s | %-20s\n", "DOC ID", "NAME", "SPECIALIZATION", "DEPARTMENT", "AVAILABILITY");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Doctor d : list) {
            System.out.printf("%-10d | %-20s | %-18s | %-28s | %-20s\n",
                    d.getDoctorId(), d.getName(), d.getSpecialization(), d.getDepartment(), d.getAvailability());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("Total Doctors Available: " + list.size());
    }

    public Doctor login(int id, String password) {
        if (DBConnection.isAvailable()) {
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM doctors WHERE doctor_id=? AND password=?")) {
                p.setInt(1, id);
                p.setString(2, password);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) {
                        return new Doctor(r.getInt("doctor_id"), r.getString("name"), r.getString("specialization"),
                                r.getString("department"), r.getString("availability"), r.getString("password"));
                    }
                }
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().loginDoctor(id, password);
    }

    public Doctor getDoctor(int id) {
        if (DBConnection.isAvailable()) {
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM doctors WHERE doctor_id=?")) {
                p.setInt(1, id);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) {
                        return new Doctor(r.getInt("doctor_id"), r.getString("name"), r.getString("specialization"),
                                r.getString("department"), r.getString("availability"), r.getString("password"));
                    }
                }
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getDoctor(id);
    }

    public void searchDoctorsWithAlgorithm(String query, MedicalSearchEngine.SearchMethod method) {
        List<Doctor> all = getAllDoctors();
        long start = System.nanoTime();
        List<MedicalSearchEngine.SearchResult<Doctor>> results = MedicalSearchEngine.searchDoctors(all, query, method);
        long elapsedMicros = (System.nanoTime() - start) / 1000;

        System.out.println("\n=== DOCTOR SEARCH RESULTS [Algorithm: " + method.getDisplayName() + "] ===");
        System.out.println("Query: \"" + query + "\" | Time: " + elapsedMicros + " us | Matches Found: " + results.size());
        if (results.isEmpty()) {
            System.out.println("No doctors matched your query.");
            return;
        }
        for (MedicalSearchEngine.SearchResult<Doctor> res : results) {
            Doctor d = res.item;
            System.out.println("-> Doctor ID: " + d.getDoctorId() + " | " + d.getName() + " (" + d.getSpecialization() + ")");
            System.out.println("   Department  : " + d.getDepartment() + " | Hours: " + d.getAvailability());
            System.out.println("   Matched on  : " + res.fieldName + " (" + res.highlightedText + ")");
            if (method == MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN) {
                System.out.printf("   Similarity  : %.1f%%\n", res.similarityScore);
            } else {
                System.out.println("   Positions   : " + res.matchIndices);
            }
            System.out.println("   --------------------------------------------------------");
        }
    }
}
