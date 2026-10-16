import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central Data Store for MedSearch Management System.
 * Supports:
 * 1. Loading and persisting CSV datasets from data/
 * 2. Embedded rich dataset fallback
 * 3. Bidirectional sync with PostgreSQL database
 * 4. In-memory high performance search cache
 */
public class DatasetStore {
    private static DatasetStore instance;

    private final List<User> patients = new CopyOnWriteArrayList<>();
    private final List<Doctor> doctors = new CopyOnWriteArrayList<>();
    private final List<Appointment> appointments = new CopyOnWriteArrayList<>();
    private final List<MedicalRecord> medicalRecords = new CopyOnWriteArrayList<>();
    private final List<LabReport> labReports = new CopyOnWriteArrayList<>();
    private final List<Treatment> treatments = new CopyOnWriteArrayList<>();
    private final List<Admin> admins = new CopyOnWriteArrayList<>();
    private final List<Manager> managers = new CopyOnWriteArrayList<>();

    private final Path dataDir = Paths.get("data");

    private DatasetStore() {
        loadAll();
    }

    public static synchronized DatasetStore getInstance() {
        if (instance == null) {
            instance = new DatasetStore();
        }
        return instance;
    }

    public synchronized void loadAll() {
        patients.clear();
        doctors.clear();
        appointments.clear();
        medicalRecords.clear();
        labReports.clear();
        treatments.clear();
        admins.clear();
        managers.clear();

        boolean loadedFromCsv = loadFromCsvFiles();
        if (!loadedFromCsv || patients.isEmpty() || doctors.isEmpty()) {
            loadEmbeddedDefaults();
        }
    }

