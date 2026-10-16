import java.util.*;



public class Main {

    static Scanner sc = new Scanner(System.in);

    static UserManagement patientManager = new UserManagement();

    static DoctorManagement doctorManager = new DoctorManagement();

    static AppointmentManagement appointmentManager = new AppointmentManagement();

    static MedicalRecordManagement recordManager = new MedicalRecordManagement();

    static LabReportManagement labManager = new LabReportManagement();

    static TreatmentManagement treatmentManager = new TreatmentManagement();

    static AdminManagement adminManager = new AdminManagement();

    static ManagerManagement managerManager = new ManagerManagement();



    public static void main(String[] args) {

        // Initialize dataset and connection

        boolean dbActive = DBConnection.isAvailable();

        DatasetStore store = DatasetStore.getInstance();



        System.out.println("================================================================================");

        System.out.println("          MEDSEARCH - ADVANCED MEDICAL INTELLIGENCE & MANAGEMENT SYSTEM         ");

        System.out.println("================================================================================");

        if (dbActive) {

            System.out.println("  [STORAGE STATUS] PostgreSQL Database: CONNECTED & SYNCHRONIZED");

        } else {

            System.out.println("  [STORAGE STATUS] Embedded High-Performance Dataset Store: ACTIVE");

        }

        System.out.printf("  [LOADED DATASET] %d Patients | %d Doctors | %d Appointments | %d Records\n",

                store.getPatients().size(), store.getDoctors().size(),

                store.getAppointments().size(), store.getMedicalRecords().size());

        System.out.println("================================================================================");



        int choice;

        do {

            System.out.println("\n========================================");

            System.out.println("   MEDSEARCH MEDICAL MANAGEMENT SYSTEM");

            System.out.println("========================================");

            System.out.println("1. Patient Portal");

            System.out.println("2. Doctor Portal");

            System.out.println("3. Admin Login");

            System.out.println("4. Manager Login");

            System.out.println("5. Specialized String Matching Search");

            System.out.println("6. Medical Information & Algorithm Analysis");

            System.out.println("7. View Medical Statistics");

            System.out.println("8. Exit");

            choice = readInt("Enter Choice: ");

            switch (choice) {

                case 1 -> patientPortal();

                case 2 -> doctorPortal();

                case 3 -> adminPortal();

                case 4 -> managerPortal();

                case 5 -> unifiedSearchMenu();

                case 6 -> algorithmMenu();

                case 7 -> new MedicalStatistics().show();

                case 8 -> System.out.println("\nThank you for using MedSearch Management System. Goodbye!");

                default -> System.out.println("Invalid Choice! Please enter 1-8.");

            }

        } while (choice != 8);

        sc.close();

    }



    // =========================================================================

    // PATIENT PORTAL

    // =========================================================================

    static void patientPortal() {

        int choice;

        do {

            System.out.println("\n===== PATIENT PORTAL =====");

            System.out.println("1. Register New Patient");

            System.out.println("2. Login");

            System.out.println("3. Back to Main Menu");

            choice = readInt("Enter Choice: ");

            if (choice == 1) {

                int id = readInt("Enter Patient ID (e.g. 126): ");

                System.out.print("Enter Full Name: ");

                String name = sc.nextLine().trim();

                int age = readInt("Enter Age: ");

                System.out.print("Enter Gender (Male/Female/Other): ");

                String gender = sc.nextLine().trim();

                System.out.print("Enter Phone: ");

                String phone = sc.nextLine().trim();

                System.out.print("Enter Password: ");

                String password = sc.nextLine().trim();

                patientManager.registerPatient(new User(id, name, age, gender, phone, password));

            } else if (choice == 2) {

                int id = readInt("Enter Patient ID (e.g. 101): ");

                System.out.print("Enter Password (e.g. pass123): ");

                String pw = sc.nextLine().trim();

                User p = patientManager.loginPatient(id, pw);

                if (p != null) {

                    System.out.println("\nWelcome back, " + p.getName() + "!");

                    patientMenu(p);

                } else {

                    System.out.println("Invalid Credentials! Please try again.");

                }

            } else if (choice != 3) {

                System.out.println("Invalid Choice!");

            }

        } while (choice != 3);

    }



