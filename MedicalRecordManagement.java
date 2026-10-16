import java.sql.*;
import java.util.*;

public class MedicalRecordManagement {

    public void addRecord(MedicalRecord r) {
        if (r == null) return;
        DatasetStore store = DatasetStore.getInstance();
        if (store.getPatient(r.getPatientId()) == null) {
            System.out.println("Error: Patient ID " + r.getPatientId() + " does not exist.");
            return;
        }

        if (DBConnection.isAvailable()) {
            String sql = "INSERT INTO medical_records(record_id,patient_id,doctor_id,diagnosis,clinical_notes,record_date) VALUES(?,?,?,?,?,?)";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, r.getRecordId());
                p.setInt(2, r.getPatientId());
                p.setInt(3, r.getDoctorId());
                p.setString(4, r.getDiagnosis());
                p.setString(5, r.getNotes());
                p.setString(6, r.getRecordDate());
                p.executeUpdate();
            } catch (Exception e) {
                System.out.println("Note: DB write skipped (" + e.getMessage() + "), saving to persistent dataset.");
            }
        }
        store.addMedicalRecord(r);
        System.out.println("Medical Record Added Successfully! (ID: " + r.getRecordId() + ")");
    }

    public List<MedicalRecord> getAllRecords() {
        if (DBConnection.isAvailable()) {
            List<MedicalRecord> list = new ArrayList<>();
            String sql = "SELECT * FROM medical_records ORDER BY record_id";
            try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
                while (r.next()) {
                    list.add(new MedicalRecord(r.getInt("record_id"), r.getInt("patient_id"), r.getInt("doctor_id"),
                            r.getString("diagnosis"), r.getString("clinical_notes"), r.getString("record_date")));
                }
                if (!list.isEmpty()) return list;
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getMedicalRecords();
    }

    public void viewByPatient(int id) {
        List<MedicalRecord> list = new ArrayList<>();
        if (DBConnection.isAvailable()) {
            String sql = "SELECT * FROM medical_records WHERE patient_id=? ORDER BY record_date";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        list.add(new MedicalRecord(r.getInt("record_id"), r.getInt("patient_id"), r.getInt("doctor_id"),
                                r.getString("diagnosis"), r.getString("clinical_notes"), r.getString("record_date")));
                    }
                }
            } catch (Exception ignored) {}
        }
        if (list.isEmpty()) {
            list = DatasetStore.getInstance().getMedicalRecordsByPatient(id);
        }

        if (list.isEmpty()) {
            System.out.println("No Medical Records Found for Patient ID: " + id);
            return;
        }
        printRecords(list);
    }

    public void viewAll() {
        List<MedicalRecord> list = getAllRecords();
        if (list.isEmpty()) {
            System.out.println("No Medical Records Found in dataset.");
            return;
        }
        printRecords(list);
    }

    private void printRecords(List<MedicalRecord> list) {
        DatasetStore store = DatasetStore.getInstance();
        System.out.println("\n=========================================================================================");
        System.out.printf("%-8s | %-16s | %-16s | %-24s | %-12s\n", "REC ID", "PATIENT", "DOCTOR", "DIAGNOSIS", "DATE");
        System.out.println("=========================================================================================");
        for (MedicalRecord r : list) {
            User p = store.getPatient(r.getPatientId());
            Doctor d = store.getDoctor(r.getDoctorId());
            String pName = (p != null) ? p.getName() : "ID:" + r.getPatientId();
            String dName = (d != null) ? d.getName() : "ID:" + r.getDoctorId();
            System.out.printf("%-8d | %-16s | %-16s | %-24s | %-12s\n",
                    r.getRecordId(), pName, dName, r.getDiagnosis(), r.getRecordDate());
            System.out.println("  Clinical Notes: " + r.getNotes());
            System.out.println("-----------------------------------------------------------------------------------------");
        }
        System.out.println("Total Records: " + list.size());
    }

    public String getAllClinicalText() {
        List<MedicalRecord> list = getAllRecords();
        StringBuilder b = new StringBuilder();
        for (MedicalRecord r : list) {
            b.append("REPORT ").append(r.getRecordId()).append(" ")
             .append(r.getDiagnosis()).append(" ")
             .append(r.getNotes()).append("\n");
        }
        return b.toString();
    }

    public String getReportText(int id) {
        MedicalRecord r = getAllRecords().stream().filter(rec -> rec.getRecordId() == id).findFirst().orElse(null);
        if (r != null) {
            return r.getDiagnosis() + " " + r.getNotes();
        }
        return "";
    }

    public void searchRecordsWithAlgorithm(String query, MedicalSearchEngine.SearchMethod method) {
        List<MedicalRecord> all = getAllRecords();
        long start = System.nanoTime();
        List<MedicalSearchEngine.SearchResult<MedicalRecord>> results = MedicalSearchEngine.searchMedicalRecords(all, query, method);
        long elapsedMicros = (System.nanoTime() - start) / 1000;

        System.out.println("\n=== MEDICAL RECORD SEARCH RESULTS [Algorithm: " + method.getDisplayName() + "] ===");
        System.out.println("Query: \"" + query + "\" | Time: " + elapsedMicros + " µs | Matches Found: " + results.size());
        if (results.isEmpty()) {
            System.out.println("No medical records matched your query.");
            return;
        }
        for (MedicalSearchEngine.SearchResult<MedicalRecord> res : results) {
            MedicalRecord r = res.item;
            System.out.println("-> Record #" + r.getRecordId() + " (Date: " + r.getRecordDate() + ") | Patient ID: " + r.getPatientId());
            System.out.println("   Diagnosis: " + r.getDiagnosis());
            System.out.println("   Notes    : " + r.getNotes());
            System.out.println("   Matched  : " + res.fieldName + " (" + res.highlightedText + ")");
            if (method == MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN) {
                System.out.printf("   Similarity Score: %.1f%%\n", res.similarityScore);
            } else {
                System.out.println("   Match Indices: " + res.matchIndices);
            }
            System.out.println("   --------------------------------------------------------");
        }
    }
}
