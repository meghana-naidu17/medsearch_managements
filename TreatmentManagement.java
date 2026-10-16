import java.sql.*;
import java.util.*;

public class TreatmentManagement {

    public void addTreatment(Treatment t) {
        if (t == null) return;
        DatasetStore store = DatasetStore.getInstance();
        if (store.getPatient(t.getPatientId()) == null) {
            System.out.println("Error: Patient ID " + t.getPatientId() + " does not exist.");
            return;
        }

        if (DBConnection.isAvailable()) {
            String q = "INSERT INTO treatments(treatment_id,patient_id,doctor_id,treatment,notes) VALUES(?,?,?,?,?)";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
                p.setInt(1, t.getTreatmentId());
                p.setInt(2, t.getPatientId());
                p.setInt(3, t.getDoctorId());
                p.setString(4, t.getTreatment());
                p.setString(5, t.getNotes());
                p.executeUpdate();
            } catch (Exception e) {
                System.out.println("Note: DB write skipped (" + e.getMessage() + "), saving to persistent dataset.");
            }
        }
        store.addTreatment(t);
        System.out.println("Treatment Added Successfully! (ID: " + t.getTreatmentId() + ")");
    }

    public List<Treatment> getAllTreatments() {
        if (DBConnection.isAvailable()) {
            List<Treatment> list = new ArrayList<>();
            String q = "SELECT * FROM treatments ORDER BY treatment_id";
            try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(q)) {
                while (r.next()) {
                    list.add(new Treatment(r.getInt("treatment_id"), r.getInt("patient_id"), r.getInt("doctor_id"),
                            r.getString("treatment"), r.getString("notes")));
                }
                if (!list.isEmpty()) return list;
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getTreatments();
    }

    public void viewByPatient(int patientId) {
        List<Treatment> all = getAllTreatments();
        List<Treatment> filtered = new ArrayList<>();
        for (Treatment t : all) {
            if (t.getPatientId() == patientId) filtered.add(t);
        }
        if (filtered.isEmpty()) {
            System.out.println("No Treatments Found for Patient ID: " + patientId);
            return;
        }
        printTreatments(filtered);
    }

    public void viewAll() {
        List<Treatment> list = getAllTreatments();
        if (list.isEmpty()) {
            System.out.println("No Treatments Found in dataset.");
            return;
        }
        printTreatments(list);
    }

    private void printTreatments(List<Treatment> list) {
        DatasetStore store = DatasetStore.getInstance();
        System.out.println("\n---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-16s | %-16s | %-32s | %-24s\n", "TRT ID", "PATIENT", "DOCTOR", "TREATMENT PLAN", "INSTRUCTIONS");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Treatment t : list) {
            User p = store.getPatient(t.getPatientId());
            Doctor d = store.getDoctor(t.getDoctorId());
            String pName = (p != null) ? p.getName() : "Patient #" + t.getPatientId();
            String dName = (d != null) ? d.getName() : "Doctor #" + t.getDoctorId();
            System.out.printf("%-8d | %-16s | %-16s | %-32s | %-24s\n",
                    t.getTreatmentId(), pName, dName, t.getTreatment(), t.getNotes());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("Total Treatments: " + list.size());
    }
}