    static void patientMenu(User p) {

        int c;

        do {

            System.out.println("\n===== PATIENT MENU [" + p.getName() + "] =====");

            System.out.println("1. View Medical Profile");

            System.out.println("2. View Medical History / Diagnoses");

            System.out.println("3. View Lab Reports");

            System.out.println("4. View Prescribed Treatments");

            System.out.println("5. Book an Appointment");

            System.out.println("6. View My Appointments");

            System.out.println("7. Search Doctors by Specialty/Condition (String Matching)");

            System.out.println("8. Logout");

            c = readInt("Enter Choice: ");

            switch (c) {

                case 1 -> p.display();

                case 2 -> recordManager.viewByPatient(p.getPatientId());

                case 3 -> labManager.viewByPatient(p.getPatientId());

                case 4 -> treatmentManager.viewByPatient(p.getPatientId());

                case 5 -> bookForPatient(p.getPatientId());

                case 6 -> appointmentManager.viewByPatient(p.getPatientId());

                case 7 -> searchDoctorsInteractive();

                case 8 -> System.out.println("Logged Out Successfully!");

                default -> System.out.println("Invalid Choice!");

            }

        } while (c != 8);

    }



    static void bookForPatient(int patientId) {

        System.out.println("\n--- Available Doctors ---");

        doctorManager.viewDoctors();

        int app = readInt("Enter New Appointment ID (e.g. 336): ");

        int doctor = readInt("Enter Doctor ID from list above: ");

        System.out.print("Enter Appointment Date & Time (e.g. 2026-10-20 10:00 AM): ");

        String date = sc.nextLine().trim();

        appointmentManager.bookAppointment(new Appointment(app, patientId, doctor, date, "Scheduled"));

    }



    // =========================================================================

    // DOCTOR PORTAL

    // =========================================================================

    static void doctorPortal() {

        int id = readInt("Enter Doctor ID (e.g. 201): ");

        System.out.print("Enter Password (e.g. doctor123): ");

        String password = sc.nextLine().trim();

        Doctor d = doctorManager.login(id, password);

        if (d == null) {

            System.out.println("Invalid Doctor Credentials!");

            return;

        }

        System.out.println("\nLogin Successful! Welcome " + d.getName() + " (" + d.getSpecialization() + ")");

        int c;

        do {

            System.out.println("\n===== DOCTOR MENU [" + d.getName() + "] =====");

            System.out.println("1. View Doctor Profile");

            System.out.println("2. View My Scheduled Appointments");

            System.out.println("3. View All Registered Patients");

            System.out.println("4. Add Medical Record / Diagnosis");

            System.out.println("5. Add Lab Report");

            System.out.println("6. Add Treatment Plan");

            System.out.println("7. Update Appointment Status");

            System.out.println("8. Search Patients & Records (String Matching)");

            System.out.println("9. Logout");

            c = readInt("Enter Choice: ");

            switch (c) {

                case 1 -> d.display();

                case 2 -> appointmentManager.viewByDoctor(d.getDoctorId());

                case 3 -> patientManager.viewPatients();

                case 4 -> addRecord(d.getDoctorId());

                case 5 -> addLabReport();

                case 6 -> addTreatment(d.getDoctorId());

                case 7 -> updateAppointmentStatusInteractive();

                case 8 -> searchPatientsOrRecordsInteractive();

                case 9 -> System.out.println("Doctor Logged Out Successfully!");

                default -> System.out.println("Invalid Choice!");

            }

        } while (c != 9);

    }



    static void addRecord(int doctorId) {

        int id = readInt("Enter New Record ID: ");

        int patient = readInt("Enter Patient ID: ");

        System.out.print("Enter Diagnosis (e.g. Type 2 Diabetes): ");

        String diagnosis = sc.nextLine().trim();

        System.out.print("Enter Detailed Clinical Notes: ");

        String notes = sc.nextLine().trim();

        System.out.print("Enter Record Date (YYYY-MM-DD): ");

        String date = sc.nextLine().trim();

        recordManager.addRecord(new MedicalRecord(id, patient, doctorId, diagnosis, notes, date));

    }



