import java.sql.*;
import java.util.*;

public class AppointmentManagement {

    public void bookAppointment(Appointment a) {
        if (a == null) return;
        DatasetStore store = DatasetStore.getInstance();

        // Validate patient exists
        if (store.getPatient(a.getPatientId()) == null) {
            System.out.println("Booking Failed: Patient ID " + a.getPatientId() + " does not exist.");
            return;
        }

        // Validate doctor exists
        if (store.getDoctor(a.getDoctorId()) == null) {
            System.out.println("Booking Failed: Doctor ID " + a.getDoctorId() + " does not exist.");
            return;
        }

        // Duplicate check
        boolean exists = store.getAppointments().stream().anyMatch(existing -> existing.getAppointmentId() == a.getAppointmentId());
        if (exists) {
            System.out.println("Booking Failed: Appointment ID " + a.getAppointmentId() + " already exists.");
            return;
        }

        if (DBConnection.isAvailable()) {
            String sql = "INSERT INTO appointments(appointment_id,patient_id,doctor_id,appointment_date,status) VALUES(?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, a.getAppointmentId());
                ps.setInt(2, a.getPatientId());
                ps.setInt(3, a.getDoctorId());
                ps.setString(4, a.getAppointmentDate());
                ps.setString(5, a.getStatus());
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Note: DB write skipped (" + e.getMessage() + "), saving to persistent dataset.");
            }
        }
        store.addAppointment(a);
        System.out.println("Appointment Booked Successfully! (ID: " + a.getAppointmentId() + ")");
    }

    public List<Appointment> getAllAppointments() {
        if (DBConnection.isAvailable()) {
            List<Appointment> list = new ArrayList<>();
            String sql = "SELECT * FROM appointments ORDER BY appointment_id";
            try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new Appointment(rs.getInt("appointment_id"), rs.getInt("patient_id"),
                            rs.getInt("doctor_id"), rs.getString("appointment_date"), rs.getString("status")));
                }
                if (!list.isEmpty()) return list;
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getAppointments();
    }

    public void viewByPatient(int patientId) {
        List<Appointment> list = new ArrayList<>();
        if (DBConnection.isAvailable()) {
            String sql = "SELECT * FROM appointments WHERE patient_id=? ORDER BY appointment_id";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Appointment(rs.getInt("appointment_id"), rs.getInt("patient_id"),
                                rs.getInt("doctor_id"), rs.getString("appointment_date"), rs.getString("status")));
                    }
                }
            } catch (Exception ignored) {}
        }
        if (list.isEmpty()) {
            list = DatasetStore.getInstance().getAppointmentsByPatient(patientId);
        }

        if (list.isEmpty()) {
            System.out.println("No Appointments Found for Patient ID: " + patientId);
            return;
        }
        printAppointmentTable(list);
    }

    public void viewByDoctor(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        if (DBConnection.isAvailable()) {
            String sql = "SELECT * FROM appointments WHERE doctor_id=? ORDER BY appointment_id";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Appointment(rs.getInt("appointment_id"), rs.getInt("patient_id"),
                                rs.getInt("doctor_id"), rs.getString("appointment_date"), rs.getString("status")));
                    }
                }
            } catch (Exception ignored) {}
        }
        if (list.isEmpty()) {
            list = DatasetStore.getInstance().getAppointmentsByDoctor(doctorId);
        }

        if (list.isEmpty()) {
            System.out.println("No Appointments Found for Doctor ID: " + doctorId);
            return;
        }
        printAppointmentTable(list);
    }

    public void viewAll() {
        List<Appointment> list = getAllAppointments();
        if (list.isEmpty()) {
            System.out.println("No Appointments Found in dataset.");
            return;
        }
        printAppointmentTable(list);
    }

    public void updateStatus(int appointmentId, String newStatus) {
        if (DBConnection.isAvailable()) {
            String sql = "UPDATE appointments SET status=? WHERE appointment_id=?";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, appointmentId);
                ps.executeUpdate();
            } catch (Exception ignored) {}
        }
        boolean ok = DatasetStore.getInstance().updateAppointmentStatus(appointmentId, newStatus);
        if (ok) {
            System.out.println("Appointment #" + appointmentId + " status updated to: " + newStatus);
        } else {
            System.out.println("Appointment #" + appointmentId + " not found.");
        }
    }

    private void printAppointmentTable(List<Appointment> list) {
        DatasetStore store = DatasetStore.getInstance();
        System.out.println("\n-------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-22s | %-22s | %-22s | %-12s\n", "APP ID", "PATIENT", "DOCTOR", "DATE & TIME", "STATUS");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        for (Appointment a : list) {
            User p = store.getPatient(a.getPatientId());
            Doctor d = store.getDoctor(a.getDoctorId());
            String pName = (p != null) ? p.getName() + " (#" + p.getPatientId() + ")" : "Patient #" + a.getPatientId();
            String dName = (d != null) ? d.getName() + " (#" + d.getDoctorId() + ")" : "Doctor #" + a.getDoctorId();
            System.out.printf("%-8d | %-22s | %-22s | %-22s | %-12s\n",
                    a.getAppointmentId(), pName, dName, a.getAppointmentDate(), a.getStatus());
        }
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.println("Total Appointments: " + list.size());
    }

    public void searchAppointmentsWithAlgorithm(String query, MedicalSearchEngine.SearchMethod method) {
        List<Appointment> all = getAllAppointments();
        long start = System.nanoTime();
        List<MedicalSearchEngine.SearchResult<Appointment>> results = MedicalSearchEngine.searchAppointments(all, query, method, DatasetStore.getInstance());
        long elapsedMicros = (System.nanoTime() - start) / 1000;

        System.out.println("\n=== APPOINTMENT SEARCH RESULTS [Algorithm: " + method.getDisplayName() + "] ===");
        System.out.println("Query: \"" + query + "\" | Time: " + elapsedMicros + " µs | Matches Found: " + results.size());
        if (results.isEmpty()) {
            System.out.println("No appointments matched your query.");
            return;
        }
        DatasetStore store = DatasetStore.getInstance();
        for (MedicalSearchEngine.SearchResult<Appointment> res : results) {
            Appointment a = res.item;
            User p = store.getPatient(a.getPatientId());
            Doctor d = store.getDoctor(a.getDoctorId());
            String pName = (p != null) ? p.getName() : "ID:" + a.getPatientId();
            String dName = (d != null) ? d.getName() : "ID:" + a.getDoctorId();

            System.out.println("-> Appointment #" + a.getAppointmentId() + " | Patient: " + pName + " | Doctor: " + dName);
            System.out.println("   Date: " + a.getAppointmentDate() + " | Status: " + a.getStatus());
            System.out.println("   Matched Field: " + res.fieldName + " (" + res.highlightedText + ")");
            if (method == MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN) {
                System.out.printf("   Similarity Score: %.1f%%\n", res.similarityScore);
            } else {
                System.out.println("   Match Indices: " + res.matchIndices);
            }
            System.out.println("   --------------------------------------------------------");
        }
    }
}