    private boolean loadFromCsvFiles() {
        try {
            if (!Files.exists(dataDir)) {
                return false;
            }
            Path pFile = dataDir.resolve("patients.csv");
            if (Files.exists(pFile)) {
                for (String line : Files.readAllLines(pFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("patient_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 6) {
                        patients.add(new User(Integer.parseInt(parts[0].trim()), parts[1].trim(),
                                Integer.parseInt(parts[2].trim()), parts[3].trim(), parts[4].trim(), parts[5].trim()));
                    }
                }
            }

            Path dFile = dataDir.resolve("doctors.csv");
            if (Files.exists(dFile)) {
                for (String line : Files.readAllLines(dFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("doctor_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 6) {
                        doctors.add(new Doctor(Integer.parseInt(parts[0].trim()), parts[1].trim(), parts[2].trim(),
                                parts[3].trim(), parts[4].trim(), parts[5].trim()));
                    }
                }
            }

            Path aFile = dataDir.resolve("appointments.csv");
            if (Files.exists(aFile)) {
                for (String line : Files.readAllLines(aFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("appointment_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 5) {
                        appointments.add(new Appointment(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                                Integer.parseInt(parts[2].trim()), parts[3].trim(), parts[4].trim()));
                    }
                }
            }

            Path rFile = dataDir.resolve("medical_records.csv");
            if (Files.exists(rFile)) {
                for (String line : Files.readAllLines(rFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("record_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 6) {
                        medicalRecords.add(new MedicalRecord(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                                Integer.parseInt(parts[2].trim()), parts[3].trim(), parts[4].trim(), parts[5].trim()));
                    }
                }
            }

            Path lFile = dataDir.resolve("lab_reports.csv");
            if (Files.exists(lFile)) {
                for (String line : Files.readAllLines(lFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("report_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 5) {
                        labReports.add(new LabReport(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                                parts[2].trim(), parts[3].trim(), parts[4].trim()));
                    }
                }
            }

            Path tFile = dataDir.resolve("treatments.csv");
            if (Files.exists(tFile)) {
                for (String line : Files.readAllLines(tFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("treatment_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 5) {
                        treatments.add(new Treatment(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                                Integer.parseInt(parts[2].trim()), parts[3].trim(), parts[4].trim()));
                    }
                }
            }

            Path admFile = dataDir.resolve("admins.csv");
            if (Files.exists(admFile)) {
                for (String line : Files.readAllLines(admFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("admin_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 3) {
                        admins.add(new Admin(Integer.parseInt(parts[0].trim()), parts[1].trim(), parts[2].trim()));
                    }
                }
            }

            Path mgrFile = dataDir.resolve("managers.csv");
            if (Files.exists(mgrFile)) {
                for (String line : Files.readAllLines(mgrFile)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("manager_id")) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 3) {
                        managers.add(new Manager(Integer.parseInt(parts[0].trim()), parts[1].trim(), parts[2].trim()));
                    }
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Warning: Error reading CSV datasets: " + e.getMessage());
            return false;
        }
    }

    private void loadEmbeddedDefaults() {
        if (patients.isEmpty()) {
            patients.add(new User(101, "Ananya Sharma", 24, "Female", "9000000001", "pass123"));
            patients.add(new User(102, "Rahul Verma", 31, "Male", "9000000002", "rahul123"));
            patients.add(new User(103, "Priya Patel", 28, "Female", "9000000003", "priya123"));
            patients.add(new User(104, "David Chen", 45, "Male", "9000000004", "david123"));
            patients.add(new User(105, "Sarah Jenkins", 52, "Female", "9000000005", "sarah123"));
            patients.add(new User(106, "Aisha Khan", 34, "Female", "9000000006", "aisha123"));
            patients.add(new User(107, "Carlos Gomez", 39, "Male", "9000000007", "carlos123"));
            patients.add(new User(108, "Emily Taylor", 29, "Female", "9000000008", "emily123"));
            patients.add(new User(109, "Vikram Malhotra", 61, "Male", "9000000009", "vikram123"));
            patients.add(new User(110, "Sneha Nair", 22, "Female", "9000000010", "sneha123"));
        }

        if (doctors.isEmpty()) {
            doctors.add(new Doctor(201, "Dr. Arun Sharma", "Cardiology", "Heart Department", "09:00 AM - 01:00 PM", "doctor123"));
            doctors.add(new Doctor(202, "Dr. Meera Iyer", "Dermatology", "Skin & Allergy Department", "11:00 AM - 03:00 PM", "doctor123"));
            doctors.add(new Doctor(203, "Dr. Ravi Kumar", "General Medicine", "Internal Medicine Department", "08:30 AM - 12:30 PM", "doctor123"));
            doctors.add(new Doctor(204, "Dr. Elena Rostova", "Neurology", "Neuroscience Department", "10:00 AM - 02:00 PM", "doctor123"));
            doctors.add(new Doctor(205, "Dr. Sanjay Gupta", "Orthopedics", "Bone & Joint Department", "01:00 PM - 05:00 PM", "doctor123"));
            doctors.add(new Doctor(206, "Dr. Sarah Mitchell", "Pediatrics", "Child Healthcare Department", "09:00 AM - 01:00 PM", "doctor123"));
            doctors.add(new Doctor(207, "Dr. Tariq Mansoor", "Oncology", "Cancer Care Department", "11:00 AM - 04:00 PM", "doctor123"));
            doctors.add(new Doctor(208, "Dr. Ananya Sen", "Endocrinology", "Diabetes & Hormone Department", "02:00 PM - 06:00 PM", "doctor123"));
        }

        if (appointments.isEmpty()) {
            appointments.add(new Appointment(301, 101, 201, "2026-09-15 09:30 AM", "Scheduled"));
            appointments.add(new Appointment(302, 102, 202, "2026-09-16 11:15 AM", "Scheduled"));
            appointments.add(new Appointment(303, 103, 203, "2026-09-17 09:00 AM", "Completed"));
            appointments.add(new Appointment(304, 104, 204, "2026-09-18 10:30 AM", "Completed"));
            appointments.add(new Appointment(305, 105, 205, "2026-09-19 01:30 PM", "Scheduled"));
        }

        if (medicalRecords.isEmpty()) {
            medicalRecords.add(new MedicalRecord(401, 101, 201, "Hypertension", "Patient has elevated systolic blood pressure. Prescribed lifestyle changes and low sodium diet.", "2026-09-01"));
            medicalRecords.add(new MedicalRecord(402, 102, 202, "Atopic Dermatitis", "Patient reports severe itching erythematous patches on forearm and neck.", "2026-09-02"));
            medicalRecords.add(new MedicalRecord(403, 103, 203, "Acute Viral Bronchitis", "Patient presents with persistent cough low-grade fever and mild fatigue.", "2026-09-03"));
            medicalRecords.add(new MedicalRecord(404, 104, 204, "Migraine with Aura", "Recurrent unilateral throbbing headache accompanied by visual aura and photophobia.", "2026-09-04"));
        }

        if (labReports.isEmpty()) {
            labReports.add(new LabReport(501, 101, "Blood Pressure Monitoring", "145/92 mmHg - Stage 1", "2026-09-01"));
            labReports.add(new LabReport(502, 102, "Serum IgE Level", "380 IU/mL - Elevated", "2026-09-02"));
            labReports.add(new LabReport(503, 103, "Complete Blood Count (CBC)", "WBC 11500 /mcL", "2026-09-03"));
        }

        if (treatments.isEmpty()) {
            treatments.add(new Treatment(601, 101, 201, "Telmisartan 40mg daily", "Check BP weekly"));
            treatments.add(new Treatment(602, 102, 202, "Mometasone furoate 0.1% cream", "Apply twice daily"));
            treatments.add(new Treatment(603, 103, 203, "Azithromycin 500mg daily", "Complete 5 day course"));
        }

        if (admins.isEmpty()) {
            admins.add(new Admin(1, "admin1", "admin123"));
            admins.add(new Admin(2, "admin2", "adminSecure456"));
        }

        if (managers.isEmpty()) {
            managers.add(new Manager(1, "manager1", "manager123"));
            managers.add(new Manager(2, "manager2", "managerSecure456"));
        }
    }

    // Patient methods
    public List<User> getPatients() { return Collections.unmodifiableList(patients); }
    public synchronized void addPatient(User p) {
        patients.removeIf(existing -> existing.getPatientId() == p.getPatientId());
        patients.add(p);
        appendToFile("patients.csv", p.getPatientId() + "," + p.getName() + "," + p.getAge() + "," + p.getGender() + "," + p.getPhone() + "," + p.getPassword());
    }
    public User getPatient(int id) {
        return patients.stream().filter(p -> p.getPatientId() == id).findFirst().orElse(null);
    }
    public User loginPatient(int id, String password) {
        return patients.stream().filter(p -> p.getPatientId() == id && p.getPassword().equals(password)).findFirst().orElse(null);
    }

    // Doctor methods
    public List<Doctor> getDoctors() { return Collections.unmodifiableList(doctors); }
    public synchronized void addDoctor(Doctor d) {
        doctors.removeIf(existing -> existing.getDoctorId() == d.getDoctorId());
        doctors.add(d);
        appendToFile("doctors.csv", d.getDoctorId() + "," + d.getName() + "," + d.getSpecialization() + "," + d.getDepartment() + "," + d.getAvailability() + "," + d.getPassword());
    }
    public Doctor getDoctor(int id) {
        return doctors.stream().filter(d -> d.getDoctorId() == id).findFirst().orElse(null);
    }
    public Doctor loginDoctor(int id, String password) {
        return doctors.stream().filter(d -> d.getDoctorId() == id && d.getPassword().equals(password)).findFirst().orElse(null);
    }

    // Appointment methods
    public List<Appointment> getAppointments() { return Collections.unmodifiableList(appointments); }
    public synchronized void addAppointment(Appointment a) {
        appointments.removeIf(existing -> existing.getAppointmentId() == a.getAppointmentId());
        appointments.add(a);
        appendToFile("appointments.csv", a.getAppointmentId() + "," + a.getPatientId() + "," + a.getDoctorId() + "," + a.getAppointmentDate() + "," + a.getStatus());
    }
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> res = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId() == patientId) res.add(a);
        }
        return res;
    }
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> res = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getDoctorId() == doctorId) res.add(a);
        }
        return res;
    }
    public synchronized boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            if (a.getAppointmentId() == appointmentId) {
                appointments.set(i, new Appointment(a.getAppointmentId(), a.getPatientId(), a.getDoctorId(), a.getAppointmentDate(), newStatus));
                rewriteAppointmentsCsv();
                return true;
            }
        }
        return false;
    }

    // Medical Records methods
    public List<MedicalRecord> getMedicalRecords() { return Collections.unmodifiableList(medicalRecords); }
    public synchronized void addMedicalRecord(MedicalRecord r) {
        medicalRecords.removeIf(existing -> existing.getRecordId() == r.getRecordId());
        medicalRecords.add(r);
        appendToFile("medical_records.csv", r.getRecordId() + "," + r.getPatientId() + "," + r.getDoctorId() + "," + r.getDiagnosis() + "," + r.getNotes() + "," + r.getRecordDate());
    }
    public List<MedicalRecord> getMedicalRecordsByPatient(int patientId) {
        List<MedicalRecord> res = new ArrayList<>();
        for (MedicalRecord r : medicalRecords) {
            if (r.getPatientId() == patientId) res.add(r);
        }
        return res;
    }
    public MedicalRecord getMedicalRecord(int id) {
        return medicalRecords.stream().filter(r -> r.getRecordId() == id).findFirst().orElse(null);
    }

    // Lab Reports methods
    public List<LabReport> getLabReports() { return Collections.unmodifiableList(labReports); }
    public synchronized void addLabReport(LabReport r) {
        labReports.removeIf(existing -> existing.getReportId() == r.getReportId());
        labReports.add(r);
        appendToFile("lab_reports.csv", r.getReportId() + "," + r.getPatientId() + "," + r.getTestName() + "," + r.getResult() + "," + r.getReportDate());
    }
    public List<LabReport> getLabReportsByPatient(int patientId) {
        List<LabReport> res = new ArrayList<>();
        for (LabReport r : labReports) {
            if (r.getPatientId() == patientId) res.add(r);
        }
        return res;
    }

    // Treatments methods
    public List<Treatment> getTreatments() { return Collections.unmodifiableList(treatments); }
    public synchronized void addTreatment(Treatment t) {
        treatments.removeIf(existing -> existing.getTreatmentId() == t.getTreatmentId());
        treatments.add(t);
        appendToFile("treatments.csv", t.getTreatmentId() + "," + t.getPatientId() + "," + t.getDoctorId() + "," + t.getTreatment() + "," + t.getNotes());
    }

    // Admin & Manager methods
    public Admin loginAdmin(String username, String password) {
        return admins.stream().filter(a -> a.getUsername().equalsIgnoreCase(username) && a.getPassword().equals(password)).findFirst().orElse(null);
    }
    public Manager loginManager(String username, String password) {
        return managers.stream().filter(m -> m.getUsername().equalsIgnoreCase(username) && m.getPassword().equals(password)).findFirst().orElse(null);
    }

    private void appendToFile(String filename, String line) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            Path p = dataDir.resolve(filename);
            Files.writeString(p, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
    }

    private void rewriteAppointmentsCsv() {
        try {
            Path p = dataDir.resolve("appointments.csv");
            List<String> lines = new ArrayList<>();
            lines.add("appointment_id,patient_id,doctor_id,appointment_date,status");
            for (Appointment a : appointments) {
                lines.add(a.getAppointmentId() + "," + a.getPatientId() + "," + a.getDoctorId() + "," + a.getAppointmentDate() + "," + a.getStatus());
            }
            Files.write(p, lines);
        } catch (Exception ignored) {}
    }

    /**
     * Seeds the in-memory dataset to the PostgreSQL database if the tables are empty.
     */
    public synchronized void syncToDatabase(Connection con) {
        try (Statement st = con.createStatement()) {
            // Check patients count
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM patients");
            int patientCount = rs.next() ? rs.getInt(1) : 0;
            if (patientCount == 0) {
                String sql = "INSERT INTO patients(patient_id,name,age,gender,phone,password) VALUES(?,?,?,?,?,?) ON CONFLICT (patient_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (User p : patients) {
                        ps.setInt(1, p.getPatientId());
                        ps.setString(2, p.getName());
                        ps.setInt(3, p.getAge());
                        ps.setString(4, p.getGender());
                        ps.setString(5, p.getPhone());
                        ps.setString(6, p.getPassword());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM doctors");
            int doctorCount = rs.next() ? rs.getInt(1) : 0;
            if (doctorCount == 0) {
                String sql = "INSERT INTO doctors(doctor_id,name,specialization,department,availability,password) VALUES(?,?,?,?,?,?) ON CONFLICT (doctor_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Doctor d : doctors) {
                        ps.setInt(1, d.getDoctorId());
                        ps.setString(2, d.getName());
                        ps.setString(3, d.getSpecialization());
                        ps.setString(4, d.getDepartment());
                        ps.setString(5, d.getAvailability());
                        ps.setString(6, d.getPassword());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM appointments");
            int appCount = rs.next() ? rs.getInt(1) : 0;
            if (appCount == 0) {
                String sql = "INSERT INTO appointments(appointment_id,patient_id,doctor_id,appointment_date,status) VALUES(?,?,?,?,?) ON CONFLICT (appointment_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Appointment a : appointments) {
                        ps.setInt(1, a.getAppointmentId());
                        ps.setInt(2, a.getPatientId());
                        ps.setInt(3, a.getDoctorId());
                        ps.setString(4, a.getAppointmentDate());
                        ps.setString(5, a.getStatus());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM medical_records");
            int recCount = rs.next() ? rs.getInt(1) : 0;
            if (recCount == 0) {
                String sql = "INSERT INTO medical_records(record_id,patient_id,doctor_id,diagnosis,clinical_notes,record_date) VALUES(?,?,?,?,?,?) ON CONFLICT (record_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (MedicalRecord r : medicalRecords) {
                        ps.setInt(1, r.getRecordId());
                        ps.setInt(2, r.getPatientId());
                        ps.setInt(3, r.getDoctorId());
                        ps.setString(4, r.getDiagnosis());
                        ps.setString(5, r.getNotes());
                        ps.setString(6, r.getRecordDate());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM lab_reports");
            int labCount = rs.next() ? rs.getInt(1) : 0;
            if (labCount == 0) {
                String sql = "INSERT INTO lab_reports(report_id,patient_id,test_name,result,report_date) VALUES(?,?,?,?,?) ON CONFLICT (report_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (LabReport l : labReports) {
                        ps.setInt(1, l.getReportId());
                        ps.setInt(2, l.getPatientId());
                        ps.setString(3, l.getTestName());
                        ps.setString(4, l.getResult());
                        ps.setString(5, l.getReportDate());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM treatments");
            int treatCount = rs.next() ? rs.getInt(1) : 0;
            if (treatCount == 0) {
                String sql = "INSERT INTO treatments(treatment_id,patient_id,doctor_id,treatment,notes) VALUES(?,?,?,?,?) ON CONFLICT (treatment_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Treatment t : treatments) {
                        ps.setInt(1, t.getTreatmentId());
                        ps.setInt(2, t.getPatientId());
                        ps.setInt(3, t.getDoctorId());
                        ps.setString(4, t.getTreatment());
                        ps.setString(5, t.getNotes());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM admins");
            int admCount = rs.next() ? rs.getInt(1) : 0;
            if (admCount == 0) {
                String sql = "INSERT INTO admins(admin_id,username,password) VALUES(?,?,?) ON CONFLICT (admin_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Admin a : admins) {
                        ps.setInt(1, a.getId());
                        ps.setString(2, a.getUsername());
                        ps.setString(3, a.getPassword());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            rs = st.executeQuery("SELECT COUNT(*) FROM managers");
            int mgrCount = rs.next() ? rs.getInt(1) : 0;
            if (mgrCount == 0) {
                String sql = "INSERT INTO managers(manager_id,username,password) VALUES(?,?,?) ON CONFLICT (manager_id) DO NOTHING";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Manager m : managers) {
                        ps.setInt(1, m.getId());
                        ps.setString(2, m.getUsername());
                        ps.setString(3, m.getPassword());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (Exception e) {
            System.err.println("Note: Database sync skipped: " + e.getMessage());
        }
    }
}