    static void addLabReport() {

        int id = readInt("Enter New Report ID: ");

        int patient = readInt("Enter Patient ID: ");

        System.out.print("Enter Test Name: ");

        String test = sc.nextLine().trim();

        System.out.print("Enter Result Value / Summary: ");

        String result = sc.nextLine().trim();

        System.out.print("Enter Report Date (YYYY-MM-DD): ");

        String date = sc.nextLine().trim();

        labManager.addReport(new LabReport(id, patient, test, result, date));

    }



    static void addTreatment(int doctorId) {

        int id = readInt("Enter New Treatment ID: ");

        int patient = readInt("Enter Patient ID: ");

        System.out.print("Enter Treatment / Medication Plan: ");

        String treatment = sc.nextLine().trim();

        System.out.print("Enter Clinical Instructions / Notes: ");

        String notes = sc.nextLine().trim();

        treatmentManager.addTreatment(new Treatment(id, patient, doctorId, treatment, notes));

    }



    static void updateAppointmentStatusInteractive() {

        int appId = readInt("Enter Appointment ID to update: ");

        System.out.println("Select New Status: 1. Completed | 2. In-Progress | 3. Confirmed | 4. Cancelled");

        int stChoice = readInt("Enter choice (1-4): ");

        String status = switch (stChoice) {

            case 1 -> "Completed";

            case 2 -> "In-Progress";

            case 3 -> "Confirmed";

            case 4 -> "Cancelled";

            default -> "Scheduled";

        };

        appointmentManager.updateStatus(appId, status);

    }



    // =========================================================================

    // ADMIN PORTAL

    // =========================================================================

    static void adminPortal() {

        System.out.print("Enter Admin Username (demo: admin1): ");

        String u = sc.nextLine().trim();

        System.out.print("Enter Password (demo: admin123): ");

        String p = sc.nextLine().trim();

        if (adminManager.login(u, p) == null) {

            System.out.println("Invalid Admin Credentials!");

            return;

        }

        System.out.println("\nWelcome, Administrator " + u + "!");

        int c;

        do {

            System.out.println("\n===== ADMIN CONTROL PANEL =====");

            System.out.println("1. View All Patients");

            System.out.println("2. View All Doctors");

            System.out.println("3. View All Medical Records");

            System.out.println("4. View All Lab Reports");

            System.out.println("5. View All Treatments");

            System.out.println("6. View All Appointments");

            System.out.println("7. Medical Statistics & Analytics");

            System.out.println("8. Unified String Matching Search");

            System.out.println("9. Algorithm Benchmark Suite");

            System.out.println("10. Reload Dataset from CSV");

            System.out.println("11. Logout");

            c = readInt("Enter Choice: ");

            switch (c) {

                case 1 -> patientManager.viewPatients();

                case 2 -> doctorManager.viewDoctors();

                case 3 -> recordManager.viewAll();

                case 4 -> labManager.viewAll();

                case 5 -> treatmentManager.viewAll();

                case 6 -> appointmentManager.viewAll();

                case 7 -> new MedicalStatistics().show();

                case 8 -> unifiedSearchMenu();

                case 9 -> algorithmMenu();

                case 10 -> {

                    DatasetStore.getInstance().loadAll();

                    System.out.println("Dataset successfully reloaded from CSV and embedded store.");

                }

                case 11 -> System.out.println("Admin Logged Out Successfully!");

                default -> System.out.println("Invalid Choice!");

            }

        } while (c != 11);

    }



    // =========================================================================

    // MANAGER PORTAL

    // =========================================================================

    static void managerPortal() {

        System.out.print("Enter Manager Username (demo: manager1): ");

        String u = sc.nextLine().trim();

        System.out.print("Enter Password (demo: manager123): ");

        String p = sc.nextLine().trim();

        if (managerManager.login(u, p) == null) {

            System.out.println("Invalid Manager Credentials!");

            return;

        }

        System.out.println("\nWelcome, Medical Operations Manager " + u + "!");

        int c;

        do {

            System.out.println("\n===== MANAGER MENU =====");

            System.out.println("1. Add New Doctor");

            System.out.println("2. View All Doctors");

            System.out.println("3. View All Patients");

            System.out.println("4. View All Appointments");

            System.out.println("5. Hospital Network & Resource Allocation");

            System.out.println("6. Optimal Greedy Appointment Scheduling");

            System.out.println("7. Unified String Matching Search");

            System.out.println("8. Algorithm Analysis");

            System.out.println("9. Logout");

            c = readInt("Enter Choice: ");

            switch (c) {

                case 1 -> addDoctor();

                case 2 -> doctorManager.viewDoctors();

                case 3 -> patientManager.viewPatients();

                case 4 -> appointmentManager.viewAll();

                case 5 -> M4_NetworkAlgorithms.run();

                case 6 -> ActivitySelection.selectAppointments();

                case 7 -> unifiedSearchMenu();

                case 8 -> algorithmMenu();

                case 9 -> System.out.println("Manager Logged Out Successfully!");

                default -> System.out.println("Invalid Choice!");

            }

        } while (c != 9);

    }



