import java.sql.*;
import java.util.*;

public class LabReportManagement {

    public void addReport(LabReport r) {
        if (r == null) return;
        DatasetStore store = DatasetStore.getInstance();
        if (store.getPatient(r.getPatientId()) == null) {
            System.out.println("Error: Patient ID " + r.getPatientId() + " does not exist.");
            return;
        }

        if (DBConnection.isAvailable()) {
            String q = "INSERT INTO lab_reports(report_id,patient_id,test_name,result,report_date) VALUES(?,?,?,?,?)";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
                p.setInt(1, r.getReportId());
                p.setInt(2, r.getPatientId());
                p.setString(3, r.getTestName());
                p.setString(4, r.getResult());
                p.setString(5, r.getReportDate());
                p.executeUpdate();
            } catch (Exception e) {
                System.out.println("Note: DB write skipped (" + e.getMessage() + "), saving to persistent dataset.");
            }
        }
        store.addLabReport(r);
        System.out.println("Lab Report Added Successfully! (ID: " + r.getReportId() + ")");
    }

    public List<LabReport> getAllReports() {
        if (DBConnection.isAvailable()) {
            List<LabReport> list = new ArrayList<>();
            String q = "SELECT * FROM lab_reports ORDER BY report_id";
            try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(q)) {
                while (r.next()) {
                    list.add(new LabReport(r.getInt("report_id"), r.getInt("patient_id"),
                            r.getString("test_name"), r.getString("result"), r.getString("report_date")));
                }
                if (!list.isEmpty()) return list;
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().getLabReports();
    }

    public void viewByPatient(int id) {
        List<LabReport> list = new ArrayList<>();
        if (DBConnection.isAvailable()) {
            String q = "SELECT * FROM lab_reports WHERE patient_id=? ORDER BY report_date";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
                p.setInt(1, id);
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        list.add(new LabReport(r.getInt("report_id"), r.getInt("patient_id"),
                                r.getString("test_name"), r.getString("result"), r.getString("report_date")));
                    }
                }
            } catch (Exception ignored) {}
        }
        if (list.isEmpty()) {
            list = DatasetStore.getInstance().getLabReportsByPatient(id);
        }

        if (list.isEmpty()) {
            System.out.println("No Lab Reports Found for Patient ID: " + id);
            return;
        }
        printReports(list);
    }

    public void viewAll() {
        List<LabReport> list = getAllReports();
        if (list.isEmpty()) {
            System.out.println("No Lab Reports Found in dataset.");
            return;
        }
        printReports(list);
    }

    private void printReports(List<LabReport> list) {
        DatasetStore store = DatasetStore.getInstance();
        System.out.println("\n---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-18s | %-32s | %-24s | %-12s\n", "REP ID", "PATIENT", "TEST NAME", "RESULT", "DATE");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (LabReport r : list) {
            User p = store.getPatient(r.getPatientId());
            String pName = (p != null) ? p.getName() : "Patient #" + r.getPatientId();
            System.out.printf("%-8d | %-18s | %-32s | %-24s | %-12s\n",
                    r.getReportId(), pName, r.getTestName(), r.getResult(), r.getReportDate());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("Total Lab Reports: " + list.size());
    }
}