    static void addDoctor() {

        int id = readInt("Enter Doctor ID (e.g. 217): ");

        System.out.print("Enter Doctor Full Name: ");

        String name = sc.nextLine().trim();

        System.out.print("Enter Specialization (e.g. Rheumatology): ");

        String spec = sc.nextLine().trim();

        System.out.print("Enter Hospital Department: ");

        String dep = sc.nextLine().trim();

        System.out.print("Enter Duty Availability (e.g. 10:00 AM - 02:00 PM): ");

        String av = sc.nextLine().trim();

        System.out.print("Enter Portal Password: ");

        String pw = sc.nextLine().trim();

        doctorManager.addDoctor(new Doctor(id, name, spec, dep, av, pw));

    }



    // =========================================================================

    // SPECIFIC STRING MATCHING METHODS & UNIFIED SEARCH SYSTEM

    // (Addresses the key user request directly)

    // =========================================================================

    static void unifiedSearchMenu() {

        int targetChoice;

        do {

            System.out.println("\n========================================================");

            System.out.println("   SPECIALIZED MEDICAL STRING MATCHING SEARCH ENGINE");

            System.out.println("========================================================");

            System.out.println("Choose Target Entity to Search:");

            System.out.println("1. Search Doctors (by Name, Specialization, Department)");

            System.out.println("2. Search Patients (by Name, Phone, Age, Gender)");

            System.out.println("3. Search Appointments (by Date, Status, Patient, Doctor)");

            System.out.println("4. Search Medical Records (by Diagnosis, Clinical Symptoms, Notes)");

            System.out.println("5. Global Search Across ALL Healthcare Datasets");

            System.out.println("6. Run Algorithm Benchmark & Performance Comparison");

            System.out.println("7. Back to Previous Menu");

            targetChoice = readInt("Select Target (1-7): ");



            if (targetChoice >= 1 && targetChoice <= 5) {

                System.out.print("\nEnter Search Term / Query: ");

                String query = sc.nextLine().trim();

                if (query.isEmpty()) {

                    System.out.println("Search query cannot be empty!");

                    continue;

                }



                MedicalSearchEngine.SearchMethod method = selectSearchMethod();

                executeEntitySearch(targetChoice, query, method);

            } else if (targetChoice == 6) {

                System.out.print("\nEnter keyword to benchmark across all string matching algorithms: ");

                String query = sc.nextLine().trim();

                if (!query.isEmpty()) {

                    String corpus = recordManager.getAllClinicalText();

                    MedicalSearchEngine.runBenchmarkComparison(corpus, query);

                }

            } else if (targetChoice != 7) {

                System.out.println("Invalid Selection!");

            }

        } while (targetChoice != 7);

    }



    static MedicalSearchEngine.SearchMethod selectSearchMethod() {
        System.out.println("\nSelect Specific String Matching Method to Use:");
        System.out.println("1. Knuth-Morris-Pratt (KMP) - Fast linear exact match via LPS");
        System.out.println("2. Rabin-Karp - Rolling polynomial hash pattern matching");
        System.out.println("3. Z-Algorithm - Linear exact match via Z-Array");
        System.out.println("4. Fuzzy Matching - Levenshtein Distance (Typo & Misspelling Tolerant)");

        int mChoice = readInt("Enter Method Choice (1-4, default 1): ");

        return switch (mChoice) {
            case 2 -> MedicalSearchEngine.SearchMethod.RABIN_KARP;
            case 3 -> MedicalSearchEngine.SearchMethod.Z_ALGORITHM;
            case 4 -> MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN;
            default -> MedicalSearchEngine.SearchMethod.KMP;
        };
    }



    static void executeEntitySearch(int targetChoice, String query, MedicalSearchEngine.SearchMethod method) {

        switch (targetChoice) {

            case 1 -> doctorManager.searchDoctorsWithAlgorithm(query, method);

            case 2 -> patientManager.searchPatientsWithAlgorithm(query, method);

            case 3 -> appointmentManager.searchAppointmentsWithAlgorithm(query, method);

            case 4 -> recordManager.searchRecordsWithAlgorithm(query, method);

            case 5 -> {

                System.out.println("\n========================================================");

                System.out.println("  GLOBAL SEARCH RESULTS ACROSS ALL DATASETS");

                System.out.println("  Query: \"" + query + "\" | Method: " + method.getDisplayName());

                System.out.println("========================================================");

                doctorManager.searchDoctorsWithAlgorithm(query, method);

                patientManager.searchPatientsWithAlgorithm(query, method);

                appointmentManager.searchAppointmentsWithAlgorithm(query, method);

                recordManager.searchRecordsWithAlgorithm(query, method);

            }

        }

    }



    static void searchDoctorsInteractive() {

        System.out.print("\nEnter Doctor Name or Specialization (e.g. Cardio, Skin, Wilson): ");

        String q = sc.nextLine().trim();

        if (!q.isEmpty()) {

            MedicalSearchEngine.SearchMethod method = selectSearchMethod();

            doctorManager.searchDoctorsWithAlgorithm(q, method);

        }

    }



    static void searchPatientsOrRecordsInteractive() {

        System.out.println("\n1. Search Patients | 2. Search Medical Records & Diagnoses");

        int c = readInt("Enter choice (1-2): ");

        System.out.print("Enter Search Term (e.g. Diabetes, Bronchitis, Ananya): ");

        String q = sc.nextLine().trim();

        if (!q.isEmpty()) {

            MedicalSearchEngine.SearchMethod method = selectSearchMethod();

            if (c == 1) {

                patientManager.searchPatientsWithAlgorithm(q, method);

            } else {

                recordManager.searchRecordsWithAlgorithm(q, method);

            }

        }

    }



    // =========================================================================

    // ALGORITHM SUITE MENU (M1 to M6)

    // =========================================================================

    static void algorithmMenu() {

        int c;

        do {

            System.out.println("\n========================================");

            System.out.println("       MEDSEARCH ALGORITHM ANALYSIS");

            System.out.println("========================================");

            System.out.println("1. M1: String Matching Algorithms (KMP, Z, Rabin-Karp, Aho-Corasick)");

            System.out.println("2. M2: Suffix Structures & Document Similarity");

            System.out.println("3. M3: Advanced Dynamic Programming (Levenshtein, Needleman-Wunsch, Smith-Waterman)");

            System.out.println("4. M4: Network Flow & Graph Algorithms (Ford-Fulkerson, Dijkstra, Bipartite Matching)");

            System.out.println("5. M5: NP-Completeness & Approximation (3-SAT, 2-Approx Vertex Cover)");

            System.out.println("6. M6: Randomized & Parallel Algorithms (Reservoir Sampling, QuickSort, Miller-Rabin)");

            System.out.println("7. Back to Main Menu");

            c = readInt("Enter Choice: ");

            switch (c) {

                case 1 -> M1_StringAlgorithms.run(sc, recordManager.getAllClinicalText());

                case 2 -> M2_DocumentSimilarity.run(sc, recordManager);

                case 3 -> M3_DynamicProgramming.run(sc);

                case 4 -> M4_NetworkAlgorithms.run();

                case 5 -> M5_Approximation.run();

                case 6 -> M6_RandomizedParallel.run();

                case 7 -> System.out.println("Returning to Main Menu...");

                default -> System.out.println("Invalid Choice!");

            }

        } while (c != 7);

    }



    public static int readInt(String prompt) {

        while (true) {

            System.out.print(prompt);

            String line = sc.nextLine().trim();

            try {

                return Integer.parseInt(line);

            } catch (Exception e) {

                System.out.println("Please enter a valid number.");

            }

        }

    }

}
